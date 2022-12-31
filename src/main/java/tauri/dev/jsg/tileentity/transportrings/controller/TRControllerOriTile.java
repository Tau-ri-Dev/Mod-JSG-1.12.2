package tauri.dev.jsg.tileentity.transportrings.controller;

import tauri.dev.jsg.block.transportrings.TransportRingsAbstractBlock;
import tauri.dev.jsg.renderer.biomes.BiomeOverlayEnum;
import tauri.dev.jsg.renderer.transportrings.controller.TRControllerAbstractRendererState;
import tauri.dev.jsg.renderer.transportrings.controller.TRControllerOriRendererState;
import tauri.dev.jsg.sound.JSGSoundHelper;
import tauri.dev.jsg.sound.SoundEventEnum;
import tauri.dev.jsg.state.State;
import tauri.dev.jsg.state.StateTypeEnum;
import tauri.dev.jsg.state.dialhomedevice.DHDActivateButtonState;
import tauri.dev.jsg.tileentity.transportrings.TransportRingsAbstractTile;
import tauri.dev.jsg.transportrings.SymbolOriEnum;
import tauri.dev.jsg.transportrings.SymbolTypeTransportRingsEnum;
import tauri.dev.jsg.transportrings.TransportRingsAddress;

import java.util.EnumSet;

import static tauri.dev.jsg.block.JSGBlocks.TRANSPORT_RINGS_ORI_BLOCK;

public class TRControllerOriTile extends TRControllerAbstractTile {

    public static final EnumSet<BiomeOverlayEnum> SUPPORTED_OVERLAYS = EnumSet.of(BiomeOverlayEnum.NORMAL, BiomeOverlayEnum.FROST);

    public void playPressSound(boolean isFinal){
        if(!isFinal)
            JSGSoundHelper.playSoundEvent(world, this.getPos(), SoundEventEnum.TR_CONTROLLER_GOAULD_BUTTON);
        else
            JSGSoundHelper.playSoundEvent(world, this.getPos(), SoundEventEnum.TR_CONTROLLER_GOAULD_BUTTON_FINAL);
    }

    @Override
    public void onLoad() {
        rendererState = new TRControllerOriRendererState(new TransportRingsAddress(SymbolTypeTransportRingsEnum.ORI), BiomeOverlayEnum.NORMAL, false);
        if (world.isRemote) {
            setBiomeOverlay(BiomeOverlayEnum.updateBiomeOverlay(world, pos, getSupportedOverlays()));
        }
        super.onLoad();
    }

    public TransportRingsAbstractBlock getTRBlock(){
        return TRANSPORT_RINGS_ORI_BLOCK;
    }

    @Override
    protected EnumSet<BiomeOverlayEnum> getSupportedOverlays() {
        return SUPPORTED_OVERLAYS;
    }

    @Override
    public SymbolTypeTransportRingsEnum getSymbolType() {
        return SymbolTypeTransportRingsEnum.ORI;
    }

    @Override
    public TRControllerAbstractRendererState getRendererState() {
        return rendererState;
    }

    @Override
    public State createState(StateTypeEnum stateType) {
        if (stateType == StateTypeEnum.RENDERER_STATE) {
            return new TRControllerOriRendererState();
        }
        return super.createState(stateType);
    }

    @Override
    public void setState(StateTypeEnum stateType, State state) {
        switch (stateType) {
            case RENDERER_STATE:
                rendererState = ((TRControllerOriRendererState) state).initClient(pos, BiomeOverlayEnum.updateBiomeOverlay(world, pos, SUPPORTED_OVERLAYS), linkedRingsTile);
                break;

            case DHD_ACTIVATE_BUTTON:
                boolean connected = false;
                if (isLinked()) {
                    if (linkedRingsTile != null)
                        connected = linkedRingsTile.isBusy();
                }
                if (state == null) break;
                DHDActivateButtonState activateState = (DHDActivateButtonState) state;

                rendererState.setIsConnected(connected);
                int symbol = activateState.symbol;
                if (activateState.deactivate) {
                    ((TRControllerOriRendererState) rendererState).deactivateSymbol(world.getTotalWorldTime(), SymbolOriEnum.valueOf(symbol));
                }
                else if (activateState.clearAll) {
                    ((TRControllerOriRendererState) rendererState).clearSymbols(world.getTotalWorldTime());
                } else {
                    ((TRControllerOriRendererState) rendererState).activateSymbol(world.getTotalWorldTime(), SymbolOriEnum.valueOf(symbol));
                }
                break;

            default:
                break;
        }
        super.setState(stateType, state);
    }

    @Override
    public State getState(StateTypeEnum stateType) {
        if (stateType == StateTypeEnum.RENDERER_STATE) {
            TransportRingsAddress address = new TransportRingsAddress(SymbolTypeTransportRingsEnum.ORI);

            if (isLinked()) {
                TransportRingsAbstractTile trTile = getLinkedRingsTile();
                if (trTile == null) return rendererState;

                address.addAll(trTile.dialedAddress);
                boolean ringsAreConnected = trTile.isBusy();

                return new TRControllerOriRendererState(address, getBiomeOverlay(), ringsAreConnected);
            }

            return new TRControllerOriRendererState(address, getBiomeOverlay(), false);
        }
        return super.getState(stateType);
    }
}
