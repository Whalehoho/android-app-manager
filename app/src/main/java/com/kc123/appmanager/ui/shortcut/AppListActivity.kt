package com.kc123.appmanager.ui.shortcut

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kc123.appmanager.R
import com.kc123.appmanager.adapter.AppSelectionAdapter
import com.kc123.appmanager.model.AppInfo

class AppListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var adapter: AppSelectionAdapter
    private var appList = listOf<AppInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_list)

        searchView = findViewById(R.id.searchView)
        recyclerView = findViewById(R.id.appRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        appList = getLaunchableApps()
        adapter = AppSelectionAdapter(appList) { appInfo ->
            val resultIntent = Intent().apply {
                putExtra("packageName", appInfo.packageName)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }
        recyclerView.adapter = adapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                val filtered = appList.filter {
                    it.appName.contains(newText.orEmpty(), ignoreCase = true)
                }
                adapter.updateList(filtered)
                return true
            }
        })
    }

    private fun getLaunchableApps(): List<AppInfo> {
        val pm = packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolvedApps = pm.queryIntentActivities(intent, 0)

        return resolvedApps.map {
            val appInfo = it.activityInfo
            AppInfo(
                appName = appInfo.loadLabel(pm).toString(),
                packageName = appInfo.packageName,
                icon = appInfo.loadIcon(pm)
            )
        }.sortedBy { it.appName }
    }
}
