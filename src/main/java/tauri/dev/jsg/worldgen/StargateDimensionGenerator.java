package tauri.dev.jsg.worldgen;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.config.JSGConfigUtil;
import tauri.dev.jsg.stargate.network.StargateAddress;
import tauri.dev.jsg.stargate.network.StargateNetwork;
import tauri.dev.jsg.stargate.network.StargatePos;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.stargate.teleportation.TeleportHelper;
import tauri.dev.jsg.util.DimensionsHelper;
import tauri.dev.jsg.worldgen.util.GeneratedStargate;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class StargateDimensionGenerator {
    @SuppressWarnings("all")
    public static void tryGenerate(@Nonnull World worldServer) {
        if (!JSGConfig.WorldGen.otherDimGenerator.generatorEnabled) {
            JSG.debug("Skipping dimensions to generate addresses... Disabled in CONFIG");
            return;
        }
        JSG.info("Checking dimensions to generate addresses of possible gates there...");
        StargateNetwork sgn = StargateNetwork.get(worldServer);
        ArrayList<Integer> dimensionsWithGate = new ArrayList<>();
        Map<StargatePos, Map<SymbolTypeEnum, StargateAddress>> virtualGates = sgn.getMapNotGenerated();
        Map<StargateAddress, StargatePos> sgNetwork = sgn.getMap().get(SymbolTypeEnum.MILKYWAY);
        for (StargatePos p : virtualGates.keySet()) {
            dimensionsWithGate.add(p.dimensionID);
        }
        for (StargatePos p : sgNetwork.values()) {
            dimensionsWithGate.add(p.dimensionID);
        }

        int i = 0;
        int y = 0;
        for (Map.Entry<Integer, DimensionType> entry : DimensionsHelper.getRegisteredDimensions().entrySet()) {
            int id = entry.getKey();
            DimensionType dt = entry.getValue();
            SymbolTypeEnum symbolType = SymbolTypeEnum.MILKYWAY;
            if (JSGConfigUtil.isDimBlacklistedForSGSpawn(id)) {
                JSG.debug("Dim " + id + " is blacklisted. Skipping...");
                continue;
            }
            if(id == 1) symbolType = SymbolTypeEnum.UNIVERSE;
            if (id == 0 || id == -1) {
                JSG.debug("Dim " + id + " is internally blacklisted. Skipping...");
                continue;
            }
            i++;
            if (dimensionsWithGate.contains(id)) {
                JSG.debug("Dim " + id + " has already gate. Skipping...");
                continue;
            }
            World world = TeleportHelper.getWorld(id);
            if (world == null || world.provider == null) {
                JSG.debug("Dim " + id + " has corrupted provider. (Is world null? " + (world == null ? "true" : "false") + ") Skipping...");
                continue;
            }
            if (!world.provider.isSurfaceWorld() && id != 1) {
                boolean shouldSkip = true;
                if (dt.getName().startsWith("planet")) shouldSkip = false;
                if (dt.getName().startsWith("moon")) shouldSkip = false;

                if (dt.getName().contains("asteroids")) shouldSkip = true;

                if (shouldSkip) {
                    JSG.debug("Dim " + id + " is not surface world. Skipping...");
                    continue;
                }
            }

            GeneratedStargate gs = generateAndPutAddresses(sgn, id, symbolType);
            JSG.debug("Found unknown dimension " + id + "! This is it's address:");
            JSG.debug(gs.address.toString());
            y++;
        }
        JSG.info("Found " + i + " unknown dimensions. Total " + y + " addresses were added as possible gates!");
    }

    public static GeneratedStargate generateAndPutAddresses(StargateNetwork sgn, int dim, SymbolTypeEnum symbolTypeEnum) {
        Random random = new Random(new BlockPos(0, 0, 0).hashCode() * 31L + dim);

        GeneratedStargate gs = null;
        StargatePos gatePos = null;
        for (SymbolTypeEnum symbolType : SymbolTypeEnum.values()) {
            StargateAddress address = new StargateAddress(symbolType);
            do {
                address.generate(random);
            } while (sgn.isStargateInNetwork(address));

            if (gatePos == null)
                gatePos = new StargatePos(dim, new BlockPos(0, 0, 0), address, symbolTypeEnum);

            sgn.addNotGeneratedStargate(address, gatePos);
            if (gs == null)
                gs = new GeneratedStargate(address, null, true, 0);
        }
        return gs;
    }
}
