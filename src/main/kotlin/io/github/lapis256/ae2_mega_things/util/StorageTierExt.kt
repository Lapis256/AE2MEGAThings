package io.github.lapis256.ae2_mega_things.util

import appeng.items.storage.StorageTier


fun StorageTier.kilobytes() = when (index) {
    in 1..5 -> 1 shl (2 * (index - 1))
    in 6..10 -> (1 shl (2 * (index - 6))) * 1000
    else -> throw IllegalArgumentException("Tier out of range")
}
