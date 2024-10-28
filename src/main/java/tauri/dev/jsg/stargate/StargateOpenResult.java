package tauri.dev.jsg.stargate;

public enum StargateOpenResult {
	OK,
	NOT_ENOUGH_POWER,
	ADDRESS_MALFORMED,
	ABORTED,
	ABORTED_BY_EVENT,
	CALLER_HUNG_UP,
	GATE_BURIED,
	SYMBOL_ADDED;
	
	public boolean ok() {
		return this == OK;
	}
}
