package tauri.dev.jsg.tileentity.dialhomedevice;

import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.fluid.JSGFluids;
import tauri.dev.jsg.item.JSGItems;
import tauri.dev.jsg.renderer.biomes.BiomeOverlayEnum;
import tauri.dev.jsg.renderer.dialhomedevice.DHDAbstractRendererState;
import tauri.dev.jsg.renderer.dialhomedevice.DHDMilkyWayRendererState;
import tauri.dev.jsg.sound.JSGSoundHelper;
import tauri.dev.jsg.sound.SoundEventEnum;
import tauri.dev.jsg.stargate.network.StargateAddressDynamic;
import tauri.dev.jsg.stargate.network.SymbolInterface;
import tauri.dev.jsg.stargate.network.SymbolMilkyWayEnum;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.state.State;
import tauri.dev.jsg.state.StateTypeEnum;
import tauri.dev.jsg.state.dialhomedevice.DHDActivateButtonState;
import tauri.dev.jsg.state.stargate.StargateBiomeOverrideState;
import tauri.dev.jsg.tileentity.stargate.StargateAbstractBaseTile;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;
import tauri.dev.jsg.tileentity.stargate.StargateMilkyWayBaseTile;
import tauri.dev.jsg.util.LinkingHelper;
import tauri.dev.jsg.util.main.JSGProps;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;

import java.util.EnumSet;

public class DHDMilkyWayTile extends DHDAbstractTile {

    public static final EnumSet<BiomeOverlayEnum> SUPPORTED_OVERLAYS = EnumSet.of(BiomeOverlayEnum.NORMAL, BiomeOverlayEnum.FROST, BiomeOverlayEnum.MOSSY, BiomeOverlayEnum.SOOTY, BiomeOverlayEnum.AGED);

    @Override
    public EnumSet<BiomeOverlayEnum> getSupportedOverlays() {
        return SUPPORTED_OVERLAYS;
    }

    @Override
    public void update() {
        if (world.isRemote) {
            // Client

            // Each 2s check for the sky
            if (world.getTotalWorldTime() % 40 == 0 && rendererStateClient != null && getRendererStateClient().biomeOverride == null) {
                rendererStateClient.setBiomeOverlay(BiomeOverlayEnum.updateBiomeOverlay(world, pos, SUPPORTED_OVERLAYS));
            }
        }
        super.update();
    }

    public void activateSymbol(SymbolInterface symbolInt) {
        StargateAbstractBaseTile gateTile = getLinkedGate(world);
        if(gateTile == null) return;

        SymbolMilkyWayEnum symbol = SymbolMilkyWayEnum.valueOf(symbolInt.getId());

        // When using OC to dial, don't play sound of the DHD button press
        if (!gateTile.getStargateState().dialingComputer() || ((StargateClassicBaseTile) gateTile).getConfig().getOption(StargateClassicBaseTile.ConfigOptions.ENABLE_DHD_PRESS_SOUND.id).getBooleanValue()) {

            if (symbol.brb()) JSGSoundHelper.playSoundEvent(world, pos, SoundEventEnum.DHD_MILKYWAY_PRESS_BRB);
            else JSGSoundHelper.playSoundEvent(world, pos, SoundEventEnum.DHD_MILKYWAY_PRESS);
        }

        world.notifyNeighborsOfStateChange(pos, JSGBlocks.DHD_BLOCK, true);

        sendState(StateTypeEnum.DHD_ACTIVATE_BUTTON, new DHDActivateButtonState(symbol));
    }

    // --------------------------------
    // LINKING

    @Override
    public void updateLinkStatus(World world, BlockPos pos) {
        BlockPos closestGate = LinkingHelper.findClosestUnlinked(world, pos, LinkingHelper.getDhdRange(), JSGBlocks.STARGATE_MILKY_WAY_BASE_BLOCK, this.getLinkId());

        int linkId = LinkingHelper.getLinkId();

        if (closestGate != null) {
            StargateMilkyWayBaseTile gateTile = (StargateMilkyWayBaseTile) world.getTileEntity(closestGate);
            if (gateTile != null) {
              gateTile.setLinkedDHD(pos, linkId);
            }
        }

        setLinkedGate(closestGate, linkId);
    }

