package tauri.dev.jsg.worldgen;

import tauri.dev.jsg.JSG;
import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.config.stargate.StargateSizeEnum;
import tauri.dev.jsg.fluid.JSGFluids;
import tauri.dev.jsg.item.JSGItems;
import tauri.dev.jsg.stargate.network.StargateAddress;
import tauri.dev.jsg.stargate.network.StargatePos;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.tileentity.dialhomedevice.DHDAbstractTile;
import tauri.dev.jsg.tileentity.stargate.StargateAbstractBaseTile;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;
import tauri.dev.jsg.util.LinkingHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.Map;
import java.util.Random;


/**
 * TODO Unify this and {@link StargateGeneratorNether} (replace this)
 *
 * @author MrJake222
 */
public class StargateGenerator {

	public static GeneratedStargate generateStargate(World world) {
		Random rand = new Random();
		BlockPos pos;
		int tries = 0;
		WorldServer worldToSpawn = world.getMinecraftServer().getWorld(0);
		int x;
		int z;
		do {
			x = (int) (tauri.dev.jsg.config.JSGConfig.mysteriousConfig.minOverworldCoords + (rand.nextFloat() * (tauri.dev.jsg.config.JSGConfig.mysteriousConfig.maxOverworldCoords - tauri.dev.jsg.config.JSGConfig.mysteriousConfig.minOverworldCoords))) * (rand.nextBoolean() ? -1 : 1);
			z = (int) (JSGConfig.mysteriousConfig.minOverworldCoords + (rand.nextFloat() * (tauri.dev.jsg.config.JSGConfig.mysteriousConfig.maxOverworldCoords - tauri.dev.jsg.config.JSGConfig.mysteriousConfig.minOverworldCoords))) * (rand.nextBoolean() ? -1 : 1);

			pos = StargateGenerator.checkForPlace(worldToSpawn, x/16, z/16);
			tries++;
		} while (pos == null && tries < 100);
		if (tries == 100) {
			JSG.logger.debug("StargateGenerator: Failed to find place - normal gate");
			return null;
		}
		if (pos == null) {
			JSG.logger.debug("StargateGenerator: Pos is null - normal gate");
			return null;
		}


		//boolean isPegasusGate = new Random().nextInt(100) < 50 && JSGConfig.devConfig.pegGatesMyst; // 50% chance && config
		Biome biome = world.getBiome(pos);
		boolean desert = biome.getRegistryName().getResourcePath().contains("desert");
		String templateName = "sg_";
		templateName += desert ? "desert" : "plains";
		//if(isPegasusGate) templateName += "_pg";
		templateName += tauri.dev.jsg.config.JSGConfig.stargateSize == StargateSizeEnum.LARGE ? "_large" : "_small";
		return generateStargate(world, pos, templateName, false, 0);
	}

