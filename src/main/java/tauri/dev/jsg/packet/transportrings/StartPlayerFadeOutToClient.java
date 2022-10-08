package tauri.dev.jsg.packet.transportrings;

import io.netty.buffer.ByteBuf;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.renderer.transportrings.PlayerFadeOutRenderEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class StartPlayerFadeOutToClient implements IMessage {	
	public StartPlayerFadeOutToClient() {}

	
	@Override
	public void toBytes(ByteBuf buf) {
		
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		
	}
	
	
	public static class StartPlayerFadeOutToClientHandler implements IMessageHandler<StartPlayerFadeOutToClient, IMessage> {

		@Override
		public IMessage onMessage(StartPlayerFadeOutToClient message, MessageContext ctx) {
			JSG.proxy.addScheduledTaskClientSide(() -> {
				PlayerFadeOutRenderEvent.startFadeOut();
			});
			
			return null;
		}
		
	}
}
