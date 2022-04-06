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
import mrjake.aunis.transportrings.SymbolTransportRingsEnum;
import mrjake.aunis.transportrings.TransportRingsAddress;
import net.minecraft.util.math.BlockPos;

public class TRControllerGoauldTile extends TRControllerAbstractTile implements StateProviderInterface {

    TRControllerGoauldRendererState rendererState = new TRControllerGoauldRendererState(new TransportRingsAddress(), BiomeOverlayEnum.NORMAL, false);

    @Override
    public void onLoad() {
        if (world.isRemote) {
            setRenderer(new TRControllerGoauldRenderer());
            setBiomeOverlay(BiomeOverlayEnum.updateBiomeOverlay(world, pos, SUPPORTED_OVERLAYS));
        }
        super.onLoad();
    }

    @Override
    public TRControllerAbstractRendererState getRendererState(){
        return rendererState;
    }

    @Override
    public State createState(StateTypeEnum stateType) {
        if (stateType == StateTypeEnum.RENDERER_STATE) {

            if (isLinked() && linkedRingsTile != null) {
                TransportRingsAbstractTile tile = linkedRingsTile;
                return new TRControllerGoauldRendererState(tile.dialedAddress, biomeOverlay, tile.isBusy());
            }

            return new TRControllerGoauldRendererState(new TransportRingsAddress(), biomeOverlay, false);
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

                if (activateState.clearAll) rendererState.clearSymbols(world.getTotalWorldTime());
                else
                    rendererState.activateSymbol(world.getTotalWorldTime(), SymbolTransportRingsEnum.valueOf(activateState.symbol));

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
                return rendererState;

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
