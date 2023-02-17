package tauri.dev.jsg.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.creativetabs.JSGCreativeTabsHandler;
import tauri.dev.jsg.sound.SoundEventEnum;

import javax.annotation.Nonnull;

public class CustomDiscsRegistry {
    public static class JSGItemRecord extends ItemRecord {
        protected JSGItemRecord(String name, SoundEvent soundIn) {
            super(name, soundIn);
            setCreativeTab(JSGCreativeTabsHandler.JSG_RECORDS);
            setUnlocalizedName(JSG.MOD_ID + "." + name);
            setRegistryName(new ResourceLocation(JSG.MOD_ID, name));
        }

        @Nonnull
        @Override
        @SideOnly(Side.CLIENT)
        public String getRecordNameLocal() {
            return I18n.format(getUnlocalizedName() + ".name");
        }
    }


    public static final Item RECORD_DESTINY = new JSGItemRecord("record_destiny", SoundEventEnum.RECORD_DESTINY.soundEvent);
    public static final Item RECORD_ATLANTIS = new JSGItemRecord("record_atlantis", SoundEventEnum.RECORD_ATLANTIS.soundEvent);
    public static final Item RECORD_ORIGINS = new JSGItemRecord("record_origins", SoundEventEnum.RECORD_ORIGINS.soundEvent);
    public static final Item RECORD_SGC = new JSGItemRecord("record_sgc", SoundEventEnum.RECORD_SGC.soundEvent);
    public static final Item RECORD_ELEVATOR = new JSGItemRecord("record_elevator", SoundEventEnum.RECORD_ELEVATOR.soundEvent);

    public static final Item[] RECORDS = {
            RECORD_DESTINY,
            RECORD_ATLANTIS,
            RECORD_ORIGINS,
            RECORD_SGC,
            RECORD_ELEVATOR
    };

    public static Item[] getRecordItems() {
        return RECORDS;
    }
}
