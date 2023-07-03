package tauri.dev.jsg.renderer.stargate;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.loader.ElementEnum;
import tauri.dev.jsg.loader.texture.Texture;
import tauri.dev.jsg.loader.texture.TextureLoader;
import tauri.dev.jsg.stargate.EnumIrisState;
import tauri.dev.jsg.stargate.EnumIrisType;
import tauri.dev.jsg.stargate.EnumMemberVariant;
import tauri.dev.jsg.stargate.merging.StargateAbstractMergeHelper;
import tauri.dev.jsg.stargate.merging.StargateMilkyWayMergeHelper;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;
import tauri.dev.jsg.util.FacingHelper;
import tauri.dev.jsg.util.JSGTextureLightningHelper;
import tauri.dev.jsg.util.main.JSGProps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class StargateClassicRenderer<S extends StargateClassicRendererState> extends StargateAbstractRenderer<S> {

    public abstract float getGateDiameter();

    public abstract double getScaleMultiplier();

    @Override
    protected void applyTransformations(StargateClassicRendererState rendererState) {
        double scale = rendererState.stargateSize.renderScale * getScaleMultiplier();
        GlStateManager.translate(0.50, ((getGateDiameter() * scale) / 2) + 0.2, 0.50);
        GlStateManager.scale(scale, scale, scale);
    }


    @Override
    protected void applyLightMap(StargateClassicRendererState rendererState, double partialTicks) {
        final int chevronCount = 6;
        ArrayList<BlockPos> list = new ArrayList<>();
        for (int i = 0; i < chevronCount; i++) {
            list.add(FacingHelper.rotateBlock(StargateMilkyWayMergeHelper.INSTANCE.getChevronBlocks().get(i), rendererState.facing, rendererState.facingVertical).add(rendererState.pos));
            //list.add(StargateMilkyWayMergeHelper.INSTANCE.getChevronBlocks().get(i).rotate(FacingHelper.getRotation(rendererState.facing)).add(rendererState.pos));
        }
        JSGTextureLightningHelper.resetLight(getWorld(), list);
    }

    @Override
    protected Map<BlockPos, IBlockState> getMemberBlockStates(StargateAbstractMergeHelper mergeHelper, EnumFacing facing, EnumFacing facingVertical) {
        Map<BlockPos, IBlockState> map = new HashMap<>();

        for (BlockPos pos : mergeHelper.getRingBlocks())
            map.put(pos, mergeHelper.getMemberBlock().getDefaultState().withProperty(JSGProps.MEMBER_VARIANT, EnumMemberVariant.RING).withProperty(JSGProps.FACING_HORIZONTAL, facing).withProperty(JSGProps.FACING_VERTICAL, facingVertical));

        for (BlockPos pos : mergeHelper.getChevronBlocks())
            map.put(pos, mergeHelper.getMemberBlock().getDefaultState().withProperty(JSGProps.MEMBER_VARIANT, EnumMemberVariant.CHEVRON).withProperty(JSGProps.FACING_HORIZONTAL, facing).withProperty(JSGProps.FACING_VERTICAL, facingVertical));

        return map;
    }


    // ----------------------------------------------------------------------------------------
    // Chevrons

    protected abstract void renderChevron(S rendererState, double partialTicks, ChevronEnum chevron, boolean onlyLight);

    protected void renderChevrons(S rendererState, double partialTicks) {

        for (ChevronEnum chevron : ChevronEnum.values()) {
            setGateHeatColor(rendererState);
            GlStateManager.pushMatrix();
            // not emissive
            renderChevron(rendererState, partialTicks, chevron, false);

            // emissive layer
            JSGTextureLightningHelper.lightUpTexture(rendererState.chevronTextureList.CHEVRON_STATE_MAP.get(chevron) / 10f);
            renderChevron(rendererState, partialTicks, chevron, true);
            applyLightMap(rendererState, partialTicks);
            GlStateManager.popMatrix();
        }

        rendererState.chevronTextureList.iterate(getWorld(), partialTicks);
    }

    // ----------------------------------------------------------------------------------------
    // Iris rendering

    protected static final ResourceLocation SHIELD_TEXTURE =
            new ResourceLocation(JSG.MOD_ID, "textures/tesr/iris/shield.jpg");

    public static final int PHYSICAL_IRIS_ANIMATION_LENGTH = 60;
    public static final int SHIELD_IRIS_ANIMATION_LENGTH = 10;

    @Override
    public void renderIris(double partialTicks, World world, S rendererState, boolean backOnly) {
        float irisAnimationStage = (world.getTotalWorldTime() - rendererState.irisAnimation);
        /*
         *
         * SHIELD:
         * MAX: 0.7
         * MIN: 0.0
         *
         * IRIS:
         * MAX: 1.7 - closed
         * MIN: 0.0 - open
         *
         */
        EnumIrisState irisState = rendererState.irisState;
        EnumIrisType irisType = rendererState.irisType;
        if (irisType == null || irisState == null) {
            //JSG.logger.debug("Iris state/type was null");
            return;
        }
        if (irisState == EnumIrisState.OPENED) return;
        if (irisType == EnumIrisType.SHIELD) {
            irisAnimationStage *= 0.7f / SHIELD_IRIS_ANIMATION_LENGTH;
            if (irisAnimationStage > 0.7f) irisAnimationStage = 0.7f;
            if (irisAnimationStage < 0) irisAnimationStage = 0;
            if (irisState == EnumIrisState.OPENING) irisAnimationStage = .7f - irisAnimationStage;
            GlStateManager.pushMatrix();

            Texture irisTexture = TextureLoader.getTexture(SHIELD_TEXTURE);
            if (irisTexture != null) irisTexture.bindTexture();
            float tick = (float) (getWorld().getTotalWorldTime() + partialTicks);

            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.enableBlend();
            GlStateManager.disableLighting();
            GlStateManager.translate(0, 0, 0.13);
            //GlStateManager.translate(0, 0, 0.13);
            for (int k = (backOnly ? 1 : 0); k < 2; k++) {
                if (k == 1 /* && !rendererState.doEventHorizonRender*/) {
                    GlStateManager.rotate(180, 0, 1, 0);
                } /*else if (k == 1) break;*/

                StargateRendererStatic.innerCircle.render(tick, false, irisAnimationStage, 0, (byte) -1);


                for (StargateRendererStatic.QuadStrip strip : StargateRendererStatic.quadStrips) {
                    strip.render(tick, false, irisAnimationStage, 0, (byte) -1);
                }
            }
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
        if ((irisType == EnumIrisType.IRIS_TITANIUM || irisType == EnumIrisType.IRIS_TRINIUM || irisType == EnumIrisType.IRIS_CREATIVE) && backOnly) {
            setIrisHeatColor(rendererState);
            irisAnimationStage *= 1.7f / PHYSICAL_IRIS_ANIMATION_LENGTH;
            if (irisAnimationStage > 1.7f) irisAnimationStage = 1.7f;
            if (irisAnimationStage < 0) irisAnimationStage = 0;
            if (irisState == EnumIrisState.OPENING)
                irisAnimationStage = 1.7f - irisAnimationStage;
            for (float i = 0; i < 20; i++) {
                float rotateIndex = 18f * i;

                GlStateManager.pushMatrix();
                GlStateManager.rotate(rotateIndex, 0, 0, 1);
                GlStateManager.translate(-irisAnimationStage, -(irisAnimationStage * 2), 0.02);
                ElementEnum.IRIS.bindTextureAndRender();
                GlStateManager.popMatrix();
            }
        }
    }

    public void setIrisHeatColor(StargateClassicRendererState rendererState, float red) {
        GlStateManager.color(1 + (red * 3F), 1, 1);
    }

    public void setIrisHeatColor(StargateClassicRendererState rendererState) {
        if (rendererState.irisHeat == -1) {
            setIrisHeatColor(rendererState, 0); // for universe gate
            return;
        }
        float red = (float) (rendererState.irisHeat / (rendererState.irisType == EnumIrisType.IRIS_TITANIUM ? StargateClassicBaseTile.IRIS_MAX_HEAT_TITANIUM : StargateClassicBaseTile.IRIS_MAX_HEAT_TRINIUM));
        if(rendererState.irisType == EnumIrisType.IRIS_CREATIVE) red = 0;
        setIrisHeatColor(rendererState, red);
        //.if(red > 0)
        //    JSGTextureLightningHelper.lightUpTexture(getWorld(), rendererState.pos, red);
    }

    public void setGateHeatColor(StargateClassicRendererState rendererState) {
        if (rendererState.gateHeat == -1) return;
        float red = (float) (rendererState.gateHeat / StargateClassicBaseTile.GATE_MAX_HEAT);
        GlStateManager.color(1 + (red * 2.7F), 1, 1);
        //.if(red > 0)
        //    JSGTextureLightningHelper.lightUpTexture(getWorld(), rendererState.pos, red);
    }
}
