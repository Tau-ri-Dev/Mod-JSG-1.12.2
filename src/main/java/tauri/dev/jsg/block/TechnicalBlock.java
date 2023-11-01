package tauri.dev.jsg.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.item.JSGItems;

import javax.annotation.ParametersAreNonnullByDefault;


/**
 * TechnicalBlock can be destroyed only using specified tools such as Wrench, Screwdriver or Hammer
 * <p>
 * Must right-click (use) on this block with the correct tool
 * otherwise block will break and drop scrap
 */
public abstract class TechnicalBlock extends JSGBlock {
    public TechnicalBlock(Material materialIn) {
        super(materialIn);
    }

    public Item correctTool = JSGItems.JSG_WRENCH;

    @ParametersAreNonnullByDefault
    public boolean isToolEffective(String type, IBlockState state) {
        return !JSGConfig.Stargate.mechanics.enableGateDisassembleWrench;
    }

    @ParametersAreNonnullByDefault
    @Override
    public void setHarvestLevel(String tool, int level) {
        super.setHarvestLevel(tool, level);
        if (!JSGConfig.Stargate.mechanics.enableGateDisassembleWrench) return;
        switch (tool.toLowerCase()) {
            case "wrench":
                correctTool = JSGItems.JSG_WRENCH;
                return;
            case "screwdriver":
                correctTool = JSGItems.JSG_SCREWDRIVER;
                return;
            case "hammer":
                correctTool = JSGItems.JSG_HAMMER;
                return;
            default:
                break;
        }
        throw new UnsupportedOperationException("Can not set tool to other than WRENCH, SCREWDRIVER, HAMMER");
    }

    @Override
    @ParametersAreNonnullByDefault
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
        if (fortune == -2) {
            super.dropBlockAsItemWithChance(worldIn, pos, state, chance, 0);
        }
    }

    public abstract void destroyAndGiveDrops(boolean isShifting, EntityPlayer player, World world, BlockPos pos, EnumHand hand, IBlockState state);

    @SuppressWarnings("unused")
    public boolean tryBreak(ItemStack toolIn, boolean isHarvesting, boolean isShifting, EntityPlayer player, World world, BlockPos pos, EnumHand hand, IBlockState state) {
        if (JSGConfig.Stargate.mechanics.enableGateDisassembleWrench && toolIn.getItem() == correctTool) {
            BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(world, pos, state, player);
            MinecraftForge.EVENT_BUS.post(event);
            if(!event.isCanceled()) {
                destroyAndGiveDrops(isShifting, player, world, pos, hand, state);
                return true;
            }
        }
        return false;
    }
}
