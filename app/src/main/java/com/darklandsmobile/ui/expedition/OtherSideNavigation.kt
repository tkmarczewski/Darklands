package com.darklandsmobile.ui.expedition

import android.content.Context
import android.content.Intent

object OtherSideNavigation {
    fun open(context: Context) {
        context.startActivity(Intent(context, OtherSideActivity::class.java))
    }
}
