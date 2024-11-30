package io.github.lapis256.ae2_mega_things.storage

import appeng.api.storage.cells.ISaveProvider
import io.github.lapis256.ae2_mega_things.item.AbstractDISKDrive
import io.github.projectet.ae2things.storage.DISKCellInventory
import io.github.projectet.ae2things.storage.IDISKCellItem
import io.github.projectet.ae2things.util.StorageManager
import net.minecraft.world.item.ItemStack
import org.jetbrains.annotations.Nullable


class AE2MTDISKCellInventory(cellType: IDISKCellItem, private val stack: ItemStack, container: ISaveProvider?, storageManager: StorageManager?) :
    DISKCellInventory(cellType, stack, container, storageManager) {
    companion object {
        fun createInventory(stack: ItemStack, saveProvider: ISaveProvider?, storageManager: StorageManager?): AE2MTDISKCellInventory? {
            val cell = stack.item as? AbstractDISKDrive ?: return null
            return AE2MTDISKCellInventory(cell, stack, saveProvider, storageManager)
        }
    }

    private val amountPerItemCount = cellType.keyType.amountPerUnit

    override fun getDescription() = stack.hoverName
    override fun getStoredItemCount() = super.getStoredItemCount() / amountPerItemCount
    override fun getNbtItemCount() = super.getNbtItemCount() / amountPerItemCount
}
