package mrjake.aunis.stargate;

import java.util.HashMap;
import java.util.Map;

public enum EnumStargateState {
	IDLE(0),
	DIALING(1),
	DIALING_COMPUTER(2),
	ENGAGED(3),
	ENGAGED_INITIATING(4),
	UNSTABLE(5),
	FAILING(6),
	INCOMING(7);

	
	public int id;
	
	private EnumStargateState(int id) {
		this.id = id;
	}
	
	public boolean idle() {
		return this == IDLE;
	}

	public boolean incoming() {
		return this == INCOMING;
	}
	
	public boolean engaged() {
		return this == ENGAGED || this == ENGAGED_INITIATING;
	}
	
	public boolean initiating() {
		return this == ENGAGED_INITIATING;
	}
	
	public boolean dialingComputer() {
		return this == DIALING_COMPUTER;
	}

	public boolean failing() {
		return this == FAILING;
	}

	public boolean dialing() {
		return this == DIALING || this == DIALING_COMPUTER;
	}

	public boolean dialingDHD() {
		return this == DIALING;
	}
	
	private static Map<Integer, EnumStargateState> idMap = new HashMap<>();
	static {
		for (EnumStargateState state : values())
			idMap.put(state.id, state);
	}
	
	public static EnumStargateState valueOf(int id) {
		return idMap.get(id);
	}
}
