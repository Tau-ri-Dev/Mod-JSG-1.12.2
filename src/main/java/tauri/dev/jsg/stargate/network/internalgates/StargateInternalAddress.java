package tauri.dev.jsg.stargate.network.internalgates;

import tauri.dev.jsg.stargate.network.StargateAddressDynamic;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;

public class StargateInternalAddress {
    public int maxAddressLength;
    public int minAddressLength;
    public StargateAddressDynamic addressToMatch;
    public StargateAddressDynamic addressToReplace;

    public StargateInternalAddress(int minAddressLength, int maxAddressLength, StargateAddressDynamic addressToMatch){
        this(minAddressLength, maxAddressLength, addressToMatch, new StargateAddressDynamic(SymbolTypeEnum.MILKYWAY));
    }

    public StargateInternalAddress(int minAddressLength, int maxAddressLength, StargateAddressDynamic addressToMatch, StargateAddressDynamic addressToReplace){
        this.addressToReplace = addressToReplace;
        this.addressToMatch = addressToMatch;
        this.maxAddressLength = maxAddressLength;
        this.minAddressLength = minAddressLength;
    }
}
