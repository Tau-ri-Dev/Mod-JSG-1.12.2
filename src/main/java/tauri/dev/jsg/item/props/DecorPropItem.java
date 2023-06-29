package tauri.dev.jsg.item.props;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import tauri.dev.jsg.block.props.JSGDecorPropBlock;
import tauri.dev.jsg.loader.ElementEnum;
import tauri.dev.jsg.tileentity.props.DecorPropTile;
import tauri.dev.jsg.util.main.JSGProps;
import tauri.dev.vector.Vector3f;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

    @SuppressWarnings("unused")
    public static abstract class PropModelRenderFunction {
        public void runOnServer(World world, PropVariants propVariant, DecorPropTile te) {
        }

        public void runOnClient(World world, PropVariants propVariant, DecorPropTile te) {
        }

        public void runOnRender(World world, PropVariants propVariant, DecorPropTile te) {
        }
    }

    public enum PropVariants {
        ABYDOS_POT(
                0,
                "abydos_pot",
                null,
                new PropModel(ElementEnum.DECOR_ABYDOS_POT, new Vector3f(-0.5f, 0, -0.5f), 1)
        ),
        ABYDOS_LAMP_OFF(
                1,
                "abydos_lamp_off",
                null,
                new PropModel(ElementEnum.DECOR_ABYDOS_LAMP, new Vector3f(-0.5f, 0, -0.5f), 1)
        ),
        ABYDOS_LAMP_ON(
                2,
                "abydos_lamp_on",
                new PropModelRenderFunction() {
                    @Override
                    public void runOnRender(World world, PropVariants propVariant, DecorPropTile te) {
                        if (world.getTotalWorldTime() % 5 == 0) {
                            world.spawnParticle(EnumParticleTypes.FLAME, te.getPos().getX() + 0.35 + Math.random() * 0.3, te.getPos().getY() + 1.8, te.getPos().getZ() + 0.35 + Math.random() * 0.3, 0, 0.01, 0);
                        }
                    }
                },
                0.7f,
                new PropModel(ElementEnum.DECOR_ABYDOS_LAMP, new Vector3f(-0.5f, 0, -0.5f), 1)
        );

        public final int id;
        public final float light;
        public final String name;
        public final PropModel[] models;
        public final PropModelRenderFunction runnableWhileRendering;

        @ParametersAreNonnullByDefault
        PropVariants(int id, String name, @Nullable PropModelRenderFunction runnableWhileRendering, PropModel... models) {
            this(id, name, runnableWhileRendering, 0, models);
        }
        PropVariants(int id, String name, @Nullable PropModelRenderFunction runnableWhileRendering, float light, PropModel... models) {
            this.id = id;
            this.light = light;
            this.name = name;
            this.models = models;
            this.runnableWhileRendering = runnableWhileRendering;
        }

        public static PropVariants byId(int id) {
            for (PropVariants variant : PropVariants.values()) {
                if (variant.id == id) return variant;
            }
            return ABYDOS_POT;
        }
    }
}
