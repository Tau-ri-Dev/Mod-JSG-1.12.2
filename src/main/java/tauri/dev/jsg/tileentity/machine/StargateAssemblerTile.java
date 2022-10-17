package tauri.dev.jsg.tileentity.machine;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.machine.ArmPos;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.StateUpdatePacketToClient;
import tauri.dev.jsg.packet.StateUpdateRequestToServer;
import tauri.dev.jsg.renderer.machine.StargateAssemblerRendererState;
import tauri.dev.jsg.state.State;
import tauri.dev.jsg.state.StateProviderInterface;
import tauri.dev.jsg.state.StateTypeEnum;
import tauri.dev.jsg.tileentity.util.IUpgradable;

public class StargateAssemblerTile extends TileEntity implements IUpgradable, StateProviderInterface, ITickable {

    protected NetworkRegistry.TargetPoint targetPoint;

    public static final ArmPos[] ARM_POSITIONS = {
            new ArmPos(0.05f, 0f, 0.05f, 0.03f),
            new ArmPos(0, 0, 0, 0.03f) // to the start
    };

    //todo(Mine): Temporally solution
    private static final float ANIMATION_LENGTH = getAnimationLength();
    private static float getAnimationLength(){
        float i = 0;
        for(ArmPos pos : ARM_POSITIONS){
            i += pos.speed;
        }
        return i;
    }

    private StargateAssemblerRendererState rendererStateClient;

    private long animationStart;
    private float animationLength;
    private boolean isWorking;

    public StargateAssemblerRendererState getRendererStateClient() {
        return rendererStateClient;
    }

    @Override
    public void onLoad() {
        if (!world.isRemote)
            targetPoint = new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512);
        else
            JSGPacketHandler.INSTANCE.sendToServer(new StateUpdateRequestToServer(pos, StateTypeEnum.RENDERER_STATE));
    }

    public boolean shouldWork(){
        return false;
    }

    @Override
    public void update() {
        if (world.isRemote) return;
        if (isWorking) {
            if (!shouldWork() || (animationStart + animationLength) < world.getTotalWorldTime()) {
                stopAnimation();
            }
        }
        else if(shouldWork()){
            startAnimation(1f);
        }
    }

    public void startAnimation(float speedCoefficient) {
        this.isWorking = true;
        this.animationStart = this.world.getTotalWorldTime();
        this.animationLength = (ANIMATION_LENGTH * speedCoefficient);
        sendState(StateTypeEnum.RENDERER_STATE, new StargateAssemblerRendererState().startAnimation(animationStart, animationLength, ARM_POSITIONS));
    }

    public void stopAnimation() {
        this.isWorking = false;
        this.animationStart = -1;
        this.animationLength = -1;
        sendState(StateTypeEnum.RENDERER_STATE, new StargateAssemblerRendererState().stopAnimation());
    }

    // Gets state from server
    @Override
    public State getState(StateTypeEnum stateType) {
        if (stateType == StateTypeEnum.RENDERER_STATE) {
            return new StargateAssemblerRendererState(this.isWorking, this.animationStart, this.animationLength);
        }
        return null;
    }

    // Creates state
    @Override
    public State createState(StateTypeEnum stateType) {
        if (stateType == StateTypeEnum.RENDERER_STATE) {
            return new StargateAssemblerRendererState();
        }
        return null;
    }

    // Set state for client
    @Override
    public void setState(StateTypeEnum stateType, State state) {
        if (stateType == StateTypeEnum.RENDERER_STATE) {
            rendererStateClient = ((StargateAssemblerRendererState) state).initClient(pos);
            this.isWorking = rendererStateClient.isWorking;
            this.animationStart = rendererStateClient.animationStart;
            this.animationLength = rendererStateClient.animationLength;
        }
    }

    protected void sendState(StateTypeEnum type, State state) {
        if (world.isRemote) return;
        if (targetPoint != null) {
            JSGPacketHandler.INSTANCE.sendToAllTracking(new StateUpdatePacketToClient(pos, type, state), targetPoint);
        } else {
            JSG.logger.debug("targetPoint was null trying to send " + type + " from " + this.getClass().getCanonicalName());
        }
    }
}
