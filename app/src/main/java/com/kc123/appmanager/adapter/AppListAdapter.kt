package com.kc123.appmanager.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.kc123.appmanager.R
import com.kc123.appmanager.model.InstalledApp

class AppListAdapter(
    private var fullList: List<InstalledApp>,
    private val onClick: (InstalledApp) -> Unit
) : RecyclerView.Adapter<AppListAdapter.ViewHolder>() {

    private var filteredList = fullList.toMutableList()

    fun filterList(query: String) {
        filteredList = if (query.isEmpty()) {
            fullList.toMutableList()
        } else {
            fullList.filter {
                it.name.contains(query, ignoreCase = true)
            }.toMutableList()
        }
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.appIcon)
        val name: TextView = itemView.findViewById(R.id.appName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_installed_app, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = filteredList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = filteredList[position]
        holder.name.text = app.name
        holder.icon.setImageDrawable(app.icon)
        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putString("appName", app.name)
                putString("packageName", app.packageName)
            }
            it.findNavController().navigate(R.id.appPatternFragment, bundle)
        }
    }
}

