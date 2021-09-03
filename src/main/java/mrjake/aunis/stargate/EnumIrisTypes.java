package mrjake.aunis.stargate;

import mrjake.aunis.item.AunisItems;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

import java.util.HashMap;
import java.util.Map;

public enum EnumIrisTypes {
    NULL((byte) 0, Items.AIR),

    IRIS_TITANIUM((byte) 1, AunisItems.UPGRADE_IRIS),
    IRIS_TRINIUM((byte) 2, AunisItems.UPGRADE_IRIS_TRINIUM),

    SHIELD((byte) 3, AunisItems.UPGRADE_SHIELD);

    public final byte id;
    public final Item item;

    EnumIrisTypes(byte id, Item item) {
        this.id = id;
        this.item = item;
    }

    public final static Map<Item, EnumIrisTypes> itemMap = new HashMap<Item, EnumIrisTypes>();

    static {
        for (EnumIrisTypes type : values()) {
            itemMap.put(type.item, type);
        }
    }


    public static EnumIrisTypes byId(byte id) {
        return (id < (values().length - 1)) ? values()[id] : NULL;
    }

    public static EnumIrisTypes byItem(Item item) {
        EnumIrisTypes type = itemMap.get(item);
        if (type == null) return NULL;
        return type;
    }
}
