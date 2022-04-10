package mrjake.aunis.transportrings;

import io.netty.buffer.ByteBuf;
import mrjake.aunis.Aunis;
import mrjake.aunis.stargate.network.SymbolInterface;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TransportRingsAddress implements INBTSerializable<NBTTagCompound> {

    public static final int MAX_SYMBOLS = 4;

    protected List<SymbolInterface> address = new ArrayList<>(MAX_SYMBOLS); // address without origin
    protected SymbolTypeTransportRingsEnum symbolType;

    public TransportRingsAddress(SymbolTypeTransportRingsEnum symbolType) {
        this.symbolType = symbolType;
    }

    public TransportRingsAddress(NBTTagCompound compound) {
        deserializeNBT(compound);
    }

    public TransportRingsAddress(List<SymbolInterface> addressList) {
        this.address = addressList;
    }

    public TransportRingsAddress generate(Random random) {
        if (!address.isEmpty()) {
            Aunis.logger.error("Tried to regenerate address already containing symbols");
            return this;
        }

        while (address.size() < MAX_SYMBOLS) {
            SymbolInterface symbol = symbolType.getRandomSymbol(random);

            if (!address.contains(symbol))
                address.add(symbol);
        }

        return this;
    }

    public void clear() {
        this.address.clear();
    }

    public SymbolTypeTransportRingsEnum getSymbolType(){
        return symbolType;
    }

    public void add(SymbolInterface symbol) {
        this.address.add(symbol);
    }

    public void addAll(TransportRingsAddress ringsAddress) {
        if (address.size() + ringsAddress.address.size() > MAX_SYMBOLS) {
            Aunis.logger.error("Tried to add symbols to already populated address");
            return;
        }

        address.addAll(ringsAddress.address);
    }

    public boolean contains(SymbolInterface symbol) {
        return address.contains(symbol);
    }

    public SymbolInterface getLast() {
        if (address.size() < 1) return symbolType.getOrigin();
        return address.get(address.size() - 1);
    }

    public TransportRingsAddress stripOrigin() {
        return new TransportRingsAddress(address.subList(0, address.size() - 1));
    }

    public boolean equalsV2(TransportRingsAddress address) {
        for (int i = 0; i < address.size(); i++) {
            if (this.address.size() >= i + 1) {
                if (this.address.get(i) != address.get(i))
                    return false;
            } else return false;
        }
        return true;
    }

    public int size() {
        return address.size();
    }

    public SymbolInterface get(int i) {
        if (i >= address.size()) return symbolType.getOrigin();
        return address.get(i);
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        try {
            compound.setInteger("addressLength", address.size());
            for (int i = 0; i < address.size(); i++)
                compound.setInteger("addressSymbol" + i, address.get(i).getId());
        } catch (Exception ignored) {}

        compound.setInteger("symbolType", symbolType.id);

        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound compound) {
        if (!address.isEmpty()) {
            Aunis.logger.error("Tried to deserialize address already containing symbols");
            return;
        }
        int length = compound.getInteger("addressLength");
        for (int i = 0; i < length; i++)
            address.add(SymbolGoauldEnum.valueOf(compound.getInteger("addressSymbol" + i)));

        symbolType = SymbolTypeTransportRingsEnum.valueOf(compound.getInteger("symbolType"));
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(symbolType.id);
        buf.writeInt(address.size());
        for (SymbolInterface symbol : address) {
            if (symbol == null) continue;
            buf.writeInt(symbol.getId());
        }
    }

    public void fromBytes(ByteBuf buf) {
        symbolType = SymbolTypeTransportRingsEnum.valueOf(buf.readInt());
        try {
            if (!address.isEmpty()) {
                Aunis.logger.error("Tried to deserialize address already containing symbols");
                return;
            }
            int length = buf.readInt();
            for (int i = 0; i < length; i++)
                address.add(SymbolGoauldEnum.valueOf(buf.readInt()));
        } catch (Exception ignored) {
        }
    }

    @Override
    public String toString() {
        StringBuilder stringAddress = new StringBuilder();
        if (address == null || address.size() < 1) return "";
        for (SymbolInterface symbol : address) {
            if (symbol == null) {
                stringAddress.append("--null--, ");
                continue;
            }
            stringAddress.append(symbol.getEnglishName()).append(", ");
        }
        return stringAddress.toString();
    }

    public String toShortString() {
        StringBuilder stringAddress = new StringBuilder();
        if (address == null || address.size() < 1) return "";
        for (SymbolInterface symbol : address) {
            if (symbol == null) {
                stringAddress.append("-, ");
                continue;
            }
            String symbolName = symbol.getEnglishName();
            stringAddress.append(symbolName.charAt(0)).append(symbolName.charAt(1)).append(", ");
        }
        return stringAddress.toString();
    }

    public String calAddress() {
        StringBuilder stringAddress = new StringBuilder();
        if (address == null || address.size() < 1) return "";
        for (SymbolInterface symbol : address) {
            if (symbol == null) {
                stringAddress.append("null");
                continue;
            }
            stringAddress.append(symbol.getEnglishName().toLowerCase());
        }
        return stringAddress.toString();
    }
}
