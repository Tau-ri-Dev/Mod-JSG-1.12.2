package tauri.dev.jsg.item.props;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import tauri.dev.jsg.block.props.JSGDecorPropBlock;
import tauri.dev.jsg.loader.ElementEnum;
import tauri.dev.jsg.util.main.JSGProps;
import tauri.dev.vector.Vector3f;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@SuppressWarnings("deprecation")
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

    public static class PropModel {
        public final ElementEnum element;
        public final Vector3f translation;
        public final float size;

        @ParametersAreNonnullByDefault
        public PropModel(ElementEnum element, Vector3f translation, float size) {
            this.element = element;
            this.translation = translation;
            this.size = size;
        }
    }

    public enum PropVariants {
        ABYDOS_POT(
                0,
                "abydos_pot",
                new PropModel(ElementEnum.DECOR_ABYDOS_POT, new Vector3f(0, 0, 0), 1)
        );

        public final int id;
        public final String name;
        public final PropModel[] models;

        @ParametersAreNonnullByDefault
        PropVariants(int id, String name, PropModel... models) {
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
