package com.darklandsmobile.ui.main

import android.content.Context
import android.content.Intent
import com.darklandsmobile.ui.expedition.OtherSideActivity
import com.darklandsmobile.ui.region.RegionActivity
import com.darklandsmobile.ui.region.GrimRegionNavigation

object GrimMapActions {

    fun openRegion(context: Context, regionName: String) {
        val intent = GrimRegionNavigation.createIntent(context, regionName)
        context.startActivity(intent)
    }

    fun openOtherSide(context: Context) {
        val intent = Intent(context, OtherSideActivity::class.java)
        context.startActivity(intent)
    }
}
