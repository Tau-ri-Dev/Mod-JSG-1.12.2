package tauri.dev.jsg.renderer.dialhomedevice;

import net.minecraft.client.renderer.GlStateManager;
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
import tauri.dev.jsg.util.JSGTextureLightningHelper;

public class DHDMilkyWayRenderer extends DHDAbstractRenderer {
    @Override
    public void renderSymbols(DHDAbstractTile te, DHDAbstractRendererState rendererState, World world, BlockPos lightPos) {
        NBTTagCompound compound = getNoteBookPage();

        for (SymbolMilkyWayEnum symbol : SymbolMilkyWayEnum.values()) {
            GlStateManager.pushMatrix();
            JSGTextureLightningHelper.lightUpTexture((rendererState.isButtonActive(symbol) ? 0.9f : 0));
            setColorByAddress(te, rendererState, compound, SymbolTypeEnum.MILKYWAY, symbol);
            rendererDispatcher.renderEngine.bindTexture(((DHDMilkyWayRendererState) rendererState).getButtonTexture(symbol, rendererState.getBiomeOverlay()));
            ModelLoader.getModel(symbol.getModelResource(rendererState.getBiomeOverlay(), te.getWorld().provider.getDimension(), true, true)).render();
            JSGTextureLightningHelper.resetLight(getWorld(), rendererState.pos);
            GlStateManager.popMatrix();
        }
    }

    @Override
    public void renderDHD(DHDAbstractRendererState rendererState, DHDAbstractTile te) {
        ElementEnum.MILKYWAY_DHD.bindTextureAndRender(rendererState.getBiomeOverlay());
        ModelLoader.getModel(((SymbolMilkyWayEnum) SymbolMilkyWayEnum.getOrigin()).getModelResource(rendererState.getBiomeOverlay(), te.getWorld().provider.getDimension(), true)).render();
    }

    @Override
    public Block getDHDBlock() {
        return JSGBlocks.DHD_BLOCK;
    }
}
