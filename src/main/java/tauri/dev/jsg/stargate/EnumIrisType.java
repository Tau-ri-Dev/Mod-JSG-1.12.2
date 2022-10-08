package tauri.dev.jsg.stargate;

import tauri.dev.jsg.item.JSGItems;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

import java.util.HashMap;
import java.util.Map;

public enum EnumIrisType {
    NULL((byte) 0, Items.AIR),

    IRIS_TITANIUM((byte) 1, JSGItems.UPGRADE_IRIS),
    IRIS_TRINIUM((byte) 2, JSGItems.UPGRADE_IRIS_TRINIUM),

    SHIELD((byte) 3, JSGItems.UPGRADE_SHIELD);

    public final byte id;
    public final Item item;

    EnumIrisType(byte id, Item item) {
        this.id = id;
        this.item = item;
    }

    public final static Map<Item, EnumIrisType> itemMap = new HashMap<Item, EnumIrisType>();

    static {
        for (EnumIrisType type : values()) {
            itemMap.put(type.item, type);
        }
    }


    public static EnumIrisType byId(byte id) {
        return (id < values().length) ? values()[id] : NULL;
    }

    public static EnumIrisType byItem(Item item) {
        EnumIrisType type = itemMap.get(item);
        if (type == null) return NULL;
        return type;
    }
}
