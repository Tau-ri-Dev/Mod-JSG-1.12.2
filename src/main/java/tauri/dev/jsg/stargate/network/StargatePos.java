package tauri.dev.jsg.stargate.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.stargate.teleportation.TeleportHelper;
import tauri.dev.jsg.tileentity.stargate.StargateAbstractBaseTile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class StargatePos implements INBTSerializable<NBTTagCompound> {

    public int dimensionID;
    public BlockPos gatePos;
    public SymbolTypeEnum symbolType;
    private SymbolTypeEnum gateSymbolType;
    public List<SymbolInterface> additionalSymbols;

    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return (name == null ? "" : name);
    }

    public StargatePos(int dimensionID, BlockPos gatePos, StargateAddress stargateAddress, SymbolTypeEnum gateSymbolType) {
        this.dimensionID = dimensionID;
        this.gatePos = gatePos;
        this.gateSymbolType = gateSymbolType;

        this.symbolType = stargateAddress.getSymbolType();
        this.additionalSymbols = new ArrayList<>(2);
        this.additionalSymbols.addAll(stargateAddress.getAdditional());
    }

    public StargatePos(SymbolTypeEnum symbolType, NBTTagCompound compound) {
        this.symbolType = symbolType;
        this.additionalSymbols = new ArrayList<>(2);

        deserializeNBT(compound);
    }

    public StargatePos(SymbolTypeEnum symbolType, ByteBuf buf) {
        this.symbolType = symbolType;
        this.additionalSymbols = new ArrayList<>(2);

        fromBytes(buf);
    }

    public SymbolTypeEnum getGateSymbolType() {
        if (gateSymbolType != null) return gateSymbolType;
        gateSymbolType = getTileEntity().getSymbolType();
        return gateSymbolType;
    }

    public World getWorld() {
        return TeleportHelper.getWorld(dimensionID);
    }

    public StargateAbstractBaseTile getTileEntity() {
        try {
            return (StargateAbstractBaseTile) getWorld().getTileEntity(gatePos);
        } catch (Exception e) {
            JSG.error("Error while getting tile entity from SG pos!", e);
            return null;
        }
    }

    public IBlockState getBlockState() {
        return getWorld().getBlockState(gatePos);
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();

        compound.setInteger("dim", dimensionID);
        compound.setLong("pos", gatePos.toLong());
        compound.setInteger("last0", ((additionalSymbols.get(0) == null) ? 0 : additionalSymbols.get(0).getId()));
        compound.setInteger("last1", ((additionalSymbols.get(1) == null) ? 0 : additionalSymbols.get(1).getId()));
        compound.setString("stargatePosName", (name == null ? "" : name));
        if (gateSymbolType != null)
            compound.setByte("gateSymbolType", (byte) gateSymbolType.id);

        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound compound) {
        dimensionID = compound.getInteger("dim");
        gatePos = BlockPos.fromLong(compound.getLong("pos"));
        additionalSymbols.add(symbolType.valueOfSymbol(compound.getInteger("last0")));
        additionalSymbols.add(symbolType.valueOfSymbol(compound.getInteger("last1")));
        name = compound.getString("stargatePosName");
        if (compound.hasKey("gateSymbolType"))
            gateSymbolType = SymbolTypeEnum.valueOf(compound.getByte("gateSymbolType"));
        else gateSymbolType = symbolType;
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(dimensionID);
        buf.writeLong(gatePos.toLong());
        if(additionalSymbols != null && additionalSymbols.size() > 0 && additionalSymbols.get(0) != null)
            buf.writeInt(additionalSymbols.get(0).getId());
        else
            buf.writeInt(0);
        if(additionalSymbols != null && additionalSymbols.size() > 1 && additionalSymbols.get(1) != null)
            buf.writeInt(additionalSymbols.get(1).getId());
        else
            buf.writeInt(0);
        if (name != null) {
            buf.writeBoolean(true);
            buf.writeInt(name.length());
            buf.writeCharSequence(name, StandardCharsets.UTF_8);
        } else
            buf.writeBoolean(false);
        if (gateSymbolType != null) {
            buf.writeBoolean(true);
            buf.writeInt(gateSymbolType.id);
        } else
            buf.writeBoolean(false);
    }

    public void fromBytes(ByteBuf buf) {
        dimensionID = buf.readInt();
        gatePos = BlockPos.fromLong(buf.readLong());
        additionalSymbols.add(symbolType.valueOfSymbol(buf.readInt()));
        additionalSymbols.add(symbolType.valueOfSymbol(buf.readInt()));
        if (buf.readBoolean()) {
            int nameSize = buf.readInt();
            name = buf.readCharSequence(nameSize, StandardCharsets.UTF_8).toString();
        }
        if (buf.readBoolean()) {
            gateSymbolType = SymbolTypeEnum.valueOf(buf.readInt());
        }
    }


    // ---------------------------------------------------------------------------------------------------
    // Hashing

    @Override
    public String toString() {
        return String.format("[dim=%d, pos=%s, add=%s, name=%s]", dimensionID, gatePos.toString(), additionalSymbols.toString(), getName());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((additionalSymbols == null) ? 0 : additionalSymbols.hashCode());
        result = prime * result + dimensionID;
        result = prime * result + ((gatePos == null) ? 0 : gatePos.hashCode());
        result = prime * result + ((symbolType == null) ? 0 : symbolType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        StargatePos other = (StargatePos) obj;
        if (additionalSymbols == null) {
            if (other.additionalSymbols != null)
                return false;
        } else if (!additionalSymbols.equals(other.additionalSymbols))
            return false;
        if (dimensionID != other.dimensionID)
            return false;
        if (gatePos == null) {
            if (other.gatePos != null)
                return false;
        } else if (!gatePos.equals(other.gatePos))
            return false;
        return symbolType == other.symbolType;
    }
}
