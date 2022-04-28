package mrjake.aunis.item.tools.staff;

import mrjake.aunis.item.tools.EnergyWeaponTEISR;
import mrjake.aunis.loader.ElementEnum;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class StaffTEISR extends EnergyWeaponTEISR {
    @Override
    public void renderByItem(ItemStack stack) {
        setPositions(0.0, -50.0, -10.0, -10.0, 0.0, -23.0, -23.0, -3.5, -3.5, 0.0, 90, 90, 1);
        super.renderByItem(stack);
    }

    @Nonnull
    @Override
    protected ElementEnum getModel() {
        return ElementEnum.STAFF;
    }

    @Override
    protected void setFixedTranslate() {
        GlStateManager.translate(0.6, 0.5, 0.4);
        GlStateManager.scale(0.045, 0.045, 0.045);
    }

    @Override
    protected void rotate(int rotation) {
        GlStateManager.rotate(rotation, 0, 1, 0);
    }

    @Override
    protected void setSize() {
        GlStateManager.scale(7.0f, 7.0f, 7.0f);
    }
}
