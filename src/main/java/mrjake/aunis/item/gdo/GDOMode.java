package mrjake.aunis.item.gdo;

import mrjake.aunis.Aunis;
import mrjake.aunis.block.AunisBlocks;
import mrjake.aunis.util.EnumKeyInterface;
import mrjake.aunis.util.EnumKeyMap;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public enum GDOMode implements EnumKeyInterface<Byte> {
    CODE_SENDER(0, "item.aunis.gdo.code_sender", true, "linkedGate",
            "nearby"),
    OC(1, "item.aunis.gdo.mode_oc", false, null, "ocmess");

    public final byte id;
    public final String translationKey;
    public final boolean linkable;
    public final String tagPosName;
    public final String tagListName;

    private GDOMode(int id, String translationKey, boolean linkable, String tagPosName, String tagListName) {
        this.id = (byte) id;
        this.translationKey = translationKey;
        this.linkable = linkable;
        this.tagPosName = tagPosName;
        this.tagListName = tagListName;

    }

    public GDOMode next() {
        switch (this) {
            case CODE_SENDER:
                return Aunis.ocWrapper.isModLoaded() ? OC : CODE_SENDER;
            case OC:
                return CODE_SENDER;


        }
        return null;
    }

    public GDOMode prev() {
        switch (this) {
            case CODE_SENDER:
                return Aunis.ocWrapper.isModLoaded() ? OC : CODE_SENDER;
            case OC:
                return CODE_SENDER;
        }

        return null;
    }

    @SideOnly(Side.CLIENT)
    public String localize() {
        return I18n.format(translationKey);
    }

    @Override
    public Byte getKey() {
        return id;
    }

    private static final EnumKeyMap<Byte, GDOMode> ID_MAP = new EnumKeyMap<Byte, GDOMode>(values());

    public static GDOMode valueOf(byte id) {
        return ID_MAP.valueOf(id);
    }
}