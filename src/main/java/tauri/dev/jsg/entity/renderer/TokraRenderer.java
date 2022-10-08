package tauri.dev.jsg.entity.renderer;

import tauri.dev.jsg.JSG;
import tauri.dev.jsg.entity.friendly.TokraEntity;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TokraRenderer extends RenderLiving<TokraEntity> {

    public static final ResourceLocation TEXTURE = new ResourceLocation(JSG.MOD_ID, "textures/entity/tokra/default.png");

    public TokraRenderer(RenderManager manager) {
        super(manager, new ModelPlayer(0.0f, false), 0.5f);
        this.addLayer(new LayerBipedArmor(this));
        this.addLayer(new LayerHeldItem(this));
        this.addLayer(new LayerArrow(this));
        this.addLayer(new LayerElytra(this));
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(@Nonnull TokraEntity entity) {
        return TEXTURE;
    }
}
