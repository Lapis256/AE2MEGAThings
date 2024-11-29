package io.github.lapis256.ae2_mega_things.integration

import io.github.lapis256.ae2_mega_things.AE2MEGAThings
import net.minecraftforge.fml.ModList


abstract class AbstractMod {
    open val modId: String get() = throw NotImplementedError()

    val loaded: Boolean
        get() = ModList.get().isLoaded(modId)

    protected abstract fun init()

    fun initIntegration() {
        if (!loaded) return

        AE2MEGAThings.LOGGER.info("Initializing integration with $modId")

        init()
    }
}
