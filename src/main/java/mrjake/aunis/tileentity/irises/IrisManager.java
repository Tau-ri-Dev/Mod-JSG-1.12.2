package mrjake.aunis.tileentity.irises;


import mrjake.aunis.stargate.EnumStargateState;
import mrjake.aunis.stargate.network.StargatePos;

import static mrjake.aunis.tileentity.irises.EnumIrisState.irisState;

public class IrisManager {

    EnumIrisState irisState = EnumIrisState.OPENED;

    public boolean irisChange(String state, StargatePos pos){
        switch(state) {
            case "close":
                if (irisState == EnumIrisState.OPENED) {
                    irisState.setIrisState(EnumIrisState.CLOSE, pos);
                    return true;
                } else {
                    return false;
                }
            case "open":
                if (irisState == EnumIrisState.CLOSED) {
                    irisState.setIrisState(EnumIrisState.OPEN, pos);
                    return true;
                } else {
                    return false;
                }
            default:
                return false;
        }
    }

}
