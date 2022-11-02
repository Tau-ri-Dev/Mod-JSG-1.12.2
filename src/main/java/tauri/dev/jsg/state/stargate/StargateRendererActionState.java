package tauri.dev.jsg.state.stargate;

import io.netty.buffer.ByteBuf;
import tauri.dev.jsg.stargate.EnumIrisState;
import tauri.dev.jsg.stargate.EnumIrisType;
import tauri.dev.jsg.state.State;

import java.util.HashMap;
import java.util.Map;

public class StargateRendererActionState extends State {
    public static final StargateRendererActionState STARGATE_HORIZON_WIDEN_ACTION = new StargateRendererActionState(EnumGateAction.STARGATE_HORIZON_WIDEN);
    public static final StargateRendererActionState STARGATE_HORIZON_SHRINK_ACTION = new StargateRendererActionState(EnumGateAction.STARGATE_HORIZON_SHRINK);

    public static enum EnumGateAction {
        CHEVRON_ACTIVATE(1),
        OPEN_GATE(3),
        CLOSE_GATE(4),
        CLEAR_CHEVRONS(5),
        LIGHT_UP_CHEVRONS(6),
        STARGATE_HORIZON_WIDEN(7),    // Used for rendering
        STARGATE_HORIZON_SHRINK(8),    // Event horizon killing box
        CHEVRON_OPEN(9),
        CHEVRON_CLOSE(10),
        CHEVRON_ACTIVATE_BOTH(11),
        CHEVRON_DIM(12),
        IRIS_UPDATE(13),

        ACTIVATE_GLYPH(14),
        HEAT_UPDATE(15);

        public final int actionID;
        private static final Map<Integer, EnumGateAction> map = new HashMap<Integer, EnumGateAction>();

        private EnumGateAction(int actionID) {
            this.actionID = actionID;
        }

        static {
            for (EnumGateAction action : EnumGateAction.values()) {
                map.put(action.actionID, action);
            }
        }

        public static EnumGateAction valueOf(int actionID) {
            return map.get(actionID);
        }
    }

    public StargateRendererActionState() {
    }

    public EnumGateAction action;
    public int chevronCount = 0;
    public boolean modifyFinal = false;
    public EnumIrisState irisState = null;
    public EnumIrisType irisType = null;
    public long irisAnimation = 0;
    public double irisHeat = 0;
    public double gateHeat = 0;

    public StargateRendererActionState(EnumGateAction action) {
        this.action = action;
    }

    public StargateRendererActionState(EnumGateAction action, int chevronCount, boolean modifyFinal) {
        this.action = action;
        this.chevronCount = chevronCount;
        this.modifyFinal = modifyFinal;
    }

    public StargateRendererActionState(double irisHeat, double gateHeat) {
        this.action = EnumGateAction.HEAT_UPDATE;
        this.irisHeat = irisHeat;
        this.gateHeat = gateHeat;
    }

    public StargateRendererActionState(EnumGateAction action, int chevronCount, boolean modifyFinal, EnumIrisType irisType, EnumIrisState irisState, long irisAnimation) {
        this.action = action;
        this.chevronCount = chevronCount;
        this.modifyFinal = modifyFinal;
        this.irisState = irisState;
        this.irisType = irisType;
        this.irisAnimation = irisAnimation;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(action.actionID);
        buf.writeInt(chevronCount);
        buf.writeBoolean(modifyFinal);
        if (irisType != null) {
            buf.writeBoolean(true);
            buf.writeByte(irisState.id);
            buf.writeByte(irisType.id);
            buf.writeLong(irisAnimation);
        } else buf.writeBoolean(false);
        buf.writeDouble(irisHeat);
        buf.writeDouble(gateHeat);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        action = EnumGateAction.valueOf(buf.readInt());
        chevronCount = buf.readInt();
        modifyFinal = buf.readBoolean();
        if (buf.readBoolean()) {
            irisState = EnumIrisState.getValue(buf.readByte());
            irisType = EnumIrisType.byId(buf.readByte());
            irisAnimation = buf.readLong();
        }
        irisHeat = buf.readDouble();
        gateHeat = buf.readDouble();
    }

}
