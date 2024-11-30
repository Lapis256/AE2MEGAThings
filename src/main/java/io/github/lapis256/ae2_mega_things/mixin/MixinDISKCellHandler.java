package io.github.lapis256.ae2_mega_things.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.lapis256.ae2_mega_things.storage.AE2MTDISKCellHandler;
import io.github.projectet.ae2things.storage.DISKCellHandler;
import io.github.projectet.ae2things.storage.DISKCellInventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(value = DISKCellHandler.class, remap = false)
public class MixinDISKCellHandler {
    @ModifyExpressionValue(method = "addCellInformationToTooltip", at = @At(value = "INVOKE", target = "Lio/github/projectet/ae2things/storage/DISKCellInventory;createInventory(Lnet/minecraft/world/item/ItemStack;Lappeng/api/storage/cells/ISaveProvider;Lio/github/projectet/ae2things/util/StorageManager;)Lio/github/projectet/ae2things/storage/DISKCellInventory;"))
    private DISKCellInventory modifyDISKCellInventory(DISKCellInventory original, @Local(argsOnly = true) ItemStack stack) {
        return original != null ? original : AE2MTDISKCellHandler.INSTANCE.getCellInventory(stack);
    }
}
