package io.github.lapis256.ae2_mega_things.integration

import io.github.lapis256.ae2_mega_things.AE2MEGAThings
import net.minecraftforge.fml.ModList


abstract class AbstractModClient {
    protected open val mod: AbstractMod get() = throw NotImplementedError()

    protected abstract fun init()

    fun initIntegration() {
        if (!mod.loaded) return

        init()
    }
}
