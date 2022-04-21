package mrjake.aunis.worldgen.structures;

import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public interface IStructure {
    public static final WorldServer worldServer = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0);
}
