package com.darklandsmobile.ui.main

import android.content.Context
import com.darklandsmobile.ui.expedition.OtherSideNavigation
import com.darklandsmobile.ui.region.GrimRegionNavigation

object GrimMapActions {
    fun openRegion(context: Context, regionName: String) {
        GrimRegionNavigation.openRegion(context, regionName)
    }

    fun openOtherSide(context: Context) {
        OtherSideNavigation.open(context)
    }
}
