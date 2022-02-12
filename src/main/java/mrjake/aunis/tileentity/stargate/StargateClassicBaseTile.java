package mrjake.aunis.tileentity.stargate;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import mrjake.aunis.Aunis;
import mrjake.aunis.beamer.BeamerLinkingHelper;
import mrjake.aunis.block.AunisBlocks;
import mrjake.aunis.chunkloader.ChunkManager;
import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.config.StargateSizeEnum;
import mrjake.aunis.gui.container.StargateContainerGuiState;
import mrjake.aunis.gui.container.StargateContainerGuiUpdate;
import mrjake.aunis.item.AunisItems;
import mrjake.aunis.item.UpgradeIris;
import mrjake.aunis.item.gdo.GDOMessages;
import mrjake.aunis.item.notebook.PageNotebookItem;
import mrjake.aunis.packet.AunisPacketHandler;
import mrjake.aunis.packet.StateUpdatePacketToClient;
import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import mrjake.aunis.renderer.stargate.StargateClassicRendererState;
import mrjake.aunis.renderer.stargate.StargateClassicRendererState.StargateClassicRendererStateBuilder;
import mrjake.aunis.sound.AunisSoundHelper;
import mrjake.aunis.sound.SoundEventEnum;
import mrjake.aunis.sound.StargateSoundEventEnum;
import mrjake.aunis.sound.StargateSoundPositionedEnum;
import mrjake.aunis.stargate.*;
import mrjake.aunis.stargate.codesender.CodeSender;
import mrjake.aunis.stargate.codesender.CodeSenderType;
import mrjake.aunis.stargate.codesender.ComputerCodeSender;
import mrjake.aunis.stargate.network.*;
import mrjake.aunis.stargate.power.StargateAbstractEnergyStorage;
import mrjake.aunis.stargate.power.StargateClassicEnergyStorage;
import mrjake.aunis.stargate.power.StargateEnergyRequired;
import mrjake.aunis.state.*;
import mrjake.aunis.state.stargate.StargateBiomeOverrideState;
import mrjake.aunis.state.stargate.StargateRendererActionState;
import mrjake.aunis.state.stargate.StargateRendererActionState.EnumGateAction;
import mrjake.aunis.state.stargate.StargateSpinState;
import mrjake.aunis.tileentity.BeamerTile;
import mrjake.aunis.tileentity.util.IUpgradable;
import mrjake.aunis.tileentity.util.ScheduledTask;
import mrjake.aunis.util.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.items.CapabilityItemHandler;
import scala.reflect.internal.Trees;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static mrjake.aunis.item.AunisItems.UPGRADE_IRIS;
import static mrjake.aunis.renderer.stargate.StargateClassicRenderer.PHYSICAL_IRIS_ANIMATION_LENGTH;
import static mrjake.aunis.renderer.stargate.StargateClassicRenderer.SHIELD_IRIS_ANIMATION_LENGTH;
import static mrjake.aunis.stargate.StargateClassicSpinHelper.A_ANGLE_PER_TICK;
import static mrjake.aunis.stargate.network.SymbolUniverseEnum.G37;
import static mrjake.aunis.stargate.network.SymbolUniverseEnum.TOP_CHEVRON;

/**
 * This class wraps common behavior for the fully-functional Stargates i.e.
 * all of them (right now) except Orlin's.
 *
 * @author MrJake222
 */
public abstract class StargateClassicBaseTile extends StargateAbstractBaseTile implements IUpgradable {

    // IRIS/SHIELD VARIABLES/CONSTANTS
    private EnumIrisState irisState = EnumIrisState.OPENED;
    private EnumIrisType irisType = EnumIrisType.NULL;
    private int irisCode = -1;
    protected EnumIrisMode irisMode = EnumIrisMode.OPENED;
    private long irisAnimation = 0;

    public int shieldKeepAlive = 0;

    private int irisDurability = 0;
    private int irisMaxDurability = 0;


    // ------------------------------------------------------------------------
    // Stargate state

    protected boolean isFinalActive;

    @Override
    protected void engageGate() {
        super.engageGate();
        for (BlockPos beamerPos : linkedBeamers) {
            if(world.getTileEntity(beamerPos) != null)
                ((BeamerTile) Objects.requireNonNull(world.getTileEntity(beamerPos))).gateEngaged(targetGatePos);
        }
    }

    @Override
    public void closeGate(StargateClosedReasonEnum reason) {
        super.closeGate(reason);
        for (BlockPos beamerPos : linkedBeamers) {
            if(world.getTileEntity(beamerPos) != null)
                ((BeamerTile) Objects.requireNonNull(world.getTileEntity(beamerPos))).gateClosed();
        }
    }

    @Override
    protected void disconnectGate() {
        super.disconnectGate();
        if (irisMode == EnumIrisMode.AUTO && isClosed()) toggleIris();
        isFinalActive = false;
        if (codeSender != null) codeSender = null;
        updateChevronLight(0, false);
        sendRenderingUpdate(EnumGateAction.CLEAR_CHEVRONS, dialedAddress.size(), isFinalActive);
    }

    @Override
    protected void failGate() {
        super.failGate();

        isFinalActive = false;

        if(stargateState != EnumStargateState.INCOMING && !isIncoming){
            updateChevronLight(0, false);
            sendRenderingUpdate(EnumGateAction.CLEAR_CHEVRONS, dialedAddress.size(), isFinalActive);
        }
    }

    @Override
    public void openGate(StargatePos targetGatePos, boolean isInitiating) {
        super.openGate(targetGatePos, isInitiating);

        this.isFinalActive = true;
    }

    public void abortDialingSequence(int type) {
        //todo(Mine): Fix aborting on uni gates and remove this if statement
        if(this instanceof StargateUniverseBaseTile){
            ((StargateUniverseBaseTile) this).abort(true);
            return;
        }

        if (stargateState.dialingComputer() || stargateState.idle() || stargateState.dialing()) {
            spinStartTime = world.getTotalWorldTime() + 3000;
            isSpinning = false;
            AunisPacketHandler.INSTANCE.sendToAllTracking(new StateUpdatePacketToClient(pos, StateTypeEnum.SPIN_STATE, new StargateSpinState(targetRingSymbol, spinDirection, true, 0)), targetPoint);
            addFailedTaskAndPlaySound();
            playPositionedSound(StargateSoundPositionedEnum.GATE_RING_ROLL, false);
            // remove last spinning finished task
            if(lastSpinFinished != null && scheduledTasks.contains(lastSpinFinished))
                removeTask(lastSpinFinished);
            failGate();
            if(!isIncoming) disconnectGate();
            if (type == 2 && this instanceof StargateUniverseBaseTile) {
                addSymbolToAddressManual(G37, null);
                playPositionedSound(StargateSoundPositionedEnum.GATE_RING_ROLL, true);
            }
            markDirty();
            if (type == 1) abortDialingSequence(2);
        }
    }


    @Override
    public void incomingWormhole(int dialedAddressSize) {
        incomingWormhole(dialedAddressSize, true);
    }

    public void incomingWormhole(int dialedAddressSize, boolean toggleIris) {
        if (irisMode == EnumIrisMode.AUTO && isOpened() && toggleIris) {
            toggleIris();
        }
        super.incomingWormhole(dialedAddressSize);
        isFinalActive = true;
        updateChevronLight(dialedAddressSize, isFinalActive);
    }

