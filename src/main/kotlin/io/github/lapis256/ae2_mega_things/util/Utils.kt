package io.github.lapis256.ae2_mega_things.util

import appeng.api.stacks.AEItemKey
import appeng.api.storage.cells.IBasicCellItem
import appeng.me.cells.BasicCellHandler
import io.github.lapis256.ae2_mega_things.item.AbstractDISKDrive
import io.github.lapis256.ae2_mega_things.storage.AE2MTDISKCellHandler
import io.github.projectet.ae2things.item.DISKDrive
import io.github.projectet.ae2things.storage.DISKCellHandler
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.ItemLike
import net.minecraftforge.registries.ForgeRegistries


object Utils {
    @JvmStatic
    fun isCellNestingPrevented(cell: AEItemKey): Boolean {
        val stack = cell.toStack()
        if (cell.item is IBasicCellItem) {
            return (BasicCellHandler.INSTANCE.getCellInventory(stack, null)?.usedBytes ?: 0) > 0L
        }

        val inv = when (cell.item) {
            is AbstractDISKDrive -> AE2MTDISKCellHandler.getCellInventory(stack, null)
            is DISKDrive -> DISKCellHandler.INSTANCE.getCellInventory(stack, null)
            else -> return false // Not a cell
        } ?: return false

        return !inv.availableStacks.isEmpty
    }

    @JvmStatic
    fun getItemId(item: ItemLike) = ForgeRegistries.ITEMS.getKey(item.asItem())

    @JvmStatic
    fun getItem(rl: ResourceLocation) = ForgeRegistries.ITEMS.getValue(rl)
}
