package tauri.dev.jsg.entity.ai;

import tauri.dev.jsg.entity.JSGTradeableEntity;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;

public class JSGLookAtTradePlayerAI extends EntityAIWatchClosest {
    private final JSGTradeableEntity entity;

    public JSGLookAtTradePlayerAI(JSGTradeableEntity entityIn) {
        super(entityIn, EntityPlayer.class, 8.0F);
        this.entity = entityIn;
    }

    public boolean shouldExecute() {
        if (this.entity.isTrading()) {
            this.closestEntity = this.entity.getCustomer();
            return true;
        } else {
            return false;
        }
    }
}
