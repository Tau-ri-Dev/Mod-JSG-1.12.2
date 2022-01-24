package mrjake.aunis.renderer.energy;

import mrjake.aunis.Aunis;
import mrjake.aunis.block.AunisBlocks;
import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.loader.ElementEnum;
import mrjake.aunis.loader.model.ModelLoader;
import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import mrjake.aunis.tileentity.energy.ZPMHubTile;
import mrjake.aunis.zpm.EnumZPMState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;

public class ZPMHubRenderer extends TileEntitySpecialRenderer<ZPMHubTile> {

    public static final long ANIMATION_LENGTH = 20;
    private static final double Y_MAX = 22.5;
    private static final double Y_MIN = 16.5;


    @Override
    public void render(ZPMHubTile te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        ZPMHubRendererState rendererState = (ZPMHubRendererState) te.getRendererStateClient();

        if (rendererState != null) {
            IBlockState state = te.getWorld().getBlockState(te.getPos()).getActualState(te.getWorld(), te.getPos());
            if (state.getBlock() != AunisBlocks.ZPM_HUB) return;
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);

            double scale = 0.027;

            GlStateManager.scale(scale, scale, scale);
            GlStateManager.translate(18.0, 23.5, 20);

            ElementEnum.ZPM_HUB.bindTextureAndRender(BiomeOverlayEnum.NORMAL);

            // animation
            long animationStart = rendererState.animationStart;
            boolean isPutting = rendererState.isPutting;
            int zpmAnimated = rendererState.zpmAnimated;

            long animationStage = te.getWorld().getTotalWorldTime() - animationStart;

            for(int i = 0; i < 3; i++) {
                if(te.lastZPMPower.size() < i+1) te.lastZPMPower.add(-1);
                if(te.itemStackHandler.getStackInSlot(i).isEmpty() && zpmAnimated != i+1 && !te.getZPMIsDown(i) && te.lastZPMPower.get(i) == -1) continue;
                double zy = Y_MIN;
                // todo(Mine): add support for OC
                //if(te.getZPMState(i+1) == EnumZPMState.UP) zy = Y_MAX;

                if(zpmAnimated != 0){
                    double calculated = ((Y_MAX - Y_MIN)*((float) animationStage/ANIMATION_LENGTH));
                    if(isPutting && zpmAnimated == i+1){
                        // putting zpm down
                        if(animationStage == 5) te.initZPMSound(true);
                        if(animationStage/ANIMATION_LENGTH <= 1 && (Y_MAX - calculated) > Y_MIN)
                            zy = Y_MAX - calculated;
                        else {
                            zy = Y_MIN;
                            te.setZPMStatus(zpmAnimated-1, true);
                        }
                    }
                    else if(!isPutting && zpmAnimated == i+1){
                        // putting zpm up
                        if(animationStage == 5) te.initZPMSound(false);
                        if(animationStage/ANIMATION_LENGTH <= 1 && (Y_MIN + calculated) < Y_MAX)
                            zy = Y_MIN + calculated;
                        else {
                            zy = Y_MAX;
                            te.setZPMStatus(zpmAnimated - 1, false);
                        }
                    }
                }

                //int powerLevel = (zy == Y_MIN) ? (Math.round(te.getEnergyLevelOfZPM(i)/2)) : 0;
                int powerLevel = te.lastZPMPowerLevel.get(i);
                boolean isActive = (zy == Y_MIN);

                double zx = 0;
                double zz = 0;
                switch (i){
                    case 0:
                        zx = 0.0;
                        zz = -9.25;
                        break;
                    case 1:
                        zx = -10.5;
                        zz = 8.75;
                        break;
                    case 2:
                        zx = 10.5;
                        zz = 8.75;
                        break;
                }
                renderZPM(zx, zy, zz, powerLevel, isActive);
            }

            GlStateManager.popMatrix();
        }
    }

    public void renderZPM(double xx, double xy, double xz, int powerLevel, boolean isActive) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(xx, xy, xz);
        if(powerLevel > 4){
            powerLevel = 4;
        }
        if(powerLevel < 0){
            powerLevel = 0;
        }
        rendererDispatcher.renderEngine.bindTexture(
                new ResourceLocation(Aunis.ModID, "textures/tesr/zpm/item/zpm_" + powerLevel + ((isActive) ? "_activated" : "") + ".png"));
        ModelLoader.getModel(new ResourceLocation(Aunis.ModID, "models/tesr/zpm/zpm.obj")).render();
        GlStateManager.popMatrix();
    }
}
