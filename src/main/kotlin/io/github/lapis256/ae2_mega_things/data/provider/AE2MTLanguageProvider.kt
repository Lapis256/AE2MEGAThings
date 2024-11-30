package io.github.lapis256.ae2_mega_things.data.provider

import io.github.lapis256.ae2_mega_things.AE2MEGAThings
import io.github.lapis256.ae2_mega_things.init.AE2MTItems
import io.github.lapis256.ae2_mega_things.init.AE2MTTexts
import net.minecraft.data.PackOutput
import net.neoforged.neoforge.common.data.LanguageProvider


class AE2MTLanguageProvider(output: PackOutput) : LanguageProvider(output, AE2MEGAThings.MOD_ID, "en_us") {
    override fun addTranslations() {
        for (item in AE2MTItems.ITEMS) {
            add(item.asItem(), item.englishName)
        }

        for(text in AE2MTTexts.entries) {
            add(text.translationKey, text.englishText)
        }
    }
}
