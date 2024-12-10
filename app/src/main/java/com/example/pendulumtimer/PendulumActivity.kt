package com.example.pendulumtimer

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout


class PendulumActivity : AppCompatActivity() {

    lateinit var pendulumCircle1:ImageView
    lateinit var pendulumString1:View
    lateinit var pendulumCircle2:ImageView
    lateinit var pendulumString2:View
    lateinit var pendulumCircle3:ImageView
    lateinit var pendulumString3:View

    lateinit var containerLeft:ConstraintLayout
    lateinit var containerRight:ConstraintLayout
    lateinit var containerCenter:ConstraintLayout

    lateinit var startStopBtn:ImageView

    private lateinit var minTimerTV: TextView
    private lateinit var secTimerTV: TextView
    private lateinit var instrTimerTV: TextView

    var currentMin:Int=0
    var currentSec:Int=0
    lateinit var updateWhichTextView:TextView
    var isUpdateTimerLocked=false

    private var maxAngle = 0f

    var isTimerStopped=true
    var lastStoppedTime=0L

    lateinit var countDownTimer:CountDownTimer
    lateinit var gestureDetector:GestureDetector



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pendulum)

        pendulumCircle1=findViewById(R.id.pendulumCircle_1)
        pendulumString1=findViewById(R.id.pendulumString_1)

        pendulumCircle2=findViewById(R.id.pendulumCircle_2)
        pendulumString2=findViewById(R.id.pendulumString_2)

        pendulumCircle3=findViewById(R.id.pendulumCircle_3)
        pendulumString3=findViewById(R.id.pendulumString_3)
//        timerTextView=findViewById(R.id.timer_text)
        minTimerTV=findViewById(R.id.timer_min_text)
        secTimerTV=findViewById(R.id.timer_sec_text)
        instrTimerTV=findViewById(R.id.timer_instruction)


        gestureDetector=GestureDetector(this,
            object : GestureDetector.SimpleOnGestureListener(){
                override fun onScroll(
                    e1: MotionEvent?,
                    e2: MotionEvent,
                    distanceX: Float,
                    distanceY: Float
                ): Boolean {

                    if(distanceY>5){
                        isIncreamentValue(true)
                    }
                    else if(distanceY<5){
                        isIncreamentValue(false)
                    }
                    return true
                }
            })

        minTimerTV.setOnTouchListener { v, event -> updateWhichTextView= v as TextView;gestureDetector.onTouchEvent(event);return@setOnTouchListener true }
        secTimerTV.setOnTouchListener{ v, event -> updateWhichTextView= v as TextView;gestureDetector.onTouchEvent(event);return@setOnTouchListener true}

        containerLeft=findViewById(R.id.container_1)
        containerRight=findViewById(R.id.container_3)
        containerCenter=findViewById(R.id.container_2)

        startStopBtn=findViewById(R.id.start_stop_iv)

        startStopBtn.setOnClickListener({
            if(isTimerStopped){
                if((currentMin>0 || currentSec>0)) {
                    isTimerStopped = false
                    startCountDownTimer()
                }
                else
                    Toast.makeText(this,"Time isn't validate",Toast.LENGTH_SHORT).show()
            }
            else{
                startStopBtn.setImageDrawable(getDrawable(R.drawable.play_arrow_48))
                countDownTimer.cancel()
                isTimerStopped=true
                isStart=false
                isUpdateTimerLocked=false

            }
        })


