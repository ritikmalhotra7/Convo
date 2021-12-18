package com.complete.convo.activities

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.complete.convo.R
import com.complete.convo.actvities.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

const val channelId = "notification_channel"
const val channelName = "com.complete.convo.activities"
class MyFirebaseMessagingService(public val title: String, public var message:String) :FirebaseMessagingService(){
    init {
        message = "you have a message from $title"
        generate(title,message)
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        if(p0.notification != null){
            generate(p0.notification!!.title!!,p0.notification!!.body!!)
        }
    }

    fun generate(title :String,message : String ){
        val intent = Intent(this,MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT)
        var builder : NotificationCompat.Builder = NotificationCompat.Builder(applicationContext,
            channelId).apply {
                setSmallIcon(R.drawable.undraw_ideas_s70l)
                setAutoCancel(true)
                setVibrate(longArrayOf(200,100,200,100,200))
                setOnlyAlertOnce(true)
                setContentIntent(pendingIntent)
        }
        builder = builder.setContent(getRemoteView(title,message))

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(channelId, channelName,NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        notificationManager.notify(0,builder.build())


    }

    @SuppressLint("RemoteViewLayout")
    private fun getRemoteView(title: String, message: String): RemoteViews? {
        val remoteView = RemoteViews(channelName,R.layout.notification).apply{
            setTextViewText(R.id.title,title)
            setTextViewText(R.id.message,message)
            setImageViewResource(R.id.logo,R.drawable.undraw_ideas_s70l)
        }
        return remoteView
    }


}