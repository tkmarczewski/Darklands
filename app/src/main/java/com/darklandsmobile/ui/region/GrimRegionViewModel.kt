package com.darklandsmobile.ui.region

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.darklandsmobile.core.GameBootstrap
import com.darklandsmobile.core.GameRepository
import com.darklandsmobile.grimreich.v1.RegionSnapshot

class GrimRegionViewModel : ViewModel() {

    private val _snapshot = MutableLiveData<RegionSnapshot?>()
    val snapshot: LiveData<RegionSnapshot?> get() = _snapshot

    init {
        val state = GameRepository.state
        if (state.grimEngine.query.getRegionSnapshot("Wybrzeże Północne") == null) {
            GameRepository.state = GameBootstrap.initialize()
        }
    }

    fun openRegion(regionName: String) {
        val state = GameRepository.state
        state.grimCurrentRegion = regionName
        _snapshot.value = state.grimEngine.query.getRegionSnapshot(regionName)
    }
}
