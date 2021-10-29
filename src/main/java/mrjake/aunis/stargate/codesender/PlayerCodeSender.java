package mrjake.aunis.stargate.codesender;

import mrjake.aunis.item.AunisItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.world.World;

import java.util.UUID;

/**
 * @author matousss
 */
public class PlayerCodeSender extends CodeSender {

    public UUID senderUUID;
    private World world;

    /**
     * @param args Require :World: on first position
     * */
    @Override
    public void prepareToLoad(Object[] args) {
        world = (World) args[0];
    }

    public PlayerCodeSender(EntityPlayer player) {
        this.senderUUID = player.getUniqueID();
        world = player.world;
    }

    public PlayerCodeSender() {

    }

    @Override
    public void sendMessage(TextComponentBase message) {
        EntityPlayer player = getPlayer();
        if (player != null) {
            player.sendStatusMessage(message, true);
        }
    }
    //todo check if the gate is same
    @Override
    public boolean canReceiveMessage() {
        EntityPlayer player = getPlayer();
        if (player == null) return false;
        ItemStack gdo = player.getHeldItemMainhand();
        if (gdo.isEmpty() || gdo.getItem() != AunisItems.GDO) {
            gdo = player.getHeldItemOffhand();
            if (gdo.isEmpty() || gdo.getItem() != AunisItems.GDO || !gdo.hasTagCompound()) return false;
        }
        return (gdo.getTagCompound().hasKey("linkedGate"));
    }

    @Override
    public CodeSenderType getType() {
        return CodeSenderType.PLAYER;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setUniqueId("sender_uuid", senderUUID);
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        senderUUID = nbt.getUniqueId("codeSender");

    }

    private EntityPlayer getPlayer() {
        if (senderUUID == null) return null;
        return world.getPlayerEntityByUUID(senderUUID);
    }
}