//        startCountDownTimer()

    }


    fun isIncreamentValue(isIncreament:Boolean){
        if(!isUpdateTimerLocked) {
            if (isIncreament) {
                if (updateWhichTextView == minTimerTV) {
                    if (currentMin < 10) {
                        currentMin++
                    }
                } else if (currentSec < 60) currentSec++
            } else {
                if (updateWhichTextView == minTimerTV) {
                    if (currentMin > 0) {
                        currentMin--
                    }
                } else if (currentSec > 0) currentSec--
            }
            minTimerTV.text =
                if (currentMin < 10) "0" + currentMin.toString() else currentMin.toString()
            secTimerTV.text =
                if (currentSec < 10) "0" + currentSec.toString() else currentSec.toString()

            if (currentMin > 0 || currentSec > 0)
                instrTimerTV.visibility = View.GONE
            else
                instrTimerTV.visibility = View.VISIBLE
        }
    }


    lateinit var forwardLeftAnimator: ObjectAnimator
    fun startAnimationContainerLeft(angle: Float) {

        // Set pivots for both containers
        containerLeft.post {
            containerLeft.pivotX = containerLeft.width / 2f
            containerLeft.pivotY = 0f // Top edge
        }

        containerCenter.post {
            containerCenter.pivotX = containerCenter.width / 2f
            containerCenter.pivotY = 0f // Top edge
        }

        containerRight.post {
            // Set pivot to top center
            containerRight.setPivotX(containerRight.getWidth() / 2f)
            containerRight.setPivotY(0f) // Top edge
        }

        var startAngleForLeft=0f
        // First animation: containerLeft moves from 0 to -angle2
        forwardLeftAnimator = ObjectAnimator.ofFloat(containerLeft, "rotation", 0f, maxAngle, -(maxAngle/5))
        forwardLeftAnimator.duration = 2000

        // Second animation: containerCenter starts when containerLeft reaches 0 during its return
        val anim2 = ObjectAnimator.ofFloat(containerCenter, "rotation", 0f, -(maxAngle/5), 0f)
        anim2.duration = 1000

        // Second animation: containerCenter starts when containerLeft reaches 0 during its return
        val anim3 = ObjectAnimator.ofFloat(containerCenter, "rotation", 0f, (maxAngle/5), 0f)
        anim3.duration = 1000

        // Backward animation for containerLeft: From -angle2 to 0
        val backwardLeftAnimator: ObjectAnimator = ObjectAnimator.ofFloat(containerLeft, "rotation", -(maxAngle/5), 0f)
        backwardLeftAnimator.duration = 1000

        // First animation: From -angle to 0
        val forwardRightAnimator: ObjectAnimator = ObjectAnimator.ofFloat(containerRight, "rotation", 0f, -(maxAngle), (maxAngle/5)-1f)
        forwardRightAnimator.duration=2000


        // Reverse animation: From 0 to -angle
        val reverseRightAnimator: ObjectAnimator = ObjectAnimator.ofFloat(containerRight, "rotation", (maxAngle/5)-1f, 0f)
        reverseRightAnimator.setDuration(1000);


        //Right pendulum animation
        var lastRightValue=0f
        var isLastEnd=false
        forwardRightAnimator.addUpdateListener { animator->
            if(lastRightValue < (animator.animatedValue as Float)) {

                if ((animator.animatedValue as Float) > 0f) {
                    isLastEnd=true
                    Log.d("TAG", "startAnimationContainerLeft: start2")
                        anim3.start()
                        reverseRightAnimator.start()
                    forwardRightAnimator.removeAllUpdateListeners() // Avoid multiple triggers
                }
            }
            lastRightValue=animator.animatedValue as Float
        }


        var lastReverse=-1f

        var lastValue=0f
        var isFirstEnd=false

        Log.d("TAG", "startAnimationContainerLeft: start")
        anim3.addUpdateListener { animator->
            var c=animator.animatedValue as Float
            Log.d("TAG", "startAnimationContainerLeftCheck: current: "+ c+ " last: "+lastReverse)
            if(c>0.7f && isLastEnd){
                isLastEnd=false
                lastValue=0f
                Log.d("TAG", "startAnimationContainerLeft: startLEFT: start")
                startAngleForLeft=(maxAngle/5)
                if(!isTimerStopped)
                    startAnimationContainerLeft(maxAngle)
                Log.d("TAG", "startAnimationContainerLeft: backward started")
                anim3.removeAllUpdateListeners()
            }
            lastReverse=c
        }



        // Listener to start containerCenter animation when containerLeft reaches 0
        forwardLeftAnimator.addUpdateListener { animator ->
            Log.d("TAG", "startAnimationContainerLeft: 0: last: "+ lastValue +" animeValue: "+ animator.animatedValue as Float)
            if(lastValue> (animator.animatedValue as Float)) {

                if ((animator.animatedValue as Float) < 2f) {
                    isFirstEnd=true
                    startAngleForLeft=0f
                    Log.d("TAG", "startAnimationContainerLeft: start2")
                    anim2.start()
                    forwardLeftAnimator.removeAllUpdateListeners() // Avoid multiple triggers
                }
            }
            lastValue=animator.animatedValue as Float

        }

        var last=1f

        Log.d("TAG", "startAnimationContainerLeft: start")
        anim2.addUpdateListener { animator->

            var c=animator.animatedValue as Float
            Log.d("TAG", "startAnimationContainerLeft: current: "+ c+ " last: "+last)
            if(last < c && isFirstEnd){
                lastValue=0f
                isFirstEnd=false
                backwardLeftAnimator.start()
                if(!isTimerStopped)
                forwardRightAnimator.start()
                Log.d("TAG", "startAnimationContainerLeft: backward started")
                anim2.removeAllUpdateListeners()
            }
            last=c
        }
        Log.d("TAG", "startAnimationContainerLeft: end")

        // AnimatorSet to chain the animations
        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(forwardLeftAnimator)
        animatorSet.start()

    }



    var isStart=false

    private fun startCountDownTimer() {
        startStopBtn.setImageDrawable(getDrawable(R.drawable.pause_24))
        instrTimerTV.visibility=View.GONE
        isUpdateTimerLocked=true
        // Timer for 1 minute (60 seconds)
            lastStoppedTime= (currentMin*60*1000 + currentSec*1000).toLong()
        var totalDurationInSeconds= lastStoppedTime/1000
        countDownTimer = object : CountDownTimer(lastStoppedTime, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                lastStoppedTime=millisUntilFinished
                // Update the timer text
                val minutes= (millisUntilFinished/1000) /60
                val seconds= (millisUntilFinished/1000) %60

                currentMin= minutes.toInt()
                currentSec=seconds.toInt()


                val secondsRemaining = millisUntilFinished / 1000

                minTimerTV.text= String.format("%02d",minutes)
                secTimerTV.text= String.format("%02d",seconds)

                if(true) {
                    // Reduce the swing range of the pendulum every second
                    maxAngle =
                        (15f * secondsRemaining / totalDurationInSeconds) // Decrease angle proportionally
                    if(maxAngle<5)
                        maxAngle=5.1f
                    Log.d("TAG", "onTick: maxAngle: "+ maxAngle)

                    if(!isStart) {
                        startAnimationContainerLeft(maxAngle)
                        isStart=true
                    }

                }

            }

            override fun onFinish() {
                forwardLeftAnimator.cancel()
                maxAngle=0f
                currentMin=0
                currentSec=0
                minTimerTV.text = "00"
                secTimerTV.text="00"
                isUpdateTimerLocked=false
                instrTimerTV.visibility=View.VISIBLE
                startStopBtn.setImageDrawable(getDrawable(R.drawable.play_arrow_48))
            }
        }
        countDownTimer.start()
    }



}