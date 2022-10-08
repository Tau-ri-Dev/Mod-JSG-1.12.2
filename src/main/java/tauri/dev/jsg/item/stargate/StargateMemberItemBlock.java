package tauri.dev.jsg.item.stargate;

import tauri.dev.jsg.util.main.JSGProps;
import tauri.dev.jsg.block.stargate.StargateAbstractMemberBlock;
import tauri.dev.jsg.stargate.EnumMemberVariant;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public abstract class StargateMemberItemBlock extends ItemBlock {
    public StargateMemberItemBlock(StargateAbstractMemberBlock block) {
        super(block);
        setRegistryName(block.getRegistryName());
        setHasSubtypes(true);
    }

    protected abstract String getRingUnlocalizedName();

    protected abstract String getChevronUnlocalizedName();

    @Override
    public final String getUnlocalizedName(ItemStack stack) {
        @SuppressWarnings("deprecation")
		EnumMemberVariant variant = block.getStateFromMeta(stack.getMetadata()).getValue(JSGProps.MEMBER_VARIANT);
        
        switch (variant) {
            case CHEVRON:
                return getChevronUnlocalizedName();

            case RING:
                return getRingUnlocalizedName();

            default:
                return stack.getUnlocalizedName();
        }
    }
}
