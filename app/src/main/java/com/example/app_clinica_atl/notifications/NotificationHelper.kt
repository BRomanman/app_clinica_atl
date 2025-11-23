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
    private const val INSURANCE_NOTIFICATION_ID = 1002
    private const val DOCTOR_CREATED_NOTIFICATION_ID = 1003

    const val APPOINTMENT_CHANNEL_ID = "appointment_confirmations"
    const val INSURANCE_CHANNEL_ID = "insurance_confirmations"
    const val DOCTOR_CHANNEL_ID = "admin_doctor_events"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val appointmentChannel = NotificationChannel(
                APPOINTMENT_CHANNEL_ID,
                context.getString(R.string.book_appointment_notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.book_appointment_notification_channel_description)
            }
            val insuranceChannel = NotificationChannel(
                INSURANCE_CHANNEL_ID,
                context.getString(R.string.insurance_notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.insurance_notification_channel_description)
            }
            val doctorChannel = NotificationChannel(
                DOCTOR_CHANNEL_ID,
                context.getString(R.string.admin_doctor_notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.admin_doctor_notification_channel_description)
            }

            val manager = context.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannels(listOf(appointmentChannel, insuranceChannel, doctorChannel))
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

        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

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

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showInsuranceConfirmation(
        context: Context,
        policyHolderName: String
    ) {
        val title = context.getString(R.string.insurance_notification_title)
        val message = context.getString(R.string.insurance_notification_body, policyHolderName)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val pendingIntent = PendingIntent.getActivity(context, 1, intent, flags)

        val notification = NotificationCompat.Builder(context, INSURANCE_CHANNEL_ID)
            .setSmallIcon(R.drawable.logo_clean)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(INSURANCE_NOTIFICATION_ID, notification)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showDoctorCreated(context: Context, doctorName: String) {
        val title = context.getString(R.string.admin_doctor_notification_title)
        val message = context.getString(R.string.admin_doctor_notification_body, doctorName)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val pendingIntent = PendingIntent.getActivity(context, 2, intent, flags)

        val notification = NotificationCompat.Builder(context, DOCTOR_CHANNEL_ID)
            .setSmallIcon(R.drawable.logo_clean)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(DOCTOR_CREATED_NOTIFICATION_ID, notification)
    }
}
