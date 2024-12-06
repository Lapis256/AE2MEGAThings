package io.github.lapis256.ae2_mega_things.mixin;

import io.github.projectet.ae2things.command.Command;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;


@Mixin(value = Command.class, remap = false)
public class MixinAE2ThingsCommand {
    @ModifyConstant(method = "help", constant = @Constant(ordinal = 1))
    private static String modifyHelpCommand(String original) {
        return original.replace("<UUID>", "<UUID> [DISK]") + " You can change the disk to be spawned by the given DISK.";
    }
}
