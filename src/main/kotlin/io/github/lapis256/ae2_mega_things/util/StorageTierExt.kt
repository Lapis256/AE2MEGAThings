package io.github.lapis256.ae2_mega_things.util

import appeng.core.definitions.AEItems
import appeng.items.materials.StorageComponentItem
import appeng.items.storage.StorageTier
import net.minecraft.world.level.ItemLike


fun StorageTier.kilobytes() = when (index) {
    in 1..5 -> 1 shl (2 * (index - 1))
    in 6..10 -> (1 shl (2 * (index - 6))) * 1000
    else -> throw IllegalArgumentException("Tier out of range")
}

fun StorageTier.componentItem(): ItemLike = when (this) {
    StorageTier.SIZE_1K -> AEItems.CELL_COMPONENT_1K
    StorageTier.SIZE_4K -> AEItems.CELL_COMPONENT_4K
    StorageTier.SIZE_16K -> AEItems.CELL_COMPONENT_16K
    StorageTier.SIZE_64K -> AEItems.CELL_COMPONENT_64K
    StorageTier.SIZE_256K -> AEItems.CELL_COMPONENT_256K
    else -> this.componentSupplier.get() as? StorageComponentItem
        ?: throw RuntimeException("StorageTier.componentSupplier.get() is not StorageComponentItem: $this")
}
