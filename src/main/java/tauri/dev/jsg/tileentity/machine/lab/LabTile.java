package tauri.dev.jsg.tileentity.machine.lab;

import tauri.dev.jsg.machine.AbstractMachineRecipe;
import tauri.dev.jsg.power.stargate.StargateAbstractEnergyStorage;
import tauri.dev.jsg.renderer.machine.AbstractMachineRendererState;
import tauri.dev.jsg.state.State;
import tauri.dev.jsg.state.StateTypeEnum;
import tauri.dev.jsg.tileentity.machine.AbstractMachineTile;
import tauri.dev.jsg.util.JSGItemStackHandler;

public class LabTile extends AbstractMachineTile {
    @Override
    public State getState(StateTypeEnum stateType) {
        return null;
    }

    @Override
    public State createState(StateTypeEnum stateType) {
        return null;
    }

    @Override
    public void setState(StateTypeEnum stateType, State state) {

    }

    @Override
    public JSGItemStackHandler getJSGItemHandler() {
        return null;
    }

    @Override
    protected void playLoopSound(boolean stop) {

    }

    @Override
    protected void playSound(boolean start) {

    }

    @Override
    public AbstractMachineRecipe getRecipeIfPossible() {
        return null;
    }

    @Override
    public StargateAbstractEnergyStorage getEnergyStorage() {
        return null;
    }

    @Override
    public AbstractMachineRendererState getRendererState() {
        return null;
    }
}
