package tauri.dev.jsg.util;

import net.minecraft.item.ItemStack;
import tauri.dev.jsg.item.energy.CapacitorItemBlockCreative;
import tauri.dev.jsg.item.energy.ZPMItemBlockCreative;
import tauri.dev.jsg.item.stargate.IrisItem;

public class CreativeItemsChecker {
    public static boolean canInteractWith(ItemStack stack, boolean isCreative) {
        if (isCreative) return true;
        if (stack == null) return true;
        if (stack.getItem() instanceof ZPMItemBlockCreative) return false;
        if (stack.getItem() instanceof IrisItem && ((IrisItem) stack.getItem()).creativeIris) return false;
        return !(stack.getItem() instanceof CapacitorItemBlockCreative);
    }
}
