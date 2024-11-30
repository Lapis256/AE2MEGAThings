package io.github.lapis256.ae2_mega_things.data

import io.github.lapis256.ae2_mega_things.AE2MEGAThings
import io.github.lapis256.ae2_mega_things.data.provider.AE2MTBlockModelProvider
import io.github.lapis256.ae2_mega_things.data.provider.AE2MTItemModelProvider
import io.github.lapis256.ae2_mega_things.data.provider.AE2MTLanguageProvider
import io.github.lapis256.ae2_mega_things.data.provider.AE2MTRecipeProvider
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.data.event.GatherDataEvent


@EventBusSubscriber(modid = AE2MEGAThings.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
object AE2MEGAThingsDataGenerator {
    @SubscribeEvent
    private fun onGatherData(event: GatherDataEvent) {
        val generator = event.generator
        val output = generator.packOutput
        val existingFileHelper = event.existingFileHelper
        val lookupProvider = event.lookupProvider

        generator.addProvider(event.includeClient(), AE2MTLanguageProvider(output))
        generator.addProvider(event.includeClient(), AE2MTItemModelProvider(output, existingFileHelper))
        generator.addProvider(event.includeClient(), AE2MTBlockModelProvider(output, existingFileHelper))

        generator.addProvider(event.includeServer(), AE2MTRecipeProvider(output, lookupProvider))
    }
}
