package io.github.lapis256.ae2_mega_things.integration

import net.neoforged.bus.api.IEventBus


abstract class AbstractModClient {
    protected open val mod: AbstractMod get() = throw NotImplementedError()

    protected abstract fun init(modEventBus: IEventBus)

    fun initIntegration(modEventBus: IEventBus) {
        if (!mod.loaded) return

        init(modEventBus)
    }
}
