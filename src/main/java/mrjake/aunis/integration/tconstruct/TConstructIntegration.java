package mrjake.aunis.integration.tconstruct;

import mrjake.aunis.fluid.AunisFluids;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
        fluidList.add(AunisFluids.moltenTitanium);
        fluidList.add(AunisFluids.moltenTrinium);

        fluids = fluidList.toArray(new Fluid[0]);
        for (MoltenMaterial mm:fluidList){
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("fluid", mm.getName());
            tag.setString("ore", mm.ORE_DICT);
            tag.setBoolean("toolforge", mm.TOOL_FORGE);
//tag.setTag("alloy", alloysTagList); // you can also send an alloy with the registration (see below)

            FMLInterModComms.sendMessage("tconstruct", "integrateSmeltery", tag);
        }
    }

    public static Fluid[] fluids = null;



}
