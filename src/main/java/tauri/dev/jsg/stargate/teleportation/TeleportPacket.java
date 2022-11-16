package tauri.dev.jsg.stargate.teleportation;

import net.minecraft.entity.player.EntityPlayerMP;
import tauri.dev.jsg.advancements.JSGAdvancements;
import tauri.dev.jsg.sound.JSGSoundHelper;
import tauri.dev.jsg.sound.SoundEventEnum;
import tauri.dev.jsg.stargate.network.StargatePos;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

import javax.vecmath.Vector2f;

/**
 * Class used for teleporting entities with saving their motion
 * 
 * @author MrJake222
 */
public class TeleportPacket {
	private final BlockPos sourceGatePos;
	private final StargatePos targetGatePos;
	
	private final Entity entity;

	private final float rotation;
	private Vector2f motionVector;
	
	public TeleportPacket(Entity entity, BlockPos source, StargatePos target, float rotation) {
		this.entity = entity;
		this.sourceGatePos = source;
		this.targetGatePos = target;
		
		this.rotation = rotation;
	}
	
	public StargatePos getTargetGatePos() {
		return targetGatePos;
	}
	
	public Entity getEntity() {
		return entity;
	}

	public void teleport() {
		teleport(true);
	}
	public void teleport(boolean playSound) {
		if(entity instanceof EntityPlayerMP)
			JSGAdvancements.WORMHOLE_GO.trigger((EntityPlayerMP) entity);
		TeleportHelper.teleportEntity(entity, sourceGatePos, targetGatePos, rotation, motionVector);
		if (playSound)
			JSGSoundHelper.playSoundEvent(targetGatePos.getWorld(), targetGatePos.getTileEntity().getGateCenterPos(), SoundEventEnum.WORMHOLE_GO);
	}

	public TeleportPacket setMotion(Vector2f motion) {
		this.motionVector = motion;
		
		return this;
	}
}
