package tauri.dev.jsg.event;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryTable;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickEmpty;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.raycaster.dhd.RaycasterDHD;

import static tauri.dev.jsg.block.JSGBlocks.INVISIBLE_BLOCK;
import static tauri.dev.jsg.block.JSGBlocks.IRIS_BLOCK;

@EventBusSubscriber
public class JSGEventHandler {

	@SubscribeEvent
	public static void onRightClickBlock(RightClickBlock event) {
		onRightClick(event);
	}

	@SubscribeEvent
	public static void onRightClickItem(RightClickItem event) {
		onRightClick(event);
	}

	@SubscribeEvent
	public static void onRightClickEmpty(RightClickEmpty event) {
		onRightClick(event);
	}

	private static void onRightClick(PlayerInteractEvent event) {
		EntityPlayer player = event.getEntityPlayer();
		World world = player.getEntityWorld();

		if (!player.isSpectator()) {
			BlockPos pos = player.getPosition();
			EnumFacing playerFacing = player.getHorizontalFacing();

			EnumFacing left = playerFacing.rotateYCCW();
			EnumFacing right = playerFacing.rotateY();

			Iterable<BlockPos> blocks = BlockPos.getAllInBox(pos.offset(left).down().offset(playerFacing.getOpposite()), pos.offset(right).up().offset(playerFacing));

			for (BlockPos activatedBlock : blocks) {
				Block block = world.getBlockState(activatedBlock).getBlock();

				/*
				 * This only activates the DHD block, on both sides and
				 * cancels the event. A packet is sent to the server by onActivated
				 * only on main hand click.
				 */
				if ((block == JSGBlocks.DHD_BLOCK || block == JSGBlocks.DHD_PEGASUS_BLOCK)) {

					if (event.isCancelable() && RaycasterDHD.INSTANCE.onActivated(world, activatedBlock, player, event.getHand(), player.isSneaking())) {
						event.setCanceled(true);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onLootTableLoad(LootTableLoadEvent event) {
		if (event.getName().toString().equals("minecraft:chests/end_city_treasure")) {
			LootEntry entry = new LootEntryTable(new ResourceLocation(JSG.MOD_ID, "end_city_treasure"), 1, 0, new LootCondition[]{}, "universe_dialer");
			LootPool pool = new LootPool(new LootEntry[]{entry}, new LootCondition[]{}, new RandomValueRange(1), new RandomValueRange(0), "universe_dialer_pool");

			event.getTable().addPool(pool);
		}
		if (event.getName().toString().equals("minecraft:chests/stronghold_corridor")
				|| event.getName().toString().equals("minecraft:chests/stronghold_crossing")
				|| event.getName().toString().equals("minecraft:chests/stronghold_library")
		) {
			LootEntry entry = new LootEntryTable(new ResourceLocation(JSG.MOD_ID, "stronghold"), 1, 0, new LootCondition[]{}, "sus_page");
			LootPool pool = new LootPool(new LootEntry[]{entry}, new LootCondition[]{}, new RandomValueRange(1), new RandomValueRange(0), "sus_page_pool");

			event.getTable().addPool(pool);
		}
	}

	@SubscribeEvent
	public static void onBlockPlace(BlockEvent.PlaceEvent event) {
		Block block = event.getWorld().getBlockState(event.getPos()).getBlock();
		if (IRIS_BLOCK.equals(block) || INVISIBLE_BLOCK.equals(block)) {
			event.setCanceled(true);
		}
	}
}
