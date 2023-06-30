package tauri.dev.jsg.packet.transportrings;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.renderer.effect.DestinyFTL;
import tauri.dev.jsg.renderer.effect.RingsWhiteFadeOut;
import tauri.dev.jsg.renderer.effect.StargateFadeOut;

import static tauri.dev.jsg.packet.transportrings.StartPlayerFadeOutToClient.EnumFadeOutEffectType.FTL_IN;

public class StartPlayerFadeOutToClient implements IMessage {
    public enum EnumFadeOutEffectType {
        RINGS(0),
        FTL_IN(1),
        FTL_OUT(2),

        STARGATE(3);

        public final int id;

        EnumFadeOutEffectType(int id) {
            this.id = id;
        }

        public static EnumFadeOutEffectType valueOf(int id) {
            for (EnumFadeOutEffectType type : EnumFadeOutEffectType.values()) {
                if (type.id == id) return type;
            }
            return RINGS;
        }
    }

    @SuppressWarnings("unused")
    // IntelliJ shit doesn't like this, but client needs it
    public StartPlayerFadeOutToClient(){

    }

    private int type;

    public StartPlayerFadeOutToClient(EnumFadeOutEffectType type) {
        this.type = type.id;
    }


    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(type);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        type = buf.readInt();
    }


    public static class StartPlayerFadeOutToClientHandler implements IMessageHandler<StartPlayerFadeOutToClient, IMessage> {

        @Override
        public IMessage onMessage(StartPlayerFadeOutToClient message, MessageContext ctx) {
            EnumFadeOutEffectType t = EnumFadeOutEffectType.valueOf(message.type);
            switch (t) {
                case RINGS:
                    JSG.proxy.addScheduledTaskClientSide(RingsWhiteFadeOut::startFadeOut);
                    break;
                case FTL_IN:
                case FTL_OUT:
                    JSG.proxy.addScheduledTaskClientSide(() -> DestinyFTL.jumpIn(t == FTL_IN));
                    break;
                case STARGATE:
                    JSG.proxy.addScheduledTaskClientSide(StargateFadeOut::startFadeOut);
                    break;
                default:
                    break;
            }
            return null;
        }

    }
}
