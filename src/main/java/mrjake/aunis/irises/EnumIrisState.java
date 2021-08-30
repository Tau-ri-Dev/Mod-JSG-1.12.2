package mrjake.aunis.irises;


import mrjake.aunis.tileentity.stargate.StargateAbstractBaseTile;

public enum EnumIrisState {

    CLOSING,
    OPENING,

    CLOSED,
    OPENED,

    CLOSE,
    OPEN;


    public static EnumIrisState irisState;

    public static void setIrisState(EnumIrisState state, StargateAbstractBaseTile gateTile) {
        switch (state) {
            case OPEN:
                irisState = OPENING;

                // TODO fucking animation shit

                irisState = OPENED;

            case CLOSE:
                irisState = CLOSING;

                // TODO fucking animation shit

                irisState = CLOSED;

        }
    }

    public static boolean opened(){ return irisState == OPENED; }
    public static boolean closed(){ return irisState == CLOSED; }
    public static boolean isNull(){ return irisState == null; }

}
