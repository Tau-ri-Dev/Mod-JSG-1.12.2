package tauri.dev.jsg.power.general;

public class EnergyRequiredToOperate {
	
	public int energyToOpen;
	public int keepAlive;

	public EnergyRequiredToOperate(int energyToOpen, int keepAlive) {
		this.energyToOpen = energyToOpen;
		this.keepAlive = keepAlive;
	}
	
	public EnergyRequiredToOperate(double energyToOpen, double keepAlive) {
		this((int)energyToOpen, (int)keepAlive);
	}

	@Override
	public String toString() {
		return "[open="+energyToOpen+", keepAlive="+keepAlive+"]";
	}

	public EnergyRequiredToOperate mul(double mul) {
		return new EnergyRequiredToOperate(energyToOpen*mul, keepAlive*mul);
	}

	public EnergyRequiredToOperate add(EnergyRequiredToOperate add) {
		return new EnergyRequiredToOperate(energyToOpen+add.energyToOpen, keepAlive+add.keepAlive);
	}

	public EnergyRequiredToOperate cap(int max) {
		return new EnergyRequiredToOperate(Math.min(energyToOpen, max), keepAlive);
	}
}
