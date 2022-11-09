package tauri.dev.jsg.tileentity.energy;

public class ZPMSlotTile extends ZPMHubTile {

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public int getAnimationLength(){
        return (int) Math.round(super.getAnimationLength()*0.75);
    }
}
