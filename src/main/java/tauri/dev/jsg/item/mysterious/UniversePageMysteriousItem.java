package tauri.dev.jsg.item.mysterious;

import tauri.dev.jsg.stargate.network.SymbolTypeEnum;

public class UniversePageMysteriousItem extends AbstractPageMysteriousItem {

    public UniversePageMysteriousItem() {
        super("universe", SymbolTypeEnum.UNIVERSE, 1);
    }

    /*@Override
    public void givePlayerPage(@Nonnull EntityPlayer player, @Nonnull EnumHand hand, GeneratedStargate stargate){
        ItemStack stack = new ItemStack(JSGItems.UNIVERSE_DIALER);
        UniverseDialerItem.initNBT(stack);

        if (stack.getTagCompound() != null) {
            NBTTagList saved = stack.getTagCompound().getTagList("saved", Constants.NBT.TAG_COMPOUND);
            NBTTagCompound compound = stargate.address.serializeNBT();
            compound.setBoolean("hasUpgrade", stargate.hasUpgrade);
            saved.appendTag(compound);
        }

        ItemStack held = player.getHeldItem(hand);
        held.shrink(1);

        if (held.isEmpty())
            player.setHeldItem(hand, stack);

        else {
            player.setHeldItem(hand, held);
            player.addItemStackToInventory(stack);
        }
    }*/
}
