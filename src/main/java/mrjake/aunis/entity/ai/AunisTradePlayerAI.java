package mrjake.aunis.entity.ai;

import mrjake.aunis.entity.AunisTradeableEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;

public class AunisTradePlayerAI extends EntityAIBase {
    private final AunisTradeableEntity entity;

    public AunisTradePlayerAI(AunisTradeableEntity entityIn)
    {
        this.entity = entityIn;
        this.setMutexBits(5);
    }

    public boolean shouldExecute()
    {
        if (!this.entity.isEntityAlive())
        {
            return false;
        }
        else if (this.entity.isInWater())
        {
            return false;
        }
        else if (!this.entity.onGround)
        {
            return false;
        }
        else if (this.entity.velocityChanged)
        {
            return false;
        }
        else
        {
            EntityPlayer entityplayer = this.entity.getCustomer();

            if (entityplayer == null)
            {
                return false;
            }
            else if (this.entity.getDistanceSq(entityplayer) > 16.0D)
            {
                return false;
            }
            else
            {
                return entityplayer.openContainer != null;
            }
        }
    }

    public void startExecuting()
    {
        this.entity.getNavigator().clearPath();
    }

    public void resetTask()
    {
        this.entity.setCustomer(null);
    }
}
