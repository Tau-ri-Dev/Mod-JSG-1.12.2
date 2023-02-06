package tauri.dev.jsg.stargate.teleportation;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import tauri.dev.jsg.advancements.JSGAdvancements;
import tauri.dev.jsg.api.event.StargateTeleportEntityEvent;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.item.stargate.UpgradeIris;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.stargate.StargateMotionToClient;
import tauri.dev.jsg.sound.JSGSoundHelper;
import tauri.dev.jsg.sound.SoundEventEnum;
import tauri.dev.jsg.stargate.network.StargatePos;
import tauri.dev.jsg.tileentity.stargate.StargateAbstractBaseTile;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;
import tauri.dev.jsg.util.JSGAdvancementsUtil;
import tauri.dev.jsg.util.JSGAxisAlignedBB;
import tauri.dev.jsg.util.main.JSGDamageSources;
import tauri.dev.jsg.util.main.JSGProps;

import javax.vecmath.Vector2f;
import java.util.*;

import static tauri.dev.jsg.util.JSGAdvancementsUtil.tryTriggerRangedAdvancement;

public class EventHorizon {
    private World world;
    private BlockPos pos;
    private BlockPos gateCenter;

    private JSGAxisAlignedBB localBox;
    private JSGAxisAlignedBB globalBox;
    public static Random randomGenerator = new Random();

    public EventHorizon(World world, BlockPos pos, BlockPos gateCenter, EnumFacing facing, JSGAxisAlignedBB localBox) {
        this.world = world;
        this.pos = pos;
        this.gateCenter = gateCenter;

        this.localBox = localBox.rotate(facing).offset(0.5, 0, 0.5);
        this.globalBox = this.localBox.offset(pos);
    }

    public void reset() {
        scheduledTeleportMap.clear();
    }

    public JSGAxisAlignedBB getLocalBox() {
        return localBox;
    }

    // ------------------------------------------------------------------------
    // Teleporting

    private final Map<Integer, TeleportPacket> scheduledTeleportMap = new HashMap<>();

    /**
     * This map is used not to double the teleport packet on Entity's
     * passengers.
     */
    private final Map<Integer, Integer> timeoutMap = new HashMap<>();

    public void scheduleTeleportation(StargatePos targetGate, boolean teleport) {
        if (targetGate == null) return;
        boolean closedIris = false;
        if (world.getTileEntity(pos) instanceof StargateClassicBaseTile && ((StargateClassicBaseTile) Objects.requireNonNull(world.getTileEntity(pos))).isIrisClosed()) {
            teleport = false;
            closedIris = true;
        }
        List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, globalBox);

        if (teleport) {
            timeoutMap.replaceAll((i, v) -> timeoutMap.get(i) - 1);
            timeoutMap.entrySet().removeIf(entry -> entry.getValue() < 0);
        }

        StargateAbstractBaseTile gateTile = (StargateAbstractBaseTile) world.getTileEntity(pos);

