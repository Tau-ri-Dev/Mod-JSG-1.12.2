package tauri.dev.jsg.stargate.network;

import tauri.dev.jsg.util.EnumKeyInterface;
import tauri.dev.jsg.util.EnumKeyMap;

import javax.annotation.Nonnull;
import java.util.Random;

public enum SymbolTypeEnum implements EnumKeyInterface<Integer> {
    MILKYWAY(0, 32, 32),
    PEGASUS(1, 27, 27),
    UNIVERSE(2, 20, 42);

    public final int id;
    public final int iconWidht;
    public final int iconHeight;

    SymbolTypeEnum(int id, int iconWidht, int iconHeight) {
        this.id = id;
        this.iconWidht = iconWidht;
        this.iconHeight = iconHeight;
    }

    public SymbolInterface getRandomSymbol(Random random) {
        switch (this) {
            case MILKYWAY:
                return SymbolMilkyWayEnum.getRandomSymbol(random);

            case PEGASUS:
                return SymbolPegasusEnum.getRandomSymbol(random);

            case UNIVERSE:
                return SymbolUniverseEnum.getRandomSymbol(random);
        }

        return null;
    }

    public SymbolInterface getBRB() {
        switch (this) {
            case MILKYWAY:
                return SymbolMilkyWayEnum.BRB;

            case PEGASUS:
                return SymbolPegasusEnum.BBB;

            case UNIVERSE:
                return SymbolUniverseEnum.getTopSymbol();
        }

        return null;
    }

    public SymbolInterface valueOfSymbol(int id) {
        switch (this) {
            case MILKYWAY:
                return SymbolMilkyWayEnum.valueOf(id);

            case PEGASUS:
                return SymbolPegasusEnum.valueOf(id);

            case UNIVERSE:
                return SymbolUniverseEnum.valueOf(id);
        }

        return null;
    }

    public boolean validateDialedAddress(StargateAddressDynamic stargateAddress) {
        switch (this) {
            case MILKYWAY:
                return SymbolMilkyWayEnum.validateDialedAddress(stargateAddress);

            case PEGASUS:
                return SymbolPegasusEnum.validateDialedAddress(stargateAddress);

            case UNIVERSE:
                return SymbolUniverseEnum.validateDialedAddress(stargateAddress);
        }

        return false;
    }

    public int getMinimalSymbolCountTo(SymbolTypeEnum symbolType, boolean localDial) {
        switch (this) {
            case MILKYWAY:
                return SymbolMilkyWayEnum.getMinimalSymbolCountTo(symbolType, localDial);

            case PEGASUS:
                return SymbolPegasusEnum.getMinimalSymbolCountTo(symbolType, localDial);

            case UNIVERSE:
                return SymbolUniverseEnum.getMinimalSymbolCountTo(symbolType, localDial);
        }

        return 0;
    }

    public SymbolInterface getOrigin() {
        switch (this) {
            case MILKYWAY:
                return SymbolMilkyWayEnum.getOrigin();

            case PEGASUS:
                return SymbolPegasusEnum.getOrigin();

            case UNIVERSE:
                return SymbolUniverseEnum.getOrigin();
        }

        return null;
    }


    public SymbolInterface fromEnglishName(String englishName) {
        switch (this) {
            case MILKYWAY:
                return SymbolMilkyWayEnum.fromEnglishName(englishName);

            case PEGASUS:
                return SymbolPegasusEnum.fromEnglishName(englishName);

            case UNIVERSE:
                SymbolUniverseEnum symbol = SymbolUniverseEnum.fromEnglishName(englishName);

                if (symbol != null) return symbol;

                try {
                    int index = Integer.parseInt(englishName.replace("G", ""));
                    if (index < 1 || index > 36) return null;

                    return SymbolUniverseEnum.values()[index];
                } catch (NumberFormatException ex) {
                    return null;
                }
        }

        return null;
    }

    public SymbolInterface[] getValues() {
        switch (this) {
            case MILKYWAY:
                return SymbolMilkyWayEnum.values();

            case PEGASUS:
                return SymbolPegasusEnum.values();

            case UNIVERSE:
                return SymbolUniverseEnum.values();
            default:
                break;
        }

        return new SymbolInterface[]{};
    }

    public int getMaxSymbolsDisplay(boolean hasUpgrade) {
        switch (this) {
            case MILKYWAY:
                return SymbolMilkyWayEnum.getMaxSymbolsDisplay(hasUpgrade);

            case PEGASUS:
                return SymbolPegasusEnum.getMaxSymbolsDisplay(hasUpgrade);

            case UNIVERSE:
                return SymbolUniverseEnum.getMaxSymbolsDisplay(hasUpgrade);
        }

        return 0;
    }

    public float getAnglePerGlyph() {
        switch (this) {
            case MILKYWAY:
                return SymbolMilkyWayEnum.getAnglePerGlyph();

            case PEGASUS:
                return SymbolPegasusEnum.getAnglePerGlyph();

            case UNIVERSE:
                return SymbolUniverseEnum.getAnglePerGlyph();
        }

        return 0;
    }

    public SymbolInterface getSymbolByAngle(float angle) {
        switch (this) {
            case MILKYWAY:
                return SymbolMilkyWayEnum.getSymbolByAngle(angle);

            case PEGASUS:
                return SymbolPegasusEnum.getOrigin();

            case UNIVERSE:
                return SymbolUniverseEnum.getSymbolByAngle(angle);
        }

        return null;
    }

    public float getAngleOfNearest(float angle) {
        switch (this) {
            case MILKYWAY:
                return SymbolMilkyWayEnum.getAngleOfNearest(angle);

            case PEGASUS:
                return 0; //SymbolPegasusEnum.getAngleOfNearest(angle);

            case UNIVERSE:
                return SymbolUniverseEnum.getAngleOfNearest(angle);
        }

        return 0;
    }


    public SymbolInterface getTopSymbol() {
        switch (this) {
            case MILKYWAY:
                return SymbolMilkyWayEnum.getTopSymbol();

            case PEGASUS:
                return SymbolPegasusEnum.getTopSymbol();

            case UNIVERSE:
                return SymbolUniverseEnum.getTopSymbol();
        }

        return null;
    }

    public boolean hasOrigin() {
        return getOrigin() != null;
    }


    // ------------------------------------------------------------
    // Static

    private static final EnumKeyMap<Integer, SymbolTypeEnum> ID_MAP = new EnumKeyMap<>(values());

    @Override
    public Integer getKey() {
        return id;
    }

    public static SymbolTypeEnum valueOf(int id) {
        return ID_MAP.valueOf(id);
    }

    @Nonnull
    public static SymbolTypeEnum getRandom() {
        return valueOf((int) (Math.random() * values().length));
    }
}
