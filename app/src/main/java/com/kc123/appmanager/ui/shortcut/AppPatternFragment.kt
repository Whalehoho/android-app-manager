package com.kc123.appmanager.ui.shortcut

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.kc123.appmanager.R
import com.kc123.appmanager.custom.PatternGridView
import com.kc123.appmanager.util.PatternStorage

class AppPatternFragment : Fragment() {
    private var isEditing = false
    private var currentPattern: List<Pair<Int, Int>>? = emptyList()
    private lateinit var patternView: PatternGridView
    private lateinit var updateButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_app_pattern, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appNameText = view.findViewById<TextView>(R.id.appName)
        val appIconView = view.findViewById<ImageView>(R.id.appIcon)

        val appName = arguments?.getString("appName")
        val packageName = arguments?.getString("packageName")

        appNameText.text = appName ?: "Unknown App"

        if (packageName != null) {
            try {
                val pm = requireContext().packageManager
                val icon = pm.getApplicationIcon(packageName)
                appIconView.setImageDrawable(icon)
            } catch (e: Exception) {
                appIconView.setImageResource(R.drawable.ic_default_app) // fallback
            }
        }

        patternView = view.findViewById(R.id.patternView)
        updateButton = view.findViewById(R.id.updateButton)

        // Load previously saved pattern
        packageName?.let {
            val savedPattern = PatternStorage.loadPattern(requireContext(), it)
            if (savedPattern.isNotEmpty()) {
                patternView.setPattern(savedPattern)
                currentPattern = savedPattern
            }
        }


        // Initially, disable drawing
        patternView.isEnabled = false

        // Listen for pattern draw
        patternView.onPatternDrawn = { pattern ->
            currentPattern = pattern
        }

        updateButton.setOnClickListener {
            isEditing = !isEditing

            if (isEditing) {
                // Start editing
                updateButton.text = "Save Pattern"
                patternView.isEnabled = true
                patternView.reset()
            } else {
                updateButton.text = "Update Pattern"
                patternView.isEnabled = false

                // Save in shared preferences
                val pattern = currentPattern
                if (pattern != null) {
                    packageName?.let {
                        PatternStorage.savePattern(requireContext(), it, pattern)
                    }
                }
            }
        }

    }

}
