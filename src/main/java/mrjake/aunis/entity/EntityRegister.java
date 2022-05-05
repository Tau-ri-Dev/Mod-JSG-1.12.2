package mrjake.aunis.entity;

import mrjake.aunis.Aunis;
import mrjake.aunis.entity.friendly.TokraEntity;
import mrjake.aunis.sound.AunisSoundHelper;
import mrjake.aunis.sound.SoundEventEnum;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class EntityRegister {

    public static final int TOKRA_ID = 120;

    public static void registerEntities() {
        registerEntity("tokra", TokraEntity.class, TOKRA_ID, 50, 11583869, 0);
    }

    private static void registerEntity(String entityName, Class<? extends Entity> entityClass, int id, int trackingRange, int spawnEggColor1, int spawnEggColor2) {
        EntityRegistry.registerModEntity(new ResourceLocation(Aunis.MOD_ID, entityName), entityClass, entityName, id, Aunis.instance, trackingRange, 1, true, spawnEggColor1, spawnEggColor2);
    }

    public static void playSoundEvent(SoundEventEnum sound, Entity entity) {
        BlockPos pos = new BlockPos(entity.posX, entity.posY, entity.posZ);
        AunisSoundHelper.playSoundEvent(entity.world, pos, sound);
    }
}
