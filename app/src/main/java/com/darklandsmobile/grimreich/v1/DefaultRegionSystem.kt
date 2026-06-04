package com.darklandsmobile.grimreich.v1

class DefaultRegionSystem : RegionSystem {
    private val regions = mutableMapOf<String, RegionConsciousness>()
    private val times = mutableMapOf<String, NonlinearTime>()
    private val architectures = mutableMapOf<String, FullnessArchitecture>()
    private var collapse: WorldCollapse? = null

    override fun applyConsciousness(region: RegionConsciousness) {
        regions[region.regionName] = region
    }

    override fun applyNonlinearTime(time: NonlinearTime) {
        times[time.regionName] = time
    }

    override fun applyArchitecture(structure: FullnessArchitecture) {
        architectures[structure.structureName] = structure
    }

    override fun applyCollapse(collapse: WorldCollapse) {
        this.collapse = collapse
    }

    fun getRegion(name: String): RegionConsciousness? = regions[name]
    fun getTime(name: String): NonlinearTime? = times[name]
    fun getArchitectures(): List<FullnessArchitecture> = architectures.values.toList()
    fun getCollapse(): WorldCollapse? = collapse
}
