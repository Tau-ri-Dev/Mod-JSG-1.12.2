package mrjake.aunis.item.props;

import mrjake.aunis.Aunis;
import mrjake.aunis.block.props.TRPlatformBlock;
import mrjake.aunis.loader.ElementEnum;
import mrjake.aunis.util.main.AunisProps;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Objects;

public class TRPlatformItem extends ItemBlock {
    public TRPlatformItem(TRPlatformBlock block) {
        super(block);
        setRegistryName(Objects.requireNonNull(block.getRegistryName()));
        setHasSubtypes(true);
    }

    @Nonnull
    @Override
    public final String getUnlocalizedName(ItemStack stack) {
        return "prop.aunis." + TRPlatformBlock.BASE + TRPlatformVariants.byId(block.getStateFromMeta(stack.getMetadata()).getValue(AunisProps.PROP_VARIANT)).name + TRPlatformBlock.END;
    }

    public enum TRPlatformVariants {
        GOAULD_BASIC(0, "goauld", ElementEnum.PLATFORM_RINGS_GOAULD_BASIC, null);

        public int id;
        public String name;
        public ElementEnum modelMoving;
        public ElementEnum modelBase;

        TRPlatformVariants(int meta, String name, ElementEnum modelMoving, ElementEnum modelBase) {
            this.id = meta;
            this.name = name;
            this.modelMoving = modelMoving;
            this.modelBase = modelBase;
        }

        @Nonnull
        public static TRPlatformVariants byId(int id) {
            for (TRPlatformVariants variant : TRPlatformVariants.values()) {
                if (variant.id == id) return variant;
            }
            return TRPlatformVariants.GOAULD_BASIC;
        }
    }
}
