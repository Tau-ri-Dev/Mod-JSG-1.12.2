package mrjake.aunis.tileentity.dialhomedevice;

import mrjake.aunis.util.main.AunisProps;
import mrjake.aunis.block.AunisBlocks;
import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.fluid.AunisFluids;
import mrjake.aunis.item.AunisItems;
import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import mrjake.aunis.renderer.dialhomedevice.DHDAbstractRendererState;
import mrjake.aunis.renderer.dialhomedevice.DHDPegasusRendererState;
import mrjake.aunis.sound.AunisSoundHelper;
import mrjake.aunis.sound.SoundEventEnum;
import mrjake.aunis.stargate.network.*;
import mrjake.aunis.state.State;
import mrjake.aunis.state.StateTypeEnum;
import mrjake.aunis.state.dialhomedevice.DHDActivateButtonState;
import mrjake.aunis.state.stargate.StargateBiomeOverrideState;
import mrjake.aunis.tileentity.stargate.StargateAbstractBaseTile;
import mrjake.aunis.tileentity.stargate.StargateClassicBaseTile;
import mrjake.aunis.tileentity.stargate.StargatePegasusBaseTile;
import mrjake.aunis.util.LinkingHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;

import java.util.EnumSet;

public class DHDPegasusTile extends DHDAbstractTile {

    public static final EnumSet<BiomeOverlayEnum> SUPPORTED_OVERLAYS = EnumSet.of(BiomeOverlayEnum.NORMAL, BiomeOverlayEnum.FROST, BiomeOverlayEnum.MOSSY, BiomeOverlayEnum.AGED);

    @Override
    public EnumSet<BiomeOverlayEnum> getSupportedOverlays() {
        return SUPPORTED_OVERLAYS;
    }

    @Override
    public void update(){
        if(world.isRemote) {
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

        SymbolPegasusEnum symbol = SymbolPegasusEnum.valueOf(symbolInt.getId());

        // When using OC to dial, don't play sound of the DHD button press
        if (!gateTile.getStargateState().dialingComputer() || ((StargateClassicBaseTile) gateTile).getConfig().getOption(StargateClassicBaseTile.ConfigOptions.ENABLE_DHD_PRESS_SOUND.id).getBooleanValue()) {

            if (symbol.brb()) AunisSoundHelper.playSoundEvent(world, pos, SoundEventEnum.DHD_PEGASUS_PRESS_BRB);
            else AunisSoundHelper.playSoundEvent(world, pos, SoundEventEnum.DHD_PEGASUS_PRESS);
        }

        world.notifyNeighborsOfStateChange(pos, AunisBlocks.DHD_PEGASUS_BLOCK, true);

        sendState(StateTypeEnum.DHD_ACTIVATE_BUTTON, new DHDActivateButtonState(symbol));
    }

    // --------------------------------
    // LINKING

    @Override
    public void updateLinkStatus(World world, BlockPos pos) {
        BlockPos closestGate = LinkingHelper.findClosestUnlinked(world, pos, LinkingHelper.getDhdRange(), AunisBlocks.STARGATE_PEGASUS_BASE_BLOCK, this.getLinkId());

        int linkId = LinkingHelper.getLinkId();

        if (closestGate != null) {
            StargatePegasusBaseTile gateTile = (StargatePegasusBaseTile) world.getTileEntity(closestGate);

            gateTile.setLinkedDHD(pos, linkId);
        }

        setLinkedGate(closestGate, linkId);
    }

    // --------------------------------
    // STATES

    @Override
    public State getState(StateTypeEnum stateType) {
        if (stateType == StateTypeEnum.RENDERER_STATE) {
            StargateAddressDynamic address = new StargateAddressDynamic(SymbolTypeEnum.PEGASUS);

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

                return new DHDPegasusRendererState(address, brbActive, determineBiomeOverride(), gateTile.connectedToGate);
            }

            return new DHDPegasusRendererState(address, false, determineBiomeOverride(), false);
        }
        throw new UnsupportedOperationException("EnumStateType." + stateType.name() + " not implemented on " + this.getClass().getName());
    }

    @Override
    public State createState(StateTypeEnum stateType) {
        if (stateType == StateTypeEnum.RENDERER_STATE) {
            return new DHDPegasusRendererState();
        }
        return super.createState(stateType);
    }

    @Override
    public void setState(StateTypeEnum stateType, State state) {

        boolean connected = false;
        if (isLinked()) {
            StargateAbstractBaseTile gateTile = getLinkedGate(world);
            if(gateTile != null)
                connected = gateTile.connectedToGate;
        }

        switch (stateType) {
            case RENDERER_STATE:
                float horizontalRotation = world.getBlockState(pos).getValue(AunisProps.ROTATION_HORIZONTAL) * -22.5f;
                rendererStateClient = ((DHDPegasusRendererState) state).initClient(pos, horizontalRotation, BiomeOverlayEnum.updateBiomeOverlay(world, pos, SUPPORTED_OVERLAYS), connected);

                break;

            case DHD_ACTIVATE_BUTTON:
                if(state == null) break;
                DHDActivateButtonState activateState = (DHDActivateButtonState) state;

                ((DHDAbstractRendererState) getRendererStateClient()).setIsConnected(connected);

                if (activateState.clearAll) ((DHDPegasusRendererState) getRendererStateClient()).clearSymbols(world.getTotalWorldTime());
                else ((DHDPegasusRendererState) getRendererStateClient()).activateSymbol(world.getTotalWorldTime(), SymbolPegasusEnum.valueOf(activateState.symbol));

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
                itemStackHandler.setStackInSlot(0, new ItemStack(AunisItems.CRYSTAL_CONTROL_PEGASUS_DHD));

                int energy = tagList.getCompoundTagAt(0).getCompoundTag("ForgeCaps").getCompoundTag("Parent").getInteger("energy");
                int fluidAmount = energy / AunisConfig.dhdConfig.energyPerNaquadah;
                fluidHandler.fillInternal(new FluidStack(AunisFluids.moltenNaquadahRefined, fluidAmount), true);
            }
        }

        super.readFromNBT(compound);
    }

}
