package tauri.dev.jsg.worldgen;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.config.JSGConfigUtil;
import tauri.dev.jsg.stargate.network.StargateAddress;
import tauri.dev.jsg.stargate.network.StargateNetwork;
import tauri.dev.jsg.stargate.network.StargatePos;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.worldgen.util.GeneratedStargate;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class StargateDimensionGenerator {
    public static void tryGenerate(@Nonnull World worldServer) {
        if (!JSGConfig.WorldGen.otherDimGenerator.generatorEnabled) {
            JSG.debug("Skipping dimensions to generate addresses... Disabled in CONFIG");
            return;
        }
        JSG.info("Checking dimensions to generate addresses of possible gates there...");
        StargateNetwork sgn = StargateNetwork.get(worldServer);
        Map<StargatePos, Map<SymbolTypeEnum, StargateAddress>> virtualGates = sgn.getMapNotGenerated();
        ArrayList<Integer> dimensionsWithGate = new ArrayList<>();
        for (StargatePos p : virtualGates.keySet()) {
            dimensionsWithGate.add(p.dimensionID);
        }

        int i = 0;
        int y = 0;
        for (DimensionType t : DimensionManager.getRegisteredDimensions().keySet()) {
            int id = t.getId();
            if (JSGConfigUtil.isDimBlacklistedForSGSpawn(id)) continue;
            if (id == 0 || id == 1 || id == -1) continue;
            i++;
            if (dimensionsWithGate.contains(id)) continue;
            try {
                DimensionManager.getProviderType(id);
            } catch (Exception e) {
                continue;
            }
            if (!DimensionManager.getProvider(id).isSurfaceWorld()) continue;

            GeneratedStargate gs = generateAndPutAddresses(sgn, id);
            JSG.debug("Found unknown dimension " + id + "! This is it's address:");
            JSG.debug(gs.address.toString());
            y++;
        }
        JSG.info("Found " + i + " unknown dimensions. Total " + y + " addresses were added as possible gates!");
    }

    public static GeneratedStargate generateAndPutAddresses(StargateNetwork sgn, int dim) {
        Random random = new Random(new BlockPos(0, 0, 0).hashCode() * 31L + dim);

        GeneratedStargate gs = null;
        for (SymbolTypeEnum symbolType : SymbolTypeEnum.values()) {
            StargateAddress address = new StargateAddress(symbolType);
            do {
                address.generate(random);
            } while (sgn.isStargateInNetwork(address));

            StargatePos gatePos = new StargatePos(dim, new BlockPos(0, 0, 0), address);

            sgn.addNotGeneratedStargate(address, gatePos);
            if (gs == null)
                gs = new GeneratedStargate(address, null, true, 0);
        }
        return gs;
    }
}
