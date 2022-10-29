package tauri.dev.jsg.integration;

import tauri.dev.jsg.fluid.JSGFluids;
import tauri.dev.jsg.fluid.MoltenMaterial;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.event.FMLInterModComms;

import java.util.ArrayList;
import java.util.List;

/**
 * @author matousss
 */

@Mod.EventBusSubscriber
public class TConstructIntegration {
    @Optional.Method(modid = "tconstruct")
    public static void initFluids() {
        List<MoltenMaterial> fluidList = new ArrayList<>();
        fluidList.add(JSGFluids.MOLTEN_TITANIUM);
        fluidList.add(JSGFluids.MOLTEN_TRINIUM);

        fluidList.add(JSGFluids.NAQUADAH_MOLTEN_REFINED);
        fluidList.add(JSGFluids.MOLTEN_NAQUADAH_ALLOY);

        fluids = fluidList.toArray(new Fluid[0]);
        for (MoltenMaterial mm:fluidList){
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("fluid", mm.getName());
            tag.setString("ore", mm.ORE_DICT);
            tag.setBoolean("toolforge", mm.TOOL_FORGE);

            FMLInterModComms.sendMessage("tconstruct", "integrateSmeltery", tag);
        }

    }

    public static Fluid[] fluids = null;



}
