package io.github.lapis256.ae2_mega_things.integration.appmek

import io.github.lapis256.ae2_mega_things.AE2MEGAThings
import io.github.lapis256.ae2_mega_things.integration.AbstractMod
import io.github.lapis256.ae2_mega_things.integration.appmek.init.AMIntegrationItems
import net.minecraft.core.registries.Registries
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.registries.RegisterEvent
import thedarkcolour.kotlinforforge.forge.MOD_BUS


object AppMek : AbstractMod() {
    override val modId get() = "appmek"

    override fun init() {
        MOD_BUS.addListener(EventPriority.HIGHEST, ::register)
        MOD_BUS.addListener(::commonSetup)
    }

    private fun register(event: RegisterEvent) {
        if (event.registryKey == Registries.ITEM) {
            AMIntegrationItems.init()
        }
    }

    fun commonSetup(event: FMLCommonSetupEvent) {
        event.enqueueWork {
            AMIntegrationItems.CHEMICAL_DISKS.forEach { AE2MEGAThings.registerDriveModel(it, "chemical") }
            AMIntegrationItems.MEGA_CHEMICAL_DISKS.forEach { AE2MEGAThings.registerDriveModel(it, "mega_chemical") }
        }
    }
}
