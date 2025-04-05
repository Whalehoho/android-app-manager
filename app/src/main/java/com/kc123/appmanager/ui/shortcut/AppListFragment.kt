package com.kc123.appmanager.ui.shortcut

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kc123.appmanager.R
import com.kc123.appmanager.adapter.AppListAdapter
import com.kc123.appmanager.model.InstalledApp

class AppListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_app_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.appRecyclerView)
        val searchView = view.findViewById<SearchView>(R.id.searchView)

        val apps = getInstalledApps()
        val adapter = AppListAdapter(apps) { selectedApp ->
            // Handle app click
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false // we handle real-time filtering instead
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filterList(newText.orEmpty())
                return true
            }
        })
    }


    private fun getInstalledApps(): List<InstalledApp> {
        val pm = requireContext().packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            // Only fetch launchable apps.
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolvedApps = pm.queryIntentActivities(intent, 0)

        return resolvedApps.map {
            val appInfo = it.activityInfo
            InstalledApp(
                name = appInfo.loadLabel(pm).toString(),
                packageName = appInfo.packageName,
                icon = appInfo.loadIcon(pm)
            )
        }.sortedBy { it.name } // optional: alphabetically
    }


}
