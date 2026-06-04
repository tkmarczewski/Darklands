package com.darklandsmobile.ui.region

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.darklandsmobile.core.GrimGameRepository
import com.darklandsmobile.core.GrimSeed
import com.darklandsmobile.grimreich.v1.RegionSnapshot

class GrimRegionViewModel : ViewModel() {
    private val _snapshot = MutableLiveData<RegionSnapshot?>()
    val snapshot: LiveData<RegionSnapshot?> get() = _snapshot

    init {
        if (GrimGameRepository.state.grimEngine.query.getRegionSnapshot("Schwarzwald") == null) {
            GrimSeed.initialize()
        }
    }

    fun openRegion(regionName: String) {
        GrimGameRepository.state.currentRegion = regionName
        _snapshot.value = GrimGameRepository.state.grimEngine.query.getRegionSnapshot(regionName)
    }
}