        for (Entity entity : entities) {
            int entityId = entity.getEntityId();
            if (!scheduledTeleportMap.containsKey(entityId) && !timeoutMap.containsKey(entityId) && !entity.isRiding()) {
                EnumFacing sourceFacing = world.getBlockState(pos).getValue(JSGProps.FACING_HORIZONTAL);
                EnumFacing targetFacing = targetGate.getBlockState().getValue(JSGProps.FACING_HORIZONTAL);

                float rotation = (float) Math.toRadians(EnumFacing.fromAngle(targetFacing.getHorizontalAngle() - sourceFacing.getHorizontalAngle()).getOpposite().getHorizontalAngle());
                TeleportPacket packet = new TeleportPacket(entity, pos, targetGate, rotation);

                if (teleport && entity instanceof EntityPlayerMP) {
                    scheduledTeleportMap.put(entityId, packet);
                    JSGPacketHandler.INSTANCE.sendTo(new StargateMotionToClient(pos), (EntityPlayerMP) entity);
                } else {
                    Vector2f motion = new Vector2f((float) entity.motionX, (float) entity.motionZ);
                    boolean front = TeleportHelper.frontSide(sourceFacing, motion);

                    if (!closedIris && teleport && front) {

                        for (Entity passenger : entity.getPassengers())
                            timeoutMap.put(passenger.getEntityId(), 40);
                        timeoutMap.put(entityId, 40);

                        scheduledTeleportMap.put(entityId, packet.setMotion(motion));
                        teleportEntity(entityId);
                    } else if ((gateTile == null || !gateTile.entitiesPassedLast.containsKey(entityId)) && !closedIris && !teleport && front && JSGConfig.Stargate.eventHorizon.wrongSideKilling)
                        wrongSideKill(entity);
                }
            }
        }
    }

    private void irisKill(Entity e) {
        e.attackEntityFrom(JSGDamageSources.DAMAGE_EVENT_IRIS_CREATIVE, Float.MAX_VALUE);
    }

    public void horizonKill(Entity e) {
        if (e instanceof EntityPlayerMP)
            JSGAdvancements.KAWOOSH_CREMATION.trigger((EntityPlayerMP) e);
        e.attackEntityFrom(JSGDamageSources.DAMAGE_EVENT_HORIZON, Float.MAX_VALUE);
    }

    public void wrongSideKill(Entity e) {
        e.attackEntityFrom(JSGDamageSources.DAMAGE_WRONG_SIDE, Float.MAX_VALUE);
    }

    public void unstableEhKill(Entity e) {
        e.attackEntityFrom(JSGDamageSources.UNSTABLE_EH_KILL, Float.MAX_VALUE);
    }

    public void teleportEntity(int entityId) {
        TeleportPacket packet = scheduledTeleportMap.get(entityId);
        StargateAbstractBaseTile baseTile = (StargateAbstractBaseTile) world.getTileEntity(pos);
        if (!new StargateTeleportEntityEvent(baseTile, packet.getTargetGatePos().getTileEntity(), packet.getEntity()).post()) {
            // Not cancelled
            StargatePos targetGatePos = packet.getTargetGatePos();
            JSGSoundHelper.playSoundEvent(world, gateCenter, SoundEventEnum.WORMHOLE_GO);
            if (targetGatePos.getTileEntity() instanceof StargateClassicBaseTile
                    && ((StargateClassicBaseTile) targetGatePos.getTileEntity()).isIrisClosed()) {

                if (packet.getEntity() instanceof IProjectile) {
                    Entity projectile = packet.getEntity();
                    projectile.motionX = 0;
                    projectile.motionY = 0;
                    projectile.motionZ = 0;
                    projectile.setDead();


                } else {
                    if (JSGConfig.Stargate.iris.killAtDestination) {
                        packet.teleport(false);
                    }
                    if (JSGConfig.Stargate.iris.allowCreative
                            && packet.getEntity() instanceof EntityPlayer
                            && ((EntityPlayer) packet.getEntity()).capabilities.isCreativeMode) {
                        if (!tauri.dev.jsg.config.JSGConfig.Stargate.iris.killAtDestination) {
                            packet.teleport();
                        }
                    } else {
                        if (!packet.getEntity().getPassengers().isEmpty()) {
                            for (Entity passenger : packet.getEntity().getPassengers())
                                irisKill(passenger);
                        }
                        irisKill(packet.getEntity());
                        packet.getEntity().setDead();
                    }

                }


                StargateClassicBaseTile classicTargetGate = ((StargateClassicBaseTile) targetGatePos.getTileEntity());
                if (classicTargetGate.isPhysicalIris()) {
                    JSGSoundHelper.playSoundEvent(packet.getTargetGatePos().getWorld(),
                            classicTargetGate.getGateCenterPos(),
                            SoundEventEnum.IRIS_HIT);
                } else if (classicTargetGate.isShieldIris()) {
                    JSGSoundHelper.playSoundEvent(packet.getTargetGatePos().getWorld(),
                            classicTargetGate.getGateCenterPos(),
                            SoundEventEnum.SHIELD_HIT);
                }
                tryTriggerRangedAdvancement(classicTargetGate, JSGAdvancementsUtil.EnumAdvancementType.IRIS_IMPACT);
                ItemStack irisItem = classicTargetGate.getItemHandler().getStackInSlot(11);
                if (irisItem.getItem() instanceof UpgradeIris && irisItem.isItemStackDamageable()) {
                    // different damages per source
                    int chance = EnchantmentHelper.getEnchantments(irisItem).containsKey(Enchantments.UNBREAKING) ? (tauri.dev.jsg.config.JSGConfig.Stargate.iris.unbreakingChance * EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, irisItem)) : 0;
                    int random = randomGenerator.nextInt(100);

                    if (random > chance) {
                        irisItem.getItem().setDamage(irisItem, irisItem.getItem().getDamage(irisItem) + 1);
                    }
                    if (irisItem.getCount() == 0) {
                        classicTargetGate.updateIrisType();
                    }
                    classicTargetGate.tryHeatUp(true, 2);
                } else {
                    IEnergyStorage energyStorage = classicTargetGate.getCapability(CapabilityEnergy.ENERGY, null);
                    if (energyStorage != null) {
                        energyStorage.extractEnergy(500, false);
                    }
                }
                classicTargetGate.sendSignal(null, "stargate_event_iris_hit", new Object[]{"Something just hit the IRIS!"});

            } else {
                if (baseTile != null && baseTile.isCurrentlyUnstable)
                    if (Math.random() < JSGConfig.Stargate.eventHorizon.ehDeathChance)
                        unstableEhKill(packet.getEntity());
                    else {
                        packet.teleport();
                        if (packet.getEntity() instanceof EntityPlayerMP)
                            JSGAdvancements.UNSTABLE_SURVIVE.trigger((EntityPlayerMP) packet.getEntity());
                    }
                else
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
