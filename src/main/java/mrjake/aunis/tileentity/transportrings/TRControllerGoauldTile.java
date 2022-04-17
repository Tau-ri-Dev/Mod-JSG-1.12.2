package mrjake.aunis.tileentity.transportrings;

import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import mrjake.aunis.renderer.transportrings.TRControllerAbstractRenderer;
import mrjake.aunis.renderer.transportrings.TRControllerAbstractRendererState;
import mrjake.aunis.renderer.transportrings.TRControllerGoauldRenderer;
import mrjake.aunis.renderer.transportrings.TRControllerGoauldRendererState;
import mrjake.aunis.state.State;
import mrjake.aunis.state.StateProviderInterface;
import mrjake.aunis.state.StateTypeEnum;
import mrjake.aunis.state.dialhomedevice.DHDActivateButtonState;
import mrjake.aunis.transportrings.SymbolGoauldEnum;
import mrjake.aunis.transportrings.SymbolTypeTransportRingsEnum;
import mrjake.aunis.transportrings.TransportRingsAddress;

public class TRControllerGoauldTile extends TRControllerAbstractTile implements StateProviderInterface {

    TRControllerGoauldRendererState rendererState = new TRControllerGoauldRendererState(new TransportRingsAddress(SymbolTypeTransportRingsEnum.GOAULD), BiomeOverlayEnum.NORMAL, false);

    @Override
    public void onLoad() {
        if (world.isRemote) {
            setRenderer(new TRControllerGoauldRenderer());
            setBiomeOverlay(BiomeOverlayEnum.updateBiomeOverlay(world, pos, SUPPORTED_OVERLAYS));
        }
        super.onLoad();
    }

    @Override
    public SymbolTypeTransportRingsEnum getSymbolType() {
        return SymbolTypeTransportRingsEnum.GOAULD;
    }

    @Override
    public TRControllerAbstractRendererState getRendererState(){
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

                if (activateState.clearAll){
                    rendererState.clearSymbols(world.getTotalWorldTime());
                }
                else {
                    rendererState.activateSymbol(world.getTotalWorldTime(), SymbolGoauldEnum.valueOf(activateState.symbol));
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
                    if(trTile == null) return rendererState;

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

    @Override
    public TRControllerAbstractRenderer getNewRenderer() {
        return new TRControllerGoauldRenderer();
    }
}