    // --------------------------------
    // STATES

    @Override
    public State getState(StateTypeEnum stateType) {
        if (stateType == StateTypeEnum.RENDERER_STATE) {
            StargateAddressDynamic address = new StargateAddressDynamic(SymbolTypeEnum.MILKYWAY);

            if (isLinked()) {
                StargateAbstractBaseTile gateTile = getLinkedGate(world);

                address.addAll(gateTile.getDialedAddress());
                boolean brbActive = false;

                switch (gateTile.getStargateState()) {
                    case ENGAGED_INITIATING:
                        brbActive = true;
                        break;

                    case ENGAGED:
                        address.clear();
                        brbActive = true;
                        break;

                    default:
                        break;
                }

                return new DHDMilkyWayRendererState(address, brbActive, determineBiomeOverride(), gateTile.connectedToGate);
            }

            return new DHDMilkyWayRendererState(address, false, determineBiomeOverride(), false);
        }
        throw new UnsupportedOperationException("EnumStateType." + stateType.name() + " not implemented on " + this.getClass().getName());
    }

    @Override
    public State createState(StateTypeEnum stateType) {
        if (stateType == StateTypeEnum.RENDERER_STATE) {
            return new DHDMilkyWayRendererState();
        }
        return super.createState(stateType);
    }

    @Override
    public void setState(StateTypeEnum stateType, State state) {

        boolean connected = false;
        if (isLinked()) {
            StargateAbstractBaseTile gateTile = getLinkedGate(world);
            if (gateTile != null)
                connected = gateTile.connectedToGate;
        }

        switch (stateType) {
            case RENDERER_STATE:
                float horizontalRotation = world.getBlockState(pos).getValue(JSGProps.ROTATION_HORIZONTAL) * -22.5f;
                rendererStateClient = ((DHDMilkyWayRendererState) state).initClient(pos, horizontalRotation, BiomeOverlayEnum.updateBiomeOverlay(world, pos, SUPPORTED_OVERLAYS), connected);

                break;

            case DHD_ACTIVATE_BUTTON:
                if (state == null) break;
                DHDActivateButtonState activateState = (DHDActivateButtonState) state;

                ((DHDAbstractRendererState) getRendererStateClient()).setIsConnected(connected);

                if (activateState.clearAll)
                    ((DHDMilkyWayRendererState) getRendererStateClient()).clearSymbols(world.getTotalWorldTime());
                else
                    ((DHDMilkyWayRendererState) getRendererStateClient()).activateSymbol(world.getTotalWorldTime(), SymbolMilkyWayEnum.valueOf(activateState.symbol));

                break;

            case BIOME_OVERRIDE_STATE:
                StargateBiomeOverrideState overrideState = (StargateBiomeOverrideState) state;

                if (rendererStateClient != null) {
                    getRendererStateClient().biomeOverride = overrideState.biomeOverride;
                }

                break;

            default:
                super.setState(stateType, state);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {

        if (compound.hasKey("inventory")) {
            NBTTagCompound inventoryTag = compound.getCompoundTag("inventory");
            NBTTagList tagList = inventoryTag.getTagList("Items", Constants.NBT.TAG_COMPOUND);

            if (tagList.tagCount() > 0) {
                itemStackHandler.setStackInSlot(0, new ItemStack(JSGItems.CRYSTAL_CONTROL_MILKYWAY_DHD));

                int energy = tagList.getCompoundTagAt(0).getCompoundTag("ForgeCaps").getCompoundTag("Parent").getInteger("energy");
                int fluidAmount = energy / JSGConfig.dhdConfig.energyPerNaquadah;
                fluidHandler.fillInternal(new FluidStack(JSGFluids.NAQUADAH_MOLTEN_REFINED, fluidAmount), true);
            }
        }

        super.readFromNBT(compound);
    }

}
