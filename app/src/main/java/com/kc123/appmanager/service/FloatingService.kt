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


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate() {
        super.onCreate()
        Log.d("FloatingService", "Service started")

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        floatingView = inflater.inflate(R.layout.floating_button, null)

        // Clip image using clipToOutline
        floatingView.outlineProvider = ViewOutlineProvider.BACKGROUND
        floatingView.clipToOutline = true


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

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
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
                        iconView.alpha = 1.0f
                        return true
                    }

                    // Move the button across the screen
                    MotionEvent.ACTION_MOVE -> {
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

                        return if (isDragging) {
                            // Drop action
                            checkDropToBin()
                            iconView.alpha = 0.7f
                            true
                        } else {
                            // Tap action
                            showPatternDialog()
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

    override fun onDestroy() {
        super.onDestroy()
        windowManager.removeView(floatingView)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
