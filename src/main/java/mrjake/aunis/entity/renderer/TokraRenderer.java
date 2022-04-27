package mrjake.aunis.entity.renderer;

import mrjake.aunis.Aunis;
import mrjake.aunis.entity.friendly.TokraEntity;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class TokraRenderer extends RenderLiving<TokraEntity> {

    public static final ResourceLocation TEXTURE = new ResourceLocation(Aunis.ModID, "textures/entity/tokra/default.png");

    public TokraRenderer(RenderManager manager) {
        super(manager, new ModelPlayer(0.8f, false), 0.5f);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(TokraEntity entity) {
        return TEXTURE;
    }
}
