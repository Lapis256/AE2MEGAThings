package io.github.lapis256.ae2_mega_things.integration.appmek.init

import io.github.lapis256.ae2_mega_things.init.AE2MTItems.drive
import io.github.lapis256.ae2_mega_things.init.AE2MTItems.housing
import io.github.lapis256.ae2_mega_things.integration.appmek.item.ChemicalDISKDrive
import io.github.lapis256.ae2_mega_things.storage.StorageTier


object AMIntegrationItems {
    val CHEMICAL_DISK_HOUSING = housing("ME Chemical", "chemical")

    val CHEMICAL_DISK_1K = chemicalDrive(StorageTier.SIZE1K)
    val CHEMICAL_DISK_4K = chemicalDrive(StorageTier.SIZE4K)
    val CHEMICAL_DISK_16K = chemicalDrive(StorageTier.SIZE16K)
    val CHEMICAL_DISK_64K = chemicalDrive(StorageTier.SIZE64K)
    val CHEMICAL_DISK_256K = chemicalDrive(StorageTier.SIZE256K)

    val CHEMICAL_DISKS = setOf(CHEMICAL_DISK_1K, CHEMICAL_DISK_4K, CHEMICAL_DISK_16K, CHEMICAL_DISK_64K, CHEMICAL_DISK_256K)


    val MEGA_CHEMICAL_DISK_HOUSING = housing("MEGA Chemical", "mega_chemical")

    val CHEMICAL_DISK_1M = megaChemicalDrive(StorageTier.SIZE1M)
    val CHEMICAL_DISK_4M = megaChemicalDrive(StorageTier.SIZE4M)
    val CHEMICAL_DISK_16M = megaChemicalDrive(StorageTier.SIZE16M)
    val CHEMICAL_DISK_64M = megaChemicalDrive(StorageTier.SIZE64M)
    val CHEMICAL_DISK_256M = megaChemicalDrive(StorageTier.SIZE256M)

    val MEGA_CHEMICAL_DISKS = setOf(CHEMICAL_DISK_1M, CHEMICAL_DISK_4M, CHEMICAL_DISK_16M, CHEMICAL_DISK_64M, CHEMICAL_DISK_256M)

    fun init() {
        // NO-OP
    }

    private fun chemicalDrive(tier: StorageTier) = drive("ME Chemical", "chemical", tier, CHEMICAL_DISK_HOUSING, ::ChemicalDISKDrive)
    private fun megaChemicalDrive(tier: StorageTier) = drive("MEGA Chemical", "chemical", tier, MEGA_CHEMICAL_DISK_HOUSING, ::ChemicalDISKDrive)
}
