package mrjake.aunis.stargate.teleportation;

import mrjake.aunis.AunisDamageSources;
import mrjake.aunis.AunisProps;
import mrjake.aunis.api.event.StargateTeleportEntityEvent;
import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.item.UpgradeIris;
import mrjake.aunis.packet.AunisPacketHandler;
import mrjake.aunis.packet.stargate.StargateMotionToClient;
import mrjake.aunis.sound.AunisSoundHelper;
import mrjake.aunis.sound.SoundEventEnum;
import mrjake.aunis.stargate.network.StargatePos;
import mrjake.aunis.tileentity.stargate.StargateAbstractBaseTile;
import mrjake.aunis.tileentity.stargate.StargateClassicBaseTile;
import mrjake.aunis.util.AunisAxisAlignedBB;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.vecmath.Vector2f;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static mrjake.aunis.item.AunisItems.UPGRADE_IRIS;

public class EventHorizon {
    private World world;
    private BlockPos pos;
    private BlockPos gateCenter;

    private AunisAxisAlignedBB localBox;
    private AunisAxisAlignedBB globalBox;
    public static Random randomGenerator = new Random();

    public EventHorizon(World world, BlockPos pos, BlockPos gateCenter, EnumFacing facing, AunisAxisAlignedBB localBox) {
        this.world = world;
        this.pos = pos;
        this.gateCenter = gateCenter;

        this.localBox = localBox.rotate(facing).offset(0.5, 0, 0.5);
        this.globalBox = this.localBox.offset(pos);
    }

    public void reset() {
        scheduledTeleportMap.clear();
    }

    public AunisAxisAlignedBB getLocalBox() {
        return localBox;
    }

    // ------------------------------------------------------------------------
    // Teleporting

    private Map<Integer, TeleportPacket> scheduledTeleportMap = new HashMap<>();

    /**
     * This map is used not to double the teleport packet on Entity's
     * passengers.
     */
    private Map<Integer, Integer> timeoutMap = new HashMap<>();

    public void scheduleTeleportation(StargatePos targetGate) {
        if (world.getTileEntity(pos) instanceof StargateClassicBaseTile && ((StargateClassicBaseTile) world.getTileEntity(pos)).isClosed())
            return;
        List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, globalBox);

//		Aunis.info(globalBox + ": " + entities + ", map: " + scheduledTeleportMap);

//		if (!timeoutMap.isEmpty())
//			Aunis.info("timeoutMap: " + timeoutMap);

        for (int entityId : timeoutMap.keySet())
            timeoutMap.put(entityId, timeoutMap.get(entityId) - 1);
        timeoutMap.entrySet().removeIf(entry -> entry.getValue() < 0);

