package tauri.dev.jsg.block.stargate;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.util.main.JSGProps;

import java.util.List;

import static net.minecraft.block.BlockLiquid.LEVEL;

public class StargateClassicMemberBlockBakedModel implements IBakedModel {

    private final Block defaultBlock;
    private final IBakedModel defaultModel;

    public StargateClassicMemberBlockBakedModel(Block defaultBlock, IBakedModel defaultModel) {
        this.defaultBlock = defaultBlock;
        this.defaultModel = defaultModel;
    }

    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
//		Mouse.setGrabbed(false);

        if (state != null) {
            IBlockState camoBlockState = ((IExtendedBlockState) state).getValue(JSGProps.CAMO_BLOCKSTATE);

            if (camoBlockState != null && camoBlockState.getBlock() != Blocks.AIR && camoBlockState.getBlock() != defaultBlock) {
                IBakedModel camoBlockModel = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(camoBlockState);

                if (camoBlockModel != defaultModel) {
                    try {
                        return camoBlockModel.getQuads(state, side, rand);
                    } catch (IllegalArgumentException e) {
                        JSG.error("IllegalArgumentException in StargateClassicMemberBlockBakedModel: Unsupported block as camo");
                    }
                }
            }
        }

        return defaultModel.getQuads(state, side, rand);
    }

    @Override
    public boolean isAmbientOcclusion() {
        return defaultModel.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return defaultModel.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return defaultModel.isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return defaultModel.getParticleTexture();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return defaultModel.getOverrides();
    }
}
