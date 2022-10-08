package tauri.dev.jsg.config.stargate;

public class StargateDimensionConfigEntry {
	
	public int energyToOpen;
	public int keepAlive;
	public String group;

	public StargateDimensionConfigEntry(int energyToOpen, int keepAlive, String group) {
		this.energyToOpen = energyToOpen;
		this.keepAlive = keepAlive;
		this.group = group;
	}

	@Override
	public String toString() {
		return "[open="+energyToOpen+", keepAlive="+keepAlive+", group: '"+group+"']";
	}

	public boolean isGroupEqual(StargateDimensionConfigEntry other) {
		if (this.group == null)
			return false;
		
		if (other.group == null)
			return false;

		String[] groups = group.split(",");
		String[] otherGroups = other.group.split(",");
		for(int i = 0; i < groups.length; i++){
			if(otherGroups.length < i) break;
			if(otherGroups[i].equals(groups[i]))
				return true;
		}
		
		return group.equals(other.group);
	}
}
