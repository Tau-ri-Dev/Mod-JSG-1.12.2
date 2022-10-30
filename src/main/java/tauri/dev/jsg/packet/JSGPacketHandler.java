package tauri.dev.jsg.packet;

import tauri.dev.jsg.item.linkable.dialer.UniverseDialerActionPacketToServer;
import tauri.dev.jsg.item.linkable.dialer.UniverseDialerActionPacketToServer.UniverseDialerActionPacketServerHandler;
import tauri.dev.jsg.item.oc.ItemOCProgramToServer;
import tauri.dev.jsg.item.oc.ItemOCProgramToServer.ItemOCProgramServerHandler;
import tauri.dev.jsg.item.linkable.gdo.GDOActionPacketToServer;
import tauri.dev.jsg.item.linkable.gdo.GDOActionPacketToServer.GDOActionPacketServerHandler;
import tauri.dev.jsg.item.notebook.NotebookActionPacketToServer;
import tauri.dev.jsg.item.notebook.NotebookActionPacketToServer.NotebookActionPacketServerHandler;
import tauri.dev.jsg.item.notebook.PageNotebookSetNameToServer;
import tauri.dev.jsg.item.notebook.PageNotebookSetNameToServer.PageNotebookSetNameServerHandler;
import tauri.dev.jsg.packet.ChangeRedstoneModeToServer.ChangeRedstoneModeServerHandler;
import tauri.dev.jsg.packet.SetOpenTabToServer.SetOpenTabServerHandler;
import tauri.dev.jsg.packet.gui.entry.EntryActionToServer;
import tauri.dev.jsg.packet.gui.entry.OCActionToServer;
import tauri.dev.jsg.packet.stargate.*;
import tauri.dev.jsg.packet.stargate.DHDButtonClickedToServer.DHDButtonClickedServerHandler;
import tauri.dev.jsg.packet.stargate.DHDPegasusButtonClickedToServer.DHDPegasusButtonClickedServerHandler;
import tauri.dev.jsg.packet.transportrings.SaveRingsParametersToServer;
import tauri.dev.jsg.packet.transportrings.StartPlayerFadeOutToClient;
import tauri.dev.jsg.packet.transportrings.TRControllerActivatedToServer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class JSGPacketHandler {
	public static SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("jsg");
	
	private static int id = 0;
	
	public static void registerPackets() {
		// serverside packets
		INSTANCE.registerMessage(DHDButtonClickedServerHandler.class, DHDButtonClickedToServer.class, id, Side.SERVER); id++;
		INSTANCE.registerMessage(DHDPegasusButtonClickedServerHandler.class, DHDPegasusButtonClickedToServer.class, id, Side.SERVER); id++;
		INSTANCE.registerMessage(StargateMotionToServer.MotionServerHandler.class, StargateMotionToServer.class, id, Side.SERVER); id++;
		INSTANCE.registerMessage(StateUpdateRequestToServer.StateUpdateServerHandler.class, StateUpdateRequestToServer.class, id, Side.SERVER); id++;
		INSTANCE.registerMessage(SaveRingsParametersToServer.SaveRingsParametersServerHandler.class, SaveRingsParametersToServer.class, id, Side.SERVER); id++;
		INSTANCE.registerMessage(TRControllerActivatedToServer.TRControllerActivatedServerHandler.class, TRControllerActivatedToServer.class, id, Side.SERVER); id++;
		INSTANCE.registerMessage(SetOpenTabServerHandler.class, SetOpenTabToServer.class, id, Side.SERVER); id++;
		INSTANCE.registerMessage(UniverseDialerActionPacketServerHandler.class, UniverseDialerActionPacketToServer.class, id, Side.SERVER); id++;
		INSTANCE.registerMessage(ItemOCProgramServerHandler.class, ItemOCProgramToServer.class, id, Side.SERVER); id++;
		INSTANCE.registerMessage(GDOActionPacketServerHandler.class, GDOActionPacketToServer.class, id, Side.SERVER); id++;
		INSTANCE.registerMessage(BeamerChangeRoleToServer.BeamerChangeRoleServerHandler.class, BeamerChangeRoleToServer.class, id, Side.SERVER); id++;
		INSTANCE.registerMessage(ChangeRedstoneModeServerHandler.class, ChangeRedstoneModeToServer.class, id, Side.SERVER); id++;
		INSTANCE.registerMessage(BeamerChangedLevelsToServer.BeamerChangedLevelsServerHandler.class, BeamerChangedLevelsToServer.class, id, Side.SERVER); id++;
		INSTANCE.registerMessage(BeamerChangedInactivityToServer.BeamerChangedInactivityServerHandler.class, BeamerChangedInactivityToServer.class, id, Side.SERVER); id++;
		INSTANCE.registerMessage(NotebookActionPacketServerHandler.class, NotebookActionPacketToServer.class, id, Side.SERVER); id++;
		INSTANCE.registerMessage(PageNotebookSetNameServerHandler.class, PageNotebookSetNameToServer.class, id, Side.SERVER); id++;
		INSTANCE.registerMessage(EntryActionToServer.EntryActionServerHandler.class, EntryActionToServer.class, id, Side.SERVER); id++;
		INSTANCE.registerMessage(OCActionToServer.OCActionServerHandler.class, OCActionToServer.class, id, Side.SERVER); id++;
		INSTANCE.registerMessage(SaveIrisCodeToServer.SaveIrisCodeToServerHandler.class, SaveIrisCodeToServer.class, id, Side.SERVER); id++;
		INSTANCE.registerMessage(SaveConfigToServer.SaveConfigToServerHandler.class, SaveConfigToServer.class, id, Side.SERVER); id++;
		INSTANCE.registerMessage(ZPMHubAnimationToServer.ZPMHubAnimationToServerHandler.class, ZPMHubAnimationToServer.class, id, Side.SERVER); id++;

		// clientside packets
		INSTANCE.registerMessage(StargateMotionToClient.RetrieveMotionClientHandler.class, StargateMotionToClient.class, id, Side.CLIENT); id++;
		INSTANCE.registerMessage(StartPlayerFadeOutToClient.StartPlayerFadeOutToClientHandler.class, StartPlayerFadeOutToClient.class, id, Side.CLIENT); id++;
		INSTANCE.registerMessage(StateUpdatePacketToClient.StateUpdateClientHandler.class, StateUpdatePacketToClient.class, id, Side.CLIENT); id++;
		INSTANCE.registerMessage(SoundPositionedPlayToClient.PlayPositionedSoundClientHandler.class, SoundPositionedPlayToClient.class, id, Side.CLIENT); id++;
	}
}
