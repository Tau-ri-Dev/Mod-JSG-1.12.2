package tauri.dev.jsg.renderer.zpm;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tauri.dev.jsg.item.energy.ZPMItemBlockCreative;
import tauri.dev.jsg.loader.ElementEnum;
import tauri.dev.jsg.loader.texture.TextureLoader;
import tauri.dev.jsg.tileentity.energy.ZPMTile;
import tauri.dev.jsg.util.JSGTextureLightningHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ZPMRenderer extends TileEntitySpecialRenderer<ZPMTile> {
    public enum ZPMModelType {
        NORMAL(0, 0f, 0f, 0f),
        CREATIVE(1, 0.9f, 0f, 1f),
        EXPLOSIVE(2, 0.615f, 0.976f, 0.952f);

        public final int id;
        public final float[] color;

        ZPMModelType(int id, float... color) {
            this.id = id;
            this.color = color;
        }

        public static ZPMModelType byId(int id) {
            if (id < 0) return null;
            if (id > (ZPMModelType.values().length - 1)) return null;
            for (ZPMModelType z : ZPMModelType.values()) {
                if (z.id == id)
                    return z;
            }
            return null;
        }

        public static ZPMModelType byStack(ItemStack stack) {
            if (stack.getItem() instanceof ZPMItemBlockCreative)
                return CREATIVE;

            if (stack.hasTagCompound()) {
                NBTTagCompound tag = stack.getTagCompound();
                if (tag != null && tag.hasKey("corrupted") && tag.getBoolean("corrupted")) {
                    return EXPLOSIVE;
                }
            }

            return NORMAL;
        }
    }

    @Override
    public void render(@Nonnull ZPMTile te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        int level = te.getPowerLevel();
        if (level > 5) level = 5;
        if (level < 0) level = 0;
        GlStateManager.translate(0.32, 0.27, 0.43);
        renderZPM(te.getWorld(), te.getPos(), level, 0.8f, te.getType());
        GlStateManager.popMatrix();
    }

    public static void renderZPM(World world, BlockPos pos, int powerLevel, float size, ZPMModelType type) {
        renderZPM(world, pos, powerLevel, size, false, type);
    }

    public static void renderZPM(World world, BlockPos pos, int powerLevel, float size, boolean on, @Nullable ZPMModelType type) {
        if (powerLevel < 0) return;
        if(type == null) type = ZPMModelType.NORMAL;
        final float[] color = type.color;

        GlStateManager.pushMatrix();
        GlStateManager.color(1 + color[0], 1 + color[1],  1 + color[2]);
        JSGTextureLightningHelper.lightUpTexture(1f);
        GlStateManager.scale(size, size, size);
        TextureLoader.getTexture(TextureLoader.getTextureResource("zpm/zpm" + powerLevel + (on ? "" : "_off") + ".png")).bindTexture();
        ElementEnum.ZPM.render();
        JSGTextureLightningHelper.resetLight(world, pos);
        GlStateManager.popMatrix();
        GlStateManager.color(1, 1, 1);
    }
}