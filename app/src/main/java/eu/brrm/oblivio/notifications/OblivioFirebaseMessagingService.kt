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
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import eu.brrm.oblivio.MainActivity
import eu.brrm.oblivio.R

class OblivioFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        logReceivedMessage(message)

        val title = message.notification?.title
            ?: message.data["title"]
            ?: getString(R.string.notifications_fallback_title)
        val body = message.notification?.body
            ?: message.data["body"]
            ?: message.data["message"]
            ?: return

        if (!hasNotificationPermission()) return

        ensureGeneralNotificationChannel()
        showNotification(message = message, title = title, body = body)
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(message: RemoteMessage, title: String, body: String) {
        NotificationManagerCompat.from(this).notify(
            message.messageId?.hashCode() ?: System.currentTimeMillis().toInt(),
            buildNotification(message = message, title = title, body = body),
        )
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Subscription is intentionally refreshed after login; backend handles deduplication.
    }

    private fun buildNotification(message: RemoteMessage, title: String, body: String) =
        NotificationCompat.Builder(this, generalChannelId())
            .setSmallIcon(R.drawable.ic_notification_small)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setContentIntent(notificationClickPendingIntent(message))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

    private fun notificationClickPendingIntent(message: RemoteMessage): PendingIntent {
        val webUrl = message.webUrl()
        if (webUrl != null) {
            val intent = Intent(Intent.ACTION_VIEW, webUrl).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            return PendingIntent.getActivity(
                this,
                webUrl.toString().hashCode(),
                intent,
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
                appendLine("notification.title=${notification?.title}")
                appendLine("notification.body=${notification?.body}")
                appendLine("notification.channelId=${notification?.channelId}")
                appendLine("notification.clickAction=${notification?.clickAction}")
                appendLine("notification.color=${notification?.color}")
                appendLine("notification.icon=${notification?.icon}")
                appendLine("notification.imageUrl=${notification?.imageUrl}")
                appendLine("notification.link=${notification?.link}")
                appendLine("notification.sound=${notification?.sound}")
                appendLine("notification.tag=${notification?.tag}")
                appendLine("notification.ticker=${notification?.ticker}")
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

    private companion object {
        const val TAG = "OblivioFcmService"
        const val MAIN_ACTIVITY_REQUEST_CODE = 1001
    }
}
