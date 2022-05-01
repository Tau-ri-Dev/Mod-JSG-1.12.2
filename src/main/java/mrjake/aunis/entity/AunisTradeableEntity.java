package mrjake.aunis.entity;

import mrjake.aunis.entity.trading.ITradeList;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.IMerchant;
import net.minecraft.world.World;

import java.util.List;

public abstract class AunisTradeableEntity extends EntityCreature implements IMerchant {
    public AunisTradeableEntity(World worldIn) {
        super(worldIn);
    }

    public abstract boolean isTrading();
    public abstract void populateBuyingList();
    public abstract List<ITradeList> getTrades();
}
