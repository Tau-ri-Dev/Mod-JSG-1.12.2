package tauri.dev.jsg.item.props;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import tauri.dev.jsg.block.props.JSGDecorPropBlock;
import tauri.dev.jsg.loader.ElementEnum;
import tauri.dev.jsg.util.main.JSGProps;

import javax.annotation.Nonnull;
import java.util.Objects;

public class DecorPropItem extends ItemBlock {
    public DecorPropItem(JSGDecorPropBlock block) {
        super(block);
        setRegistryName(Objects.requireNonNull(block.getRegistryName()));
        setHasSubtypes(true);
    }

    @Nonnull
    @Override
    public final String getUnlocalizedName(ItemStack stack) {
        return "prop.jsg." + JSGDecorPropBlock.BASE + PropVariants.byId(block.getStateFromMeta(stack.getMetadata()).getValue(JSGProps.PROP_VARIANT)).name + JSGDecorPropBlock.END;
    }

    public enum PropVariants {
        ABYDOS_POT(
                0,
                "abydos_pot",
                ElementEnum.DECOR_ABYDOS_POT
        );

        public final int id;
        public final String name;
        public final ElementEnum[] models;

        PropVariants(int id, String name, ElementEnum... models) {
            this.id = id;
            this.name = name;
            this.models = models;
        }

        public static PropVariants byId(int id) {
            for (PropVariants variant : PropVariants.values()) {
                if (variant.id == id) return variant;
            }
            return ABYDOS_POT;
        }
    }
}