        for (Entity entity : entities) {
            int entityId = entity.getEntityId();

            if (!scheduledTeleportMap.containsKey(entityId) && !timeoutMap.containsKey(entityId) && !entity.isRiding()) {
                EnumFacing sourceFacing = world.getBlockState(pos).getValue(AunisProps.FACING_HORIZONTAL);
                EnumFacing targetFacing = targetGate.getBlockState().getValue(AunisProps.FACING_HORIZONTAL);

                float rotation = (float) Math.toRadians(EnumFacing.fromAngle(targetFacing.getHorizontalAngle() - sourceFacing.getHorizontalAngle()).getOpposite().getHorizontalAngle());
                TeleportPacket packet = new TeleportPacket(entity, pos, targetGate, rotation);

                if (entity instanceof EntityPlayerMP) {
                    scheduledTeleportMap.put(entityId, packet);
                    AunisPacketHandler.INSTANCE.sendTo(new StargateMotionToClient(pos), (EntityPlayerMP) entity);
                } else {
                    Vector2f motion = new Vector2f((float) entity.motionX, (float) entity.motionZ);

                    if (TeleportHelper.frontSide(sourceFacing, motion)) {

                        for (Entity passenger : entity.getPassengers())
                            timeoutMap.put(passenger.getEntityId(), 40);
                        timeoutMap.put(entityId, 40);

                        scheduledTeleportMap.put(entityId, packet.setMotion(motion));
                        teleportEntity(entityId);
                    }
					/*else {
						//entity.attackEntityFrom(AunisDamageSources.DAMAGE_EVENT_HORIZON, 20);
						//System.out.println("Test!!!");
						// TODO Back side killing
						// Make custom message appear
					}*/
                }
            }
        }
    }

    private void irisKill(Entity e) {
        e.attackEntityFrom(AunisDamageSources.DAMAGE_EVENT_IRIS_CREATIVE, Float.MAX_VALUE);
    }

    public void teleportEntity(int entityId) {
        TeleportPacket packet = scheduledTeleportMap.get(entityId);
        if (!new StargateTeleportEntityEvent((StargateAbstractBaseTile) world.getTileEntity(pos), packet.getTargetGatePos().getTileEntity(), packet.getEntity()).post()) {
            // Not cancelled
            StargatePos targetGatePos = packet.getTargetGatePos();
            if (targetGatePos.getTileEntity() instanceof StargateClassicBaseTile
                    && ((StargateClassicBaseTile) targetGatePos.getTileEntity()).isClosed()) {

                if (packet.getEntity() instanceof IProjectile) {
                    Entity projectile = packet.getEntity();
                    projectile.setVelocity(0, 0, 0);
                    projectile.setDead();


                } else {
                    if (AunisConfig.irisConfig.killAtDestination) {
                        packet.teleport(false);
                    }
                    //packet.getEntity().setDead();
                    if (AunisConfig.irisConfig.allowCreative
                            && packet.getEntity() instanceof EntityPlayer
                            && ((EntityPlayer) packet.getEntity()).capabilities.isCreativeMode) {
                        if (!AunisConfig.irisConfig.killAtDestination) {
                            packet.teleport();
                        }
                    } else {
                        if (!packet.getEntity().getPassengers().isEmpty()) {
                            for (Entity passenger : packet.getEntity().getPassengers()) irisKill(passenger);
                        }
                        irisKill(packet.getEntity());
                    }

                }


                if (((StargateClassicBaseTile) targetGatePos.getTileEntity()).isPhysicalIris()) {
                    AunisSoundHelper.playSoundEvent(packet.getTargetGatePos().getWorld(),
                            targetGatePos.getTileEntity().getGateCenterPos(),
                            SoundEventEnum.IRIS_HIT);
                } else if (((StargateClassicBaseTile) packet.getTargetGatePos().getTileEntity()).isShieldIris()) {
                    AunisSoundHelper.playSoundEvent(packet.getTargetGatePos().getWorld(),
                            targetGatePos.getTileEntity().getGateCenterPos(),
                            SoundEventEnum.SHIELD_HIT);
                }
                ItemStack irisItem = ((StargateClassicBaseTile) targetGatePos.getTileEntity()).getItemHandler().getStackInSlot(11);
                if (irisItem.getItem() instanceof UpgradeIris) {
                    // different damages per source
                    int chance = EnchantmentHelper.getEnchantments(irisItem).containsKey(Enchantments.UNBREAKING) ? (AunisConfig.irisConfig.unbreakingChance * EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, irisItem)) : 0;
                    int random = randomGenerator.nextInt(100);

                    if (random > chance) {
                        UPGRADE_IRIS.setDamage(irisItem, UPGRADE_IRIS.getDamage(irisItem) + 1);
                    }
                    if (irisItem.getCount() == 0) {
                        ((StargateClassicBaseTile) targetGatePos.getTileEntity()).updateIrisType();
                    }
                }
                else {
                    IEnergyStorage energyStorage = targetGatePos.getTileEntity().getCapability(CapabilityEnergy.ENERGY, null);
                    if (energyStorage != null) {
                        energyStorage.extractEnergy(500, false);
                    }
                }
                targetGatePos.getTileEntity().sendSignal(null, "stargate_event_iris_hit", new Object[]{"Something just hit the IRIS!"});

            } else {
                AunisSoundHelper.playSoundEvent(world, gateCenter, SoundEventEnum.WORMHOLE_GO);
                packet.teleport();
            }
        }

        scheduledTeleportMap.remove(entityId);
    }

    public void removeEntity(int entityId) {
        scheduledTeleportMap.remove(entityId);
    }

    public void setMotion(int entityId, Vector2f motionVector) {
        scheduledTeleportMap.get(entityId).setMotion(motionVector);
    }
}
