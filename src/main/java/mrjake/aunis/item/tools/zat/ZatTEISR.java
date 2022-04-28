package mrjake.aunis.item.tools.zat;

import mrjake.aunis.item.tools.EnergyWeaponTEISR;
import mrjake.aunis.loader.ElementEnum;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ZatTEISR extends EnergyWeaponTEISR {
    @Override
    public void renderByItem(ItemStack stack) {
        setPositions(1.5, -1.0, -0.3, -0.3, -1.0, -0.3, 0.8, -0.05, -0.05, 0.4, -10, 15, 0.1);
        super.renderByItem(stack);
    }

    @Nonnull
    @Override
    protected ElementEnum getModel() {
        return ElementEnum.ZAT;
    }

    @Override
    protected void setFixedTranslate() {
        GlStateManager.translate(0.37, 0.3, 0.65);
        GlStateManager.rotate(80, 0, 1, 0);
        GlStateManager.scale(0.2f, 0.2f, 0.2f);
    }

    @Override
    protected void rotate(int rotation) {
        GlStateManager.rotate(rotation, 0, 1, 0);
    }

    @Override
    protected void setSize() {
        GlStateManager.scale(0.30f, 0.30f, 0.30f);
    }
}