	public static GeneratedStargate generateStargate(World world, BlockPos pos, String templateName, boolean isPegasusGate, int dimToSpawn) {
		WorldServer worldToSpawn = world.getMinecraftServer().getWorld(dimToSpawn);

		EnumFacing facing = findOptimalRotation(worldToSpawn, pos);
		Rotation rotation;

		switch (facing) {
			case SOUTH: rotation = Rotation.CLOCKWISE_90; break;
			case WEST:  rotation = Rotation.CLOCKWISE_180; break;
			case NORTH: rotation = Rotation.COUNTERCLOCKWISE_90; break;
			default:    rotation = Rotation.NONE; break;
		}

		WorldServer worldServer = (WorldServer) world;
		MinecraftServer server = world.getMinecraftServer();
		Biome biome = world.getBiome(pos);
		TemplateManager templateManager = worldServer.getStructureTemplateManager();
		Template template = templateManager.getTemplate(server, new ResourceLocation(JSG.MOD_ID, templateName));

		if (template != null) {
			Random rand = new Random();

			PlacementSettings settings = new PlacementSettings().setIgnoreStructureBlock(false).setRotation(rotation);
			template.addBlocksToWorld(world, pos, settings);

			Map<BlockPos, String> datablocks = template.getDataBlocks(pos, settings);
			BlockPos gatePos = null;
			BlockPos dhdPos = null;

			for (BlockPos dataPos : datablocks.keySet()) {
				String name = datablocks.get(dataPos);

				if (name.equals("base")) {
					gatePos = dataPos.add(0, -3, 0);

					world.getTileEntity(gatePos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).insertItem(4, new ItemStack(JSGBlocks.CAPACITOR_BLOCK), false);

					((StargateAbstractBaseTile) world.getTileEntity(gatePos)).getMergeHelper().updateMembersBasePos(world, gatePos, facing);

					world.setBlockToAir(dataPos);
					world.setBlockToAir(dataPos.down()); // save block
				}

				else if (name.equals("dhd")) {
					dhdPos = dataPos.down();

					if (rand.nextFloat() < tauri.dev.jsg.config.JSGConfig.mysteriousConfig.despawnDhdChance) {
						world.setBlockToAir(dhdPos);
					}

					else {
						int fluid = tauri.dev.jsg.config.JSGConfig.powerConfig.stargateEnergyStorage / tauri.dev.jsg.config.JSGConfig.dhdConfig.energyPerNaquadah;

						DHDAbstractTile dhdTile = (DHDAbstractTile) world.getTileEntity(dhdPos);

						ItemStack crystal = new ItemStack(isPegasusGate ? JSGItems.CRYSTAL_CONTROL_PEGASUS_DHD : JSGItems.CRYSTAL_CONTROL_MILKYWAY_DHD);

						dhdTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).insertItem(0, crystal, false);
						if(isPegasusGate) dhdTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).insertItem(1, new ItemStack(JSGItems.CRYSTAL_GLYPH_DHD), false);
						((FluidTank) dhdTile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)).fillInternal(new FluidStack(JSGFluids.moltenNaquadahRefined, fluid), true);
					}

					world.setBlockToAir(dataPos);
				}
			}

			LinkingHelper.updateLinkedGate(world, gatePos, dhdPos);
			StargateClassicBaseTile gateTile = (StargateClassicBaseTile) world.getTileEntity(gatePos);
			gateTile.refresh();
			gateTile.markDirty();

			StargateAddress address = gateTile.getStargateAddress(SymbolTypeEnum.MILKYWAY);

			if(address != null && !gateTile.getNetwork().isStargateInNetwork(address))
				gateTile.getNetwork().addStargate(address, new StargatePos(world.provider.getDimensionType().getId(), gatePos, address));

			return new GeneratedStargate(address, biome.getRegistryName().getResourcePath(), isPegasusGate);
		}

		else {
			JSG.logger.error("template null");
		}

		return null;

	}

	private static final int SG_SIZE_X = 12;
	private static final int SG_SIZE_Z = 13;

	private static final int SG_SIZE_X_PLAINS = 11;
	private static final int SG_SIZE_Z_PLAINS = 11;

	public static BlockPos checkForPlace(World world, int chunkX, int chunkZ) {
		if (world.isChunkGeneratedAt(chunkX, chunkZ))
			return null;

		Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);

		int y = chunk.getHeightValue(8, 8);

		if (y > 240)
			return null;

		BlockPos pos = new BlockPos(chunkX*16, y, chunkZ*16);
		String biomeName = chunk.getBiome(pos, world.getBiomeProvider()).getRegistryName().getResourcePath();

		boolean desert = biomeName.contains("desert");

		if (!biomeName.contains("ocean") && !biomeName.contains("river") && !biomeName.contains("beach")) {
			int x = desert ? SG_SIZE_X : SG_SIZE_X_PLAINS;
			int z = desert ? SG_SIZE_Z : SG_SIZE_Z_PLAINS;

			int y1 = chunk.getHeightValue(0, 0);
			int y2 = chunk.getHeightValue(x, z);

			int y3 = chunk.getHeightValue(x, 0);
			int y4 = chunk.getHeightValue(0, z);

			// No steep hill
			if (Math.abs(y1 - y2) <= 1 && Math.abs(y3 - y4) <= 1) {
				return pos.subtract(new BlockPos(0, 1, 0));
			}

			else {
				JSG.logger.debug("StargateGenerator: too steep");
			}
		}

		else {
			JSG.logger.debug("StargateGenerator: failed, " + biomeName);
		}

		return null;
	}

	private static final int MAX_CHECK = 100;

	private static EnumFacing findOptimalRotation(World world, BlockPos pos) {
		BlockPos start = pos.add(0, 5, 5);
		int max = -1;
		EnumFacing maxFacing = EnumFacing.EAST;

		for (EnumFacing facing : EnumFacing.HORIZONTALS) {
			RayTraceResult rayTraceResult = world.rayTraceBlocks(new Vec3d(start), new Vec3d(start.offset(facing, MAX_CHECK)));

			if (rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
				int distance = (int) rayTraceResult.getBlockPos().distanceSq(start);
//				JSG.info(facing.getName().toUpperCase() + ": distance: " + distance);

				if (distance > max) {
					max = distance;
					maxFacing = facing;
				}
			}

			else {
//				JSG.info(facing.getName().toUpperCase() + ": null");

				max = 100000;
				maxFacing = facing;
			}
		}

//		JSG.info("maxFacing: " + maxFacing.getName().toUpperCase());
		return maxFacing;
	}

	public static class GeneratedStargate {

		public StargateAddress address;
		public String path;
		public boolean hasUpgrade;

		public GeneratedStargate(StargateAddress address, String path, boolean upgrade) {
			this.address = address;
			this.path = path;
			this.hasUpgrade = upgrade;
		}

	}
}
