package com.darklandsmobile.ui.region

import android.content.Context
import android.content.Intent

object GrimRegionNavigation {
    private const val EXTRA_REGION = "grim_region"

    fun openRegion(context: Context, regionName: String) {
        val intent = Intent(context, RegionActivity::class.java)
        intent.putExtra(EXTRA_REGION, regionName)
        context.startActivity(intent)
    }

    fun extractRegion(intent: Intent?): String = intent?.getStringExtra(EXTRA_REGION) ?: "Schwarzwald"
}
