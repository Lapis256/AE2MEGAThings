package io.github.lapis256.ae2_mega_things.integration.appmek

import io.github.lapis256.ae2_mega_things.AE2MEGAThings
import io.github.lapis256.ae2_mega_things.integration.AbstractMod
import io.github.lapis256.ae2_mega_things.integration.appmek.init.AMIntegrationItems
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent


object AppMek : AbstractMod() {
    override val modId get() = "appmek"

    override fun init(modEventBus: IEventBus) {
        AMIntegrationItems.init()
    }
}
