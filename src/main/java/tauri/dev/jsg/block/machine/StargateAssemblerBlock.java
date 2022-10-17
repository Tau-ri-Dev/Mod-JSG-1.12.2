package tauri.dev.jsg.block.machine;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.renderer.machine.StargateAssemblerRenderer;
import tauri.dev.jsg.tileentity.machine.StargateAssemblerTile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class StargateAssemblerBlock extends JSGMachineBlock {
    private static final String BLOCK_NAME = "stargate_assembler";

    public StargateAssemblerBlock() {
        super(BLOCK_NAME);
    }

    @Override
    public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
        return new StargateAssemblerTile();
    }

    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
        tooltip.add(JSG.getInProgress());
    }

    @Override
    public Class<? extends TileEntity> getTileEntityClass() {
        return StargateAssemblerTile.class;
    }

    @Override
    public TileEntitySpecialRenderer<? extends TileEntity> getTESR() {
        return new StargateAssemblerRenderer();
    }

    @Override
    protected void showGui(EntityPlayer player, EnumHand hand, World world, BlockPos pos) {

    }
}
