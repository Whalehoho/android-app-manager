package com.kc123.appmanager.service
import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewOutlineProvider
import android.view.WindowManager
import android.widget.ImageView
import com.kc123.appmanager.R
import com.kc123.appmanager.ui.shortcut.PatternOverlayActivity

// A foreground Service that creates a draggable floating button, similar to Facebook Messenger chat bubbles.
class FloatingService : Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: View
    private lateinit var params: WindowManager.LayoutParams
    private lateinit var binView: View
    private lateinit var patternTarget: View
    private lateinit var bubbleTarget: View



    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate() {
        super.onCreate()
        Log.d("FloatingService", "Service started")

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        floatingView = inflater.inflate(R.layout.floating_button, null)

        patternTarget = inflater.inflate(R.layout.floating_target_pattern, null)
        bubbleTarget = inflater.inflate(R.layout.floating_target_bubble, null)

        // Clip image using clipToOutline
        floatingView.outlineProvider = ViewOutlineProvider.BACKGROUND
        floatingView.clipToOutline = true



        val targetParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        patternTarget.visibility = View.GONE
        bubbleTarget.visibility = View.GONE

        targetParams.gravity = Gravity.TOP or Gravity.START
        targetParams.x = 50
        targetParams.y = 50
        windowManager.addView(patternTarget, targetParams)

        val bubbleParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        bubbleParams.gravity = Gravity.TOP or Gravity.START
        bubbleParams.x = 50
        bubbleParams.y = 50 + 300

        bubbleTarget.visibility = View.GONE
        windowManager.addView(bubbleTarget, bubbleParams)




        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.START
        params.x = 100
        params.y = 300

        windowManager.addView(floatingView, params)


        // Reference to the icon inside the floating layout
        val iconView = floatingView.findViewById<ImageView>(R.id.floating_icon)
        iconView.alpha = 0.7f

        // Enable drag behavior
        iconView.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f
            private var isDragging = false
            private val CLICK_THRESHOLD = 10

            override fun onTouch(view: View, event: MotionEvent): Boolean {
                when (event.action) {
                    // Capture initial position
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        isDragging = false
                        binView.visibility = View.VISIBLE
                        patternTarget.visibility = View.VISIBLE
                        bubbleTarget.visibility = View.VISIBLE
                        iconView.alpha = 1.0f
                        return true
                    }

                    // Move the button across the screen
                    MotionEvent.ACTION_MOVE -> {
                        val floatPos = IntArray(2)
                        val patternPos = IntArray(2)
                        val bubblePos = IntArray(2)
                        floatingView.getLocationOnScreen(floatPos)
                        patternTarget.getLocationOnScreen(patternPos)
                        bubbleTarget.getLocationOnScreen(bubblePos)

                        val fx = floatPos[0]
                        val fy = floatPos[1]
                        val patternHit = fx in patternPos[0]..(patternPos[0] + patternTarget.width) &&
                                fy in patternPos[1]..(patternPos[1] + patternTarget.height)
                        val bubbleHit = fx in bubblePos [0]..(bubblePos[0] + bubbleTarget.width) &&
                                fy in bubblePos[1]..(bubblePos [1] + bubbleTarget.height)

                        patternTarget.alpha = if (patternHit) 1.0f else 0.75f
                        bubbleTarget.alpha = if (bubbleHit) 1.0f else 0.75f

                        val dx = event.rawX - initialTouchX
                        val dy = event.rawY - initialTouchY
                        if (dx * dx + dy * dy > CLICK_THRESHOLD * CLICK_THRESHOLD) {
                            isDragging = true
                        }

                        params.x = initialX + dx.toInt()
                        params.y = initialY + dy.toInt()
                        windowManager.updateViewLayout(floatingView, params)

                        // handle bin highlighting
                        checkOverlapWithBin()

                        return true
                    }

                    // Evaluate drop behavior
                    MotionEvent.ACTION_UP -> {
                        binView.visibility = View.GONE
                        patternTarget.visibility = View.GONE
                        bubbleTarget.visibility = View.GONE
                        checkDropToTarget()


                        return if (isDragging) {
                            // Drop action
                            checkDropToBin()
                            iconView.alpha = 0.7f
                            true
                        } else {
                            // Tap action
//                            showPatternDialog()
//                            showOverlaySwitcher()
                            iconView.alpha = 0.7f
                            true
                        }
                    }
                }
                return false
            }
        })

        binView = inflater.inflate(R.layout.floating_bin, null)


        val binParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        binParams.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        binParams.y = 100
        binView.visibility = View.GONE
        windowManager.addView(binView, binParams)

    }

    private fun checkDropToBin() {
        val floatingLocation = IntArray(2)
        val binLocation = IntArray(2)
        floatingView.getLocationOnScreen(floatingLocation)
        binView.getLocationOnScreen(binLocation)

        val fx = floatingLocation[0]
        val fy = floatingLocation[1]
        val bx = binLocation[0]
        val by = binLocation[1]

        val isOverBin = fx in bx..(bx + binView.width) &&
                fy in by..(by + binView.height)

        if (isOverBin) stopSelf()
    }

    private fun checkOverlapWithBin() {
        val floatingLocation = IntArray(2)
        val binLocation = IntArray(2)
        floatingView.getLocationOnScreen(floatingLocation)
        binView.getLocationOnScreen(binLocation)

        val fx = floatingLocation[0]
        val fy = floatingLocation[1]
        val bx = binLocation[0]
        val by = binLocation[1]

        val isOverBin = fx in bx..(bx + binView.width) &&
                fy in by..(by + binView.height)

        binView.alpha = if (isOverBin) 1.0f else 0.75f
    }



    private fun showPatternDialog() {
        val intent = Intent(this, PatternOverlayActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }



//    private fun showBubbleDialog() {
//        val intent = Intent(this, BubbleOverlayActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        startActivity(intent)
//    }


    override fun onDestroy() {
        super.onDestroy()
        windowManager.removeView(floatingView)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun checkDropToTarget() {
        val floatPos = IntArray(2)
        floatingView.getLocationOnScreen(floatPos)

        val patternPos = IntArray(2)
        patternTarget.getLocationOnScreen(patternPos)

        val bubblePos = IntArray(2)
        bubbleTarget.getLocationOnScreen(bubblePos)

        val fx = floatPos[0]
        val fy = floatPos[1]

        val patternHit = fx in patternPos[0]..(patternPos[0] + patternTarget.width) &&
                fy in patternPos[1]..(patternPos[1] + patternTarget.height)

        val bubbleHit = fx in bubblePos[0]..(bubblePos[0] + bubbleTarget.width) &&
                fy in bubblePos[1]..(bubblePos[1] + bubbleTarget.height)


        when {
            patternHit -> showPatternDialog()
//            bubbleHit -> showBubbleDialog()
        }

    }

}
