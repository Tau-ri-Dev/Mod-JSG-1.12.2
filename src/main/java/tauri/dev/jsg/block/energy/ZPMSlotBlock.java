package tauri.dev.jsg.block.energy;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.gui.GuiIdEnum;
import tauri.dev.jsg.renderer.zpm.ZPMSlotRenderer;
import tauri.dev.jsg.tileentity.energy.ZPMSlotTile;

import javax.annotation.Nonnull;

public class ZPMSlotBlock extends ZPMHubBlock {

    public static final String BLOCK_NAME = "zpm_slot_block";

    public ZPMSlotBlock() {
        super(BLOCK_NAME);
    }

    @Override
    public boolean onBlockActivated(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, EntityPlayer player, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!player.isSneaking())
            player.openGui(JSG.instance, GuiIdEnum.GUI_ZPM_SLOT.id, world, pos.getX(), pos.getY(), pos.getZ());

        return !player.isSneaking();
    }

    @Override
    public Class<? extends TileEntity> getTileEntityClass() {
        return ZPMSlotTile.class;
    }

    @Override
    public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
        return new ZPMSlotTile();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public TileEntitySpecialRenderer<? extends TileEntity> getTESR() {
        return new ZPMSlotRenderer();
    }
}
