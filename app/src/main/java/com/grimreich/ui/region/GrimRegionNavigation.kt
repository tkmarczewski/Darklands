package com.grimreich.ui.region

import android.content.Context
import android.content.Intent

object GrimRegionNavigation {
    private const val EXTRA_REGION = "grim_region_name"

    fun createIntent(context: Context, regionName: String): Intent =
        Intent(context, RegionActivity::class.java).putExtra(EXTRA_REGION, regionName)

    fun extractRegion(intent: Intent): String =
        intent.getStringExtra(EXTRA_REGION) ?: "Wybrzeże Północne"
}
