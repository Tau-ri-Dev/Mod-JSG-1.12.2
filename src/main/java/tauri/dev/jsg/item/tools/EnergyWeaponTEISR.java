package tauri.dev.jsg.item.tools;

import tauri.dev.jsg.item.renderer.ItemRenderHelper;
import tauri.dev.jsg.loader.ElementEnum;
import tauri.dev.jsg.renderer.biomes.BiomeOverlayEnum;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHandSide;

import javax.annotation.Nonnull;

public abstract class EnergyWeaponTEISR extends TileEntityItemStackRenderer {

    private double y = 0;
    private double x = 0;
    private double z = 0;
    private int rot = 0;

    private int R_MAX = 0;
    private int R_MIN = 0;

    private double X_MIN = 0;
    private double Y_MIN = 0;

    private double Z_MIN = 0;

    private double X_MIN2 = 0;
    private double Y_MIN2 = 0;

    private double X_MAX = 0;
    private double Y_MAX = 0;

    private double Z_MAX = 0;

    private double X_MAX2 = 0;
    private double Y_MAX2 = 0;

    private double STEP = 1;

    protected void setPositions(double xMin, double xMin2, double yMin, double yMin2, double zMin, double xMax, double xMax2, double yMax, double yMax2, double zMax, int rScoped, int rDefault, double step) {
        this.X_MIN = xMin;
        this.Y_MIN = yMin;
        this.Z_MIN = zMin;
        this.X_MIN2 = xMin2;
        this.Y_MIN2 = yMin2;

        this.X_MAX = xMax;
        this.Y_MAX = yMax;
        this.Z_MAX = zMax;
        this.X_MAX2 = xMax2;
        this.Y_MAX2 = yMax2;

        this.R_MAX = rScoped;
        this.R_MIN = rDefault;

        this.STEP = step;
    }

    @Override
    public void renderByItem(ItemStack stack) {
        if (!(stack.getItem() instanceof EnergyWeapon)) return;
        EnergyWeapon item = (EnergyWeapon) stack.getItem();

        float partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();
        ItemCameraTransforms.TransformType transformType = item.getLastTransform();

        GlStateManager.pushMatrix();

        NBTTagCompound compound = stack.getTagCompound();
        boolean isScoped = false;
        if (compound != null) {
            isScoped = compound.getBoolean("scope");
        }

        // Item frame
        if (transformType == ItemCameraTransforms.TransformType.FIXED) {
            setFixedTranslate();
        } else {
            boolean mainhand = item.getLastTransform() == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND;
            EnumHandSide handSide = mainhand ? EnumHandSide.RIGHT : EnumHandSide.LEFT;

            EntityPlayer player = Minecraft.getMinecraft().player;
            float pitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks;
            float angle = ItemRenderHelper.getMapAngleFromPitch(pitch);
            angle = 1 - angle;


            double k = -0.1;
            setPos(handSide, isScoped);
            if (handSide != EnumHandSide.RIGHT)
                k = 0;

            GlStateManager.translate(x, y, z);
            rotate(rot);
            GlStateManager.translate(0, 0.3 * angle, k * angle);

            setSize();
        }

        getModel().bindTextureAndRender(BiomeOverlayEnum.NORMAL);
        GlStateManager.popMatrix();
    }

    @Nonnull
    protected abstract ElementEnum getModel();

    protected abstract void setFixedTranslate();

    protected abstract void rotate(int rotation);

    protected abstract void setSize();

    protected void setPos(EnumHandSide handSide, boolean isScoped) {
        if (handSide == EnumHandSide.RIGHT) {
            if (!isScoped) {
                rot = R_MIN;
                if (x < X_MIN - STEP) {
                    x += STEP;
                }
                if (x > X_MIN + STEP) {
                    x -= STEP;
                }

                if (y < Y_MIN - STEP) {
                    y += STEP;
                }
                if (y > Y_MIN + STEP) {
                    y -= STEP;
                }

                if (z < Z_MIN - STEP) {
                    z += STEP;
                }
                if (z > Z_MIN + STEP) {
                    z -= STEP;
                }
            } else {
                rot = R_MAX;
                if (x < X_MAX - STEP) {
                    x += STEP;
                }
                if (x > X_MAX + STEP) {
                    x -= STEP;
                }

                if (y < Y_MAX - STEP) {
                    y += STEP;
                }
                if (y > Y_MAX + STEP) {
                    y -= STEP;
                }

                if (z < Z_MAX - STEP) {
                    z += STEP;
                }
                if (z > Z_MAX + STEP) {
                    z -= STEP;
                }
            }
        } else {
            if (!isScoped) {
                rot = R_MIN;
                if (x < X_MIN2 - STEP) {
                    x += STEP;
                }
                if (x > X_MIN2 + STEP) {
                    x -= STEP;
                }

                if (y < Y_MIN2 - STEP) {
                    y += STEP;
                }
                if (y > Y_MIN2 + STEP) {
                    y -= STEP;
                }

                if (z < Z_MIN - STEP) {
                    z += STEP;
                }
                if (z > Z_MIN + STEP) {
                    z -= STEP;
                }
            } else {
                rot = R_MAX;
                if (x < X_MAX2 - STEP) {
                    x += STEP;
                }
                if (x > X_MAX2 + STEP) {
                    x -= STEP;
                }

                if (y < Y_MAX2 - STEP) {
                    y += STEP;
                }
                if (y > Y_MAX2 + STEP) {
                    y -= STEP;
                }

                if (z < Z_MAX - STEP) {
                    z += STEP;
                }
                if (z > Z_MAX + STEP) {
                    z -= STEP;
                }
            }
        }
    }
}
