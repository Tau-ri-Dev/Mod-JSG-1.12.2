package mrjake.aunis.renderer.stargate;

import java.util.HashMap;
import java.util.Map;

import mrjake.aunis.Aunis;
import mrjake.aunis.AunisProps;
import mrjake.aunis.loader.ElementEnum;
import mrjake.aunis.loader.texture.Texture;
import mrjake.aunis.loader.texture.TextureLoader;
import mrjake.aunis.stargate.EnumIrisState;
import mrjake.aunis.stargate.EnumIrisType;
import mrjake.aunis.stargate.EnumMemberVariant;
import mrjake.aunis.stargate.merging.StargateAbstractMergeHelper;
import mrjake.aunis.stargate.merging.StargateMilkyWayMergeHelper;
import mrjake.aunis.util.FacingToRotation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public abstract class StargateClassicRenderer<S extends StargateClassicRendererState> extends StargateAbstractRenderer<S> {


    @Override
    protected void applyLightMap(StargateClassicRendererState rendererState, double partialTicks) {
        final int chevronCount = 6;
        int skyLight = 0;
        int blockLight = 0;

        for (int i = 0; i < chevronCount; i++) {
            BlockPos blockPos = StargateMilkyWayMergeHelper.INSTANCE.getChevronBlocks().get(i).rotate(FacingToRotation.get(rendererState.facing)).add(rendererState.pos);

            skyLight += getWorld().getLightFor(EnumSkyBlock.SKY, blockPos);
            blockLight += getWorld().getLightFor(EnumSkyBlock.BLOCK, blockPos);
        }

        skyLight /= chevronCount;
        blockLight /= chevronCount;

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, blockLight * 16, skyLight * 16);
    }

    @Override
    protected Map<BlockPos, IBlockState> getMemberBlockStates(StargateAbstractMergeHelper mergeHelper, EnumFacing facing) {
        Map<BlockPos, IBlockState> map = new HashMap<>();

        for (BlockPos pos : mergeHelper.getRingBlocks())
            map.put(pos, mergeHelper.getMemberBlock().getDefaultState().withProperty(AunisProps.MEMBER_VARIANT, EnumMemberVariant.RING).withProperty(AunisProps.FACING_HORIZONTAL, facing));

        for (BlockPos pos : mergeHelper.getChevronBlocks())
            map.put(pos, mergeHelper.getMemberBlock().getDefaultState().withProperty(AunisProps.MEMBER_VARIANT, EnumMemberVariant.CHEVRON).withProperty(AunisProps.FACING_HORIZONTAL, facing));

        return map;
    }


    // ----------------------------------------------------------------------------------------
    // Chevrons

    protected abstract void renderChevron(S rendererState, double partialTicks, ChevronEnum chevron);

    protected void renderChevrons(S rendererState, double partialTicks) {
        for (ChevronEnum chevron : ChevronEnum.values())
            renderChevron(rendererState, partialTicks, chevron);

        rendererState.chevronTextureList.iterate(getWorld(), partialTicks);
    }

    // ----------------------------------------------------------------------------------------
    // Iris rendering

    protected static final ResourceLocation SHIELD_TEXTURE =
            new ResourceLocation(Aunis.ModID, "textures/tesr/iris/shield.jpg");
    protected static final ResourceLocation IRIS_TEXTURE =
            new ResourceLocation(Aunis.ModID, "textures/tesr/iris/iris_blade.jpg");

    protected ResourceLocation getIrisTexture(boolean physicsOrShield) {
        return physicsOrShield ? IRIS_TEXTURE : SHIELD_TEXTURE;
    }

    public static final int PHYSICAL_IRIS_ANIMATION_LENGTH = 60;
    public static final int SHIELD_IRIS_ANIMATION_LENGTH = 10;

    @Override
    public void renderIris(double partialTicks, Float alpha, World world, S rendererState) {
        //System.out.println(rendererState.irisAnimation);
        float irisAnimationStage = (world.getTotalWorldTime() - rendererState.irisAnimation);
        /**
         *
         * SHIELD:
         * MAX: 0.7
         * MIN: 0.0
         *
         * IRIS:
         * MAX: 1.7 - zavřený
         * MIN: 0.0 - otevřený
         *
         */
        EnumIrisState irisState = rendererState.irisState;
        EnumIrisType irisType = rendererState.irisType;
        if (irisType == null || irisState == null) {
            System.out.println("iris type/iris state is null");
            return;
        }
        if (irisType == EnumIrisType.SHIELD) {
            irisAnimationStage *= 0.7f/SHIELD_IRIS_ANIMATION_LENGTH;
            if(irisAnimationStage > 0.7f) irisAnimationStage = 0.7f;
            if(irisAnimationStage < 0) irisAnimationStage = 0;
            if (irisState == EnumIrisState.OPENING) irisAnimationStage = .7f-irisAnimationStage;
            GlStateManager.pushMatrix();

            Texture irisTexture = TextureLoader.getTexture(getIrisTexture(false));
            if (irisTexture != null) irisTexture.bindTexture();
            float tick = (float) (getWorld().getTotalWorldTime() + partialTicks);

            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.enableBlend();
            GlStateManager.translate(0, 0, 0.13);
            for (int k = 0; k < 2; k++) {
                if (k == 1) {
                    GlStateManager.rotate(180, 0, 1, 0);
                }

                StargateRendererStatic.innerCircle.render(tick, false, irisAnimationStage, 0);


                for (StargateRendererStatic.QuadStrip strip : StargateRendererStatic.quadStrips) {
                    strip.render(tick, false, irisAnimationStage, 0);
                }
            }
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
        if (irisType == EnumIrisType.IRIS_TITANIUM || irisType == EnumIrisType.IRIS_TRINIUM) {
            irisAnimationStage *= 1.7f/PHYSICAL_IRIS_ANIMATION_LENGTH;
            if(irisAnimationStage > 1.7f) irisAnimationStage = 1.7f;
            if(irisAnimationStage < 0) irisAnimationStage = 0;
            if (irisState == EnumIrisState.OPENING || irisState == EnumIrisState.OPENED) irisAnimationStage = 1.7f-irisAnimationStage;
            for (float i = 0; i < 20; i++) {
                float rotateIndex = 18f * i;

                GlStateManager.pushMatrix();
                ElementEnum.IRIS.bindTexture(rendererState.getBiomeOverlay());
                GlStateManager.rotate(rotateIndex, 0, 0, 1);
                GlStateManager.translate(-irisAnimationStage, -(irisAnimationStage * 2), 0.02);
                ElementEnum.IRIS.render();

                GlStateManager.popMatrix();
            }
        }
    }
}
