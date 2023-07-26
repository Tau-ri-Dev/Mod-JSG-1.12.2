package tauri.dev.jsg.stargate.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants.NBT;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.config.JSGConfigUtil;
import tauri.dev.jsg.datafixer.StargateNetworkReader18;
import tauri.dev.jsg.stargate.network.internalgates.StargateAddressesEnum;
import tauri.dev.jsg.stargate.network.internalgates.StargateInternalAddress;
import tauri.dev.jsg.stargate.network.internalgates.StargateInternalGates;
import tauri.dev.jsg.worldgen.structures.EnumStructures;
import tauri.dev.jsg.worldgen.structures.JSGStructuresGenerator;
import tauri.dev.jsg.worldgen.structures.stargate.StargateGenerator;
import tauri.dev.jsg.worldgen.util.GeneratedStargate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class StargateNetwork extends WorldSavedData {

    private static final String DATA_NAME = JSG.MOD_ID + "_StargateNetworkData";
    public final StargateInternalGates INTERNAL_GATES = new StargateInternalGates();
    private final Map<SymbolTypeEnum, Map<StargateAddress, StargatePos>> stargateNetworkMap = new HashMap<>();
    private final Map<StargatePos, Map<SymbolTypeEnum, StargateAddress>> notGeneratedStargates = new HashMap<>();
    private StargateAddress netherGateAddress;

    public StargateNetwork() {
        super(DATA_NAME);
        init();
    }


    // ---------------------------------------------------------------------------------------------------------
    // Stargate Network

    public StargateNetwork(String dataName) {
        super(dataName);
        init();
    }

    public static StargateNetwork get(World world) {
        MapStorage storage = world.getMapStorage();
        StargateNetwork instance = (StargateNetwork) Objects.requireNonNull(storage).getOrLoadData(StargateNetwork.class, DATA_NAME);

        if (instance == null) {
            instance = new StargateNetwork();
            storage.setData(DATA_NAME, instance);
        }

        return instance;
    }

    private void init() {
        for (SymbolTypeEnum symbolType : SymbolTypeEnum.values()) {
            stargateNetworkMap.put(symbolType, new ConcurrentHashMap<>());
        }
        INTERNAL_GATES.init();
    }

    private Map<StargateAddress, StargatePos> getMapFromAddress(StargateAddress address) {
        if (address == null) return new HashMap<>();
        return stargateNetworkMap.get(address.getSymbolType());
    }

    public Map<SymbolTypeEnum, Map<StargateAddress, StargatePos>> getMap() {
        return stargateNetworkMap;
    }

    public Map<StargatePos, Map<SymbolTypeEnum, StargateAddress>> getMapNotGenerated() {
        if (!JSGConfig.WorldGen.otherDimGenerator.generatorEnabled) {
            return new HashMap<>();
        }
        return notGeneratedStargates;
    }

    @Nullable
    public Map.Entry<StargatePos, Map<SymbolTypeEnum, StargateAddress>> getRandomNotGeneratedStargate() {
        if (!JSGConfig.WorldGen.otherDimGenerator.generatorEnabled)
            return null;
        int size = notGeneratedStargates.size();
        if (size < 1) return null;
        int index = (int) (Math.random() * size);
        int i = 0;
        for (Map.Entry<StargatePos, Map<SymbolTypeEnum, StargateAddress>> map : notGeneratedStargates.entrySet()) {
            if (i == index) return map;
            i++;
        }
        return null;
    }

    @Nullable
    public StargatePos getStargatePosFromAddressNotGenerated(StargateAddressDynamic address) {
        for (StargatePos pos : notGeneratedStargates.keySet()) {
            Map<SymbolTypeEnum, StargateAddress> m = notGeneratedStargates.get(pos);
            if (m == null) continue;
            StargateAddress a = m.get(address.symbolType);
            if (a == null) continue;
            if (a.equalsV2(address)) {
                return pos;
            }
        }
        return null;
    }

    public boolean isStargateInNetwork(StargateAddress gateAddress) {
        if (getMapFromAddress(gateAddress) == null) return false;
        return getMapFromAddress(gateAddress).containsKey(gateAddress);
    }

    @Nullable
    public StargatePos getStargate(StargateAddress address) {
        if (address == null)
            return null;
        if (address.getSize() < 7)
            return null;

        StargatePos pos = getMapFromAddress(address).get(address);
        if (pos != null && pos.getWorld() == null) return null;
        return pos;
    }

    public void checkAndGenerateStargate(StargateAddressDynamic address) {
        if (address == null)
            return;

        if (address.getSize() < 5) return;

        StargatePos pos = getStargatePosFromAddressNotGenerated(address);
        if (pos == null) return;
        int id = pos.dimensionID;
        BlockPos bp = pos.gatePos;
        EnumStructures structure = EnumStructures.INTERNAL_MW;
        switch (pos.getGateSymbolType()) {
            case PEGASUS:
                structure = EnumStructures.INTERNAL_PG;
                break;
            case UNIVERSE:
                structure = EnumStructures.INTERNAL_UNI;
                break;
            default:
                break;
        }
        GeneratedStargate gs;
        if(pos.symbolType == SymbolTypeEnum.UNIVERSE)
            // Generate stargate on the main island...
            gs = StargateGenerator.mystPageGeneration(pos.getWorld(), structure, id, bp, 5, 50);
        else
            gs = StargateGenerator.mystPageGeneration(pos.getWorld(), structure, id, bp);
        if (gs == null) return;
        Objects.requireNonNull(this.getStargate(gs.address)).getTileEntity().setGateAddress(address.symbolType, notGeneratedStargates.get(pos).get(address.symbolType));
        removeNotGeneratedStargate(pos);
    }

    public void addStargate(StargateAddress gateAddress, StargatePos stargatePos) {
        if (gateAddress == null) return;

        getMapFromAddress(gateAddress).put(gateAddress, stargatePos);

        markDirty();
    }

    public void addNotGeneratedStargate(StargateAddress gateAddress, StargatePos stargatePos) {
        if (gateAddress == null) return;

        Map<SymbolTypeEnum, StargateAddress> map = null;
        for (StargatePos p : notGeneratedStargates.keySet()) {
            if (p.dimensionID == stargatePos.dimensionID) {
                map = notGeneratedStargates.get(p);
                break;
            }
        }
        if (map == null) {
            notGeneratedStargates.put(stargatePos, new HashMap<>());
            map = notGeneratedStargates.get(stargatePos);
        }
        map.put(gateAddress.symbolType, gateAddress);
        markDirty();
    }

    public void removeStargate(StargateAddress gateAddress) {
        if (gateAddress == null) return;

        getMapFromAddress(gateAddress).remove(gateAddress);

        markDirty();
    }

    public void removeNotGeneratedStargate(StargatePos pos) {
        if (pos == null) return;

        notGeneratedStargates.remove(pos);

        markDirty();
    }

    public boolean hasNetherGate() {
        return netherGateAddress != null;
    }

    public void deleteNetherGate() {
        netherGateAddress = null;
        markDirty();
    }

    public StargateAddress getNetherGate() {
        return netherGateAddress;
    }

    public void setNetherGate(StargateAddress address) {
        netherGateAddress = address;
        markDirty();
    }

    public static GeneratedStargate generateNetherGate(StargateNetwork network, World world, BlockPos pos) {
        GeneratedStargate stargate = JSGStructuresGenerator.generateStructure(EnumStructures.NETHER_MW, world, new Random(), pos.getX() / 16 / 8, pos.getZ() / 16 / 8, true);
        if (stargate != null)
            network.setNetherGate(stargate.address);

        return stargate;
    }

    public StargateInternalAddress getInternalAddress(int id) {
        return INTERNAL_GATES.map.get(id);
    }

    public void setLastActivatedOrlins(StargateAddress address) {
        getInternalAddress(StargateAddressesEnum.EARTH.id).addressToReplace.clear();
        getInternalAddress(StargateAddressesEnum.EARTH.id).addressToReplace.addAll(address);
        markDirty();
    }

    // ---------------------------------------------------------------------------------------------------------
    // Reading and writing

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("size")) StargateNetworkReader18.readOldMap(compound, this);

        NBTTagList stargateTagList = compound.getTagList("stargates", NBT.TAG_COMPOUND);

        for (NBTBase baseTag : stargateTagList) {
            NBTTagCompound stargateCompound = (NBTTagCompound) baseTag;

            StargateAddress stargateAddress = new StargateAddress(stargateCompound.getCompoundTag("address"));
            StargatePos stargatePos = new StargatePos(stargateAddress.getSymbolType(), stargateCompound.getCompoundTag("pos"));

            addStargate(stargateAddress, stargatePos);
        }

        NBTTagList notGeneratedStargates = compound.getTagList("notGeneratedStargates", NBT.TAG_COMPOUND);
        for (NBTBase baseTag : notGeneratedStargates) {
            if (!JSGConfig.WorldGen.otherDimGenerator.generatorEnabled) break;
            NBTTagCompound stargateCompound = (NBTTagCompound) baseTag;

            StargateAddress stargateAddress = new StargateAddress(stargateCompound.getCompoundTag("address"));
            StargatePos stargatePos = new StargatePos(stargateAddress.getSymbolType(), stargateCompound.getCompoundTag("pos"));

            if (JSGConfigUtil.isDimBlacklistedForSGSpawn(stargatePos.dimensionID)) continue;

            addNotGeneratedStargate(stargateAddress, stargatePos);
        }

        if (compound.hasKey("netherGateAddress"))
            netherGateAddress = new StargateAddress(compound.getCompoundTag("netherGateAddress"));

        INTERNAL_GATES.deserializeNBT(compound.getCompoundTag("internalGates"));

        if (compound.hasKey("lastActivatedOrlins")) {
            getInternalAddress(StargateAddressesEnum.EARTH.id).addressToReplace.clear();
            getInternalAddress(StargateAddressesEnum.EARTH.id).addressToReplace.addAll(new StargateAddressDynamic(compound.getCompoundTag("lastActivatedOrlins")));
        }
    }


    // ---------------------------------------------------------------------------------------------------------
    // Static

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        NBTTagList stargateTagList = new NBTTagList();

        for (Map<StargateAddress, StargatePos> stargateMap : stargateNetworkMap.values()) {
            for (Map.Entry<StargateAddress, StargatePos> stargateEntry : stargateMap.entrySet()) {
                NBTTagCompound stargateCompound = new NBTTagCompound();
                stargateCompound.setTag("address", stargateEntry.getKey().serializeNBT());
                stargateCompound.setTag("pos", stargateEntry.getValue().serializeNBT());
                stargateTagList.appendTag(stargateCompound);
            }
        }
        compound.setTag("stargates", stargateTagList);

        NBTTagList notGeneratedStargates = new NBTTagList();
        for (StargatePos pos : this.notGeneratedStargates.keySet()) {
            for (Map.Entry<SymbolTypeEnum, StargateAddress> entry : this.notGeneratedStargates.get(pos).entrySet()) {
                NBTTagCompound stargateCompound = new NBTTagCompound();
                stargateCompound.setTag("address", entry.getValue().serializeNBT());
                stargateCompound.setTag("pos", pos.serializeNBT());
                notGeneratedStargates.appendTag(stargateCompound);
            }
        }

        compound.setTag("notGeneratedStargates", notGeneratedStargates);

        if (netherGateAddress != null) compound.setTag("netherGateAddress", netherGateAddress.serializeNBT());

        compound.setTag("internalGates", INTERNAL_GATES.serializeNBT());

        return compound;
    }


    public void toBytes(ByteBuf buf) {

        // Write addresses
        int networkSize = 0;
        for (Map<StargateAddress, StargatePos> stargateMap : stargateNetworkMap.values()) {
            for (Map.Entry<StargateAddress, StargatePos> ignored : stargateMap.entrySet()) {
                networkSize++;
            }
        }
        buf.writeInt(networkSize);
        for (Map<StargateAddress, StargatePos> stargateMap : stargateNetworkMap.values()) {
            for (Map.Entry<StargateAddress, StargatePos> stargateEntry : stargateMap.entrySet()) {
                stargateEntry.getKey().toBytes(buf);
                stargateEntry.getValue().toBytes(buf);
            }
        }
        // ---------------

        // Write not generated gates
        int notGeneratedSize = 0;
        for (StargatePos pos : notGeneratedStargates.keySet()) {
            for (Map.Entry<SymbolTypeEnum, StargateAddress> ignored : notGeneratedStargates.get(pos).entrySet()) {
                notGeneratedSize++;
            }
        }
        buf.writeInt(notGeneratedSize);
        for (StargatePos pos : notGeneratedStargates.keySet()) {
            for (Map.Entry<SymbolTypeEnum, StargateAddress> entry : notGeneratedStargates.get(pos).entrySet()) {
                StargateAddress address = entry.getValue();
                if (pos == null || address == null) {
                    notGeneratedSize--;
                    continue;
                }
                address.toBytes(buf);
                pos.toBytes(buf);
            }
        }
        // ---------------

        // Write nether gate
        if (netherGateAddress != null) {
            buf.writeBoolean(true);
            netherGateAddress.toBytes(buf);
        } else
            buf.writeBoolean(false);
        // ---------------

        // Write internal gates
        INTERNAL_GATES.toBytes(buf);
        // ---------------
    }

    public void fromBytes(ByteBuf buf) {
        // Read addresses
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            StargateAddress stargateAddress = new StargateAddress(buf);
            StargatePos stargatePos = new StargatePos(stargateAddress.getSymbolType(), buf);

            getMapFromAddress(stargateAddress).put(stargateAddress, stargatePos);
        }
        // ---------------

        // Read not generated gates
        size = buf.readInt();
        for (int i = 0; i < size; i++) {
            StargateAddress stargateAddress = new StargateAddress(buf);
            StargatePos stargatePos = new StargatePos(stargateAddress.getSymbolType(), buf);

            addNotGeneratedStargate(stargateAddress, stargatePos);
        }
        // ---------------

        // Read nether gate
        if (buf.readBoolean()) {
            netherGateAddress = new StargateAddress(buf);
        }
        // ---------------

        // Read internal gates
        INTERNAL_GATES.fromBytes(buf);
        // ---------------
    }
}
