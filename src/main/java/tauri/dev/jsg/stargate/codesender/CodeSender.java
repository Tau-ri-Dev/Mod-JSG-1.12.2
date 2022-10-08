package tauri.dev.jsg.stargate.codesender;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentBase;
import net.minecraftforge.common.util.INBTSerializable;


/**
 * @author matousss
 */
public abstract class CodeSender implements INBTSerializable<NBTTagCompound> {
    public abstract void sendMessage(TextComponentBase message);
    public boolean canReceiveMessage() {return true;}
    public abstract CodeSenderType getType();
    /**
     * @param args any arguments needed before deserialization
     * */
    public void prepareToLoad(Object[] args) {}

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("type", getType().id);
        return nbt;
    }

}
