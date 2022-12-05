package tauri.dev.jsg.entity;

import tauri.dev.jsg.JSG;
import tauri.dev.jsg.entity.friendly.TokraEntity;
import tauri.dev.jsg.sound.JSGSoundHelper;
import tauri.dev.jsg.sound.SoundEventEnum;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class EntityRegister {

    public static final int TOKRA_ID = 120;

    public static final int KINO_ID = 130;

    public static void registerEntities() {
        registerEntity("tokra", TokraEntity.class, TOKRA_ID, 50, 11583869, 0);


        registerEntity("kino", EntityKino.class, KINO_ID, 512, 7105386, 12895428);
    }

    private static void registerEntity(String entityName, Class<? extends Entity> entityClass, int id, int trackingRange, int spawnEggColor1, int spawnEggColor2) {
        EntityRegistry.registerModEntity(new ResourceLocation(JSG.MOD_ID, entityName), entityClass, entityName, id, JSG.instance, trackingRange, 1, true, spawnEggColor1, spawnEggColor2);
    }

    public static void playSoundEvent(SoundEventEnum sound, Entity entity) {
        BlockPos pos = new BlockPos(entity.posX, entity.posY, entity.posZ);
        JSGSoundHelper.playSoundEvent(entity.world, pos, sound);
    }
}
