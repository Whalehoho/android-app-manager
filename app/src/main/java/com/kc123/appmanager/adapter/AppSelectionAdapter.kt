package com.kc123.appmanager.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kc123.appmanager.R
import com.kc123.appmanager.model.AppInfo
import com.kc123.appmanager.model.InstalledApp

// This adapter is used by feature#2: Bubble Shortcut
class AppSelectionAdapter(
    private var appList: List<AppInfo>,
    private val onAppSelected: (AppInfo) -> Unit
) : RecyclerView.Adapter<AppSelectionAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.appIcon)
        val name: TextView = itemView.findViewById(R.id.appName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_installed_app, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = appList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = appList[position]
        holder.name.text = app.appName
        holder.icon.setImageDrawable(app.icon)
        holder.itemView.setOnClickListener {
            onAppSelected(app)
        }
    }

    fun updateList(newList: List<AppInfo>) {
        appList = newList
        notifyDataSetChanged()
    }

}
