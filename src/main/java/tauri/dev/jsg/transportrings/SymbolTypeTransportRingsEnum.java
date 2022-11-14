package tauri.dev.jsg.transportrings;

import tauri.dev.jsg.stargate.network.SymbolInterface;
import tauri.dev.jsg.util.EnumKeyInterface;
import tauri.dev.jsg.util.EnumKeyMap;

import java.util.List;
import java.util.Random;

public enum SymbolTypeTransportRingsEnum implements EnumKeyInterface<Integer> {
    GOAULD(0, 32, 32),
    ORI(1, 32, 32),
    ANCIENT(2, 32, 32);

    public final int id;
    public final int iconWidth;
    public final int iconHeight;

    SymbolTypeTransportRingsEnum(int id, int iconWidth, int iconHeight) {
        this.id = id;
        this.iconWidth = iconWidth;
        this.iconHeight = iconHeight;
    }

    public SymbolInterface getRandomSymbol(Random random) {
        switch (this) {
            case GOAULD:
                return SymbolGoauldEnum.getRandomSymbol(random);
            case ORI:
                return SymbolOriEnum.getRandomSymbol(random);
            case ANCIENT:
                return SymbolAncientEnum.getRandomSymbol(random);
        }

        return null;
    }

    public int getSymbolsCount() {
        switch (this) {
            case GOAULD:
                return SymbolGoauldEnum.values().length;
            case ORI:
                return SymbolOriEnum.values().length;
            case ANCIENT:
                return SymbolAncientEnum.values().length;
        }

        return 0;
    }

    public SymbolInterface getLight() {
        switch (this) {
            case GOAULD:
            case ORI:
            case ANCIENT:
                return getSymbol(6);
        }
        return getOrigin();
    }

    public List<SymbolInterface> stripOrigin(List<SymbolInterface> dialedAddress) {
        switch (this) {
            case GOAULD:
                return SymbolGoauldEnum.stripOrigin(dialedAddress);
            case ORI:
                return SymbolOriEnum.stripOrigin(dialedAddress);
            case ANCIENT:
                return SymbolAncientEnum.stripOrigin(dialedAddress);
        }

        return null;
    }

    public SymbolInterface getOrigin() {
        switch (this) {
            case GOAULD:
                return SymbolGoauldEnum.getOrigin();
            case ORI:
                return SymbolOriEnum.getOrigin();
            case ANCIENT:
                return SymbolAncientEnum.getOrigin();
        }

        return null;
    }

    public SymbolInterface getSymbol(int symbolId) {
        switch (this) {
            case GOAULD:
                return SymbolGoauldEnum.valueOf(symbolId);
            case ORI:
                return SymbolOriEnum.valueOf(symbolId);
            case ANCIENT:
                return SymbolAncientEnum.valueOf(symbolId);
        }

        return null;
    }

    public SymbolInterface getSymbolByAngleIndex(int angleIndex) {
        switch (this) {
            case GOAULD:
                return SymbolGoauldEnum.getSymbolByAngleIndex(angleIndex);
            case ORI:
                return SymbolOriEnum.getSymbolByAngleIndex(angleIndex);
            case ANCIENT:
                return SymbolAncientEnum.getSymbolByAngleIndex(angleIndex);
        }

        return null;
    }


    public SymbolInterface fromEnglishName(String englishName) {
        switch (this) {
            case GOAULD:
                return SymbolGoauldEnum.fromEnglishName(englishName);
            case ORI:
                return SymbolOriEnum.fromEnglishName(englishName);
            case ANCIENT:
                return SymbolAncientEnum.fromEnglishName(englishName);
        }

        return null;
    }

    public boolean hasOrigin() {
        return getOrigin() != null;
    }

    public boolean validateDialedAddress(TransportRingsAddress address) {
        switch (this) {
            case GOAULD:
                return SymbolGoauldEnum.validateDialedAddress(address);
            case ORI:
                return SymbolOriEnum.validateDialedAddress(address);
            case ANCIENT:
                return SymbolAncientEnum.validateDialedAddress(address);
        }

        return false;
    }


    // ------------------------------------------------------------
    // Static

    private static final EnumKeyMap<Integer, SymbolTypeTransportRingsEnum> ID_MAP = new EnumKeyMap<Integer, SymbolTypeTransportRingsEnum>(values());

    @Override
    public Integer getKey() {
        return id;
    }

    public static SymbolTypeTransportRingsEnum valueOf(int id) {
        return ID_MAP.valueOf(id);
    }
}
