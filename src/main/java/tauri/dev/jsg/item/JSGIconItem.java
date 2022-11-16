package tauri.dev.jsg.item;

import net.minecraft.item.Item;
import tauri.dev.jsg.JSG;

/**
 * Item that is used as an icon for advancements.
 */
public class JSGIconItem extends Item {
    public JSGIconItem(String iconId) {
        setRegistryName(JSG.MOD_ID + ":icon_" + iconId);
        setUnlocalizedName(JSG.MOD_ID + ".icon." + iconId);
    }
}
