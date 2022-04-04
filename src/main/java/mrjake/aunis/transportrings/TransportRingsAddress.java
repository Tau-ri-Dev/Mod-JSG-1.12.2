package mrjake.aunis.transportrings;

import io.netty.buffer.ByteBuf;
import mrjake.aunis.Aunis;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TransportRingsAddress implements INBTSerializable<NBTTagCompound> {

    public static final int MAX_SYMBOLS = 4;

    protected List<SymbolTransportRingsEnum> address = new ArrayList<>(MAX_SYMBOLS); // address without origin

    public TransportRingsAddress(){}

    public TransportRingsAddress(NBTTagCompound compound) {
        deserializeNBT(compound);
    }

    public TransportRingsAddress(List<SymbolTransportRingsEnum> addressList) {
        this.address = addressList;
    }

    public TransportRingsAddress generate(Random random) {
        if (!address.isEmpty()) {
            Aunis.logger.error("Tried to regenerate address already containing symbols");
            return this;
        }

        while (address.size() < MAX_SYMBOLS) {
            SymbolTransportRingsEnum symbol = SymbolTransportRingsEnum.getRandomSymbol(random);

            if (!address.contains(symbol))
                address.add(symbol);
        }

        return this;
    }

    public void clear(){
        this.address.clear();
    }

    public void add(SymbolTransportRingsEnum symbol){
        this.address.add(symbol);
    }

    public boolean contains(SymbolTransportRingsEnum symbol){
        return address.contains(symbol);
    }

    public SymbolTransportRingsEnum getLast(){
        return address.get(address.size()-1);
    }

    public TransportRingsAddress stripOrigin() {
        return new TransportRingsAddress(address.subList(0, address.size()-1));
    }

    public boolean equalsV2(TransportRingsAddress address) {
        for(int i = 0; i < address.size(); i++){
            if(this.address.size() >= i+1){
                if(this.address.get(i) != address.get(i))
                    return false;
            }
            else return false;
        }
        return true;
    }

    public int size(){
        return address.size();
    }

    public SymbolTransportRingsEnum get(int i){
        if(i >= address.size()) return SymbolTransportRingsEnum.getOrigin();
        return address.get(i);
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        try {
            compound.setInteger("addressLength", address.size());
            for (int i = 0; i < address.size(); i++)
                compound.setInteger("addressSymbol" + i, address.get(i).getId());
        }
        catch (Exception ignored){}

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
            address.add(SymbolTransportRingsEnum.valueOf(compound.getInteger("addressSymbol" + i)));
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(address.size());
        for (SymbolTransportRingsEnum symbol : address) {
            if(symbol == null) continue;
            buf.writeInt(symbol.getId());
        }
    }

    public void fromBytes(ByteBuf buf) {
        try {
            if (!address.isEmpty()) {
                Aunis.logger.error("Tried to deserialize address already containing symbols");
                return;
            }
            int length = buf.readInt();
            for (int i = 0; i < length; i++)
                address.add(SymbolTransportRingsEnum.valueOf(buf.readInt()));
        }
        catch (Exception ignored){}
    }

    public String toString() {
        StringBuilder stringAddress = new StringBuilder();
        if(address == null || address.size() < 1) return "";
        for (SymbolTransportRingsEnum symbol : address) {
            if (symbol == null){
                stringAddress.append("--null--, ");
                continue;
            }
            stringAddress.append(symbol.getEnglishName()).append(", ");
        }
        return stringAddress.toString();
    }

    public String calAddress() {
        StringBuilder stringAddress = new StringBuilder();
        if(address == null || address.size() < 1) return "";
        for (SymbolTransportRingsEnum symbol : address) {
            if (symbol == null){
                stringAddress.append("null");
                continue;
            }
            stringAddress.append(symbol.getEnglishName().toLowerCase());
        }
        return stringAddress.toString();
    }
}
