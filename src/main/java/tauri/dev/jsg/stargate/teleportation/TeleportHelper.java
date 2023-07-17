package tauri.dev.jsg.stargate.teleportation;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.ITeleporter;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.transportrings.StartPlayerFadeOutToClient;
import tauri.dev.jsg.stargate.network.StargatePos;
import tauri.dev.jsg.tileentity.stargate.StargateAbstractBaseTile;
import tauri.dev.jsg.tileentity.stargate.StargateOrlinBaseTile;
import tauri.dev.vector.Matrix2f;

import javax.vecmath.Vector2f;
import java.util.List;
import java.util.Objects;

public class TeleportHelper {
	
	enum EnumFlipAxis {
		X(0x01),
		Z(0x02);
		
		public final int mask;
		
		EnumFlipAxis(int mask) {
			this.mask = mask;
		}
		
		public static boolean masked(EnumFlipAxis flipAxis, int in) {
			return (in & flipAxis.mask) != 0;
		}
	}
	
	private static void translateTo00(Vector2f center, Vector2f v) {
		v.x -= center.x;
		v.y -= center.y;
	}
	
	public static void rotateAround00(Vector2f v, float rotation, int flip) {
		Matrix2f m = new Matrix2f();
		Matrix2f p = new Matrix2f();
		
		float sin = MathHelper.sin(rotation);
		float cos = MathHelper.cos(rotation);
		
		if ( EnumFlipAxis.masked(EnumFlipAxis.X, flip) )
			v.x *= -1;
		
		if ( EnumFlipAxis.masked(EnumFlipAxis.Z, flip) )
			v.y *= -1;
		
		m.m00 = cos;	m.m10 = -sin;
		m.m01 = sin;	m.m11 =  cos;
		p.m00 = v.x;	p.m10 = 0;
		p.m01 = v.y;	p.m11 = 0;
		
		Matrix2f out = Matrix2f.mul(m, p, null);
		
		v.x = out.m00;
		v.y = out.m01;
	}
	
	private static void translateToDest(Vector2f v, Vector2f dest) {
		v.x += dest.x;
		v.y += (dest.y + 0.5f);
	}
	
	public static void teleportEntity(Entity entity, BlockPos sourceGatePos, StargatePos targetGatePos, float rotation, Vector2f motionVector) {
		List<Entity> passengers = null;
				
		if (entity.isRiding())
			return;
		
		if (entity.isBeingRidden()) {
			passengers = entity.getPassengers();
			entity.removePassengers();
			
			for (Entity passenger : passengers) {
				teleportEntity(passenger, sourceGatePos, targetGatePos, rotation, motionVector);
			}
		}
		
		World world = entity.getEntityWorld();
		int sourceDim = world.provider.getDimension();

		StargateAbstractBaseTile targetTile = targetGatePos.getTileEntity();
		StargateAbstractBaseTile sourceTile = targetTile;
		TileEntity sourceTileEntity = (sourceGatePos == null ? null : world.getTileEntity(sourceGatePos));
		if(sourceTileEntity instanceof StargateAbstractBaseTile){
			sourceTile = (StargateAbstractBaseTile) sourceTileEntity;
		}
		
		int flipAxis = 0;
		
		if (sourceTile.getFacing().getAxis() == targetTile.getFacing().getAxis())
			flipAxis |= EnumFlipAxis.X.mask;
		else
			flipAxis |= EnumFlipAxis.Z.mask;
		
		Vec3d pos;
		BlockPos tPos = targetGatePos.gatePos;
		
		if (sourceTile instanceof StargateOrlinBaseTile)
			pos = new Vec3d(tPos.getX() + 0.5, tPos.getY() + 2.0, tPos.getZ() + 0.5);
		else if (targetTile instanceof StargateOrlinBaseTile)
			pos = new Vec3d(tPos.getX() + 0.5, tPos.getY() + 0.5, tPos.getZ() + 0.5);
		else
			pos = getPosition(entity, sourceTile.getGateCenterPos(), targetTile.getGateCenterPos(), rotation, targetTile.getFacing().getAxis()==Axis.Z ? ~flipAxis : flipAxis);
		
		final float yawRotated = getRotation(entity.isBeingRidden() ? Objects.requireNonNull(entity.getControllingPassenger()) : entity, rotation, flipAxis);
		boolean isPlayer = entity instanceof EntityPlayerMP;
				
		if (sourceDim == targetGatePos.dimensionID) {
			setRotationAndPosition(entity, yawRotated, pos);
			if(isPlayer){
				// Do white flash for the traveller
				JSGPacketHandler.INSTANCE.sendTo(new StartPlayerFadeOutToClient(StartPlayerFadeOutToClient.EnumFadeOutEffectType.STARGATE), (EntityPlayerMP) entity);
			}
		}
		
		else {
	        if (!ForgeHooks.onTravelToDimension(entity, targetGatePos.dimensionID)) return;
			
			final Vec3d posFinal = pos;
			
			ITeleporter teleporter = (world1, entity1, yaw) -> setRotationAndPosition(entity1, yawRotated, posFinal);
			
			if (isPlayer) {
				EntityPlayerMP player = (EntityPlayerMP) entity;
				Objects.requireNonNull(player.getServer()).getPlayerList().transferPlayerToDimension(player, targetGatePos.dimensionID, teleporter);
			}
			
			else {			
				entity = entity.changeDimension(targetGatePos.dimensionID, teleporter);
				if(entity == null) // Entity has been probably despawned
					return;
			}
		}
		
		setMotion(entity, rotation, motionVector);
		
		sourceTile.entityPassing(entity, false);
		targetTile.entityPassing(entity, true);
		
		if (passengers != null) {
			for (Entity passenger : passengers) {
				passenger.startRiding(entity);
			}
		}
	}

