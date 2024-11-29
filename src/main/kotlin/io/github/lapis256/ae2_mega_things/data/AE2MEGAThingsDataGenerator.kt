package io.github.lapis256.ae2_mega_things.data

import io.github.lapis256.ae2_mega_things.AE2MEGAThings
import io.github.lapis256.ae2_mega_things.data.provider.AE2MTBlockModelProvider
import io.github.lapis256.ae2_mega_things.data.provider.AE2MTItemModelProvider
import io.github.lapis256.ae2_mega_things.data.provider.AE2MTLanguageProvider
import io.github.lapis256.ae2_mega_things.data.provider.AE2MTRecipeProvider
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.data.event.GatherDataEvent
import net.minecraftforge.fml.common.Mod
import thedarkcolour.kotlinforforge.forge.MOD_BUS


@Mod.EventBusSubscriber(modid = AE2MEGAThings.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
object AE2MEGAThingsDataGenerator {
    init {
        MOD_BUS.addListener(::onGatherData)
    }

    private fun onGatherData(event: GatherDataEvent) {
        val generator = event.generator
        val output = generator.packOutput
        val existingFileHelper = event.existingFileHelper

        generator.addProvider(event.includeClient(), AE2MTLanguageProvider(output))
        generator.addProvider(event.includeClient(), AE2MTItemModelProvider(output, existingFileHelper))
        generator.addProvider(event.includeClient(), AE2MTBlockModelProvider(output, existingFileHelper))

        generator.addProvider(event.includeServer(), AE2MTRecipeProvider(output))
    }
}
