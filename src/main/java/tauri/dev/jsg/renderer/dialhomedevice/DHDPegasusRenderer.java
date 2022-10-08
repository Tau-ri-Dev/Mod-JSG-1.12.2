package tauri.dev.jsg.renderer.dialhomedevice;

import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.loader.ElementEnum;
import tauri.dev.jsg.loader.model.ModelLoader;
import tauri.dev.jsg.stargate.network.SymbolPegasusEnum;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.tileentity.dialhomedevice.DHDAbstractTile;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DHDPegasusRenderer extends DHDAbstractRenderer {
    @Override
    public void renderSymbols(DHDAbstractTile te, DHDAbstractRendererState rendererState, World world, BlockPos lightPos) {
        NBTTagCompound compound = getNoteBookPage();

        for (SymbolPegasusEnum symbol : SymbolPegasusEnum.values()) {
            setColorByAddress(te, rendererState, compound, SymbolTypeEnum.PEGASUS, symbol);
            rendererDispatcher.renderEngine.bindTexture(((DHDPegasusRendererState) rendererState).getButtonTexture(symbol, rendererState.getBiomeOverlay()));
            ModelLoader.getModel(symbol.modelResource).render();
        }
    }

    @Override
    public void renderDHD(DHDAbstractRendererState rendererState) {
        ElementEnum.PEGASUS_DHD.bindTextureAndRender(rendererState.getBiomeOverlay());
    }

    @Override
    public Block getDHDBlock() {
        return JSGBlocks.DHD_PEGASUS_BLOCK;
    }
}
