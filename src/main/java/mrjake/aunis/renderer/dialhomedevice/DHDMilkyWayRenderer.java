package mrjake.aunis.renderer.dialhomedevice;

import mrjake.aunis.block.AunisBlocks;
import mrjake.aunis.item.AunisItems;
import mrjake.aunis.loader.ElementEnum;
import mrjake.aunis.loader.model.ModelLoader;
import mrjake.aunis.stargate.network.SymbolMilkyWayEnum;
import mrjake.aunis.stargate.network.SymbolTypeEnum;
import mrjake.aunis.tileentity.dialhomedevice.DHDAbstractTile;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DHDMilkyWayRenderer extends DHDAbstractRenderer {
    @Override
    public void renderSymbols(DHDAbstractTile te, DHDAbstractRendererState rendererState, World world, BlockPos lightPos){
        NBTTagCompound compound = null;
        ItemStack item = Minecraft.getMinecraft().player.getHeldItem(EnumHand.MAIN_HAND);
        if(item.hasTagCompound()) //&& item.equals(new ItemStack(AunisItems.PAGE_NOTEBOOK_ITEM)))
            compound = item.getTagCompound();

        for (SymbolMilkyWayEnum symbol : SymbolMilkyWayEnum.values()) {
            setColorByAddress(te, rendererState, compound, SymbolTypeEnum.MILKYWAY, symbol);
            rendererDispatcher.renderEngine.bindTexture(((DHDMilkyWayRendererState) rendererState).getButtonTexture(symbol, rendererState.getBiomeOverlay()));
            ModelLoader.getModel(symbol.modelResource).render();
        }
    }

    @Override
    public void renderDHD(DHDAbstractRendererState rendererState){
        ElementEnum.MILKYWAY_DHD.bindTextureAndRender(rendererState.getBiomeOverlay());
    }

    @Override
    public Block getDHDBlock(){
        return AunisBlocks.DHD_BLOCK;
    }
}
