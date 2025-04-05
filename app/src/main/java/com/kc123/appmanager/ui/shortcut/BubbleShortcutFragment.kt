package com.kc123.appmanager.ui.shortcut

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kc123.appmanager.R


class BubbleShortcutFragment : Fragment() {

    private val PREFS_NAME = "BubbleShortcuts"
    private val KEY_PREFIX = "bubble_slot_"


    private val prefs by lazy {
        requireContext().getSharedPreferences("bubble_shortcuts", Context.MODE_PRIVATE)
    }

    private lateinit var packageManager: PackageManager

    private var selectedSlotIndex = -1

    private val appPickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        view?.findViewById<ProgressBar>(R.id.loadingSpinner)?.visibility = View.GONE

        if (result.resultCode == Activity.RESULT_OK) {
            val packageName = result.data?.getStringExtra("packageName") ?: return@registerForActivityResult
            val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString("$KEY_PREFIX$selectedSlotIndex", packageName).apply()
            refreshSlots()
        }
    }

    private fun refreshSlots() {
        val grid = requireView().findViewById<GridLayout>(R.id.bubbleGrid)
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        for (i in 0 until grid.childCount) {
            val slot = grid.getChildAt(i)
            val icon = slot.findViewById<ImageView>(R.id.slotIcon)
            val close = slot.findViewById<ImageView>(R.id.removeIcon)

            val savedPackage = prefs.getString("$KEY_PREFIX$i", null)

            if (savedPackage != null) {
                try {
                    val appIcon = requireContext().packageManager.getApplicationIcon(savedPackage)
                    icon.setImageDrawable(appIcon)
                    close?.visibility = View.VISIBLE
                } catch (e: Exception) {
                    icon.setImageResource(R.drawable.ic_add)
                    close?.visibility = View.GONE
                }
            } else {
                icon.setImageResource(R.drawable.ic_add)
                close?.visibility = View.GONE
            }
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_bubble_shortcut, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val grid = view.findViewById<GridLayout>(R.id.bubbleGrid)
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val loadingSpinner = view.findViewById<ProgressBar>(R.id.loadingSpinner)

        // 1️⃣ Inflate item_slot.xml 9 times dynamically
        repeat(9) { i ->
            val slot = layoutInflater.inflate(R.layout.item_bubble_slot, grid, false)
            val icon = slot.findViewById<ImageView>(R.id.slotIcon)
            val close = slot.findViewById<ImageView>(R.id.removeIcon)

            // Load assigned app icon if exists
            val savedPackage = prefs.getString("$KEY_PREFIX$i", null)
            if (savedPackage != null) {
                try {
                    val appIcon = requireContext().packageManager.getApplicationIcon(savedPackage)
                    icon.setImageDrawable(appIcon)
                    close?.visibility = View.VISIBLE
                } catch (e: Exception) {
                    prefs.edit().remove("$KEY_PREFIX$i").apply()
                }
            }

            // ✕ Remove icon
            close?.setOnClickListener {
                icon.setImageResource(R.drawable.ic_add)
                close.visibility = View.GONE
                prefs.edit().remove("$KEY_PREFIX$i").apply()
            }

            // ✚ Tap slot to assign app
            slot.setOnClickListener {
                val isEmpty = prefs.getString("$KEY_PREFIX$i", null) == null
                if (isEmpty) {
                    selectedSlotIndex = i
                    loadingSpinner.visibility = View.VISIBLE

                    Handler(Looper.getMainLooper()).postDelayed({
                        val intent = Intent(requireContext(), AppListActivity::class.java)
                        appPickerLauncher.launch(intent)
                        // Spinner dismissed after user returns
                    }, 250)
                } else {
                    Toast.makeText(requireContext(), "App already assigned", Toast.LENGTH_SHORT).show()
                }
            }

            grid.addView(slot)
        }
    }


}