    @Override
    public void onGateBroken() {
//        if (StargateNetwork.get(world).getNetherGate().equals(this.getStargateAddress(SymbolTypeEnum.MILKYWAY))) {
//            StargateNetwork.get(world).setNetherGate(null);
//        }
        super.onGateBroken();
        updateChevronLight(0, false);
        if (irisType != EnumIrisType.NULL && irisState == EnumIrisState.CLOSED) {
            setIrisBlocks(false);
        }
        isSpinning = false;
        irisState = mrjake.aunis.stargate.EnumIrisState.OPENED;
        irisType = EnumIrisType.NULL;
        currentRingSymbol = getSymbolType().getTopSymbol();
        AunisPacketHandler.INSTANCE.sendToAllTracking(new StateUpdatePacketToClient(pos, StateTypeEnum.SPIN_STATE, new StargateSpinState(currentRingSymbol, spinDirection, true, 0)), targetPoint);

        playPositionedSound(StargateSoundPositionedEnum.GATE_RING_ROLL, false);
        ItemHandlerHelper.dropInventoryItems(world, pos, itemStackHandler);

        for (BlockPos beamerPos : linkedBeamers) {
            if(world.getTileEntity(beamerPos) != null){
                BeamerTile beamerTile = (BeamerTile) world.getTileEntity(beamerPos);
                beamerTile.setLinkedGate(null, null);
            }
        }

        linkedBeamers.clear();

    }

    @Override
    protected void onGateMerged() {
        super.onGateMerged();

        BeamerLinkingHelper.findBeamersInFront(world, pos, facing);
        updateBeamers();
        updateIrisType();
    }


    // ------------------------------------------------------------------------
    // Loading and ticking


    @Override
    public void onLoad() {
        super.onLoad();

        lastPos = pos;

        if (!world.isRemote) {

            updateBeamers();
            updatePowerTier();

            updateIrisType();
            boolean set = irisType != EnumIrisType.NULL;
            if (isMerged()) {
                setIrisBlocks(set && irisState == EnumIrisState.CLOSED);
            }
        }
    }


    protected abstract boolean onGateMergeRequested();

    private BlockPos lastPos = BlockPos.ORIGIN;
    boolean isFacingFixed = false;

    @Override
    public void update() {

        /*
         * =========================================================================
         * Stargate Random Incoming Generator (RIG)
         */
        if(!world.isRemote) {

            // Load entities
            String[] entityListString = AunisConfig.randomIncoming.entitiesToSpawn;
            List<Entity> entityList = new ArrayList<Entity>();
            for(String entityString : entityListString){
                String[] entityTemporallyList = entityString.split(":");
                if(entityTemporallyList.length < 2) continue; // prevents from Ticking block entity null pointer
                String entityStringNew =
                        (
                                (entityTemporallyList[0].equals("minecraft"))
                                        ? entityTemporallyList[1]
                                        : entityTemporallyList[0] + ":" + entityTemporallyList[1]
                        );
                ResourceLocation rlString = new ResourceLocation(entityStringNew);
                entityList.add(EntityList.createEntityByIDFromName(rlString, world));
            }

            Random rand = new Random();
            if(AunisConfig.randomIncoming.enableRandomIncoming && world.isAreaLoaded(pos, 10)) {
                if (world.getTotalWorldTime() % 200 == 0) { // every 10 seconds
                    int chanceToRandom = rand.nextInt(1000);

                    //if chance && stargate state is idle or dialing by DHD and RANDOM INCOMING IS NOT ACTIVATED YET
                    if (chanceToRandom <= AunisConfig.randomIncoming.chance) {
                        int entities = rand.nextInt(25);
                        int delay = rand.nextInt(200);
                        if(this instanceof StargateUniverseBaseTile) {
                            delay = rand.nextInt(300);
                            if(delay < 120) delay = 120;
                        }
                        if(delay < 80) delay = 80;
                        if(entities < 3) entities = 3;

                        generateIncoming(entities, 7, delay); // execute
                    }
                }
            }

            if (randomIncomingIsActive) {

                int wait = 4 * 20;
                int waitOpen = randomIncomingOpenDelay + 20;
                if(waitOpen < 80) waitOpen = 80;

                if (isMerged()) {
                    if (randomIncomingState == 0) { // incoming wormhole
                        randomIncomingState++;
                        int period = (((waitOpen) / 20) * 1000) / randomIncomingAddrSize;
                        stargateState = EnumStargateState.INCOMING;
                        isIncoming = true;
                        markDirty();
                        if(AunisConfig.stargateConfig.allowIncomingAnimations) this.incomingWormhole(randomIncomingAddrSize, period);
                        else this.incomingWormhole(randomIncomingAddrSize);
                        this.sendSignal(null, "stargate_incoming_wormhole", new Object[]{randomIncomingAddrSize});
                        this.failGate();
                    } else if (randomIncomingState < waitOpen) { // wait waitOpen ticks to open gate
                        stargateState = EnumStargateState.INCOMING;
                        randomIncomingState++;
                    } else if (randomIncomingState == waitOpen) { // open gate
                        randomIncomingState++;
                        targetGatePos = null;

                        ChunkManager.forceChunk(world, new ChunkPos(pos));

                        sendRenderingUpdate(EnumGateAction.OPEN_GATE, 0, false);

                        addTask(new ScheduledTask(EnumScheduledTask.STARGATE_OPEN_SOUND, getOpenSoundDelay()));
                        addTask(new ScheduledTask(EnumScheduledTask.STARGATE_HORIZON_LIGHT_BLOCK, EnumScheduledTask.STARGATE_OPEN_SOUND.waitTicks + 19 + getTicksPerHorizonSegment(true)));
                        addTask(new ScheduledTask(EnumScheduledTask.STARGATE_HORIZON_WIDEN, EnumScheduledTask.STARGATE_OPEN_SOUND.waitTicks + 23 + getTicksPerHorizonSegment(true))); // 1.3s of the sound to the kill
                        addTask(new ScheduledTask(EnumScheduledTask.STARGATE_ENGAGE));

                        sendSignal(null, "stargate_open", new Object[]{false});

                        // activate DHD brb
                        activateDHDSymbolBRB();

                        markDirty();

                        this.isFinalActive = true;
                    } else if (randomIncomingState < (waitOpen + wait)) {
                        randomIncomingState++;
                    } else if (randomIncomingState >= (waitOpen + wait) && randomIncomingEntities > 0 && (stargateState == EnumStargateState.ENGAGED || stargateState == EnumStargateState.INCOMING)) {
                        randomIncomingState++;
                        int randomDelay = new Random().nextInt(16);
                        if (randomDelay <= 0) randomDelay = 1;
                        if (randomIncomingState % (5 * randomDelay) == 0) {
                            randomIncomingEntities--;
                            int posX = this.getGateCenterPos().getX();
                            int posY = this.getGateCenterPos().getY();
                            int posZ = this.getGateCenterPos().getZ();
                            // create entity
                            Entity mobEntity = new EntityZombie(world);

                            int entitiesLength = entityList.size();
                            if(entitiesLength > 0) {
                                int randomEntity = rand.nextInt(entitiesLength);
                                if(entityList.get(randomEntity) != null)
                                    mobEntity = entityList.get(randomEntity);
                            }
                            mobEntity.setLocationAndAngles(posX, posY, posZ, 0, 0);
                            if (isOpened() || irisType.equals(EnumIrisType.NULL)) {
                                // spawn zombie
                                this.world.spawnEntity(mobEntity);
                                AunisSoundHelper.playSoundEvent(world, getGateCenterPos(), SoundEventEnum.WORMHOLE_GO);
                            } else {
                                // do iris shit

                                if (isPhysicalIris()) {
                                    AunisSoundHelper.playSoundEvent(world,
                                            getGateCenterPos(),
                                            SoundEventEnum.IRIS_HIT);
                                } else if (isShieldIris()) {
                                    AunisSoundHelper.playSoundEvent(world,
                                            getGateCenterPos(),
                                            SoundEventEnum.SHIELD_HIT);
                                }
                                ItemStack irisItem = getItemHandler().getStackInSlot(11);
                                if (irisItem.getItem() instanceof UpgradeIris) {
                                    // different damages per source
                                    int chance = EnchantmentHelper.getEnchantments(irisItem).containsKey(Enchantments.UNBREAKING) ? (AunisConfig.irisConfig.unbreakingChance * EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, irisItem)) : 0;
                                    int random = rand.nextInt(100);

                                    if (random > chance) {
                                        UPGRADE_IRIS.setDamage(irisItem, UPGRADE_IRIS.getDamage(irisItem) + 1);
                                    }
                                    if (irisItem.getCount() == 0) {
                                        updateIrisType();
                                    }
                                } else {
                                    IEnergyStorage energyStorage = getCapability(CapabilityEnergy.ENERGY, null);
                                    if (energyStorage != null) {
                                        energyStorage.extractEnergy(500, false);
                                    }
                                }
                                sendSignal(null, "stargate_event_iris_hit", new Object[]{"Something just hit the IRIS!"});
                            }

                        }
                    } else if ((randomIncomingEntities <= 0 && randomIncomingState >= (waitOpen + wait)) || stargateState != EnumStargateState.ENGAGED) {
                        resetRandomIncoming();
                        closeGate(StargateClosedReasonEnum.AUTOCLOSE);

                        if(this instanceof StargatePegasusBaseTile) ((StargatePegasusBaseTile) this).clearDHDSymbols();
                        if(this instanceof StargateMilkyWayBaseTile) ((StargateMilkyWayBaseTile) this).clearDHDSymbols();
                    }
                } else resetRandomIncoming();
            }
        }

