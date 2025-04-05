package com.kc123.appmanager.ui.shortcut

import android.app.Activity
import android.os.Bundle
import android.view.WindowManager
import com.kc123.appmanager.R
import com.kc123.appmanager.custom.PatternGridView
import com.kc123.appmanager.util.PatternStorage
import android.widget.Toast

class PatternOverlayActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Optional: make the background transparent
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        )

        setContentView(R.layout.activity_pattern_overlay)

        val patternView = findViewById<PatternGridView>(R.id.patternOverlayGrid)

        patternView.onPatternDrawn = { drawnPattern ->
            val context = this
            val allPatterns = PatternStorage.getAllPatterns(context)

            val match = allPatterns.entries.firstOrNull { it.value == drawnPattern }

            if (match != null) {
                // Pattern matched, launch the app
                val launchIntent = packageManager.getLaunchIntentForPackage(match.key)
                if (launchIntent != null) {
                    startActivity(launchIntent)
                } else {
                    Toast.makeText(context, "Unable to launch app", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "No matching pattern found", Toast.LENGTH_SHORT).show()
            }

            // Close overlay
            finish()
        }
    }

}
