package io.github.lapis256.ae2_mega_things.item

import appeng.api.config.FuzzyMode
import appeng.api.stacks.AEKey
import appeng.api.stacks.AEKeyType
import appeng.api.upgrades.IUpgradeInventory
import appeng.api.upgrades.UpgradeInventories
import appeng.hooks.AEToolItem
import appeng.items.contents.CellConfig
import appeng.util.ConfigInventory
import appeng.util.InteractionUtil
import io.github.lapis256.ae2_mega_things.init.AE2MTTexts
import io.github.lapis256.ae2_mega_things.storage.AE2MTDISKCellHandler
import io.github.projectet.ae2things.AE2Things
import io.github.projectet.ae2things.storage.IDISKCellItem
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.Level
import java.util.*


abstract class AbstractDISKDrive(
    private val keyType: AEKeyType, private val component: ItemLike, private val housing: ItemLike, kilobytes: Int, private val idleDrain: Double
) : Item(
    Properties()
        .stacksTo(1)
        .fireResistant()
        .component(AE2Things.DATA_DISK_ITEM_COUNT, 0L)
        .component(AE2Things.DATA_FUZZY_MODE, FuzzyMode.IGNORE_ALL)
), IDISKCellItem, AEToolItem {
    private val bytes = kilobytes * 1000

    val isSupportsFuzzyMode = keyType.supportsFuzzyRangeSearch()
    override fun getKeyType() = keyType
    override fun isBlackListed(cellItem: ItemStack, requestedAddition: AEKey) = false
    override fun getBytes(stack: ItemStack) = bytes
    override fun getIdleDrain() = idleDrain

    // Override isStorageCell to use AE2MTDISKCellInventory instead of DISKCellInventory.
    override fun isStorageCell(stack: ItemStack) = false

    override fun getUpgrades(stack: ItemStack): IUpgradeInventory {
        return UpgradeInventories.forItem(stack, if (isSupportsFuzzyMode) 2 else 1)
    }

    override fun getConfigInventory(stack: ItemStack): ConfigInventory {
        return CellConfig.create(setOf(this.keyType), stack)
    }

    override fun getFuzzyMode(stack: ItemStack): FuzzyMode {
        return stack.getOrDefault(AE2Things.DATA_FUZZY_MODE, FuzzyMode.IGNORE_ALL)
    }

    override fun setFuzzyMode(stack: ItemStack, mode: FuzzyMode) {
        stack.set(AE2Things.DATA_FUZZY_MODE, mode)
    }

    override fun appendHoverText(stack: ItemStack, context: TooltipContext, tooltip: MutableList<Component>, isAdvanced: TooltipFlag) {
        tooltip.add(
            Component.literal("Deep Item Storage disK - Storage for dummies").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC)
        )
        AE2MTDISKCellHandler.addCellInformationToTooltip(stack, tooltip)
    }

    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
        this.disassembleDrive(player.getItemInHand(hand), level, player)
        return InteractionResultHolder(InteractionResult.sidedSuccess(level.isClientSide), player.getItemInHand(hand))
    }

    override fun onItemUseFirst(stack: ItemStack, context: UseOnContext): InteractionResult {
        val player = context.player ?: return InteractionResult.PASS
        if (this.disassembleDrive(stack, context.level, player)) {
            return InteractionResult.sidedSuccess(context.level.isClientSide)
        }
        return InteractionResult.PASS
    }

    private fun disassembleDrive(stack: ItemStack, level: Level, player: Player): Boolean {
        if (level.isClientSide || !InteractionUtil.isInAlternateUseMode(player)) {
            return false
        }

        val inventory = player.inventory
        if (inventory.getSelected() != stack) {
            return false
        }
        val inv = AE2MTDISKCellHandler.getCellInventory(stack, null) ?: return false
        if (!inv.availableStacks.isEmpty) {
            player.displayClientMessage(AE2MTTexts.Chat.OnlyEmptyDISKsCanBeDisassembled.text(), true)
            return false
        }

        inventory.setItem(inventory.selected, ItemStack.EMPTY)
        inventory.placeItemBackInInventory(ItemStack(component))
        getUpgrades(stack).forEach(inventory::placeItemBackInInventory)
        inventory.placeItemBackInInventory(ItemStack(housing))
        return true
    }

    override fun clone(stack: ItemStack): ItemStack {
        val diskId = stack.get(AE2Things.DATA_DISK_ID) ?: return stack.copy()
        val id = UUID.randomUUID()
        return stack.copy().also {
            it.set(AE2Things.DATA_DISK_ID, id)
            it.count = it.maxStackSize

            val storageManager = AE2Things.currentStorageManager() ?: run {
                it.remove(AE2Things.DATA_DISK_ITEM_COUNT)
                return it
            }

            val storage = storageManager.getOrCreateDisk(diskId)
            it.set(AE2Things.DATA_DISK_ITEM_COUNT, storage.itemCount)
            storageManager.updateDisk(id, storage)
        }
    }
}
