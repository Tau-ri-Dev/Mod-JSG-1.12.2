package tauri.dev.jsg.worldgen.structures;

import net.minecraft.util.Rotation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraftforge.fml.common.FMLCommonHandler;

public interface IStructure {
    PlacementSettings defaultSettings = new PlacementSettings().setIgnoreStructureBlock(false).setRotation(Rotation.NONE).setIgnoreEntities(false);
}
