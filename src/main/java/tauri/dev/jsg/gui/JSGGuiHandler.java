package tauri.dev.jsg.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import tauri.dev.jsg.gui.container.beamer.BeamerContainer;
import tauri.dev.jsg.gui.container.beamer.BeamerContainerGui;
import tauri.dev.jsg.gui.container.capacitor.CapacitorContainer;
import tauri.dev.jsg.gui.container.capacitor.CapacitorContainerGui;
import tauri.dev.jsg.gui.container.countdown.CountDownContainer;
import tauri.dev.jsg.gui.container.countdown.CountDownContainerGui;
import tauri.dev.jsg.gui.container.dhd.DHDMilkyWayContainer;
import tauri.dev.jsg.gui.container.dhd.DHDMilkyWayContainerGui;
import tauri.dev.jsg.gui.container.dhd.DHDPegasusContainer;
import tauri.dev.jsg.gui.container.dhd.DHDPegasusContainerGui;
import tauri.dev.jsg.gui.container.machine.assembler.AssemblerContainer;
import tauri.dev.jsg.gui.container.machine.assembler.AssemblerContainerGui;
import tauri.dev.jsg.gui.container.machine.crystalchamber.CrystalChamberContainer;
import tauri.dev.jsg.gui.container.machine.crystalchamber.CrystalChamberContainerGui;
import tauri.dev.jsg.gui.container.machine.orewashing.OreWashingContainer;
import tauri.dev.jsg.gui.container.machine.orewashing.OreWashingContainerGui;
import tauri.dev.jsg.gui.container.machine.pcbfabricator.PCBFabricatorContainer;
import tauri.dev.jsg.gui.container.machine.pcbfabricator.PCBFabricatorContainerGui;
import tauri.dev.jsg.gui.container.stargate.StargateContainer;
import tauri.dev.jsg.gui.container.stargate.StargateContainerGui;
import tauri.dev.jsg.gui.container.transportrings.TRContainer;
import tauri.dev.jsg.gui.container.transportrings.TRGui;
import tauri.dev.jsg.gui.container.zpmhub.ZPMHubContainer;
import tauri.dev.jsg.gui.container.zpmhub.ZPMHubContainerGui;
import tauri.dev.jsg.gui.container.zpmslot.ZPMSlotContainer;
import tauri.dev.jsg.gui.container.zpmslot.ZPMSlotContainerGui;

public class JSGGuiHandler implements IGuiHandler {

    public static JSGGuiHandler INSTANCE = new JSGGuiHandler();

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        boolean isOp = player.isCreative();
        switch (GuiIdEnum.valueOf(ID)) {
            case GUI_DHD:
                return new DHDMilkyWayContainer(player.inventory, world, x, y, z);

            case GUI_PEGASUS_DHD:
                return new DHDPegasusContainer(player.inventory, world, x, y, z);

            case GUI_STARGATE:
                return new StargateContainer(player.inventory, world, x, y, z, isOp);

            case GUI_CAPACITOR:
                return new CapacitorContainer(player.inventory, world, x, y, z);

            case GUI_BEAMER:
                return new BeamerContainer(player.inventory, world, x, y, z);

            case GUI_RINGS:
                return new TRContainer(player.inventory, world, x, y, z, isOp);

            case GUI_ASSEMBLER:
                return new AssemblerContainer(player.inventory, world, x, y, z);

            case GUI_CRYSTAL_CHAMBER:
                return new CrystalChamberContainer(player.inventory, world, x, y, z);

            case GUI_PCB_FABRICATOR:
                return new PCBFabricatorContainer(player.inventory, world, x, y, z);

            case GUI_ORE_WASHING:
                return new OreWashingContainer(player.inventory, world, x, y, z);

            case GUI_ZPM_HUB:
                return new ZPMHubContainer(player.inventory, world, x, y, z);

            case GUI_ZPM_SLOT:
                return new ZPMSlotContainer(player.inventory, world, x, y, z);

            case GUI_COUNTDOWN:
                return new CountDownContainer(player.inventory, world, x, y, z, isOp);
        }

        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        boolean isOp = player.isCreative();
        switch (GuiIdEnum.valueOf(ID)) {
            case GUI_DHD:
                return new DHDMilkyWayContainerGui(new DHDMilkyWayContainer(player.inventory, world, x, y, z));

            case GUI_PEGASUS_DHD:
                return new DHDPegasusContainerGui(new DHDPegasusContainer(player.inventory, world, x, y, z));

            case GUI_STARGATE:
                return new StargateContainerGui(new BlockPos(x, y, z), new StargateContainer(player.inventory, world, x, y, z, isOp));

            case GUI_CAPACITOR:
                return new CapacitorContainerGui(new CapacitorContainer(player.inventory, world, x, y, z));

            case GUI_BEAMER:
                return new BeamerContainerGui(new BeamerContainer(player.inventory, world, x, y, z));

            case GUI_RINGS:
                return new TRGui(new BlockPos(x, y, z), new TRContainer(player.inventory, world, x, y, z, isOp));

            case GUI_ASSEMBLER:
                return new AssemblerContainerGui(new AssemblerContainer(player.inventory, world, x, y, z));

            case GUI_CRYSTAL_CHAMBER:
                return new CrystalChamberContainerGui(new CrystalChamberContainer(player.inventory, world, x, y, z));

            case GUI_PCB_FABRICATOR:
                return new PCBFabricatorContainerGui(new PCBFabricatorContainer(player.inventory, world, x, y, z));

            case GUI_ORE_WASHING:
                return new OreWashingContainerGui(new OreWashingContainer(player.inventory, world, x, y, z));

            case GUI_ZPM_HUB:
                return new ZPMHubContainerGui(new ZPMHubContainer(player.inventory, world, x, y, z));

            case GUI_ZPM_SLOT:
                return new ZPMSlotContainerGui(new ZPMSlotContainer(player.inventory, world, x, y, z));

            case GUI_COUNTDOWN:
                return new CountDownContainerGui(new BlockPos(x, y, z), new CountDownContainer(player.inventory, world, x, y, z, isOp));

        }

        return null;
    }

}
