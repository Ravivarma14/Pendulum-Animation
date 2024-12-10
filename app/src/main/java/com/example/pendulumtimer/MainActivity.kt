package com.example.pendulumtimer

import android.animation.ValueAnimator
import android.graphics.Interpolator
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    lateinit var timerTv:TextView
    lateinit var ballIv:ImageView
    private var pendulumAnimater: ValueAnimator? = null
    var totalDuration:Int=60

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        timerTv=findViewById(R.id.timer_text)
        ballIv=findViewById(R.id.pendulum)

        ballIv.post {
            ballIv.pivotX = ballIv.width / 2f
            ballIv.pivotY = 0f
        }

        startTimer(totalDuration)
    }

    fun startTimer(seconds:Int){

        object : CountDownTimer(seconds*1000L, 1000){
            override fun onTick(millisUntilFinished: Long) {
                val minutes= (millisUntilFinished/1000) /60
                val seconds= (millisUntilFinished/1000) %60

                timerTv.text= String.format("%02d:%02d",minutes,seconds)

                val remainTimeFraction= (millisUntilFinished)/(totalDuration *1000f)
                updatePendulumAnimation(remainTimeFraction)
            }

            override fun onFinish() {
                timerTv.text="00:00"
                pendulumAnimater?.cancel()
            }

        }.start()
    }

    fun updatePendulumAnimation(remainTime: Float) {
        Log.d("Pendulum", "updatePendulumAnimation: remainTime: $remainTime")
        val maxAmplitude = 60f
        val currentAmplitude = maxAmplitude * remainTime // Calculate amplitude dynamically

        Log.d("Pendulum", "Current Amplitude: $currentAmplitude")

        if (pendulumAnimater == null) {
            // Create and start the ValueAnimator only once
            pendulumAnimater = ValueAnimator.ofFloat(-currentAmplitude, currentAmplitude).apply {
                duration = 1000 // Duration for one swing
                interpolator = LinearInterpolator() // Smooth motion
                repeatMode = ValueAnimator.REVERSE // Oscillate back and forth
                repeatCount = ValueAnimator.INFINITE // Keep swinging

                addUpdateListener {
                    val rotation = it.animatedValue as Float
                    ballIv.rotation = rotation
                }
                start()
            }
        } else {
            // Dynamically update the amplitude range without restarting the animation
            pendulumAnimater?.setFloatValues(-currentAmplitude, currentAmplitude)
        }
    }

}