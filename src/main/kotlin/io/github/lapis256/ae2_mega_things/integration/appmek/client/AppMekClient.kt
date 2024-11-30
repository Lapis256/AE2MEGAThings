package io.github.lapis256.ae2_mega_things.integration.appmek.client

import io.github.lapis256.ae2_mega_things.integration.AbstractModClient
import io.github.lapis256.ae2_mega_things.integration.appmek.AppMek
import net.neoforged.bus.api.IEventBus


object AppMekClient : AbstractModClient() {
    override val mod get() = AppMek

    override fun init(modEventBus: IEventBus) {
    }
}
