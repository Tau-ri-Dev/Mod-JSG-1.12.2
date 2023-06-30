package tauri.dev.jsg.item.props;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import tauri.dev.jsg.block.JSGBlock;
import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.block.props.AncientSignBlock;
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

    public static class PropAbstractBlock extends JSGBlock {

        public PropAbstractBlock() {
            super(Material.AIR);
        }
    }

    public enum PropVariants {
        ABYDOS_POT(
                0,
                "abydos_pot",
                null,
                new PropAbstractBlock() {
                    @Nonnull
                    @Override
                    public AxisAlignedBB getBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess source, @Nonnull BlockPos pos) {
                        return new AxisAlignedBB(0.3, 0, 0.3, 0.7, 0.7, 0.7);
                    }

                    @Nonnull
                    @Override
                    public AxisAlignedBB getCollisionBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess source, @Nonnull BlockPos pos) {
                        return new AxisAlignedBB(0.3, 0, 0.3, 0.7, 0.7, 0.7);
                    }
                    @Override
                    public boolean renderHighlight(IBlockState state) {
                        return false;
                    }
                },
                new PropModel(ElementEnum.DECOR_ABYDOS_POT, new Vector3f(0, 0, 0), 1)
        ),
        ABYDOS_LAMP_OFF(
                1,
                "abydos_lamp_off",
                null,
                new PropAbstractBlock() {
                    @Nonnull
                    @Override
                    public AxisAlignedBB getBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess source, @Nonnull BlockPos pos) {
                        return new AxisAlignedBB(0.3, 0, 0.3, 0.7, 2, 0.7);
                    }

                    @Nonnull
                    @Override
                    public AxisAlignedBB getCollisionBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess source, @Nonnull BlockPos pos) {
                        return new AxisAlignedBB(0.3, 0, 0.3, 0.7, 2, 0.7);
                    }
                },
                new PropModel(ElementEnum.DECOR_ABYDOS_LAMP, new Vector3f(0, 0, 0), 1)
        ),
        ABYDOS_LAMP_ON(
                2,
                "abydos_lamp_on",
                new PropModelRenderFunction() {
                    @Override
                    public void runOnRender(World world, PropVariants propVariant, DecorPropTile te) {
                        if (world.getTotalWorldTime() % 5 == 0) {
                            if(Math.random() < 0.05f)
                                world.spawnParticle(EnumParticleTypes.FLAME, te.getPos().getX() + 0.35 + Math.random() * 0.3, te.getPos().getY() + 1.82, te.getPos().getZ() + 0.35 + Math.random() * 0.3, 0, 0, 0);
                        }
                    }

                    @Override
                    public void runOnClient(World world, PropVariants propVariant, DecorPropTile te) {
                        if (world.rand.nextInt(24) == 0) {
                            world.playSound(((float) te.getPos().getX() + 0.5F), ((float) te.getPos().getY() + 0.5F), ((float) te.getPos().getZ() + 0.5F), SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 1.0F + world.rand.nextFloat(), world.rand.nextFloat() * 0.7F + 0.3F, false);
                        }
                    }
                },
                ((JSGBlock) new PropAbstractBlock() {
                    @Nonnull
                    @Override
                    public AxisAlignedBB getBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess source, @Nonnull BlockPos pos) {
                        return new AxisAlignedBB(0.3, 0, 0.3, 0.7, 2, 0.7);
                    }

                    @Nonnull
                    @Override
                    public AxisAlignedBB getCollisionBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess source, @Nonnull BlockPos pos) {
                        return new AxisAlignedBB(0.3, 0, 0.3, 0.7, 2, 0.7);
                    }
                }.setLightLevel(0.7f)),
                new PropModel(ElementEnum.DECOR_ABYDOS_LAMP, new Vector3f(0, 0, 0), 1)
        ),
        DESTINY_BEARING_OFF(
                3,
                "destiny_bearing_off",
                null,
                new AncientSignBlock(),
                new PropModel(ElementEnum.DESTINY_BEARING_OFF, new Vector3f(0, 0, 0), 1)
        ),
        DESTINY_BEARING_ON(
                4,
                "destiny_bearing_on",
                null,
                new PropAbstractBlock(),
                new PropModel(ElementEnum.DESTINY_BEARING_ON, new Vector3f(0, 0, 0), 1)
        );

        public final int id;
        public final JSGBlock abstractBlock;
        public final String name;
        public final PropModel[] models;
        public final PropModelRenderFunction runnableWhileRendering;

        PropVariants(int id, String name, @Nullable PropModelRenderFunction runnableWhileRendering, JSGBlock abstractBlock, PropModel... models) {
            this.id = id;
            this.abstractBlock = abstractBlock;
            this.name = name;
            this.models = models;
            this.runnableWhileRendering = runnableWhileRendering;
        }

        public IBlockState getBlockState(){
            return JSGBlocks.DECOR_PROP_BLOCK.getDefaultState().withProperty(JSGProps.PROP_VARIANT, this.id);
        }

        public static PropVariants byId(int id) {
            for (PropVariants variant : PropVariants.values()) {
                if (variant.id == id) return variant;
            }
            return ABYDOS_POT;
        }
    }
}
