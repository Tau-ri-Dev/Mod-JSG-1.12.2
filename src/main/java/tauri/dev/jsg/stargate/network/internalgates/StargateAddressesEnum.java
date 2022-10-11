package tauri.dev.jsg.stargate.network.internalgates;

import tauri.dev.jsg.JSG;
import tauri.dev.jsg.stargate.network.*;
import tauri.dev.jsg.stargate.network.*;
import tauri.dev.jsg.tileentity.stargate.StargateAbstractBaseTile;

public enum StargateAddressesEnum {
    EARTH(0,
            6,
            7,
            SymbolTypeEnum.MILKYWAY,
            new SymbolInterface[] { // address to match
                SymbolMilkyWayEnum.AURIGA,
                SymbolMilkyWayEnum.CETUS,
                SymbolMilkyWayEnum.CENTAURUS,
                SymbolMilkyWayEnum.CANCER,
                SymbolMilkyWayEnum.SCUTUM,
                SymbolMilkyWayEnum.ERIDANUS,
                SymbolMilkyWayEnum.SERPENSCAPUT
            }
    )
    ;

    public final StargateInternalAddress address;
    public final int id;

    StargateAddressesEnum(int id, int minAddressLength, int maxAddressLength, SymbolTypeEnum symbolType, SymbolInterface[] symbols){
        this.id = id;
        StargateAddressDynamic addressToMatch = new StargateAddressDynamic(symbolType);
        for(SymbolInterface symbol : symbols)
            addressToMatch.addSymbol(symbol);

        this.address = new StargateInternalAddress(minAddressLength, maxAddressLength, addressToMatch);
    }
    StargateAddressesEnum(int id, int minAddressLength, int maxAddressLength, SymbolTypeEnum symbolType, SymbolInterface[] symbols, SymbolInterface[] symbols2){
        this.id = id;
        StargateAddressDynamic addressToMatch = new StargateAddressDynamic(symbolType);
        for(SymbolInterface symbol : symbols)
            addressToMatch.addSymbol(symbol);

        StargateAddressDynamic addressToReplace = new StargateAddressDynamic(symbolType);
        for(SymbolInterface symbol : symbols2)
            addressToReplace.addSymbol(symbol);

        this.address = new StargateInternalAddress(minAddressLength, maxAddressLength, addressToMatch, addressToReplace);
    }

    public static boolean tryDialInternal(StargateAbstractBaseTile sgTile, SymbolInterface symbolToEngage){
        StargateNetwork network = sgTile.getNetwork();
        StargateAddressDynamic tileDialedAddress = sgTile.getDialedAddress();
        StargateAddressDynamic dialedAddress = new StargateAddressDynamic(tileDialedAddress);

        dialedAddress.addSymbol(symbolToEngage);

        for(StargateAddressesEnum e : values()){
            StargateInternalAddress address = network.getInternalAddress(e.id);

            if(address == null) continue;
            if(address.addressToReplace == null) continue;
            if(address.addressToMatch.getSymbolType() != sgTile.getSymbolType()) continue;
            if(dialedAddress.size() < address.minAddressLength) continue;
            if(dialedAddress.size() > address.maxAddressLength) continue;
            if(network.isStargateInNetwork(dialedAddress)) continue;
            if(!dialedAddress.equalsV2(address.addressToMatch, dialedAddress.size())) continue;
            if(address.addressToReplace.size() == 0) continue;

            StargateAddressDynamic subAddress = new StargateAddressDynamic(address.addressToReplace.getSymbolType());
            subAddress.addAll(address.addressToReplace.subList(0, dialedAddress.size()));

            StargateAddressDynamic subAddressWithOrigin = new StargateAddressDynamic(subAddress);
            subAddressWithOrigin.addOrigin();

            if(!sgTile.checkAddressAndEnergy(subAddressWithOrigin).ok()) continue;


            JSG.debug("Dialed address changed to: ");
            JSG.debug(subAddress.toString());

            tileDialedAddress.clear();
            tileDialedAddress.addAll(subAddress);
            return true;
        }
        return false;
    }
}
