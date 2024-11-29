package io.github.lapis256.ae2_mega_things.integration.appmek

import io.github.lapis256.ae2_mega_things.AE2MEGAThings
import io.github.lapis256.ae2_mega_things.integration.AbstractMod
import io.github.lapis256.ae2_mega_things.integration.appmek.init.AMIntegrationItems
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import thedarkcolour.kotlinforforge.forge.MOD_BUS


object AppMek : AbstractMod() {
    override val modId get() = "appmek"

    override fun init() {
        AMIntegrationItems.init()

        MOD_BUS.addListener(::commonSetup)
    }

    fun commonSetup(event: FMLCommonSetupEvent) {
        event.enqueueWork {
            AMIntegrationItems.CHEMICAL_DISKS.forEach { AE2MEGAThings.registerDriveModel(it, "chemical") }
            AMIntegrationItems.MEGA_CHEMICAL_DISKS.forEach { AE2MEGAThings.registerDriveModel(it, "mega_chemical") }
        }
    }
}
