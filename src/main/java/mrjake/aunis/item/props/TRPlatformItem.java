package mrjake.aunis.item.props;

import mrjake.aunis.block.props.TRPlatformBlock;
import mrjake.aunis.loader.ElementEnum;
import mrjake.aunis.sound.SoundEventEnum;
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
        SHIPS_PLATFORM(
                0,
                "earth",
                ElementEnum.PLATFORM_SHIPS_MOVING,
                ElementEnum.PLATFORM_SHIPS_BASE,
                SoundEventEnum.RINGS_PLATFORM_SHIPS_OPEN,
                SoundEventEnum.RINGS_PLATFORM_SHIPS_CLOSE
        );

        public int id;
        public String name;
        public ElementEnum modelMoving;
        public ElementEnum modelBase;
        public SoundEventEnum openingSound;
        public SoundEventEnum closingSound;

        TRPlatformVariants(int meta, String name, ElementEnum modelMoving, ElementEnum modelBase, SoundEventEnum openingSound, SoundEventEnum closingSound) {
            this.id = meta;
            this.name = name;
            this.modelMoving = modelMoving;
            this.modelBase = modelBase;
            this.openingSound = openingSound;
            this.closingSound = closingSound;
        }

        public static TRPlatformVariants byId(int id) {
            for (TRPlatformVariants variant : TRPlatformVariants.values()) {
                if (variant.id == id) return variant;
            }
            return SHIPS_PLATFORM;
        }
    }
}
