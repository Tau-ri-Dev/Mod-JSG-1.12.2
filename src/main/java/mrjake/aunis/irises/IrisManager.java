package mrjake.aunis.irises;


import mrjake.aunis.tileentity.stargate.StargateAbstractBaseTile;

public class IrisManager {

    EnumIrisState irisState = EnumIrisState.OPENED;

    public boolean irisChange(String state, StargateAbstractBaseTile gateTile){
        switch(state) {
            case "close":
                if (irisState == EnumIrisState.OPENED) {
                    irisState.setIrisState(EnumIrisState.CLOSE, gateTile);
                    return true;
                }
                else return false;
            case "open":
                if (irisState == EnumIrisState.CLOSED) {
                    irisState.setIrisState(EnumIrisState.OPEN, gateTile);
                    return true;
                }
                else return false;
            default:
                return false;
        }
    }

}
