package tauri.dev.jsg.entity;

import tauri.dev.jsg.entity.trading.ITradeList;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.IMerchant;
import net.minecraft.world.World;

import java.util.List;

public abstract class JSGTradeableEntity extends EntityCreature implements IMerchant {
    public JSGTradeableEntity(World worldIn) {
        super(worldIn);
    }

    public abstract boolean isTrading();
    public abstract void populateBuyingList();
    public abstract List<ITradeList> getTrades();
}