        /*
         * =========================================================================
         */


        /*
         * Draw power (shield)
         */
        super.extractEnergyByShield(0);
        if (!world.isRemote && isShieldIris()) {
            shieldKeepAlive = AunisConfig.irisConfig.shieldPowerDraw;
            //if (isClosed()) getEnergyStorage().extractEnergy(shieldKeepAlive, false);
            if (isClosed()) super.extractEnergyByShield(shieldKeepAlive);
            if (getEnergyStorage().getEnergyStored() < shieldKeepAlive) {
                toggleIris();
                sendSignal(null, "stargate_iris_out_of_power", new Object[]{"Shield runs out of power! Opening shield..."});
            } else if (irisMode == EnumIrisMode.CLOSED && isOpened()) {
                toggleIris();
            }
        }

        super.update();

        if (!world.isRemote) {


            if (!lastPos.equals(pos)) {
                lastPos = pos;
                generateAddresses(!hasUpgrade(StargateClassicBaseTile.StargateUpgradeEnum.CHEVRON_UPGRADE));

                if (isMerged()) {
                    updateMergeState(onGateMergeRequested(), facing);
                }
            }

            if (givePageTask != null) {
                if (givePageTask.update(world.getTotalWorldTime())) {
                    givePageTask = null;
                }
            }

            if (doPageProgress) {
                if (world.getTotalWorldTime() % 2 == 0) {
                    pageProgress++;

                    if (pageProgress > 18) {
                        pageProgress = 0;
                        doPageProgress = false;
                    }
                }

                if (itemStackHandler.getStackInSlot(pageSlotId).isEmpty()) {
                    lockPage = false;
                    doPageProgress = false;
                    pageProgress = 0;
                    givePageTask = null;
                }
            } else {
                if (lockPage && itemStackHandler.getStackInSlot(pageSlotId).isEmpty()) {
                    lockPage = false;
                }

                if (!lockPage) {
                    for (int i = 7; i < 10; i++) {
                        if (!itemStackHandler.getStackInSlot(i).isEmpty()) {
                            doPageProgress = true;
                            lockPage = true;
                            pageSlotId = i;
                            givePageTask = new ScheduledTask(EnumScheduledTask.STARGATE_GIVE_PAGE, 36);
                            givePageTask.setTaskCreated(world.getTotalWorldTime());
                            givePageTask.setExecutor(this);

                            break;
                        }
                    }
                }
            }

            if (!(isClosed() || isOpened()) && (world.getTotalWorldTime() - irisAnimation) > (isPhysicalIris() ? PHYSICAL_IRIS_ANIMATION_LENGTH : SHIELD_IRIS_ANIMATION_LENGTH)) {
                switch (irisState) {
                    case OPENING:
                        irisState = mrjake.aunis.stargate.EnumIrisState.OPENED;

                        sendRenderingUpdate(EnumGateAction.IRIS_UPDATE, 0, false, irisType, irisState, irisAnimation);
                        if (afterIrisDone != null) afterIrisDone.run();
                        afterIrisDone = null;
                        sendSignal(null, "stargate_iris_opened", new Object[]{"Iris is opened"});
                        break;
                    case CLOSING:
                        irisState = mrjake.aunis.stargate.EnumIrisState.CLOSED;
                        setIrisBlocks(true);
                        sendRenderingUpdate(EnumGateAction.IRIS_UPDATE, 0, false, irisType, irisState, irisAnimation);
                        if (afterIrisDone != null) afterIrisDone.run();
                        afterIrisDone = null;
                        sendSignal(null, "stargate_iris_closed", new Object[]{"Iris is closed"});
                        break;
                    default:
                        break;
                }
                markDirty();
            }

        } else {
            // Client

            // Each 2s check for the biome overlay
            if (world.getTotalWorldTime() % 40 == 0 && rendererStateClient != null) {
                if (getRendererStateClient().biomeOverride == null)
                    rendererStateClient.setBiomeOverlay(BiomeOverlayEnum.updateBiomeOverlay(world, getMergeHelper().getTopBlock().add(pos), getSupportedOverlays()));
//               if (getRendererStateClient().irisType != EnumIrisType.NULL
//                       && (getRendererStateClient().irisType != irisType || getRendererStateClient().irisState != irisState)) {
//                   sendRenderingUpdate(EnumGateAction.IRIS_UPDATE, 0, false, irisType, irisState, irisAnimation);
//                }
            }
        }
    }

    @Override
    protected void kawooshDestruction() {
        if (!isClosed() || irisType == EnumIrisType.NULL) super.kawooshDestruction();
    }

    // Server
    private BiomeOverlayEnum determineBiomeOverride() {
        ItemStack stack = itemStackHandler.getStackInSlot(BIOME_OVERRIDE_SLOT);

        if (stack.isEmpty()) {
            return null;
        }

        BiomeOverlayEnum biomeOverlay = AunisConfig.stargateConfig.getBiomeOverrideItemMetaPairs().get(new ItemMetaPair(stack));

        if (getSupportedOverlays().contains(biomeOverlay)) {
            return biomeOverlay;
        }

        return null;
    }

    @Override
    protected boolean shouldAutoclose() {
        boolean beamerActive = false;

        for (BlockPos beamerPos : linkedBeamers) {
            if(world.getTileEntity(beamerPos) != null){
                BeamerTile beamerTile = (BeamerTile) world.getTileEntity(beamerPos);
                beamerActive = beamerTile.isActive();
            }

            if (beamerActive) break;
        }

        return !beamerActive && super.shouldAutoclose();
    }

    // ------------------------------------------------------------------------
    // NBT

    @Override
    protected void setWorldCreate(World world) {
        setWorld(world);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("itemHandler", itemStackHandler.serializeNBT());
        compound.setBoolean("isFinalActive", isFinalActive);

        compound.setBoolean("isSpinning", isSpinning);
        compound.setLong("spinStartTime", spinStartTime);
        compound.setInteger("currentRingSymbol", currentRingSymbol.getId());
        compound.setInteger("targetRingSymbol", targetRingSymbol.getId());
        compound.setInteger("spinDirection", spinDirection.id);

        NBTTagList linkedBeamersTagList = new NBTTagList();
        for (BlockPos vect : linkedBeamers)
            linkedBeamersTagList.appendTag(new NBTTagLong(vect.toLong()));
        compound.setTag("linkedBeamers", linkedBeamersTagList);
        if (irisState == null) {
            if (codeSender != null) {
                codeSender.sendMessage(GDOMessages.OPENED.textComponent);
                codeSender = null;
            }
            irisState = EnumIrisState.OPENED;
        }
        compound.setByte("irisState", irisState.id);
        compound.setInteger("irisCode", irisCode);
        compound.setByte("irisMode", irisMode.id);
        if (codeSender != null && !world.isRemote) {
            compound.setTag("codeSender", codeSender.serializeNBT());
        }
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        itemStackHandler.deserializeNBT(compound.getCompoundTag("itemHandler"));

        if (compound.getBoolean("hasUpgrade")) {
            itemStackHandler.setStackInSlot(0, new ItemStack(AunisItems.CRYSTAL_GLYPH_STARGATE));
        }

        isFinalActive = compound.getBoolean("isFinalActive");

        isSpinning = compound.getBoolean("isSpinning");
        spinStartTime = compound.getLong("spinStartTime");
        currentRingSymbol = getSymbolType().valueOfSymbol(compound.getInteger("currentRingSymbol"));
        targetRingSymbol = getSymbolType().valueOfSymbol(compound.getInteger("targetRingSymbol"));
        spinDirection = EnumSpinDirection.valueOf(compound.getInteger("spinDirection"));

        for (NBTBase tag : compound.getTagList("linkedBeamers", NBT.TAG_LONG))
            linkedBeamers.add(BlockPos.fromLong(((NBTTagLong) tag).getLong()));


        irisState = mrjake.aunis.stargate.EnumIrisState.getValue(compound.getByte("irisState"));
        irisCode = compound.getInteger("irisCode") != 0 ? compound.getInteger("irisCode") : -1;
        irisMode = EnumIrisMode.getValue(compound.getByte("irisMode"));
        if (compound.hasKey("codeSender") && !world.isRemote) {
            NBTTagCompound nbt = compound.getCompoundTag("codeSender");
            codeSender = codeSenderFromNBT(nbt);
        }
        super.readFromNBT(compound);
    }

    private CodeSender codeSenderFromNBT(NBTTagCompound compound) {
        codeSender = CodeSenderType.fromId(compound.getInteger("type")).constructor.get();
        switch (codeSender.getType()) {
            case PLAYER:
                codeSender.prepareToLoad(new Object[]{world});
                break;
            case COMPUTER:
                codeSender.prepareToLoad(null);
                break;

        }
        codeSender.deserializeNBT(compound);
        return codeSender;
    }


    // ------------------------------------------------------------------------
    // Rendering

    protected void updateChevronLight(int lightUp, boolean isFinalActive) {
        //		Aunis.info("Updating chevron light to: " + lightUp);

        if (isFinalActive) lightUp--;

        for (int i = 0; i < 9; i++) {
            BlockPos chevPos = getMergeHelper().getChevronBlocks().get(i).rotate(FacingToRotation.get(facing)).add(pos);

            if (getMergeHelper().matchMember(world.getBlockState(chevPos))) {
                StargateClassicMemberTile memberTile = (StargateClassicMemberTile) world.getTileEntity(chevPos);
                memberTile.setLitUp(i == 8 ? isFinalActive : lightUp > i);
            }
        }
    }

    @Override
    protected StargateClassicRendererStateBuilder getRendererStateServer() {
        return new StargateClassicRendererStateBuilder(super.getRendererStateServer())
                .setSymbolType(getSymbolType())
                .setActiveChevrons(dialedAddress.size())
                .setFinalActive(isFinalActive)
                .setCurrentRingSymbol(currentRingSymbol)
                .setSpinDirection(spinDirection)
                .setSpinning(isSpinning)
                .setTargetRingSymbol(targetRingSymbol)
                .setSpinStartTime(spinStartTime)
                .setBiomeOverride(determineBiomeOverride())
                .setIrisState(irisState)
                .setIrisType(irisType)
                .setIrisMode(irisMode)
                .setIrisCode(irisCode)
                .setIrisAnimation(irisAnimation);
    }

    @Override
    public StargateClassicRendererState getRendererStateClient() {
        return (StargateClassicRendererState) super.getRendererStateClient();
    }

    public static final AunisAxisAlignedBB RENDER_BOX = new AunisAxisAlignedBB(-5.5, 0, -0.5, 5.5, 10.5, 0.5);

    @Override
    protected AunisAxisAlignedBB getRenderBoundingBoxRaw() {
        return RENDER_BOX;
    }

    protected long getSpinStartOffset() {
        return 0;
    }

    // -----------------------------------------------------------------
    // States

    @Override
    public State getState(StateTypeEnum stateType) {
        switch (stateType) {
            case GUI_STATE:
                return new StargateContainerGuiState(gateAddressMap);

            case GUI_UPDATE:
                return new StargateContainerGuiUpdate(energyStorage.getEnergyStoredInternally(), energyTransferedLastTick, energySecondsToClose, this.irisMode, this.irisCode, getOpenedSecondsToDisplay());

//            case IRIS_UPDATE:
//                return getRendererStateServer().build();

            default:
                return super.getState(stateType);
        }
    }

    @Override
    public State createState(StateTypeEnum stateType) {
        switch (stateType) {
            case GUI_STATE:
                return new StargateContainerGuiState();

            case GUI_UPDATE:
                return new StargateContainerGuiUpdate();

            case SPIN_STATE:
                return new StargateSpinState();

            case BIOME_OVERRIDE_STATE:
                return new StargateBiomeOverrideState();

            default:
                return super.createState(stateType);
        }
    }

    @Override
    public void setState(StateTypeEnum stateType, State state) {
        switch (stateType) {
            case RENDERER_UPDATE:
                if (getRendererStateClient() == null) return;
                StargateRendererActionState gateActionState = (StargateRendererActionState) state;

                switch (gateActionState.action) {
                    case CHEVRON_ACTIVATE:
                        if (gateActionState.modifyFinal)
                            getRendererStateClient().chevronTextureList.activateFinalChevron(world.getTotalWorldTime());
                        else getRendererStateClient().chevronTextureList.activateNextChevron(world.getTotalWorldTime(), gateActionState.chevronCount);

                        break;


                    case CLEAR_CHEVRONS:

                        getRendererStateClient().clearChevrons(world.getTotalWorldTime());
                        break;

                    case LIGHT_UP_CHEVRONS:
                        getRendererStateClient().chevronTextureList.lightUpChevrons(world.getTotalWorldTime(), gateActionState.chevronCount);
                        break;

                    case CHEVRON_ACTIVATE_BOTH:
                        getRendererStateClient().chevronTextureList.activateNextChevron(world.getTotalWorldTime());
                        getRendererStateClient().chevronTextureList.activateFinalChevron(world.getTotalWorldTime());
                        break;

                    case CHEVRON_DIM:
                        getRendererStateClient().chevronTextureList.deactivateFinalChevron(world.getTotalWorldTime());
                        break;

                    case IRIS_UPDATE:
                        getRendererStateClient().irisState = gateActionState.irisState;
                        getRendererStateClient().irisType = gateActionState.irisType;
                        if (gateActionState.irisState == EnumIrisState.CLOSING || gateActionState.irisState == EnumIrisState.OPENING) {
                            getRendererStateClient().irisAnimation = world.getTotalWorldTime();
                        }
                        break;


                    default:
                        break;
                }

                break;

            case GUI_STATE:
                StargateContainerGuiState guiState = (StargateContainerGuiState) state;
                gateAddressMap = guiState.gateAdddressMap;
                break;

            case GUI_UPDATE:
                StargateContainerGuiUpdate guiUpdate = (StargateContainerGuiUpdate) state;
                energyStorage.setEnergyStoredInternally(guiUpdate.energyStored);
                energyTransferedLastTick = guiUpdate.transferedLastTick;
                energySecondsToClose = guiUpdate.secondsToClose;
                irisMode = guiUpdate.irisMode;
                irisCode = guiUpdate.irisCode;
                secondsOpened = guiUpdate.openedSeconds;
                break;

            case SPIN_STATE:
                StargateSpinState spinState = (StargateSpinState) state;
                if (spinState.setOnly) {
                    getRendererStateClient().spinHelper.setIsSpinning(false);
                    getRendererStateClient().spinHelper.setCurrentSymbol(spinState.targetSymbol);
                } else
                    getRendererStateClient().spinHelper.initRotation(world.getTotalWorldTime(), spinState.targetSymbol, spinState.direction, getSpinStartOffset(), spinState.plusRounds);

                break;

            case BIOME_OVERRIDE_STATE:
                StargateBiomeOverrideState overrideState = (StargateBiomeOverrideState) state;

                if (rendererStateClient != null) {
                    getRendererStateClient().biomeOverride = overrideState.biomeOverride;
                }

                break;
            default:
                break;
        }

        super.setState(stateType, state);
    }


    // -----------------------------------------------------------------
    // Scheduled tasks

    @Override
    public void executeTask(EnumScheduledTask scheduledTask, NBTTagCompound customData) {
        switch (scheduledTask) {
            case STARGATE_HORIZON_LIGHT_BLOCK:
                if (irisType == EnumIrisType.NULL || !isClosed()) {
                    super.executeTask(scheduledTask, customData);
                } else if (isClosed()) {
                    world.getBlockState(getGateCenterPos()).getBlock().setLightLevel(.7f);
                }
                break;
            case STARGATE_CLOSE:
                if (irisType == EnumIrisType.NULL || !isClosed()) {
                    super.executeTask(scheduledTask, customData);
                } else if (isClosed()) {
                    world.getBlockState(getGateCenterPos()).getBlock().setLightLevel(0);
                    disconnectGate();
                }
                break;

            case STARGATE_SPIN_FINISHED:
                isSpinning = false;
                currentRingSymbol = targetRingSymbol;
                if (!(this instanceof StargatePegasusBaseTile))
                    playPositionedSound(StargateSoundPositionedEnum.GATE_RING_ROLL, false);
                else if (!((StargatePegasusBaseTile) this).continueDialing)
                    playPositionedSound(StargateSoundPositionedEnum.GATE_RING_ROLL, false);

                playSoundEvent(StargateSoundEventEnum.CHEVRON_SHUT);

                markDirty();
                break;

            case STARGATE_GIVE_PAGE:
                SymbolTypeEnum symbolType = SymbolTypeEnum.valueOf(pageSlotId - 7);
                ItemStack stack = itemStackHandler.getStackInSlot(pageSlotId);

                if (stack.getItem() == AunisItems.UNIVERSE_DIALER) {
                    NBTTagList saved = stack.getTagCompound().getTagList("saved", NBT.TAG_COMPOUND);
                    NBTTagCompound compound = gateAddressMap.get(symbolType).serializeNBT();
                    compound.setBoolean("hasUpgrade", hasUpgrade(StargateUpgradeEnum.CHEVRON_UPGRADE));
                    saved.appendTag(compound);
                } else {
                    Aunis.logger.debug("Giving Notebook page of address " + symbolType);

                    NBTTagCompound compound = PageNotebookItem.getCompoundFromAddress(gateAddressMap.get(symbolType), hasUpgrade(StargateUpgradeEnum.CHEVRON_UPGRADE), PageNotebookItem.getRegistryPathFromWorld(world, pos));

                    stack = new ItemStack(AunisItems.PAGE_NOTEBOOK_ITEM, 1, 1);
                    stack.setTagCompound(compound);
                    itemStackHandler.setStackInSlot(pageSlotId, stack);
                }

                break;

            default:
                super.executeTask(scheduledTask, customData);
        }
    }


    // ------------------------------------------------------------------------
    // Ring spinning

    protected boolean isSpinning;
    protected long spinStartTime;
    protected SymbolInterface currentRingSymbol = getSymbolType().getTopSymbol();
    protected SymbolInterface targetRingSymbol = getSymbolType().getTopSymbol();
    protected EnumSpinDirection spinDirection = EnumSpinDirection.COUNTER_CLOCKWISE;
    protected Object ringSpinContext;

    public void addSymbolToAddressManual(SymbolInterface targetSymbol, @Nullable Object context) {
        targetRingSymbol = targetSymbol;

        boolean moveOnly = targetRingSymbol == currentRingSymbol;
        int plusRounds = 0;


        if(moveOnly && targetSymbol instanceof SymbolUniverseEnum && !targetSymbol.equals(G37)) {
            addTask(new ScheduledTask(EnumScheduledTask.STARGATE_SPIN_FINISHED, 0));
            doIncomingAnimation(10, true);
        }
        else if(targetSymbol instanceof SymbolUniverseEnum && targetSymbol.equals(G37)){
            spinDirection = spinDirection.opposite();

            float distance = 360;

            int duration = StargateClassicSpinHelper.getAnimationDuration(distance);
            doIncomingAnimation(duration, true);

            AunisPacketHandler.INSTANCE.sendToAllTracking(new StateUpdatePacketToClient(pos, StateTypeEnum.SPIN_STATE, new StargateSpinState(targetRingSymbol, spinDirection, false, 0)), targetPoint);
            lastSpinFinished = new ScheduledTask(EnumScheduledTask.STARGATE_SPIN_FINISHED, duration - 5);
            addTask(lastSpinFinished);
            playPositionedSound(StargateSoundPositionedEnum.GATE_RING_ROLL, true);

            targetRingSymbol = TOP_CHEVRON;

            isSpinning = true;
            spinStartTime = world.getTotalWorldTime();

            ringSpinContext = null;
            markDirty();
        }
        else {
            spinDirection = spinDirection.opposite();

            float distance = spinDirection.getDistance(currentRingSymbol, targetRingSymbol);
            if (moveOnly) {
                distance = 360;
                plusRounds += 1;
            }

            if(!AunisConfig.stargateConfig.fasterMWGateDial) {
                if (distance < 90) {
                    distance += 360;
                    plusRounds += 1;
                }
                if (distance < 270 && this instanceof StargateMilkyWayBaseTile && targetRingSymbol == SymbolMilkyWayEnum.getOrigin()) {
                    distance += 360;
                    plusRounds += 1;
                }
            }
            else if (distance > 180) {
                spinDirection = spinDirection.opposite();
                distance = spinDirection.getDistance(currentRingSymbol, targetRingSymbol);
                plusRounds = 0;
            }

            int duration = StargateClassicSpinHelper.getAnimationDuration(distance);
            doIncomingAnimation(duration, true);

            AunisPacketHandler.INSTANCE.sendToAllTracking(new StateUpdatePacketToClient(pos, StateTypeEnum.SPIN_STATE, new StargateSpinState(targetRingSymbol, spinDirection, false, plusRounds)), targetPoint);
            lastSpinFinished = new ScheduledTask(EnumScheduledTask.STARGATE_SPIN_FINISHED, duration - 5);
            addTask(lastSpinFinished);
            playPositionedSound(StargateSoundPositionedEnum.GATE_RING_ROLL, true);

            isSpinning = true;
            spinStartTime = world.getTotalWorldTime();

            ringSpinContext = context;
            if (context != null)
                sendSignal(context, "stargate_spin_start", new Object[]{dialedAddress.size(), stargateWillLock(targetRingSymbol), targetSymbol.getEnglishName()});

            markDirty();
        }
    }

    // -----------------------------------------------------------------------------
    // Page conversion

    private short pageProgress = 0;
    private int pageSlotId;
    private boolean doPageProgress;
    private ScheduledTask givePageTask;
    private boolean lockPage;

    public short getPageProgress() {
        return pageProgress;
    }

    public void setPageProgress(int pageProgress) {
        this.pageProgress = (short) pageProgress;
    }

    // -----------------------------------------------------------------------------
    // Item handler

    public static final int BIOME_OVERRIDE_SLOT = 10;

    private final AunisItemStackHandler itemStackHandler = new AunisItemStackHandler(12) {

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            Item item = stack.getItem();
            boolean isItemCapacitor = (item == Item.getItemFromBlock(AunisBlocks.CAPACITOR_BLOCK));
            switch (slot) {
                case 0:
                case 1:
                case 2:
                case 3:
                    return StargateUpgradeEnum.contains(item) && !hasUpgrade(item);

                case 4:
                    return isItemCapacitor && getSupportedCapacitors() >= 1;
                case 5:
                    return isItemCapacitor && getSupportedCapacitors() >= 2;
                case 6:
                    return isItemCapacitor && getSupportedCapacitors() >= 3;

                case 7:
                case 8:
                    return item == AunisItems.PAGE_NOTEBOOK_ITEM;

                case 9:
                    return item == AunisItems.PAGE_NOTEBOOK_ITEM || item == AunisItems.UNIVERSE_DIALER;

                case BIOME_OVERRIDE_SLOT:
                    BiomeOverlayEnum override = AunisConfig.stargateConfig.getBiomeOverrideItemMetaPairs().get(new ItemMetaPair(stack));
                    if (override == null) return false;

                    return getSupportedOverlays().contains(override);
                case 11:
                    return StargateIrisUpgradeEnum.contains(item);
                default:
                    return true;
            }
        }

        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            switch (slot) {
                case 4:
                case 5:
                case 6:
                    updatePowerTier();
                    break;

                case BIOME_OVERRIDE_SLOT:
                    sendState(StateTypeEnum.BIOME_OVERRIDE_STATE, new StargateBiomeOverrideState(determineBiomeOverride()));
                    break;
                // iris update state
                case 11:
                    updateIrisType();
                    break;
                default:
                    break;
            }

            markDirty();
        }
    };

    public abstract int getSupportedCapacitors();


    public static enum StargateUpgradeEnum implements EnumKeyInterface<Item> {
        MILKYWAY_GLYPHS(AunisItems.CRYSTAL_GLYPH_MILKYWAY),
        PEGASUS_GLYPHS(AunisItems.CRYSTAL_GLYPH_PEGASUS),
        UNIVERSE_GLYPHS(AunisItems.CRYSTAL_GLYPH_UNIVERSE),
        CHEVRON_UPGRADE(AunisItems.CRYSTAL_GLYPH_STARGATE);

        public Item item;

        private StargateUpgradeEnum(Item item) {
            this.item = item;
        }

        @Override
        public Item getKey() {
            return item;
        }

        private static final EnumKeyMap<Item, StargateUpgradeEnum> idMap = new EnumKeyMap<Item, StargateClassicBaseTile.StargateUpgradeEnum>(values());

        public static StargateUpgradeEnum valueOf(Item item) {
            return idMap.valueOf(item);
        }

        public static boolean contains(Item item) {
            return idMap.contains(item);
        }
    }

    // ----------------------------------------------------------
    // IRISES

    public static enum StargateIrisUpgradeEnum implements EnumKeyInterface<Item> {
        IRIS_UPGRADE_CLASSIC(AunisItems.UPGRADE_IRIS),
        IRIS_UPGRADE_TRINIUM(AunisItems.UPGRADE_IRIS_TRINIUM),
        IRIS_UPGRADE_SHIELD(AunisItems.UPGRADE_SHIELD);

        public Item item;

        private StargateIrisUpgradeEnum(Item item) {
            this.item = item;
        }

        @Override
        public Item getKey() {
            return item;
        }

        private static final EnumKeyMap<Item, StargateIrisUpgradeEnum> idMap =
                new EnumKeyMap<Item, StargateClassicBaseTile.StargateIrisUpgradeEnum>(values());

        public static StargateIrisUpgradeEnum valueOf(Item item) {
            return idMap.valueOf(item);
        }

        public static boolean contains(Item item) {
            return idMap.contains(item);
        }
    }

    public void updateIrisType() {
        updateIrisType(true);
    }

    public void updateIrisType(boolean markDirty) {
        irisType = EnumIrisType.byItem(itemStackHandler.getStackInSlot(11).getItem());
        irisAnimation = getWorld().getTotalWorldTime();
        if (irisType == EnumIrisType.NULL)
            irisState = mrjake.aunis.stargate.EnumIrisState.OPENED;
        sendRenderingUpdate(EnumGateAction.IRIS_UPDATE, 0, false, irisType, irisState, irisAnimation);
        updateIrisDurability();
        if (markDirty) markDirty();
        if (!world.isRemote && isOpened()) {
            setIrisBlocks(false);

        }
    }

    public void updateIrisDurability() {
        irisDurability = 0;
        irisMaxDurability = 0;
        if (isPhysicalIris()) {
            irisMaxDurability = (irisType == EnumIrisType.IRIS_TITANIUM ? AunisConfig.irisConfig.titaniumIrisDurability : AunisConfig.irisConfig.triniumIrisDurability);
            irisDurability = irisMaxDurability - itemStackHandler.getStackInSlot(11).getItem().getDamage(itemStackHandler.getStackInSlot(11));
        }
    }

    public int getIrisDurability() {
        updateIrisDurability();
        return irisDurability;
    }

    public int getIrisMaxDurability() {
        updateIrisDurability();
        return irisDurability;
    }

    public EnumIrisType getIrisType() {
        return irisType;
    }

    public EnumIrisState getIrisState() {
        return irisState;
    }

    public boolean isClosed() {
        return irisState == mrjake.aunis.stargate.EnumIrisState.CLOSED;
    }

    public boolean isOpened() {
        return irisState == mrjake.aunis.stargate.EnumIrisState.OPENED;
    }

    public boolean isPhysicalIris() {
        switch (irisType) {
            case IRIS_TITANIUM:
            case IRIS_TRINIUM:
                return true;
            default:
                return false;
        }
    }

    public boolean hasIris() {
        return irisType != EnumIrisType.NULL;
    }

    public boolean isShieldIris() {
        return irisType == EnumIrisType.SHIELD;
    }

    public boolean toggleIris() {
        if (irisType == EnumIrisType.NULL) return false;
        if (isClosed() || isOpened())
            irisAnimation = getWorld().getTotalWorldTime();
        SoundEventEnum openSound;
        SoundEventEnum closeSound;
        if (isPhysicalIris()) {
            openSound = SoundEventEnum.IRIS_OPENING;
            closeSound = SoundEventEnum.IRIS_CLOSING;
        } else {
            openSound = SoundEventEnum.SHIELD_OPENING;
            closeSound = SoundEventEnum.SHIELD_CLOSING;
        }
        //StargateAbstractBaseTile targetGate = targetGatePos.getTileEntity();
        switch (irisState) {
            case OPENED:
                if (isShieldIris() && getEnergyStorage().getEnergyStored() < shieldKeepAlive * 3)
                    return false;

                irisState = mrjake.aunis.stargate.EnumIrisState.CLOSING;
                sendRenderingUpdate(EnumGateAction.IRIS_UPDATE, 0, true, irisType, irisState, irisAnimation);
                sendSignal(null, "stargate_iris_closing", new Object[]{"Iris is closing"});
                markDirty();
                playSoundEvent(closeSound);
                if (targetGatePos != null) executeTask(EnumScheduledTask.STARGATE_HORIZON_LIGHT_BLOCK, null);
                break;
            case CLOSED:
                irisState = mrjake.aunis.stargate.EnumIrisState.OPENING;
                setIrisBlocks(false);
                sendRenderingUpdate(EnumGateAction.IRIS_UPDATE, 0, true, irisType, irisState, irisAnimation);
                sendSignal(null, "stargate_iris_opening", new Object[]{"Iris is opening"});
                markDirty();
                playSoundEvent(openSound);
                if (targetGatePos != null) executeTask(EnumScheduledTask.STARGATE_HORIZON_LIGHT_BLOCK, null);
                break;
            default:
                return false;
        }
        markDirty();
        return true;
    }

    protected CodeSender codeSender;

    public void receiveIrisCode(CodeSender sender, int code) {
        sendSignal(null, "received_code", code);
        if (irisMode != EnumIrisMode.AUTO) {
            sender.sendMessage(GDOMessages.SEND_TO_COMPUTER.textComponent);
            codeSender = sender;
            return;
        }
        if (code == this.irisCode) {
            switch (this.irisState) {
                case OPENED:
                    sender.sendMessage(GDOMessages.OPENED.textComponent);
                    break;
                case CLOSED:
                    sender.sendMessage(GDOMessages.CODE_ACCEPTED.textComponent);
                    codeSender = sender;
                    toggleIris();
                    break;
                case OPENING:
                case CLOSING:
                    sender.sendMessage(GDOMessages.BUSY.textComponent);
                    break;
                default:
                    break;
            }
        } else {
            sender.sendMessage(GDOMessages.CODE_REJECTED.textComponent);
        }
        markDirty();

    }

    public void setIrisCode(int code) {
        this.irisCode = code;
        markDirty();
    }

    private Runnable afterIrisDone = null;

    public void setIrisMode(EnumIrisMode irisMode) {
        if (this.irisMode != irisMode && hasIris()) {
            switch (irisMode) {
                case OPENED:
                case CLOSED:
                    irisModeAction(irisMode);
                    break;
                case AUTO:
                    if (getStargateState().engaged()) {
                        if (irisState == EnumIrisState.OPENED) toggleIris();
                    } else {
                        if (isClosed()) toggleIris();
                    }
                    break;
                case OC:
                default:
                    break;
            }


        }

        this.irisMode = irisMode;
        markDirty();
    }

    private void irisModeAction(EnumIrisMode mode) {
        EnumIrisState p, p2;
        if (mode == EnumIrisMode.OPENED) {
            p = EnumIrisState.CLOSED;
            p2 = EnumIrisState.CLOSING;
        } else if (mode == EnumIrisMode.CLOSED) {
            p = EnumIrisState.OPENED;
            p2 = EnumIrisState.OPENING;
        } else return;

        if (irisState == p) toggleIris();
        else if (irisState == p2) afterIrisDone = this::toggleIris;

    }

    public int getIrisCode() {
        return this.irisCode;
    }

    public EnumIrisMode getIrisMode() {
        return this.irisMode;
    }




    private void setIrisBlocks(boolean set) {
        IBlockState invBlockState = AunisBlocks.IRIS_BLOCK.getDefaultState();
        if (set) invBlockState = AunisBlocks.IRIS_BLOCK.getStateFromMeta(getFacing().getHorizontalIndex());
        Rotation invBlocksRotation = FacingToRotation.get(facing);
        BlockPos startPos = this.pos;
        for (BlockPos invPos : Objects.requireNonNull(StargateSizeEnum.getIrisBLocksPatter(getStargateSize()))) {
            BlockPos newPos = startPos.add(invPos.rotate(invBlocksRotation));

            if (set) {

                if (world.getBlockState(newPos).getMaterial() != Material.AIR) {
                    if (!AunisConfig.irisConfig.irisDestroysBlocks) continue;
                    world.destroyBlock(newPos, true);
                }
                world.setBlockState(newPos, invBlockState, 3);
                if (newPos == getGateCenterPos() && targetGatePos != null) {
                    world.getBlockState(newPos).getBlock().setLightLevel(1f);
                }

            } else {
                if (newPos == getGateCenterPos() && targetGatePos != null)
                    executeTask(EnumScheduledTask.STARGATE_HORIZON_LIGHT_BLOCK, null);
                if (world.getBlockState(newPos).getBlock() == AunisBlocks.IRIS_BLOCK) world.setBlockToAir(newPos);
            }

        }

    }

    @Nonnull
    abstract StargateSizeEnum getStargateSize();

    // -----------------------------------------------------------

    private static final List<Integer> UPGRADE_SLOTS_IDS = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 11));

    @Override
    public Iterator<Integer> getUpgradeSlotsIterator() {
//        return IntStream.range(0, 7).iterator();
        return UPGRADE_SLOTS_IDS.iterator();
    }

    // -----------------------------------------------------------------------------
    // Power system

    private final StargateClassicEnergyStorage energyStorage = new StargateClassicEnergyStorage() {

        @Override
        protected void onEnergyChanged() {
            markDirty();
        }
    };

    @Override
    protected StargateAbstractEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    private int currentPowerTier = 1;

    public int getPowerTier() {
        return currentPowerTier;
    }

    private void updatePowerTier() {
        int powerTier = 1;

        for (int i = 4; i < 7; i++) {
            if (!itemStackHandler.getStackInSlot(i).isEmpty()) {
                powerTier++;
            }
        }

        if (powerTier != currentPowerTier) {
            currentPowerTier = powerTier;

            energyStorage.clearStorages();

            for (int i = 4; i < 7; i++) {
                ItemStack stack = itemStackHandler.getStackInSlot(i);

                if (!stack.isEmpty()) {
                    energyStorage.addStorage(stack.getCapability(CapabilityEnergy.ENERGY, null));
                }
            }

            Aunis.logger.debug("Updated to power tier: " + powerTier);
        }
    }


    // -----------------------------------------------------------------------------
    // Capabilities

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing == null) || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing == null)
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(itemStackHandler);

        return super.getCapability(capability, facing);
    }


    // -----------------------------------------------------------------
    // Beamers

    private final List<BlockPos> linkedBeamers = new ArrayList<>();

    public void addLinkedBeamer(BlockPos pos) {
        if (stargateState.engaged()) {
            ((BeamerTile) world.getTileEntity(pos)).gateEngaged(targetGatePos);
        }

        linkedBeamers.add(pos.toImmutable());
        markDirty();
    }

    public void removeLinkedBeamer(BlockPos pos) {
        linkedBeamers.remove(pos);
        markDirty();
    }

    private void updateBeamers() {
        if (stargateState.engaged()) {
            for (BlockPos beamerPos : linkedBeamers) {
                if(world.getTileEntity(beamerPos) != null)
                    ((BeamerTile) Objects.requireNonNull(world.getTileEntity(beamerPos))).gateEngaged(targetGatePos);
            }
        }

    }


    // -----------------------------------------------------------------
    // OpenComputers methods


    @Optional.Method(modid = "opencomputers")
    @Callback
    public Object[] getOpenedTime(Context context, Arguments args) {
        if (stargateState.engaged()) {
            float openedSeconds = getOpenedSecondsToDisplay();
            int minutes = ((int) Math.floor(openedSeconds / 60));
            int seconds = ((int) (openedSeconds - (60 * minutes)));
            String secondsString = ((seconds < 10) ? "0" + seconds : "" + seconds);
            if(openedSeconds > 0) return new Object[]{true, "stargate_time", "" + minutes, "" + secondsString};
            return new Object[] {false, "stargate_not_connected"};
        }
        return new Object[] {false, "stargate_not_connected"};
    }

    @Optional.Method(modid = "opencomputers")
    @Callback(doc = "function() -- close/open the iris/shield")
    public Object[] toggleIris(Context context, Arguments args) {
        if (irisType == EnumIrisType.NULL)
            return new Object[]{false, "stargate_iris_missing", "Iris is not installed!"};
        if (irisMode != EnumIrisMode.OC)
            return new Object[]{false, "stargate_iris_error_mode", "Iris mode must be set to OC"};
        boolean result = toggleIris();
        markDirty();
        if (!result && (isShieldIris() && isOpened() && getEnergyStorage().getEnergyStored() < shieldKeepAlive * 3))
            return new Object[]{false, "stargate_iris_not_power", "Not enough power to close shield"};
        else if (!result)
            return new Object[]{false, "stargate_iris_busy", "Iris is busy"};
        else if (result)
            return new Object[]{true};
        else
            return new Object[]{false, "stargate_iris_error_unknown", "Unknow error while toggling iris!"};
    }

    @Optional.Method(modid = "opencomputers")
    @Callback(doc = "function() -- get info about iris")
    public Object[] getIrisState(Context context, Arguments args) {
        return new Object[]{irisState.toString()};
    }

    @Optional.Method(modid = "opencomputers")
    @Callback(doc = "function() -- get info about iris")
    public Object[] getIrisType(Context context, Arguments args) {
        return new Object[]{irisType.toString()};
    }

    @Optional.Method(modid = "opencomputers")
    @Callback(doc = "function() -- get info about iris")
    public Object[] getIrisDurability(Context context, Arguments args) {
        updateIrisDurability();
        return new Object[]{irisDurability + "/" + irisMaxDurability, irisDurability, irisMaxDurability};
    }

    @Optional.Method(modid = "opencomputers")
    @Callback(doc = "function(message:string) -- Sends message to last person, who sent code for iris")
    public Object[] sendMessageToIncoming(Context context, Arguments args) {
        if (!isMerged()) return new Object[]{null, "stargate_failure_not_merged", "Stargate is not merged"};
        if (!stargateState.engaged()) return new Object[]{null, "stargate_failure_not_engaged", "Stargate is not engaged"};
        if (!args.isString(0)) return new Object[]{false, "wrong_argument_type"};

        if (codeSender != null && codeSender.canReceiveMessage()) {
            codeSender.sendMessage(new TextComponentString(args.checkString(0)));
            return new Object[]{true, "success"};
        }

        return new Object[]{false, "no_listener_available"};
    }

    @Optional.Method(modid = "opencomputers")
    @Callback(doc = "function(code:integer) -- send code like GDO")
    public Object[] sendIrisCode(Context context, Arguments args) {

        StargatePos destinationPos = StargateNetwork.get(world).getStargate(dialedAddress);
        if (!args.isInteger(0)) {
            throw new IllegalArgumentException("code must be integer!");
        }
        if (destinationPos == null) return new Object[]{false, "stargate_not_engaged"};
        StargateAbstractBaseTile te = destinationPos.getTileEntity();
        if (te instanceof StargateClassicBaseTile) {
            ((StargateClassicBaseTile) te).receiveIrisCode(new ComputerCodeSender(
                            StargateNetwork.get(world).getStargate(
                                    this.getStargateAddress(SymbolTypeEnum.MILKYWAY)
                            )
                    ), args.checkInteger(0)
            );
        } else {
            return new Object[]{false, "invalid_target_gate"};
        }
        return new Object[]{true, "success"};
    }

    @Optional.Method(modid = "opencomputers")
    @Callback(doc = "function(symbolName:string) -- Spins the ring to the given symbol and engages/locks it")
    public Object[] engageSymbol(Context context, Arguments args) {
        if (!isMerged()) return new Object[]{null, "stargate_failure_not_merged", "Stargate is not merged"};

        if (!stargateState.idle()) {
            return new Object[]{null, "stargate_failure_busy", "Stargate is busy, state: " + stargateState.toString()};
        }

        if (dialedAddress.size() == 9) {
            return new Object[]{null, "stargate_failure_full", "Already dialed 9 chevrons"};
        }

        SymbolInterface targetSymbol = getSymbolFromNameIndex(args.checkAny(0));

        // disables engaging unknown symbols (gate has only 36, but dhd 38)
        if (targetSymbol == SymbolPegasusEnum.UNKNOW1 || targetSymbol == SymbolPegasusEnum.UNKNOW2)
            throw new IllegalArgumentException("bad argument (symbol name/index invalid)");
        addSymbolToAddressManual(targetSymbol, context);
        markDirty();

        return new Object[]{"stargate_spin"};
    }

    @Optional.Method(modid = "opencomputers")
    @Callback(doc = "function() - aborts dialing")
    public Object[] abortDialing(Context context, Arguments args) {
        if (!isMerged()) return new Object[]{null, "stargate_failure_not_merged", "Stargate is not merged"};

        if (stargateState.dialingComputer() || stargateState.idle()) {
            abortDialingSequence(1);
            markDirty();
            return new Object[]{null, "stargate_aborting", "Aborting dialing"};
        }
        return new Object[]{null, "stargate_aborting_failed", "Aborting dialing failed"};
    }

    @Optional.Method(modid = "opencomputers")
    @Callback(doc = "function() -- Tries to open the gate")
    public Object[] engageGate(Context context, Arguments args) {
        if (!isMerged()) return new Object[]{null, "stargate_failure_not_merged", "Stargate is not merged"};

        if (stargateState.idle()) {
            StargateOpenResult gateState = attemptOpenAndFail();

            if (gateState.ok()) {
                return new Object[]{"stargate_engage"};
            } else {
                sendSignal(null, "stargate_failed", new Object[]{});
                return new Object[]{null, "stargate_failure_opening", "Stargate failed to open", gateState.toString()};
            }
        } else {
            return new Object[]{null, "stargate_failure_busy", "Stargate is busy", stargateState.toString()};
        }
    }

    @Optional.Method(modid = "opencomputers")
    @Callback(doc = "function() -- Tries to close the gate")
    public Object[] disengageGate(Context context, Arguments args) {
        if (!isMerged()) return new Object[]{null, "stargate_failure_not_merged", "Stargate is not merged"};

        if (stargateState.engaged()) {
            if (getStargateState().initiating()) {
                attemptClose(StargateClosedReasonEnum.REQUESTED);
                return new Object[]{"stargate_disengage"};
            } else return new Object[]{null, "stargate_failure_wrong_end", "Unable to close the gate on this end"};
        } else {
            return new Object[]{null, "stargate_failure_not_open", "The gate is closed"};
        }
    }

    @Optional.Method(modid = "opencomputers")
    @Callback
    public Object[] getCapacitorsInstalled(Context context, Arguments args) {
        return new Object[]{isMerged() ? currentPowerTier - 1 : null};
    }

    @Optional.Method(modid = "opencomputers")
    @Callback
    public Object[] getGateType(Context context, Arguments args) {
        return new Object[]{isMerged() ? getSymbolType() : null};
    }

    @Optional.Method(modid = "opencomputers")
    @Callback
    public Object[] getGateStatus(Context context, Arguments args) {
        if (!isMerged()) return new Object[]{"not_merged"};

        if (stargateState.engaged()) return new Object[]{"open", stargateState.initiating()};

        return new Object[]{stargateState.toString().toLowerCase()};
    }

    @SuppressWarnings("unchecked")
    @Optional.Method(modid = "opencomputers")
    @Callback
    public Object[] getEnergyRequiredToDial(Context context, Arguments args) {
        if (!isMerged()) return new Object[]{"not_merged"};

        StargateAddressDynamic stargateAddress = new StargateAddressDynamic(getSymbolType());
        Iterator<Object> iter = null;

        if (args.isTable(0)) {
            iter = args.checkTable(0).values().iterator();
        } else {
            iter = args.iterator();
        }

        while (iter.hasNext()) {
            Object symbolObj = iter.next();

            if (stargateAddress.size() == 9) {
                throw new IllegalArgumentException("Too much glyphs");
            }

            SymbolInterface symbol = getSymbolFromNameIndex(symbolObj);
            if (stargateAddress.contains(symbol)) {
                throw new IllegalArgumentException("Duplicate glyph");
            }

            stargateAddress.addSymbol(symbol);
        }

        if (!stargateAddress.getLast().origin() && stargateAddress.size() < 9) stargateAddress.addOrigin();

        if (!stargateAddress.validate()) return new Object[]{"address_malformed"};

        if (!canDialAddress(stargateAddress)) return new Object[]{"address_malformed"};

        StargateEnergyRequired energyRequired = getEnergyRequiredToDial(network.getStargate(stargateAddress));
        Map<String, Object> energyMap = new HashMap<>(2);

        energyMap.put("open", energyRequired.energyToOpen);
        energyMap.put("keepAlive", energyRequired.keepAlive);
        energyMap.put("canOpen", getEnergyStorage().getEnergyStored() >= energyRequired.energyToOpen);

        return new Object[]{energyMap};
    }


}
