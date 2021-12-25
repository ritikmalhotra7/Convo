package com.complete.convo.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.complete.convo.R
import com.complete.convo.activities.LoginActivity
import com.complete.convo.actvities.MainActivity
import com.wang.avi.AVLoadingIndicatorView

@Suppress("DEPRECATION")
class SplashScreen : AppCompatActivity() {
    private lateinit var avi :AVLoadingIndicatorView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        avi = AVLoadingIndicatorView(this)

        // This is used to hide the status bar and make
        // the splash screen as a full screen activity.
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        startAnim()

        // we used the postDelayed(Runnable, time) method
        // to send a message with a delayed time.
        Handler().postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            stopAnim()
            finish()
        }, 3000) // 3000 is the delayed time in milliseconds.
    }
    fun startAnim() {
        avi.smoothToShow()
        // or avi.smoothToShow();
    }

    fun stopAnim() {
        avi.smoothToHide()
        // or avi.smoothToHide();
    }
}
