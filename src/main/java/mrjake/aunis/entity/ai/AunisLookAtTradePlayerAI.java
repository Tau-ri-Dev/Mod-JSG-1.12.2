package mrjake.aunis.entity.ai;

import mrjake.aunis.entity.AunisTradeableEntity;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;

public class AunisLookAtTradePlayerAI extends EntityAIWatchClosest {
    private final AunisTradeableEntity entity;

    public AunisLookAtTradePlayerAI(AunisTradeableEntity entityIn) {
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
