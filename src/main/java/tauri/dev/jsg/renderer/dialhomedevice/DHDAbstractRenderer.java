package tauri.dev.jsg.renderer.dialhomedevice;

import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.util.main.JSGProps;
import tauri.dev.jsg.item.notebook.NotebookItem;
import tauri.dev.jsg.renderer.BlockRenderer;
import tauri.dev.jsg.stargate.network.StargateAddress;
import tauri.dev.jsg.stargate.network.SymbolInterface;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.tileentity.dialhomedevice.DHDAbstractTile;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class DHDAbstractRenderer extends TileEntitySpecialRenderer<DHDAbstractTile> {

    protected static final BlockPos ZERO_BLOCKPOS = new BlockPos(0, 0, 0);

    @Override
    public void render(DHDAbstractTile te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        DHDAbstractRendererState rendererState = te.getRendererStateClient();

        if (rendererState != null) {
            IBlockState state = te.getWorld().getBlockState(te.getPos()).getActualState(te.getWorld(), te.getPos());
            if (state.getBlock() != getDHDBlock()) return;

            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);

            if (state.getValue(JSGProps.SNOWY)) {
                BlockRenderer.render(getWorld(), ZERO_BLOCKPOS, Blocks.SNOW_LAYER.getDefaultState(), te.getPos());
            }

            GlStateManager.translate(0.5, 0, 0.5);
            GlStateManager.rotate(rendererState.horizontalRotation, 0, 1, 0);

            renderDHD(rendererState, te);
            renderSymbols(te, rendererState, te.getWorld(), new BlockPos(x, y, z));

            GlStateManager.popMatrix();

            rendererState.iterate(getWorld(), partialTicks);
        }
    }

    public abstract void renderSymbols(DHDAbstractTile te, DHDAbstractRendererState rendererState, World world, BlockPos lightPos);

    public abstract void renderDHD(DHDAbstractRendererState rendererState, DHDAbstractTile te);

    public abstract Block getDHDBlock();

    protected void setColorByAddress(DHDAbstractTile te, DHDAbstractRendererState rendererState, NBTTagCompound compound, SymbolTypeEnum symbolType, SymbolInterface symbol) {
        GlStateManager.color(1, 1, 1, 1);
        if (compound != null && JSGConfig.notebookOptions.enablePageHint) {

            // if item is notebook item
            if (compound.hasKey("addressList"))
                compound = NotebookItem.getSelectedPageFromCompound(compound);


            if (!compound.hasKey("symbolType")) return;
            if (!compound.hasKey("address")) return;
            SymbolTypeEnum st = SymbolTypeEnum.valueOf(compound.getInteger("symbolType"));
            int maxSymbols = symbolType.getMaxSymbolsDisplay(compound.getBoolean("hasUpgrade"));
            StargateAddress stargateAddress = new StargateAddress(compound.getCompoundTag("address"));

            // check address type && button is not activated
            if (st == symbolType && !rendererState.isButtonActive(symbol) && !rendererState.isButtonActive(st.getOrigin())) {

                int activatedButtons = rendererState.getActivatedButtons();
                SymbolInterface displayedSymbol = st.getOrigin();
                if (activatedButtons < maxSymbols && !rendererState.stargateIsConnected)
                    displayedSymbol = stargateAddress.get(activatedButtons);

                // set color
                if ((stargateAddress.contains(symbol) || symbol.origin()) && displayedSymbol == symbol) {
                    if (!symbol.origin())
                        GlStateManager.color(0.5f, 1, 1, 1);
                    else
                        GlStateManager.color(0.5f, 1, 0.5f, 1);
                }
            }
        }
    }

    public NBTTagCompound getNoteBookPage() {
        NBTTagCompound compound = null;
        ItemStack item = Minecraft.getMinecraft().player.getHeldItem(EnumHand.MAIN_HAND);
        if (item.hasTagCompound())
            compound = item.getTagCompound();
        else {
            item = Minecraft.getMinecraft().player.getHeldItem(EnumHand.OFF_HAND);
            if (item.hasTagCompound())
                compound = item.getTagCompound();
        }
        return compound;
    }
}