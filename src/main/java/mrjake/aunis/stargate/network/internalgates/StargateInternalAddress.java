package mrjake.aunis.stargate.network.internalgates;

import mrjake.aunis.stargate.network.StargateAddressDynamic;
import mrjake.aunis.stargate.network.SymbolTypeEnum;

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
