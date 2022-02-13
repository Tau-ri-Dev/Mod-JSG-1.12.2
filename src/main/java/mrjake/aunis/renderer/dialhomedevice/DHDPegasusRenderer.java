package mrjake.aunis.renderer.dialhomedevice;

import mrjake.aunis.block.AunisBlocks;
import mrjake.aunis.item.AunisItems;
import mrjake.aunis.loader.ElementEnum;
import mrjake.aunis.loader.model.ModelLoader;
import mrjake.aunis.stargate.network.SymbolPegasusEnum;
import mrjake.aunis.stargate.network.SymbolTypeEnum;
import mrjake.aunis.tileentity.dialhomedevice.DHDAbstractTile;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DHDPegasusRenderer extends DHDAbstractRenderer {
  @Override
  public void renderSymbols(DHDAbstractTile te, DHDAbstractRendererState rendererState, World world, BlockPos lightPos){
    NBTTagCompound compound = null;
    ItemStack item = Minecraft.getMinecraft().player.getHeldItem(EnumHand.MAIN_HAND);
    if(item.hasTagCompound()) //&& item.equals(new ItemStack(AunisItems.PAGE_NOTEBOOK_ITEM)))
      compound = item.getTagCompound();

    for (SymbolPegasusEnum symbol : SymbolPegasusEnum.values()) {
      setColorByAddress(te, rendererState, compound, SymbolTypeEnum.PEGASUS, symbol);
      rendererDispatcher.renderEngine.bindTexture(((DHDPegasusRendererState) rendererState).getButtonTexture(symbol, rendererState.getBiomeOverlay()));
      ModelLoader.getModel(symbol.modelResource).render();
    }
  }

  @Override
  public void renderDHD(DHDAbstractRendererState rendererState){
    ElementEnum.PEGASUS_DHD.bindTextureAndRender(rendererState.getBiomeOverlay());
  }

  @Override
  public Block getDHDBlock(){
    return AunisBlocks.DHD_PEGASUS_BLOCK;
  }
}
