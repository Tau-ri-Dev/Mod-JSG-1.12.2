package mrjake.aunis.transportrings;

import mrjake.aunis.stargate.network.*;
import mrjake.aunis.util.EnumKeyInterface;
import mrjake.aunis.util.EnumKeyMap;

import java.util.List;
import java.util.Random;

public enum SymbolTypeTransportRingsEnum implements EnumKeyInterface<Integer> {
  GOAULD(0, 32, 32);

  public int id;
  public int iconWidht;
  public int iconHeight;

  private SymbolTypeTransportRingsEnum(int id, int iconWidht, int iconHeight) {
    this.id = id;
    this.iconWidht = iconWidht;
    this.iconHeight = iconHeight;
  }

  public SymbolInterface getRandomSymbol(Random random) {
    switch (this) {
      case GOAULD:
        return SymbolGoauldEnum.getRandomSymbol(random);
    }

    return null;
  }

  public List<SymbolInterface> stripOrigin(List<SymbolInterface> dialedAddress) {
    switch (this) {
      case GOAULD:
        return SymbolGoauldEnum.stripOrigin(dialedAddress);
    }

    return null;
  }

  public SymbolInterface getOrigin() {
    switch (this) {
      case GOAULD:
        return SymbolGoauldEnum.getOrigin();
    }

    return null;
  }

  public SymbolInterface getSymbol(int symbolId) {
    switch (this) {
      case GOAULD:
        return SymbolGoauldEnum.valueOf(symbolId);
    }

    return null;
  }


  public SymbolInterface fromEnglishName(String englishName) {
    switch (this) {
      case GOAULD:
        return SymbolGoauldEnum.fromEnglishName(englishName);
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
