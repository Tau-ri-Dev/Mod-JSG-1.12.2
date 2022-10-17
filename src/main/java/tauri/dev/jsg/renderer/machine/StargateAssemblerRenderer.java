package tauri.dev.jsg.renderer.machine;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import tauri.dev.jsg.machine.ArmPos;
import tauri.dev.jsg.tileentity.machine.StargateAssemblerTile;

import static tauri.dev.jsg.block.JSGBlocks.SG_ASSEMBLER;

public class StargateAssemblerRenderer extends TileEntitySpecialRenderer<StargateAssemblerTile> {

    @Override
    public void render(StargateAssemblerTile te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        StargateAssemblerRendererState rendererState = te.getRendererStateClient();

        if (rendererState != null) {
            IBlockState state = te.getWorld().getBlockState(te.getPos()).getActualState(te.getWorld(), te.getPos());
            if (state.getBlock() != SG_ASSEMBLER) return;

            long tick = te.getWorld().getTotalWorldTime();
            ArmPos currentPos;
            for(ArmPos pos : rendererState.positions){
                currentPos = pos;
            }

            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);

            GlStateManager.popMatrix();
        }
    }
}
