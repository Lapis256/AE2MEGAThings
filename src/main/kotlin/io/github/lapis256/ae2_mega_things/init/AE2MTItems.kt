package io.github.lapis256.ae2_mega_things.init

import appeng.api.upgrades.Upgrades
import appeng.core.definitions.AEItems
import appeng.core.definitions.ItemDefinition
import io.github.lapis256.ae2_mega_things.AE2MEGAThings
import io.github.lapis256.ae2_mega_things.item.AbstractDISKDrive
import io.github.lapis256.ae2_mega_things.item.DISKHousing
import io.github.lapis256.ae2_mega_things.item.FluidDISKDrive
import io.github.lapis256.ae2_mega_things.item.ItemDISKDrive
import io.github.lapis256.ae2_mega_things.storage.StorageTier
import net.minecraft.world.item.Item
import net.minecraft.world.level.ItemLike


typealias MEGADISKDriveFactory = (component: ItemLike, housing: ItemLike, kilobyte: Int, idleDrain: Double) -> AbstractDISKDrive

object AE2MTItems {
    val ITEMS = mutableSetOf<ItemDefinition<*>>()
    val DISK_DRIVES = mutableSetOf<AbstractDISKDrive>()


    val MEGA_ITEM_DISK_HOUSING = housing("MEGA Item", "mega_item")

    val ITEM_DISK_1M = megaItemDrive(StorageTier.SIZE1M)
    val ITEM_DISK_4M = megaItemDrive(StorageTier.SIZE4M)
    val ITEM_DISK_16M = megaItemDrive(StorageTier.SIZE16M)
    val ITEM_DISK_64M = megaItemDrive(StorageTier.SIZE64M)
    val ITEM_DISK_256M = megaItemDrive(StorageTier.SIZE256M)

    val MEGA_ITEM_DISKS = setOf(ITEM_DISK_1M, ITEM_DISK_4M, ITEM_DISK_16M, ITEM_DISK_64M, ITEM_DISK_256M)


    val FLUID_DISK_HOUSING = housing("ME Fluid", "fluid")

    val FLUID_DISK_1K = fluidDrive(StorageTier.SIZE1K)
    val FLUID_DISK_4K = fluidDrive(StorageTier.SIZE4K)
    val FLUID_DISK_16K = fluidDrive(StorageTier.SIZE16K)
    val FLUID_DISK_64K = fluidDrive(StorageTier.SIZE64K)
    val FLUID_DISK_256K = fluidDrive(StorageTier.SIZE256K)

    val FLUID_DISKS = setOf(FLUID_DISK_1K, FLUID_DISK_4K, FLUID_DISK_16K, FLUID_DISK_64K, FLUID_DISK_256K)


    val MEGA_FLUID_DISK_HOUSING = housing("MEGA Fluid", "mega_fluid")

    val FLUID_DISK_1M = megaFluidDrive(StorageTier.SIZE1M)
    val FLUID_DISK_4M = megaFluidDrive(StorageTier.SIZE4M)
    val FLUID_DISK_16M = megaFluidDrive(StorageTier.SIZE16M)
    val FLUID_DISK_64M = megaFluidDrive(StorageTier.SIZE64M)
    val FLUID_DISK_256M = megaFluidDrive(StorageTier.SIZE256M)

    val MEGA_FLUID_DISKS = setOf(FLUID_DISK_1M, FLUID_DISK_4M, FLUID_DISK_16M, FLUID_DISK_64M, FLUID_DISK_256M)


    fun initUpgrades() {
        for (drive in DISK_DRIVES) {
            if (drive.isSupportsFuzzyMode) {
                Upgrades.add(AEItems.FUZZY_CARD, drive, 1)
            }
            Upgrades.add(AEItems.INVERTER_CARD, drive, 1)
        }
    }

    private fun <T : Item> register(englishName: String, path: String, factory: () -> T): ItemDefinition<T> {
        return factory().let {
            ItemDefinition(englishName, AE2MEGAThings.rl(path), it).also(ITEMS::add)
        }
    }

    fun housing(prefix: String, idPrefix: String) = register("$prefix DISK Housing", "${idPrefix}_disk_housing") { DISKHousing() }

    fun drive(typeName: String, idPrefix: String, tier: StorageTier, housing: ItemLike, factory: MEGADISKDriveFactory) =
        register("${tier.affix.replace("m", "M")} $typeName DISK Drive", "${idPrefix}_disk_drive_${tier.affix}") {
            factory(tier.component, housing, tier.kilobytes, tier.idleDrain).also { DISK_DRIVES.add(it) }
        }

    private fun megaItemDrive(tier: StorageTier) = drive("MEGA Item", "item", tier, MEGA_ITEM_DISK_HOUSING, ::ItemDISKDrive)
    private fun fluidDrive(tier: StorageTier) = drive("ME Fluid", "fluid", tier, FLUID_DISK_HOUSING, ::FluidDISKDrive)
    private fun megaFluidDrive(tier: StorageTier) = drive("MEGA Fluid", "fluid", tier, MEGA_FLUID_DISK_HOUSING, ::FluidDISKDrive)
}
