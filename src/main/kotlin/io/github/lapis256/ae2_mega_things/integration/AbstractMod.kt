package io.github.lapis256.ae2_mega_things.integration

import io.github.lapis256.ae2_mega_things.AE2MEGAThings
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModList


abstract class AbstractMod {
    open val modId: String get() = throw NotImplementedError()

    val loaded: Boolean
        get() = ModList.get().isLoaded(modId)

    protected abstract fun init(modEventBus: IEventBus)

    fun initIntegration(modEventBus: IEventBus) {
        if (!loaded) return

        AE2MEGAThings.LOGGER.info("Initializing integration with $modId")

        init(modEventBus)
    }
}
