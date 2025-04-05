package com.kc123.appmanager.util

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import com.kc123.appmanager.model.AppInfo

object AppUtils {

    fun getInstalledApps(context: Context): List<AppInfo> {
        val pm = context.packageManager
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)

        return packages.mapNotNull {
            try {
                val name = pm.getApplicationLabel(it).toString()
                val icon = pm.getApplicationIcon(it)
                AppInfo(name, it.packageName, icon)
            } catch (e: Exception) {
                null
            }
        }.sortedBy { it.appName }
    }

    fun getAppIcon(context: Context, packageName: String): Drawable? {
        return try {
            context.packageManager.getApplicationIcon(packageName)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

}
