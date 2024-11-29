package io.github.lapis256.ae2_mega_things.integration.appmek.client

import io.github.lapis256.ae2_mega_things.integration.AbstractModClient
import io.github.lapis256.ae2_mega_things.integration.appmek.AppMek


object AppMekClient : AbstractModClient() {
    override val mod get() = AppMek

    override fun init() {
    }
}
