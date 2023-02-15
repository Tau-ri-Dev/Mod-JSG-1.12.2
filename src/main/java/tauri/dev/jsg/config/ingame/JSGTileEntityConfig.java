package tauri.dev.jsg.config.ingame;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class JSGTileEntityConfig {

    private List<JSGConfigOption> options;

    public JSGTileEntityConfig() {
        this.options = new ArrayList<>();
    }

    public JSGTileEntityConfig(ByteBuf buf) {
        this.fromBytes(buf);
    }

    public void addOption(@Nonnull JSGConfigOption option) {
        this.options.add(option);
    }

    public List<JSGConfigOption> getOptions() {
        return options;
    }

    @Nonnull
    public JSGConfigOption getOption(int id) {
        return Objects.requireNonNull(getOption(id, false));
    }

    @Nullable
    public JSGConfigOption getOption(int id, boolean canBeNull) {
        if (id < options.size())
            return options.get(id);
        if (canBeNull) return null;
        return new JSGConfigOption(id).setLabel("error while getting option! (" + id + ")").setComment("").setType(JSGConfigOptionTypeEnum.TEXT).setValue("");
    }

    public void clearOptions() {
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

    public void toBytes(ByteBuf buf) {
        buf.writeInt(options.size());
        for (JSGConfigOption option : options) {
            option.toBytes(buf);
        }
    }

    public void fromBytes(ByteBuf buf) {
        this.options = new ArrayList<>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            options.add(new JSGConfigOption(buf));
        }
    }

    // Static
    public static void initConfig(JSGTileEntityConfig config, ITileConfigEntry[] entries) {
        initConfig(config, Arrays.asList(entries));
    }

    public static void initConfig(JSGTileEntityConfig config, List<ITileConfigEntry> entries) {
        if (config.getOptions().size() != entries.size()) {
            //config.clearOptions();
            for (ITileConfigEntry option : entries) {
                if(config.getOption(option.getId(), true) != null) continue;
                JSGConfigOption optionNew = new JSGConfigOption(option.getId()).setType(option.getType());

                if (option.getType() == JSGConfigOptionTypeEnum.SWITCH)
                    optionNew.setPossibleValues(option.getPossibleValues());

                optionNew.setLabel(option.getLabel())
                        .setValue(option.getDefaultValue())
                        .setDefaultValue(option.getDefaultValue())
                        .setMinInt(option.getMin())
                        .setMaxInt(option.getMax())
                        .setComment(option.getComment());

                config.addOption(optionNew);
            }
        }
        // setup config option when there is a change (by config for example)
        for (ITileConfigEntry option : entries) {
            JSGConfigOption o = config.getOption(option.getId(), true);
            if (o == null) continue;

            if (o.type != option.getType()) o.setType(option.getType());
            if (!Objects.equals(o.defaultValue, option.getDefaultValue())) o.setDefaultValue(option.getDefaultValue());
            if (!Objects.equals(o.getLabel(), option.getLabel())) o.setLabel(option.getLabel());
            if (!o.getComment().equals(Arrays.asList(option.getComment()))) o.setComment(option.getComment());

            o.setMaxInt(option.getMax());
            o.setMinInt(option.getMin());

            if (option.getType() != JSGConfigOptionTypeEnum.SWITCH) continue;
            if (option.getPossibleValues().size() == o.possibleValues.size()) continue;
            o.setPossibleValues(option.getPossibleValues()).setDefaultValue(option.getDefaultValue()).setValue(option.getDefaultValue());
        }
    }
}
