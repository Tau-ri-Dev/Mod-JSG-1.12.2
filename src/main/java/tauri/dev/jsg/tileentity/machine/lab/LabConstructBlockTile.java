package tauri.dev.jsg.tileentity.machine.lab;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import tauri.dev.jsg.block.machine.lab.ChemLabControlBlock;

public class LabConstructBlockTile extends TileEntity implements ITickable {
    public boolean isMerged = false;
    public LabTile linkedTo = null;

    public boolean isLinked() {
        return linkedTo != null;
    }

    public void showLabGui(EntityPlayer player, EnumHand hand, World world) {
        if (isLinked()) {
            Block b = world.getBlockState(linkedTo.getPos()).getBlock();
            if (b instanceof ChemLabControlBlock) {
                ((ChemLabControlBlock) b).showGuiHandler(player, hand, world, this.getPos());
            }
        }
    }

    @Override
    public void update() {
        if (!world.isRemote) {
            if (isLinked()) {
                if (world.getTileEntity(linkedTo.getPos()) == null) {
                    linkedTo = null;
                    markDirty();
                }
            }
        }
    }
}
