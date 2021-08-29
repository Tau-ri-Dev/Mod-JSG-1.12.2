package mrjake.aunis.irises;


import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Packet;
import li.cil.oc.api.network.WirelessEndpoint;
import mrjake.aunis.Aunis;
import mrjake.aunis.stargate.network.StargatePos;
import mrjake.aunis.stargate.network.SymbolTypeEnum;
import net.minecraftforge.fml.common.Optional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IrisManager {

    EnumIrisState irisState = EnumIrisState.OPENED;

    public boolean irisChange(String state, StargatePos pos){
        switch(state) {
            case "close":
                if (irisState == EnumIrisState.OPENED) {
                    irisState.setIrisState(EnumIrisState.CLOSE, pos);
                    return true;
                }
                else return false;
            case "open":
                if (irisState == EnumIrisState.CLOSED) {
                    irisState.setIrisState(EnumIrisState.OPEN, pos);
                    return true;
                }
                else return false;
            default:
                return false;
        }
    }

}
