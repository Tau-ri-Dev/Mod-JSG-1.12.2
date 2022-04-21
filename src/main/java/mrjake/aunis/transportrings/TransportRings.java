package mrjake.aunis.transportrings;

import mrjake.aunis.Aunis;
import mrjake.aunis.stargate.network.SymbolInterface;
import mrjake.aunis.tileentity.transportrings.TransportRingsAbstractTile;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Defines one, specific ring platform
 * Contains their address, name, BlockPos etc.
 * <p>
 * Is NBT serializable and should be used to save ring data to NBT
 *
 * @author MrJake
 */
public class TransportRings {

    /**
     * Rings address
     */
    private Map<SymbolTypeTransportRingsEnum, TransportRingsAddress> addressMap = new HashMap<>();
    /**
     * Rings display name
     */
    private String name;
    /**
     * BlockPos of the main block
     */
    private BlockPos pos;
    /**
     * Distance to rings, set by cloning.
     * <p>
     * It is only saved to NBT in clones, as main object has no distance
     */
    private double distance;
    /**
     * Distance of rings from the base block
     */
    private int ringsDistance = 2;
    /**
     * Defines if the object is a clone
     */
    private boolean isClone;

    /**
     * Called when new tile entity is created by world(first block placement),
     * by reading NBT data(main tile or clones)
     *
     * @param pos - mandatory, points to rings base block
     */
    public TransportRings(Map<SymbolTypeTransportRingsEnum, TransportRingsAddress> addressMap, BlockPos pos) {
        this(addressMap, "", pos, false);
    }

    /**
     * Used only for menu client-side
     *
     * @param addressMap rings addresses
     * @param name       name of rings
     */
    public TransportRings(Map<SymbolTypeTransportRingsEnum, TransportRingsAddress> addressMap, String name) {
        this(addressMap, name, new BlockPos(0, 0, 0), false);
    }

    /**
     * NBT version of the constructor.
     *
     * @param compound {@link NBTTagCompound} read from NBT.
     */
    public TransportRings(NBTTagCompound compound) {
        deserializeNBT(compound);
    }

    /**
     * Called internally
     */
    private TransportRings(Map<SymbolTypeTransportRingsEnum, TransportRingsAddress> addressMap, String name, BlockPos pos, boolean isClone) {
        this.addressMap = addressMap;
        this.name = name;
        this.pos = pos;

        this.isClone = isClone;
    }

    public TransportRingsAddress getAddress(SymbolTypeTransportRingsEnum symbolType) {
        return addressMap.get(symbolType);
    }

    public Map<SymbolTypeTransportRingsEnum, TransportRingsAddress> getAddresses() {
        return addressMap;
    }

    public void setAddress(SymbolTypeTransportRingsEnum symbolType, TransportRingsAddress address) {
        this.addressMap.put(symbolType, address);
    }

    public void setAddress(Map<SymbolTypeTransportRingsEnum, TransportRingsAddress> addresses) {
        this.addressMap = addresses;
    }

    public String getName() {
        if (name == null) return "[empty]";

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BlockPos getPos() {
        return pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    public double getDistance() {
        return distance;
    }

    public int getRingsDistance() {
        return ringsDistance;
    }

    public void setRingsDistance(int dist) {
        ringsDistance = dist;
    }

    /**
     * Returns new instance of this object with specified distance to the
     * rings requiring the clone
     */
    public TransportRings cloneWithNewDistance(BlockPos callerPos) {
        return new TransportRings(addressMap, name, pos, true).setDistanceTo(callerPos);
    }

    /**
     * Checks if address has been set(not equal to -1)
     *
     * @return should put rings on map
     */
    public boolean isInGrid() {
        return true; // rings can operate without name
        //return (name != null && !(name.equals("")) && !(name.equals("[empty]")) && name.length() > 0);
    }

    /**
     * Sets this rings distance to caller position
     *
     * @param pos - caller position
     * @return this instance
     */
    private TransportRings setDistanceTo(BlockPos pos) {
        distance = this.pos.getDistance(pos.getX(), pos.getY(), pos.getZ());

        return this;
    }

    /**
     * Saves data of this ring
     *
     * @return new tag compound
     */
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();

        if (addressMap != null) {
            for (SymbolTypeTransportRingsEnum symbolType : SymbolTypeTransportRingsEnum.values())
                compound.setTag("address_" + symbolType.id, addressMap.get(symbolType).serializeNBT());
        }

        if (name != null) compound.setString("name", name);

        compound.setLong("pos", pos.toLong());

        if (isClone) compound.setDouble("distance", distance);

        compound.setInteger("ringsDistance", ringsDistance);

        return compound;
    }

    public TransportRings deserializeNBT(NBTTagCompound compound) {
        addressMap = new HashMap<>();
        for (SymbolTypeTransportRingsEnum symbolType : SymbolTypeTransportRingsEnum.values()) {
            if (compound.hasKey("address_" + symbolType.id)) {
                addressMap.put(symbolType, new TransportRingsAddress(compound.getCompoundTag("address_" + symbolType.id)));
            } else
                addressMap.put(symbolType, new TransportRingsAddress(symbolType));
        }

        if (compound.hasKey("name")) name = compound.getString("name");

        pos = BlockPos.fromLong(compound.getLong("pos"));

        if (compound.hasKey("distance")) {
            isClone = true;

            distance = compound.getDouble("distance");
        }

        ringsDistance = compound.getInteger("ringsDistance");

        return this;
    }

    public Map<SymbolTypeTransportRingsEnum, TransportRingsAddress> newAddressMap() {
        Map<SymbolTypeTransportRingsEnum, TransportRingsAddress> map = new HashMap<>();
        for (SymbolTypeTransportRingsEnum symbolType : SymbolTypeTransportRingsEnum.values()) {
            map.put(symbolType, new TransportRingsAddress(symbolType));
        }
        return map;
    }

    @Override
    public String toString() {
        return "[pos=" + pos.toString() + ", address=" + addressMap.toString() + ", name=" + name + "]";
    }

    public List<String> getAddressNameList(SymbolTypeTransportRingsEnum symbolType){
        List<String> names = new ArrayList<>();
        for(SymbolInterface symbol : addressMap.get(symbolType).address){
            names.add(symbol.getEnglishName());
        }
        return names;
    }
}