	/**
	 * Push player one block forward on teleport.
	 * - debug for backside killing
	 * @param gateRotation rotation of target gate
	 * @param pos position of teleported player -> after teleport
	 * @return Vec3d
	 */
	@Deprecated
	public static Vec3d plusOneBlock(EnumFacing gateRotation, Vec3d pos){
		float k = 1.5f;

		switch (gateRotation) {
			case NORTH:
				return new Vec3d(pos.x, pos.y, pos.z-k);
			case WEST:
				return new Vec3d(pos.x-k, pos.y, pos.z);
			case EAST:
				return new Vec3d(pos.x+k, pos.y, pos.z);
			default:
				return new Vec3d(pos.x, pos.y, pos.z+k);
		}
	}
	
	public static void setRotationAndPosition(Entity entity, float yawRotated, Vec3d pos) {
		entity.rotationYaw = yawRotated;
		entity.setPositionAndUpdate(pos.x, pos.y, pos.z);
		entity.getEntityWorld().updateEntityWithOptionalForce(entity, true);
	}
	
	public static float getRotation(Entity player, float rotation, int flipAxis) {
		Vec3d lookVec = player.getLookVec();
		Vector2f lookVec2f = new Vector2f( (float)(lookVec.x), (float)(lookVec.z) );
		
		rotateAround00(lookVec2f, rotation, flipAxis);
		
		return (float) Math.toDegrees(MathHelper.atan2(lookVec2f.x, lookVec2f.y));		
	}
	
	public static void setMotion(Entity entity, float rotation, Vector2f motionVec2f) {
		if (motionVec2f != null) {		
			rotateAround00(motionVec2f, rotation, 0);

			if(entity != null) {
				entity.motionX = motionVec2f.x;
				entity.motionZ = motionVec2f.y;
				entity.velocityChanged = true;
			}
		}
	}
	
	public static Vec3d getPosition(Entity player, BlockPos sourceGatePos, BlockPos targetGatePos, float rotation, int flipAxis) {
		Vector2f sourceCenter = new Vector2f( sourceGatePos.getX()+0.5f, sourceGatePos.getZ()+0.5f );
		Vector2f destCenter = new Vector2f( targetGatePos.getX()+0.5f, targetGatePos.getZ()+0.5f );
		Vector2f playerPosition = new Vector2f( (float)(player.posX), (float)(player.posZ) );  
		
		translateTo00(sourceCenter, playerPosition);
		rotateAround00(playerPosition, rotation, flipAxis);				
		translateToDest(playerPosition, destCenter);
		
		float y = (float) (targetGatePos.getY() + ( player.posY - sourceGatePos.getY() ));
		return new Vec3d(playerPosition.x, y, playerPosition.y);
	}
	
	public static World getWorld(int dimension) {
		World world = DimensionManager.getWorld(0);
		
		if (dimension == 0)
			return world;

		if(!DimensionManager.isDimensionRegistered(dimension)) return null;
		
		return Objects.requireNonNull(world.getMinecraftServer()).getWorld(dimension);
	}

	public static boolean frontSide(EnumFacing sourceFacing, Vector2f motionVec) {
		Axis axis = sourceFacing.getAxis();
		AxisDirection direction = sourceFacing.getAxisDirection();
		float motion;
		
		if (axis == Axis.X)
			motion = motionVec.x;			
		else
			motion = motionVec.y;
				
		// If facing positive, then player should move negative
		if (direction == AxisDirection.POSITIVE)
			return motion <= 0;
		else
			return motion >= 0;
	}
	
}
