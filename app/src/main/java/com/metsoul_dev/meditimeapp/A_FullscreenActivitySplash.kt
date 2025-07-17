package com.metsoul_dev.meditimeapp

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsets
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.metsoul_dev.meditimeapp.databinding.ActivityFullscreenSplashBinding


@Suppress("DEPRECATION")
class A_FullscreenActivitySplash : AppCompatActivity() {

    private lateinit var binding: ActivityFullscreenSplashBinding
    private lateinit var fullscreenContent: TextView
    private lateinit var fullscreenContentControls: LinearLayout


    private var isFullscreen: Boolean = false

    private lateinit var mediaPlayer: MediaPlayer

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityFullscreenSplashBinding.inflate(layoutInflater)
        setContentView(binding.root)



        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        isFullscreen = true
        fullscreenContent = binding.fullscreenContent
        fullscreenContentControls = binding.fullscreenContentControls


        supportActionBar?.hide()
        fullscreenContentControls.visibility = View.GONE
        isFullscreen = false
        if (Build.VERSION.SDK_INT >= 30) {
            fullscreenContent.windowInsetsController?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            fullscreenContent.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LOW_PROFILE or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }

        // Lanza MainActivity después de 4 segundos (en lugar de 5, para mayor precisión)
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, B_Login::class.java)
            startActivity(intent)
            finish() // Finaliza la actividad de Splash
        }, 4000) //4000


    }

    override fun onDestroy() {
        super.onDestroy()

    }

}