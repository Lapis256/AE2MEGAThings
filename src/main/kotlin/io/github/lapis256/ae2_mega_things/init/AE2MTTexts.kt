package io.github.lapis256.ae2_mega_things.init

import appeng.core.localization.LocalizationEnum


object AE2MTTexts {
    enum class Chat(private val englishText: String) : LocalizationEnum {
        OnlyEmptyDISKsCanBeDisassembled("Only empty DISKs can be disassembled."),
        UnknownDISKDrive("Unknown DISK Drive '[%s]'");

        override fun getEnglishText() = englishText
        override fun getTranslationKey() = "chat.ae2_mega_things.$name"
    }


    val entries: List<LocalizationEnum> = Chat.entries
}
