package tauri.dev.jsg.config.ingame;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.gui.element.EnumButton;
import tauri.dev.jsg.gui.element.ModeButton;
import tauri.dev.jsg.gui.element.NumberOnlyTextField;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JSGConfigOption {
    public int id;
    public List<String> comment = new ArrayList<>();
    public List<JSGConfigEnumEntry> possibleValues = new ArrayList<>();
    public JSGConfigOptionTypeEnum type = JSGConfigOptionTypeEnum.TEXT;
    public String value = "";
    public String defaultValue = "";
    private String label = "";

    private int minInt = -1;
    private int maxInt = -1;

    public JSGConfigOption(int id) {
        this.id = id;
    }

    public JSGConfigOption(NBTTagCompound compound) {
        this.deserializeNBT(compound);
    }

    public JSGConfigOption(ByteBuf buf) {
        this.fromBytes(buf);
    }

    protected static final int X = -25;


    @SideOnly(Side.CLIENT)
    public GuiTextField createField(int y) {
        int componentId = id + 100;
        GuiTextField field;
        if (this.type == JSGConfigOptionTypeEnum.NUMBER)
            field = new NumberOnlyTextField(componentId, Minecraft.getMinecraft().fontRenderer, X, y, 90, 15);
        else
            field = new GuiTextField(componentId, Minecraft.getMinecraft().fontRenderer, X, y, 90, 15);

        field.setText(this.value);
        return field;
    }

    public ModeButton createButton(int y) {
        int componentId = id + 100;
        String textureBase = "textures/gui/config/";
        ModeButton button = null;
        if (this.type == JSGConfigOptionTypeEnum.BOOLEAN)
            button = new ModeButton(
                    componentId, X, y, 16, new ResourceLocation(JSG.MOD_ID, textureBase + "boolean_modes.png"),
                    32, 32, 2
            );
        if (this.type == JSGConfigOptionTypeEnum.SWITCH)
            button = new EnumButton(componentId, X, y, possibleValues);

        if (button != null)
            button.setCurrentState(this.getIntValue());
        return button;
    }

    public String getLabel() {
        return label;
    }

    public JSGConfigOption setLabel(String label) {
        this.label = label;
        return this;
    }

    public List<String> getCommentToRender() {
        List<String> c = new ArrayList<>(getComment());
        if (maxInt != -1 || minInt != -1) {
            c.add("---------------------------------");
            if (minInt != -1)
                c.add("Min: " + minInt);
            if (maxInt != -1)
                c.add("Max: " + maxInt);
            c.add("---------------------------------");
        }
        return c;
    }

    public List<String> getComment() {
        return comment;
    }

    public JSGConfigOption setComment(String... comment) {
        this.comment = Arrays.asList(comment);
        return this;
    }

    public JSGConfigOption setType(JSGConfigOptionTypeEnum type) {
        this.type = type;
        return this;
    }

    public JSGConfigOption setValue(String value) {
        if (this.type == JSGConfigOptionTypeEnum.NUMBER || this.type == JSGConfigOptionTypeEnum.SWITCH)
            return this.setIntValue(value);
        else if (this.type == JSGConfigOptionTypeEnum.BOOLEAN)
            return this.setBooleanValue(value);
        return this.setStringValue(value);
    }

    public JSGConfigOption setPossibleValues(List<JSGConfigEnumEntry> values) {
        if(this.type != JSGConfigOptionTypeEnum.SWITCH){
            JSG.error("Can not set values of config option!");
            JSG.error("Option is not SWITCH type!");
            return this;
        }
        return this.setEnumValues(values);
    }

    public JSGConfigOption setDefaultValue(String value) {
        this.defaultValue = value;
        return this;
    }

    public JSGConfigOption setMinInt(int value) {
        this.minInt = value;
        return this;
    }

    public JSGConfigOption setMaxInt(int value) {
        this.maxInt = value;
        return this;
    }

    public boolean getBooleanValue() {
        if (this.value == null) return false;
        return this.value.equals("true");
    }

    private JSGConfigOption setBooleanValue(String value) {
        if (value.equals("true") || value.equals("1"))
            this.value = "true";
        else
            this.value = "false";
        return this;
    }

    public int getIntValue() {
        return getIntValue(false);
    }

    public int getIntValue(boolean getDefault) {
        String v = value;
        if (getDefault)
            v = defaultValue;

        if (v == null) return -1;
        try {
            return Integer.parseInt(v);
        } catch (Exception e) {
            if (v.equals("true"))
                return 1;
            if (v.equals("false"))
                return 0;
            return -1;
        }
    }

    private JSGConfigOption setIntValue(String value) {
        try {
            int i = Integer.parseInt(value);
            if ((maxInt == -1 || i <= maxInt) && (minInt == -1 || i >= minInt))
                this.value = i + "";
        } catch (Exception ignored) {
        }
        return this;
    }

    public String getStringValue() {
        return this.value;
    }

    private JSGConfigOption setStringValue(String value) {
        this.value = value;
        return this;
    }

    @Nullable
    public JSGConfigEnumEntry getEnumValue(){
        if(possibleValues.size() >= getIntValue()) return null;
        return possibleValues.get(getIntValue());
    }

    private JSGConfigOption setEnumValues(List<JSGConfigEnumEntry> entries){
        this.possibleValues = entries;
        return this;
    }

    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("id", id);
        compound.setString("label", label);
        compound.setInteger("commentSize", comment.size());
        for (int i = 0; i < comment.size(); i++) {
            compound.setString("comment" + i, comment.get(i));
        }
        compound.setInteger("type", type.id);
        compound.setString("value", value);
        compound.setInteger("minInt", minInt);
        compound.setInteger("maxInt", maxInt);
        compound.setString("defaultValue", defaultValue);
        compound.setInteger("possibleValuesLength", possibleValues.size());
        int i = 0;
        for(JSGConfigEnumEntry e : possibleValues){
            compound.setString("possibleValue" + i, e.value);
            compound.setString("possibleValueName" + i++, e.name);
        }

        return compound;
    }

    public void deserializeNBT(NBTTagCompound compound) {
        this.id = compound.getInteger("id");
        this.label = compound.getString("label");
        int size = compound.getInteger("commentSize");
        comment.clear();
        for (int i = 0; i < size; i++) {
            comment.add(compound.getString("comment" + i));
        }
        this.type = JSGConfigOptionTypeEnum.byId(compound.getInteger("type"));
        this.value = compound.getString("value");
        this.minInt = compound.getInteger("minInt");
        this.maxInt = compound.getInteger("maxInt");
        this.defaultValue = compound.getString("defaultValue");
        this.possibleValues = new ArrayList<>();
        int s = compound.getInteger("possibleValuesLength");
        for(int i = 0; i < s; i++){
            possibleValues.add(new JSGConfigEnumEntry(compound.getString("possibleValueName" + i), compound.getString("possibleValue" + i)));
        }
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(id);
        buf.writeInt(label.length());
        buf.writeCharSequence(label, StandardCharsets.UTF_8);
        buf.writeInt(comment.size());
        for (String com : comment) {
            buf.writeInt(com.length());
            buf.writeCharSequence(com, StandardCharsets.UTF_8);
        }
        buf.writeInt(type.id);
        buf.writeInt(value.length());
        buf.writeCharSequence(value, StandardCharsets.UTF_8);
        buf.writeInt(minInt);
        buf.writeInt(maxInt);
        buf.writeInt(defaultValue.length());
        buf.writeCharSequence(defaultValue, StandardCharsets.UTF_8);

        buf.writeInt(possibleValues.size());
        for(JSGConfigEnumEntry e : possibleValues){
            buf.writeInt(e.value.length());
            buf.writeCharSequence(e.value, StandardCharsets.UTF_8);
            buf.writeInt(e.name.length());
            buf.writeCharSequence(e.name, StandardCharsets.UTF_8);
        }
    }

    public void fromBytes(ByteBuf buf) {
        this.id = buf.readInt();
        int labelSize = buf.readInt();
        this.label = buf.readCharSequence(labelSize, StandardCharsets.UTF_8).toString();
        int commentsSize = buf.readInt();
        comment.clear();
        for (int i = 0; i < commentsSize; i++) {
            int x = buf.readInt();
            comment.add(buf.readCharSequence(x, StandardCharsets.UTF_8).toString());
        }
        this.type = JSGConfigOptionTypeEnum.byId(buf.readInt());
        int valueSize = buf.readInt();
        this.value = buf.readCharSequence(valueSize, StandardCharsets.UTF_8).toString();
        this.minInt = buf.readInt();
        this.maxInt = buf.readInt();
        int defaultValueSize = buf.readInt();
        this.defaultValue = buf.readCharSequence(defaultValueSize, StandardCharsets.UTF_8).toString();

        int s = buf.readInt();
        for(int i = 0; i < s; i++){
            int valueSize2 = buf.readInt();
            String value = buf.readCharSequence(valueSize2, StandardCharsets.UTF_8).toString();
            int nameSize = buf.readInt();
            String name = buf.readCharSequence(nameSize, StandardCharsets.UTF_8).toString();
            possibleValues.add(new JSGConfigEnumEntry(name, value));
        }
    }
}
