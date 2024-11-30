package io.github.lapis256.ae2_mega_things.storage

import appeng.api.storage.cells.ISaveProvider
import io.github.lapis256.ae2_mega_things.item.AbstractDISKDrive
import io.github.projectet.ae2things.AE2Things
import io.github.projectet.ae2things.storage.DISKCellHandler
import net.minecraft.world.item.ItemStack


object AE2MTDISKCellHandler : DISKCellHandler() {
    override fun isCell(stack: ItemStack): Boolean {
        return stack.item is AbstractDISKDrive
    }

    override fun getCellInventory(stack: ItemStack, container: ISaveProvider?): AE2MTDISKCellInventory? {
        return AE2MTDISKCellInventory.createInventory(stack, container, AE2Things.currentStorageManager())
    }

    fun getCellInventory(stack: ItemStack): AE2MTDISKCellInventory? {
        return AE2MTDISKCellInventory.createInventory(stack, null, null)
    }
}
