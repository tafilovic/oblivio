package eu.brrm.oblivio.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import eu.brrm.oblivio.MainActivity
import eu.brrm.oblivio.R

class OblivioFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title
            ?: message.data["title"]
            ?: getString(R.string.notifications_fallback_title)
        val body = message.notification?.body
            ?: message.data["body"]
            ?: message.data["message"]
            ?: return

        ensureGeneralNotificationChannel()
        NotificationManagerCompat.from(this).notify(
            message.messageId?.hashCode() ?: System.currentTimeMillis().toInt(),
            buildNotification(title = title, body = body),
        )
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Subscription is intentionally refreshed after login; backend handles deduplication.
    }

    private fun buildNotification(title: String, body: String) =
        NotificationCompat.Builder(this, GENERAL_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setContentIntent(mainActivityPendingIntent())
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

    private fun mainActivityPendingIntent(): PendingIntent {
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

    private fun ensureGeneralNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            GENERAL_CHANNEL_ID,
            getString(R.string.notifications_channel_general_name),
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = getString(R.string.notifications_channel_general_description)
        }
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    private companion object {
        const val GENERAL_CHANNEL_ID = "oblivio_general_notifications"
        const val MAIN_ACTIVITY_REQUEST_CODE = 1001
    }
}
