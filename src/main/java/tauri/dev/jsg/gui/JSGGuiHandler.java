package tauri.dev.jsg.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import tauri.dev.jsg.gui.container.beamer.BeamerContainer;
import tauri.dev.jsg.gui.container.beamer.BeamerContainerGui;
import tauri.dev.jsg.gui.container.capacitor.CapacitorContainer;
import tauri.dev.jsg.gui.container.capacitor.CapacitorContainerGui;
import tauri.dev.jsg.gui.container.dhd.DHDMilkyWayContainer;
import tauri.dev.jsg.gui.container.dhd.DHDMilkyWayContainerGui;
import tauri.dev.jsg.gui.container.dhd.DHDPegasusContainer;
import tauri.dev.jsg.gui.container.dhd.DHDPegasusContainerGui;
import tauri.dev.jsg.gui.container.machine.assembler.AssemblerContainer;
import tauri.dev.jsg.gui.container.machine.assembler.AssemblerContainerGui;
import tauri.dev.jsg.gui.container.machine.crystalchamber.CrystalChamberContainer;
import tauri.dev.jsg.gui.container.machine.crystalchamber.CrystalChamberContainerGui;
import tauri.dev.jsg.gui.container.stargate.StargateContainer;
import tauri.dev.jsg.gui.container.stargate.StargateContainerGui;
import tauri.dev.jsg.gui.container.transportrings.TRContainer;
import tauri.dev.jsg.gui.container.transportrings.TRGui;

public class JSGGuiHandler implements IGuiHandler {

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

        }

        return null;
    }

}
