package eu.brrm.oblivio.notifications

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import eu.brrm.oblivio.BuildConfig
import eu.brrm.oblivio.MainActivity
import eu.brrm.oblivio.R

private const val TYPE_NEW_MESSAGE = "new_message"

class OblivioFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        logReceivedMessage(message)

        val pushPayload = OblivioPushPayload.from(message.data)
        val title = pushPayload?.notificationTitleResId?.let(::getString)
            ?: message.notification?.title
            ?: message.data["title"]
            ?: getString(R.string.notifications_fallback_title)
        val body = pushPayload?.notificationBodyResId?.let(::getString)
            ?: message.notification?.body
            ?: message.data["body"]
            ?: message.data["message"]
            ?: return

        if (!hasNotificationPermission()) return

        ensureGeneralNotificationChannel()
        showNotification(
            message = message,
            title = title,
            body = body,
            clickUri = pushPayload?.clickUri() ?: message.webUrl(),
            notificationKey = pushPayload?.notificationKey ?: message.messageId,
        )
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(
        message: RemoteMessage,
        title: String,
        body: String,
        clickUri: Uri?,
        notificationKey: String?,
    ) {
        NotificationManagerCompat.from(this).notify(
            notificationKey?.hashCode()
                ?: message.messageId?.hashCode()
                ?: System.currentTimeMillis().toInt(),
            buildNotification(title = title, body = body, clickUri = clickUri),
        )
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Subscription is intentionally refreshed after login; backend handles deduplication.
    }

    private fun buildNotification(title: String, body: String, clickUri: Uri?) =
        NotificationCompat.Builder(this, generalChannelId())
            .setSmallIcon(R.drawable.ic_notification_small)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setContentIntent(notificationClickPendingIntent(clickUri))
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

    private fun notificationClickPendingIntent(clickUri: Uri?): PendingIntent {
        val customTabsIntent = clickUri?.let(::customTabsIntent)
        if (customTabsIntent != null) {
            return PendingIntent.getActivity(
                this,
                clickUri.toString().hashCode(),
                customTabsIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(
            this,
            MAIN_ACTIVITY_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun customTabsIntent(uri: Uri): Intent? {
        val packageName = CustomTabsClient.getPackageName(this, null) ?: return null
        val intent = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .build()
            .intent
            .apply {
                data = uri
                setPackage(packageName)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        return intent.takeIf { it.resolveActivity(packageManager) != null }
    }

    private fun RemoteMessage.webUrl(): Uri? {
        val rawUrl = data["url"]
            ?: data["link"]
            ?: data["webUrl"]
            ?: notification?.link?.toString()
        val uri = rawUrl?.takeIf { it.isNotBlank() }?.let(Uri::parse)
        return uri?.takeIf { it.scheme == "https" || it.scheme == "http" }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun ensureGeneralNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            generalChannelId(),
            getString(R.string.notifications_channel_general_name),
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = getString(R.string.notifications_channel_general_description)
        }
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    private fun logReceivedMessage(message: RemoteMessage) {
        val notification = message.notification
        Log.d(
            TAG,
            buildString {
                appendLine("Received FCM push message")
                appendLine("messageId=${message.messageId}")
                appendLine("messageType=${message.messageType}")
                appendLine("from=${message.from}")
                appendLine("to=${message.to}")
                appendLine("collapseKey=${message.collapseKey}")
                appendLine("sentTime=${message.sentTime}")
                appendLine("ttl=${message.ttl}")
                appendLine("priority=${message.priority}")
                appendLine("originalPriority=${message.originalPriority}")
                appendLine("data=${message.data}")
            },
        )
    }

    private fun hasNotificationPermission(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
    }

    private fun generalChannelId(): String = getString(R.string.notifications_channel_general_id)

    private sealed interface OblivioPushPayload {
        val notificationKey: String

        val notificationTitleResId: Int

        val notificationBodyResId: Int

        fun clickUri(): Uri?

        data class NewMessage(
            val fromUserId: String?,
            val toUserId: String,
            val conversationId: String?,
        ) : OblivioPushPayload {
            override val notificationTitleResId: Int = R.string.app_name

            override val notificationBodyResId: Int = R.string.notifications_new_message

            override val notificationKey: String =
                conversationId?.let { "$TYPE_NEW_MESSAGE:$it" }
                    ?: listOf(
                        TYPE_NEW_MESSAGE,
                        fromUserId.orEmpty(),
                        toUserId,
                    ).joinToString(separator = ":")

            override fun clickUri(): Uri? {
                val baseUri = BuildConfig.WEB_BASE_URL.takeIf { it.isNotBlank() }?.let(Uri::parse)
                return baseUri?.buildUpon()
                    ?.appendPath(toUserId)
                    ?.build()
            }
        }

        companion object {
            fun from(data: Map<String, String>): OblivioPushPayload? {
                return when (data["type"]) {
                    TYPE_NEW_MESSAGE -> {
                        val toUserId = data["toUserId"]?.takeIf { it.isNotBlank() } ?: return null
                        NewMessage(
                            fromUserId = data["fromUserId"]?.takeIf { it.isNotBlank() },
                            toUserId = toUserId,
                            conversationId = data["conversationId"]?.takeIf { it.isNotBlank() }
                                ?: data["chatId"]?.takeIf { it.isNotBlank() },
                        )
                    }
                    else -> null
                }
            }
        }
    }

    private companion object {
        const val TAG = "OblivioFcmService"
        const val MAIN_ACTIVITY_REQUEST_CODE = 1001
    }
}
