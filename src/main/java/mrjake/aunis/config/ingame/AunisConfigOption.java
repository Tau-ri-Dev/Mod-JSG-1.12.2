package mrjake.aunis.config.ingame;

import io.netty.buffer.ByteBuf;
import mrjake.aunis.Aunis;
import mrjake.aunis.gui.element.ModeButton;
import mrjake.aunis.gui.element.NumberOnlyTextField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AunisConfigOption {
    public int id;
    public List<String> comment = new ArrayList<>();
    public AunisConfigOptionTypeEnum type = AunisConfigOptionTypeEnum.TEXT;
    public String value = "";
    public String defaultValue = "";
    private String label = "";

    private int minInt = -1;
    private int maxInt = -1;

    public AunisConfigOption(int id) {
        this.id = id;
    }

    public AunisConfigOption(NBTTagCompound compound) {
        this.deserializeNBT(compound);
    }

    public AunisConfigOption(ByteBuf buf) {
        this.fromBytes(buf);
    }

    private static final int X = -25;

    public GuiTextField createField(int y) {
        int componentId = id + 100;
        GuiTextField field = null;
        if (this.type == AunisConfigOptionTypeEnum.NUMBER)
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
        if (this.type == AunisConfigOptionTypeEnum.BOOLEAN)
            button = new ModeButton(
                    componentId, X, y, 16, new ResourceLocation(Aunis.ModID, textureBase + "boolean_modes.png"),
                    32, 32, 2
            );

        if(button != null)
            button.setCurrentState(this.getIntValue());
        return button;
    }

    public String getLabel() {
        return label;
    }

    public AunisConfigOption setLabel(String label) {
        this.label = label;
        return this;
    }

    public List<String> getCommentToRender() {
        List<String> c = new ArrayList<>(getComment());
        if(maxInt != -1 || minInt != -1){
            c.add("---------------------------------");
            if(minInt != -1)
                c.add("Min: " + minInt);
            if(maxInt != -1)
                c.add("Max: " + maxInt);
            c.add("---------------------------------");
        }
        return c;
    }

    public List<String> getComment() {
        return comment;
    }

    public AunisConfigOption setComment(String... comment) {
        this.comment = Arrays.asList(comment);
        return this;
    }

    public AunisConfigOption setType(AunisConfigOptionTypeEnum type) {
        this.type = type;
        return this;
    }

    public AunisConfigOption setValue(String value) {
        if (this.type == AunisConfigOptionTypeEnum.NUMBER)
            return this.setIntValue(value);
        else if (this.type == AunisConfigOptionTypeEnum.BOOLEAN)
            return this.setBooleanValue(value);
        return this.setStringValue(value);
    }

    public AunisConfigOption setDefaultValue(String value) {
        this.defaultValue = value;
        return this;
    }

    public AunisConfigOption setMinInt(int value){
        this.minInt = value;
        return this;
    }

    public AunisConfigOption setMaxInt(int value){
        this.maxInt = value;
        return this;
    }

    public boolean getBooleanValue() {
        if (this.value == null) return false;
        return this.value.equals("true");
    }

    private AunisConfigOption setBooleanValue(String value) {
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
        if(getDefault)
            v = defaultValue;

        if (v == null) return -1;
        try {
            return Integer.parseInt(v);
        } catch (Exception e) {
            if(v.equals("true"))
                return 1;
            if(v.equals("false"))
                return 0;
            return -1;
        }
    }

    private AunisConfigOption setIntValue(String value) {
        try {
            int i = Integer.parseInt(value);
            if((maxInt == -1 || i <= maxInt) && (minInt == -1 || i >= minInt))
                this.value = i + "";
        } catch (Exception ignored) {
        }
        return this;
    }

    public String getStringValue() {
        return this.value;
    }

    private AunisConfigOption setStringValue(String value) {
        this.value = value;
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
        this.type = AunisConfigOptionTypeEnum.byId(compound.getInteger("type"));
        this.value = compound.getString("value");
        this.minInt = compound.getInteger("minInt");
        this.maxInt = compound.getInteger("maxInt");
        this.defaultValue = compound.getString("defaultValue");
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
        this.type = AunisConfigOptionTypeEnum.byId(buf.readInt());
        int valueSize = buf.readInt();
        this.value = buf.readCharSequence(valueSize, StandardCharsets.UTF_8).toString();
        this.minInt = buf.readInt();
        this.maxInt = buf.readInt();
        int defaultValueSize = buf.readInt();
        this.defaultValue = buf.readCharSequence(defaultValueSize, StandardCharsets.UTF_8).toString();
    }
}
