package tauri.dev.jsg.config.ingame;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JSGTileEntityConfig {

    private List<JSGConfigOption> options;

    public JSGTileEntityConfig() {
        this.options = new ArrayList<>();
    }

    public JSGTileEntityConfig(ByteBuf buf) {
        this.fromBytes(buf);
    }

    public void addOptions(@Nonnull JSGConfigOption... options){
        this.options.addAll(Arrays.asList(options));
    }

    public void addOption(@Nonnull JSGConfigOption options){
        this.options.add(options);
    }

    public List<JSGConfigOption> getOptions() {
        return options;
    }

    public JSGConfigOption getOption(int id){
        if(id < options.size())
            return options.get(id);
        return new JSGConfigOption(id).setLabel("error while getting option! (" + id + ")").setComment("").setType(JSGConfigOptionTypeEnum.TEXT).setValue("");
    }

    public void clearOptions(){
        options.clear();
    }

    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("Size", options.size());
        for (int i = 0; i < options.size(); i++) {
            compound.setTag("Option" + i, options.get(i).serializeNBT());
        }
        return compound;
    }

    public void deserializeNBT(NBTTagCompound compound) {
        int size = compound.getInteger("Size");
        options.clear();
        for (int i = 0; i < size; i++) {
            options.add(new JSGConfigOption(compound.getCompoundTag("Option" + i)));
        }
    }

    public void toBytes(ByteBuf buf){
        buf.writeInt(options.size());
        for (JSGConfigOption option : options) {
            option.toBytes(buf);
        }
    }

    public void fromBytes(ByteBuf buf) {
        this.options = new ArrayList<>();
        int size = buf.readInt();
        for(int i = 0; i < size; i++){
            options.add(new JSGConfigOption(buf));
        }
    }
}
