package io.github.lapis256.ae2_mega_things.integration.appmek.init

import appeng.items.storage.StorageTier
import gripe._90.megacells.definition.MEGAItems
import io.github.lapis256.ae2_mega_things.init.AE2MTItems.drive
import io.github.lapis256.ae2_mega_things.init.AE2MTItems.housing
import io.github.lapis256.ae2_mega_things.integration.appmek.item.ChemicalDISKDrive
import java.util.function.Supplier


object AMIntegrationItems {
    val CHEMICAL_DISK_HOUSING = housing("ME Chemical", "chemical")

    val CHEMICAL_DISK_1K = chemicalDrive { StorageTier.SIZE_1K }
    val CHEMICAL_DISK_4K = chemicalDrive { StorageTier.SIZE_4K }
    val CHEMICAL_DISK_16K = chemicalDrive { StorageTier.SIZE_16K }
    val CHEMICAL_DISK_64K = chemicalDrive { StorageTier.SIZE_64K }
    val CHEMICAL_DISK_256K = chemicalDrive { StorageTier.SIZE_256K }

    val CHEMICAL_DISKS = setOf(CHEMICAL_DISK_1K, CHEMICAL_DISK_4K, CHEMICAL_DISK_16K, CHEMICAL_DISK_64K, CHEMICAL_DISK_256K)


    val MEGA_CHEMICAL_DISK_HOUSING = housing("MEGA Chemical", "mega_chemical")

    val CHEMICAL_DISK_1M = megaChemicalDrive { MEGAItems.TIER_1M }
    val CHEMICAL_DISK_4M = megaChemicalDrive { MEGAItems.TIER_4M }
    val CHEMICAL_DISK_16M = megaChemicalDrive { MEGAItems.TIER_16M }
    val CHEMICAL_DISK_64M = megaChemicalDrive { MEGAItems.TIER_64M }
    val CHEMICAL_DISK_256M = megaChemicalDrive { MEGAItems.TIER_256M }

    val MEGA_CHEMICAL_DISKS = setOf(CHEMICAL_DISK_1M, CHEMICAL_DISK_4M, CHEMICAL_DISK_16M, CHEMICAL_DISK_64M, CHEMICAL_DISK_256M)

    fun init() {
        // NO-OP
    }

    private fun chemicalDrive(tierSupplier: Supplier<StorageTier>) = drive("ME Chemical", "chemical", tierSupplier, CHEMICAL_DISK_HOUSING, ::ChemicalDISKDrive)
    private fun megaChemicalDrive(tierSupplier: Supplier<StorageTier>) = drive("MEGA Chemical", "chemical", tierSupplier, MEGA_CHEMICAL_DISK_HOUSING, ::ChemicalDISKDrive)
}
