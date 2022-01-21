package mrjake.aunis.sound;

import com.google.common.collect.Maps;
import mrjake.aunis.Aunis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;

/**
 * THIS CLASS FILE WAS DOWNLOADED FROM
 * https://forums.minecraftforge.net/topic/42439-adding-additional-soundcategorys/
 * Developers of Aunis are not creators of this class file
 */

public class AunisSoundCategory {
    private static final String SRG_soundLevels = "field_186714_aM";
    private static final String SRG_SOUND_CATEGORIES = "field_187961_k";
    private static Map<SoundCategory, Float> soundLevels;
    private static Map<SoundCategory, Float> SOUND_LEVELS;
    private static final AunisSoundCategory instance = new AunisSoundCategory();

    private AunisSoundCategory() {}

    public static AunisSoundCategory getInstance() {return instance;}

    public static SoundCategory add(String name)
    {
        Map<String, SoundCategory> SOUND_CATEGORIES;

        String constantName = name.toUpperCase().replace(" ", "");
        String referenceName = constantName.toLowerCase();
        SoundCategory soundCategory = EnumHelper.addEnum(SoundCategory.class, constantName, new Class[]{String.class}, new Object[]{referenceName});
        SOUND_CATEGORIES = ObfuscationReflectionHelper.getPrivateValue(SoundCategory.class, SoundCategory.BLOCKS, "SOUND_CATEGORIES", SRG_SOUND_CATEGORIES);

        if (SOUND_CATEGORIES.containsKey(referenceName))
            throw new Error("Clash in Sound Category name pools! Cannot insert " + constantName);

        SOUND_CATEGORIES.put(referenceName, soundCategory);
        if(!SOUND_CATEGORIES.containsKey(referenceName)){
            Aunis.warn("Aunis sound category did not load properly, forcing into Blocks category - 2");
            return SoundCategory.BLOCKS;
        }

        try {
            if (FMLLaunchHandler.side() == Side.CLIENT){
                setSoundLevels();
                SOUND_LEVELS.get(soundCategory);
            }
            return soundCategory;
        }
        catch(Exception e){
            Aunis.warn("Aunis sound category did not load properly, forcing into Blocks category - 1");
            return SoundCategory.BLOCKS;
        }
    }

    /** Game sound level options settings only exist on the client side */
    @SideOnly(Side.CLIENT)
    private static void setSoundLevels()
    {
        /** SoundCategory now contains 'name' sound category so build a new map */
        soundLevels = Maps.newEnumMap(SoundCategory.class);
        /** Replace the map in the GameSettings.class */
        ObfuscationReflectionHelper.setPrivateValue(GameSettings.class, Minecraft.getMinecraft().gameSettings, soundLevels, "soundLevels", SRG_soundLevels);
        SOUND_LEVELS = ObfuscationReflectionHelper.getPrivateValue(GameSettings.class, Minecraft.getMinecraft().gameSettings, "soundLevels", SRG_soundLevels);
    }
}
