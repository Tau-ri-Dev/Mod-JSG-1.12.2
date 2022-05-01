package mrjake.aunis.entity.renderer;

import mrjake.aunis.Aunis;
import mrjake.aunis.entity.friendly.TokraEntity;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TokraRenderer extends RenderLiving<TokraEntity> {

    public static final ResourceLocation TEXTURE = new ResourceLocation(Aunis.ModID, "textures/entity/tokra/default.png");

    public TokraRenderer(RenderManager manager) {
        super(manager, new ModelPlayer(0.0f, false), 0.5f);
        this.addLayer(new LayerBipedArmor(this));
        this.addLayer(new LayerHeldItem(this));
        this.addLayer(new LayerArrow(this));
        this.addLayer(new LayerElytra(this));
        this.addLayer(new LayerEntityOnShoulder(renderManager));
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(@Nonnull TokraEntity entity) {
        return TEXTURE;
    }
}
