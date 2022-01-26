package mrjake.aunis.renderer.energy;

import io.netty.buffer.ByteBuf;
import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import mrjake.aunis.state.State;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class ZPMHubRendererState extends State {

    public ZPMHubRendererState() {}

    public ZPMHubRendererState initClient(BlockPos pos, long tick, boolean isPutting, int zpmsCount, int zpmAnimated, long energyTransferedLastTick, ArrayList<Integer> lastZPMPowerLevel, ArrayList<Integer> lastZPMPower) {
        this.pos = pos;
        this.animationStart = tick;
        this.isPutting = isPutting;
        this.zpmsCount = zpmsCount;
        this.zpmAnimated = zpmAnimated;
        this.energyTransferedLastTick = energyTransferedLastTick;
        this.lastZPMPowerLevel = lastZPMPowerLevel;
        this.lastZPMPower = lastZPMPower;

        return this;
    }


    // Global
    // Not saved
    //private final Map<Integer, Integer> ZMP_STATE_MAP = new HashMap<>(3);
    public BlockPos pos;

    // zpmsActive
    // Saved
    public int zpmsActive;
    public long animationStart;
    public boolean isPutting;
    public int zpmsCount;
    public int zpmAnimated;
    public long energyTransferedLastTick;
    public ArrayList<Integer> lastZPMPowerLevel = new ArrayList<Integer>(3);
    public ArrayList<Integer> lastZPMPower = new ArrayList<Integer>(3);

    public BiomeOverlayEnum getBiomeOverlay() {
        return BiomeOverlayEnum.NORMAL;
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(zpmsActive);
        buf.writeLong(animationStart);
        buf.writeBoolean(isPutting);
        buf.writeInt(zpmsCount);
        buf.writeInt(zpmAnimated);
        buf.writeLong(energyTransferedLastTick);

        for(int i = 0; i < 3; i++){
            if(lastZPMPower.size() < i+1) lastZPMPower.add(-1);
            if(lastZPMPowerLevel.size() < i+1) lastZPMPowerLevel.add(0);

            buf.writeInt(lastZPMPower.get(i));
            buf.writeInt(lastZPMPowerLevel.get(i));
        }
    }

    public void fromBytes(ByteBuf buf) {
        zpmsActive = buf.readInt();
        animationStart = buf.readLong();
        isPutting = buf.readBoolean();
        zpmsCount = buf.readInt();
        zpmAnimated = buf.readInt();
        energyTransferedLastTick = buf.readLong();

        lastZPMPower.clear();
        lastZPMPowerLevel.clear();
        for(int i = 0; i < 3; i++){
            lastZPMPower.add(buf.readInt());
            lastZPMPowerLevel.add(buf.readInt());
        }
    }
}
