package io.github.lapis256.ae2_mega_things.init

import appeng.api.upgrades.Upgrades
import appeng.core.definitions.AEItems
import appeng.core.definitions.ItemDefinition
import appeng.items.storage.StorageTier
import gripe._90.megacells.definition.MEGAItems
import io.github.lapis256.ae2_mega_things.AE2MEGAThings
import io.github.lapis256.ae2_mega_things.item.AbstractDISKDrive
import io.github.lapis256.ae2_mega_things.item.DISKHousing
import io.github.lapis256.ae2_mega_things.item.FluidDISKDrive
import io.github.lapis256.ae2_mega_things.item.ItemDISKDrive
import io.github.lapis256.ae2_mega_things.util.isMEGA
import io.github.lapis256.ae2_mega_things.util.kilobytes
import net.minecraft.world.item.Item
import net.minecraft.world.level.ItemLike
import net.neoforged.neoforge.registries.DeferredRegister


typealias MEGADISKDriveFactory = (component: ItemLike, housing: ItemLike, kilobyte: Int, idleDrain: Double) -> AbstractDISKDrive

object AE2MTItems {
    val REGISTRY = DeferredRegister.createItems(AE2MEGAThings.MOD_ID)

    val ITEMS = mutableSetOf<ItemDefinition<*>>()
    val DISK_DRIVES = mutableSetOf<DISKDriveDefinition>()


    val MEGA_ITEM_DISK_HOUSING = housing("MEGA Item", "mega_item")

    val ITEM_DISK_1M = megaItemDrive(MEGAItems.TIER_1M)
    val ITEM_DISK_4M = megaItemDrive(MEGAItems.TIER_4M)
    val ITEM_DISK_16M = megaItemDrive(MEGAItems.TIER_16M)
    val ITEM_DISK_64M = megaItemDrive(MEGAItems.TIER_64M)
    val ITEM_DISK_256M = megaItemDrive(MEGAItems.TIER_256M)

    val MEGA_ITEM_DISKS = setOf(ITEM_DISK_1M, ITEM_DISK_4M, ITEM_DISK_16M, ITEM_DISK_64M, ITEM_DISK_256M)


    val FLUID_DISK_HOUSING = housing("ME Fluid", "fluid")

    val FLUID_DISK_1K = fluidDrive(StorageTier.SIZE_1K)
    val FLUID_DISK_4K = fluidDrive(StorageTier.SIZE_4K)
    val FLUID_DISK_16K = fluidDrive(StorageTier.SIZE_16K)
    val FLUID_DISK_64K = fluidDrive(StorageTier.SIZE_64K)
    val FLUID_DISK_256K = fluidDrive(StorageTier.SIZE_256K)

    val FLUID_DISKS = setOf(FLUID_DISK_1K, FLUID_DISK_4K, FLUID_DISK_16K, FLUID_DISK_64K, FLUID_DISK_256K)


    val MEGA_FLUID_DISK_HOUSING = housing("MEGA Fluid", "mega_fluid")

    val FLUID_DISK_1M = megaFluidDrive(MEGAItems.TIER_1M)
    val FLUID_DISK_4M = megaFluidDrive(MEGAItems.TIER_4M)
    val FLUID_DISK_16M = megaFluidDrive(MEGAItems.TIER_16M)
    val FLUID_DISK_64M = megaFluidDrive(MEGAItems.TIER_64M)
    val FLUID_DISK_256M = megaFluidDrive(MEGAItems.TIER_256M)

    val MEGA_FLUID_DISKS = setOf(FLUID_DISK_1M, FLUID_DISK_4M, FLUID_DISK_16M, FLUID_DISK_64M, FLUID_DISK_256M)


    fun initUpgrades() {
        for (drive in DISK_DRIVES) {
            if (drive.asItem().isSupportsFuzzyMode) {
                Upgrades.add(AEItems.FUZZY_CARD, drive, 1)
            }
            Upgrades.add(AEItems.INVERTER_CARD, drive, 1)
        }
    }

    private fun <T : Item> register(englishName: String, path: String, factory: () -> T): ItemDefinition<T> {
        return ItemDefinition(englishName, REGISTRY.registerItem(path) { factory() }).also(ITEMS::add).also(AE2MTCreativeTab::add)
    }

    fun housing(prefix: String, idPrefix: String) = register("$prefix DISK Housing", "${idPrefix}_disk_housing") { DISKHousing() }

    fun drive(typeKey: String, tier: StorageTier, housing: ItemLike, factory: MEGADISKDriveFactory) =
        register(makeDriveName(tier, typeKey), "${typeKey}_disk_drive_${tier.namePrefix}") {
            factory(tier.componentSupplier.get(), housing, tier.kilobytes(), tier.idleDrain)
        }.also { DISK_DRIVES.add(DISKDriveDefinition(it, typeKey, tier)) }

    private fun makeDriveName(tier: StorageTier, typeKey: String) =
        "${tier.namePrefix.replace("m", "M")} ${if (tier.isMEGA) "MEGA" else "ME"} ${typeKey.replaceFirstChar(Char::titlecase)} DISK Drive"

    private fun megaItemDrive(tier: StorageTier) = drive("item", tier, MEGA_ITEM_DISK_HOUSING, ::ItemDISKDrive)
    private fun fluidDrive(tier: StorageTier) = drive("fluid", tier, FLUID_DISK_HOUSING, ::FluidDISKDrive)
    private fun megaFluidDrive(tier: StorageTier) = drive("fluid", tier, MEGA_FLUID_DISK_HOUSING, ::FluidDISKDrive)

    data class DISKDriveDefinition(val item: ItemDefinition<AbstractDISKDrive>, val keyType: String, val tier: StorageTier) : ItemLike by item {
        override fun asItem() = item.asItem()
    }
}