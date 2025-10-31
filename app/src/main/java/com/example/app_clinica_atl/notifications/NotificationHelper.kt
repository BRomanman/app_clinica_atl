package com.example.app_clinica_atl.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.app_clinica_atl.MainActivity
import com.example.app_clinica_atl.R
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object NotificationHelper {

    private const val APPOINTMENT_NOTIFICATION_ID = 1001
    const val APPOINTMENT_CHANNEL_ID = "appointment_confirmations"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = context.getString(R.string.book_appointment_notification_channel_name)
            val channelDescription = context.getString(R.string.book_appointment_notification_channel_description)
            val channel = NotificationChannel(
                APPOINTMENT_CHANNEL_ID,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = channelDescription
            }

            val manager = context.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    @RequiresApi(Build.VERSION_CODES.O)
    fun showAppointmentConfirmation(
        context: Context,
        doctorName: String,
        date: LocalDate,
        time: LocalTime
    ) {
        val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())

        val title = context.getString(R.string.book_appointment_notification_title)
        val message = context.getString(
            R.string.book_appointment_notification_body,
            doctorName,
            date.format(dateFormatter),
            time.format(timeFormatter)
        )

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val flags = PendingIntent.FLAG_UPDATE_CURRENT or
                PendingIntent.FLAG_IMMUTABLE

        val pendingIntent = PendingIntent.getActivity(context, 0, intent, flags)

        val notification = NotificationCompat.Builder(context, APPOINTMENT_CHANNEL_ID)
            .setSmallIcon(R.drawable.logo_clean)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(APPOINTMENT_NOTIFICATION_ID, notification)
    }
}
