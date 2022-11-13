package tauri.dev.jsg.renderer.dialhomedevice;

import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.loader.ElementEnum;
import tauri.dev.jsg.loader.model.ModelLoader;
import tauri.dev.jsg.stargate.network.SymbolMilkyWayEnum;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.tileentity.dialhomedevice.DHDAbstractTile;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DHDMilkyWayRenderer extends DHDAbstractRenderer {
    @Override
    public void renderSymbols(DHDAbstractTile te, DHDAbstractRendererState rendererState, World world, BlockPos lightPos) {
        NBTTagCompound compound = getNoteBookPage();

        for (SymbolMilkyWayEnum symbol : SymbolMilkyWayEnum.values()) {
            setColorByAddress(te, rendererState, compound, SymbolTypeEnum.MILKYWAY, symbol);
            rendererDispatcher.renderEngine.bindTexture(((DHDMilkyWayRendererState) rendererState).getButtonTexture(symbol, rendererState.getBiomeOverlay()));
            ModelLoader.getModel(symbol.getModelResource(rendererState.biomeOverlay, te.getWorld().provider.getDimension())).render();
        }
    }

    @Override
    public void renderDHD(DHDAbstractRendererState rendererState) {
        ElementEnum.MILKYWAY_DHD.bindTextureAndRender(rendererState.getBiomeOverlay());
    }

    @Override
    public Block getDHDBlock() {
        return JSGBlocks.DHD_BLOCK;
    }
}
