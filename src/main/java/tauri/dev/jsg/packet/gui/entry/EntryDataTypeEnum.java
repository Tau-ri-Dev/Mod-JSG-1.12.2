package tauri.dev.jsg.packet.gui.entry;

public enum EntryDataTypeEnum {
	PAGE,
	UNIVERSE,
	OC,
	ADMIN_CONTROLLER;
	
	boolean page() {
		return this == PAGE;
	}
	
	boolean universe() {
		return this == UNIVERSE;
	}

	boolean oc() {
		return this == OC;
	}
	boolean admin() {
		return this == ADMIN_CONTROLLER;
	}
}
