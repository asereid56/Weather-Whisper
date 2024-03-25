package com.aser.weatherwhisper.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.aser.weatherwhisper.MainActivity
import com.aser.weatherwhisper.R
import com.aser.weatherwhisper.db.CitiesLocalDataSource
import com.aser.weatherwhisper.model.City
import com.aser.weatherwhisper.model.WeatherRepository
import com.aser.weatherwhisper.model.WeatherResponse
import com.aser.weatherwhisper.network.RetrofitHelper
import com.aser.weatherwhisper.network.WeatherRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AlertWorker(private val context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    companion object {
        const val KEY_LATITUDE = "latitude"
        const val KEY_LONGITUDE = "longitude"
        const val KEY_UNIT = "unit"
        const val KEY_LANG = "language"
        const val KEY_DATE_TIME_MILLIS = "dateTimeMillis"
        const val CHANNEL_ID = "YourChannelId"
        const val NOTIFICATION_ID = 123
        const val KEY_CITY_NAME = "cityName"
    }

    override suspend fun doWork(): Result {
        try {
            val latitude = inputData.getDouble(KEY_LATITUDE, 0.0)
            val longitude = inputData.getDouble(KEY_LONGITUDE, 0.0)
            val unit = inputData.getString(KEY_UNIT)
            val lang = inputData.getString(KEY_LANG)
            val dateTimeMillis = inputData.getLong(KEY_DATE_TIME_MILLIS, 0L)
            val cityName = inputData.getString(KEY_CITY_NAME)
            val city = City(cityName!!, longitude, latitude, "Alert")


            val weatherResponse = getWeatherDetails(latitude, longitude, unit, lang)

            weatherResponse?.let {
                showNotification(
                    context.getString(R.string.weatherDisc) + " " + cityName + " " + context.getString(
                        R.string.completeWeatherDis
                    ) + it.current.weather[0].description
                )
                deleteCityFromRoom(city)
            }

            return Result.success()

        } catch (e: Exception) {

            return Result.failure()
        }
    }

    private suspend fun deleteCityFromRoom(city: City) {
        val repo = WeatherRepository.getInstance(
            WeatherRemoteDataSource.instance,
            CitiesLocalDataSource.getInstance(context)
        )
        repo.deleteCityFromAlert(city)
    }


    private suspend fun getWeatherDetails(
        latitude: Double,
        longitude: Double,
        unit: String?,
        lang: String?
    ): WeatherResponse? {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitHelper.service.getWeatherDetails(
                    latitude,
                    longitude,
                    lang ?: "",
                    unit ?: ""
                )
                if (response.isSuccessful) {
                    response.body()
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun showNotification(message: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Make alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_MUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Weather Alert")
            .setContentText(message)
            .setSmallIcon(R.drawable.cloud_alert)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
