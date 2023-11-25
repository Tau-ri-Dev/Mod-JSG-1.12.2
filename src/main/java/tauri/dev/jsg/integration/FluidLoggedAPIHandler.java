package tauri.dev.jsg.integration;

import git.jbredwards.fluidlogged_api.api.event.FluidloggableEvent;
import git.jbredwards.fluidlogged_api.api.event.FluidloggedEvent;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.block.stargate.StargateAbstractBaseBlock;
import tauri.dev.jsg.block.stargate.StargateAbstractMemberBlock;
import tauri.dev.jsg.block.stargate.StargateClassicMemberBlock;
import tauri.dev.jsg.util.main.JSGProps;

/**
 * Handler of fluid logging event fired by FluidLogged-API mod
 * <p>
 * Used as optional dependency
 */
public class FluidLoggedAPIHandler {

    public static Block[] LOGGED_BLOCKS = {
            JSGBlocks.STARGATE_ORLIN_BASE_BLOCK,
            JSGBlocks.STARGATE_MILKY_WAY_BASE_BLOCK,
            JSGBlocks.STARGATE_PEGASUS_BASE_BLOCK,
            JSGBlocks.STARGATE_UNIVERSE_BASE_BLOCK,

            JSGBlocks.STARGATE_ORLIN_MEMBER_BLOCK,
            JSGBlocks.STARGATE_MILKY_WAY_MEMBER_BLOCK,
            JSGBlocks.STARGATE_PEGASUS_MEMBER_BLOCK,
            JSGBlocks.STARGATE_UNIVERSE_MEMBER_BLOCK,

            JSGBlocks.DHD_BLOCK,
            JSGBlocks.DHD_PEGASUS_BLOCK,

            JSGBlocks.TR_CONTROLLER_GOAULD_BLOCK,

            JSGBlocks.ZPM,
            JSGBlocks.ZPM_HUB,
            JSGBlocks.ZPM_SLOT,

            JSGBlocks.ZPM_CREATIVE,

            JSGBlocks.CAPACITOR_BLOCK_EMPTY,

            JSGBlocks.MACHINE_CHAMBER,
            JSGBlocks.MACHINE_ASSEMBLER,
            JSGBlocks.MACHINE_ORE_WASHING,
            JSGBlocks.MACHINE_PCB_FABRICATOR,

            JSGBlocks.DESTINY_COUNTDOWN_BLOCK,
            JSGBlocks.ANCIENT_SIGN_BLOCK,
    };


    private boolean checkNotGate(IBlockState state, Block block){
        if (block instanceof StargateAbstractBaseBlock || block instanceof StargateAbstractMemberBlock) {
            // Non-merged gates should not be fluid loggable
            if (state.getValue(JSGProps.RENDER_BLOCK)) {
                return true;
            }

            // Gate blocks with camo should not be also water loggable
            if (block instanceof StargateClassicMemberBlock) {
                IBlockState camoBlockState = ((IExtendedBlockState) state).getValue(JSGProps.CAMO_BLOCKSTATE);
                return camoBlockState != null && camoBlockState.getBlock() != Blocks.AIR && camoBlockState.getBlock() != block;
            }
        }
        return false;
    }

    @SubscribeEvent
    public void onLogCheck(FluidloggableEvent event) {
        for (Block b : LOGGED_BLOCKS) {
            if (event.state.getBlock() == b) {
                if(checkNotGate(event.state, b)) return;
                event.setResult(Event.Result.ALLOW);
                return;
            }
        }
    }

    @SubscribeEvent
    public void onFluidLogged(FluidloggedEvent event) {
        for (Block b : LOGGED_BLOCKS) {
            if (event.here.getBlock() == b) {
                if(checkNotGate(event.here, b)) return;
                event.setResult(Event.Result.ALLOW);
                return;
            }
        }
    }
}
