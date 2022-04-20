package mrjake.aunis.tileentity.transportrings;

import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import mrjake.aunis.renderer.transportrings.TRControllerAbstractRendererState;
import mrjake.aunis.renderer.transportrings.TRControllerGoauldRendererState;
import mrjake.aunis.state.State;
import mrjake.aunis.state.StateTypeEnum;
import mrjake.aunis.state.dialhomedevice.DHDActivateButtonState;
import mrjake.aunis.transportrings.SymbolGoauldEnum;
import mrjake.aunis.transportrings.SymbolTypeTransportRingsEnum;
import mrjake.aunis.transportrings.TransportRingsAddress;

import java.util.EnumSet;

public class TRControllerGoauldTile extends TRControllerAbstractTile {

    public static final EnumSet<BiomeOverlayEnum> SUPPORTED_OVERLAYS = EnumSet.of(BiomeOverlayEnum.NORMAL, BiomeOverlayEnum.FROST);

    @Override
    public void onLoad() {
        rendererState = new TRControllerGoauldRendererState(new TransportRingsAddress(SymbolTypeTransportRingsEnum.GOAULD), BiomeOverlayEnum.NORMAL, false);
        if (world.isRemote) {
            setBiomeOverlay(BiomeOverlayEnum.updateBiomeOverlay(world, pos, getSupportedOverlays()));
        }
        super.onLoad();
    }

    @Override
    protected EnumSet<BiomeOverlayEnum> getSupportedOverlays() {
        return SUPPORTED_OVERLAYS;
    }

    @Override
    public SymbolTypeTransportRingsEnum getSymbolType() {
        return SymbolTypeTransportRingsEnum.GOAULD;
    }

    @Override
    public TRControllerAbstractRendererState getRendererState() {
        return rendererState;
    }

    @Override
    public State createState(StateTypeEnum stateType) {
        if (stateType == StateTypeEnum.RENDERER_STATE) {
            return new TRControllerGoauldRendererState();
        }
        return super.createState(stateType);
    }

    @Override
    public void setState(StateTypeEnum stateType, State state) {
        switch (stateType) {
            case RENDERER_STATE:
                rendererState = ((TRControllerGoauldRendererState) state).initClient(pos, BiomeOverlayEnum.updateBiomeOverlay(world, pos, SUPPORTED_OVERLAYS), linkedRingsTile);
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
                    ((TRControllerGoauldRendererState) rendererState).deactivateSymbol(world.getTotalWorldTime(), SymbolGoauldEnum.valueOf(symbol));
                }
                else if (activateState.clearAll) {
                    ((TRControllerGoauldRendererState) rendererState).clearSymbols(world.getTotalWorldTime());
                } else {
                    ((TRControllerGoauldRendererState) rendererState).activateSymbol(world.getTotalWorldTime(), SymbolGoauldEnum.valueOf(symbol));
                }
                break;

            default:
                break;
        }
        super.setState(stateType, state);
    }

    @Override
    public State getState(StateTypeEnum stateType) {
        switch (stateType) {
            case RENDERER_STATE:
                TransportRingsAddress address = new TransportRingsAddress(SymbolTypeTransportRingsEnum.GOAULD);

                if (isLinked()) {
                    TransportRingsAbstractTile trTile = getLinkedRingsTile();
                    if (trTile == null) return rendererState;

                    address.addAll(trTile.dialedAddress);
                    boolean ringsAreConnected = trTile.isBusy();

                    return new TRControllerGoauldRendererState(address, getBiomeOverlay(), ringsAreConnected);
                }

                return new TRControllerGoauldRendererState(address, getBiomeOverlay(), false);

            default:
                break;
        }
        return super.getState(stateType);
    }
}
