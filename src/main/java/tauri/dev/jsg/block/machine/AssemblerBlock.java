package tauri.dev.jsg.block.machine;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.gui.GuiIdEnum;
import tauri.dev.jsg.item.machine.StargateAssemblerItemBlock;
import tauri.dev.jsg.tileentity.machine.StargateAssemblerTile;

import javax.annotation.Nonnull;

public class AssemblerBlock extends JSGMachineBlock {
    public static final String BLOCK_NAME = "assembler_machine_block";
    public static final int MAX_ENERGY = 9_000_000;
    public static final int MAX_ENERGY_TRANSFER = 20_000;

    public AssemblerBlock() {
        super(BLOCK_NAME);
    }

    @Override
    public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
        return new StargateAssemblerTile();
    }

    @Override
    public Class<? extends TileEntity> getTileEntityClass() {
        return StargateAssemblerTile.class;
    }

    @Override
    protected void showGui(EntityPlayer player, EnumHand hand, World world, BlockPos pos) {
        player.openGui(JSG.instance, GuiIdEnum.GUI_ASSEMBLER.id, world, pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public ItemBlock getItemBlock() {
        return new StargateAssemblerItemBlock(this);
    }
}
