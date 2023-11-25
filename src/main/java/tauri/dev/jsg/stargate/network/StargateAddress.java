package tauri.dev.jsg.stargate.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import tauri.dev.jsg.JSG;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StargateAddress implements INBTSerializable<NBTTagCompound> {

    public StargateAddress(SymbolTypeEnum symbolType) {
		this.symbolType = symbolType;
	}

	public StargateAddress(ByteBuf byteBuf) {
		fromBytes(byteBuf);
	}
	
	public StargateAddress(NBTTagCompound compound) {
		deserializeNBT(compound);
	}
	
	protected int getSavedSymbols() {
		return 8;
	}
	
	
	// ---------------------------------------------------------------------------------
	// Address
	
	protected SymbolTypeEnum symbolType;
	protected List<SymbolInterface> address = new ArrayList<>(8);
	
	public SymbolTypeEnum getSymbolType() {
		return symbolType;
	}
	
	/**
	 * Generates new 8 chevron random address.
	 * @param random {@link Random} instance.
	 */
	public StargateAddress generate(Random random) {
		if (!address.isEmpty()) {
			JSG.error("Tried to regenerate address already containing symbols");
			return this;
		}
		
		while (address.size() < 8) {
			SymbolInterface symbol = symbolType.getRandomSymbol(random);

			if (!address.contains(symbol))
				address.add(symbol);
		}
				
		return this;
	}
	
	/**
	 * Get glyph at position {@code position}
	 * @param pos Position of the glyph
	 */
	public SymbolInterface get(int pos) {
		return address.get(pos);
	}
	
	public SymbolInterface getLast() {
		if (address.size() == 0)
			return null;
		
		return address.get(address.size() - 1);
	}

	public int getSize(){
		return address.size();
	}
	
	public List<String> getNameList() {
		List<String> out = new ArrayList<>(address.size());
		
		for (SymbolInterface symbol : address) {
			out.add(symbol.getEnglishName());
		}
		
		return out;
	}
	
	public List<SymbolInterface> subList(int start, int end) {
		return address.subList(start, end);
	}
	
	/**
	 * Get 7th and 8th symbols, as they're not saved by this implementation.
	 */
	public List<SymbolInterface> getAdditional() {
		return address.subList(6, 8);
	}
	
	
	// ---------------------------------------------------------------------------------
	// Serialization
	
	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		
		compound.setInteger("symbolType", symbolType.id);

		for (int i=0; i<getSavedSymbols(); i++)
			compound.setInteger("symbol"+i, address.get(i).getId());
		
		return compound;
	}
	
	@Override
	public void deserializeNBT(NBTTagCompound compound) {
		if (!address.isEmpty()) {
			JSG.error("Tried to deserialize address already containing symbols");
			return;
		}
		
		symbolType = SymbolTypeEnum.valueOf(compound.getInteger("symbolType"));
		
		for (int i=0; i<getSavedSymbols(); i++)
			address.add(symbolType.valueOfSymbol(compound.getInteger("symbol"+i)));
	}
	
	public void toBytes(ByteBuf buf) {
		buf.writeByte(symbolType.id);
		
		for (int i=0; i<getSavedSymbols(); i++)
			buf.writeByte(address.get(i).getId());
	}
	
	public void fromBytes(ByteBuf buf) {
		if (!address.isEmpty()) {
			JSG.error("Tried to deserialize address already containing symbols");
			return;
		}
		
		symbolType = SymbolTypeEnum.valueOf(buf.readByte());
		
		for (int i=0; i<getSavedSymbols(); i++)
			address.add(symbolType.valueOfSymbol(buf.readByte()));
	}	

	
	// ---------------------------------------------------------------------------------
	// Hashing
	
	@Override
	public String toString() {
		return address.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.subList(0, 6).hashCode());
		result = prime * result + ((symbolType == null) ? 0 : symbolType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof StargateAddress))
			return false;
		StargateAddress other = (StargateAddress) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		}
		else if(address.size() < 7) return false;
		else if(other.address.size() < 7) return false;
		else if (!address.subList(0, 6).equals(other.address.subList(0, 6)))
			return false;
		return symbolType == other.symbolType;
	}

	public boolean equalsV2(StargateAddressDynamic address) {
		return equalsV2(address, address.getSize());
	}

	public boolean equalsV2(StargateAddressDynamic address, int checkLength) {
		for(int i = 0; i < address.getSize(); i++){
			if(i + 1 > checkLength) break;
			if(this.address.size() >= i+1){
				if(this.address.get(i) != address.get(i))
					return false;
			}
			else return false;
		}
		return true;
	}

	public boolean contains(SymbolInterface symbol){
		for(SymbolInterface s : address){
			if(s.equals(symbol))
				return true;
		}
		return false;
	}
}
