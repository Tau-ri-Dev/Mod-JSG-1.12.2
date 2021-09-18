package mrjake.aunis.stargate.network;

import mrjake.aunis.Aunis;
import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.loader.model.ModelLoader;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public enum SymbolPegasusEnum implements SymbolInterface {
  ROEHI(37, "Roehi", "37.obj"),
  ONCEEL(36, "Once El", "36.obj"),
  BASELAI(35, "Baselai", "35.obj"),
  SANDOVI(34, "Sandovi", "34.obj"),
  ILLUME(33, "Illume", "33.obj"),
  AMIWILL(32, "Amiwill", "32.obj"),
  SIBBRON(31, "Sibbron", "31.obj"),
  GILLTIN(30, "Gilltin", "30.obj"),
  UNKNOW2(29, "Unknow2", "29.obj"),
  RAMNON(28, "Ramnon", "28.obj"),
  OLAVII(27, "Olavii", "27.obj"),
  HACEMILL(26, "Hacemill", "26.obj"),
  POCORE(25, "Poco Re", "25.obj"),
  ABRIN(24, "Abrin", "24.obj"),
  SALMA(23, "Salma", "23.obj"),
  HAMLINTO(22, "Hamlinto", "22.obj"),
  ELENAMI(21, "Elenami", "21.obj"),
  TAHNAN(20, "Tahnan", "20.obj"),
  ZEO(19, "Zeo", "19.obj"),
  UNKNOW1(18, "Unknow1", "18.obj"),
  ROBANDUS(17, "Robandus", "17.obj"),
  RECKTIC(16, "Recktic", "16.obj"),
  ZAMILLOZ(15, "Zamilloz", "15.obj"),
  SUBIDO(14, "Subido", "14.obj"), // origin
  DAWNRE(13, "Dawnre", "13.obj"),
  ACJESIS(12, "Acjesis", "12.obj"),
  LENCHAN(11, "Lenchan", "11.obj"),
  ALURA(10, "Alura", "10.obj"),
  CAPO(9, "Ca Po", "9.obj"),
  LAYLOX(8, "Laylox", "8.obj"),
  ECRUMIG(7, "Ecrumig", "7.obj"),
  AVONIV(6, "Avoniv", "6.obj"),
  BYDO(5, "Bydo", "5.obj"),
  AAXEL(4, "Aaxel", "4.obj"),
  ALDENI(3, "Aldeni", "3.obj"),
  SETAS(2, "Setas", "2.obj"),
  ARAMI(1, "Arami", "1.obj"),
  DANAMI(0, "Danami", "0.obj"),

  BRB(38, "Bright Red Button", "BRB.obj");

  public int id;
  public String englishName;
  public String translationKey;
  public ResourceLocation iconResource;
  public ResourceLocation modelResource;

  private SymbolPegasusEnum(int id, String englishName, String model) {
    this.id = id;

    this.englishName = englishName;
    this.translationKey = "glyph.aunis.pegasus." + englishName.toLowerCase().replace(" ", "_");
    this.iconResource = new ResourceLocation(Aunis.ModID, "textures/gui/symbol/pegasus/" + englishName.toLowerCase() + ".png");

    this.modelResource = ModelLoader.getModelResource("pegasus/" + model);
  }

  @Override
  public boolean origin() {
    return this == SUBIDO;
  }

  public boolean brb() {
    return this == BRB;
  }

  @Override
  public float getAngle() {
    return id;
  }

  @Override
  public int getAngleIndex() {
    return id;
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public String getEnglishName() {
    return englishName;
  }

  @Override
  public String toString() {
    return getEnglishName();
  }

  @Override
  public ResourceLocation getIconResource() {
    return iconResource;
  }

  @Override
  public String localize() {
    return Aunis.proxy.localize(translationKey);
  }

  @Override
  public SymbolTypeEnum getSymbolType() {
    return SymbolTypeEnum.PEGASUS;
  }

  // ------------------------------------------------------------
  // Static

  public static SymbolPegasusEnum getRandomSymbol(Random random) {
    int id = 0;
    do {
      id = random.nextInt(38);
    } while (id == SUBIDO.id || id == UNKNOW2.id || id == UNKNOW1.id );

    return valueOf(id);
  }

  public static boolean validateDialedAddress(StargateAddressDynamic stargateAddress) {
    if (stargateAddress.size() < 7) return false;

    if (!stargateAddress.get(stargateAddress.size() - 1).origin()) return false;

    return true;
  }

  public static List<SymbolInterface> stripOrigin(List<SymbolInterface> dialedAddress) {
    return dialedAddress.subList(0, dialedAddress.size() - 1);
  }

  public static int getMinimalSymbolCountTo(SymbolTypeEnum symbolType, boolean localDial) {

    boolean eightChevrons = AunisConfig.stargateConfig.pegAndMilkUseEightChevrons;

    switch (symbolType) {
      case MILKYWAY:
        if(eightChevrons)
          return 8;
        else
          return localDial ? 7 : 8;
      case PEGASUS:
        return localDial ? 7 : 8;
      case UNIVERSE:
        return 9;
    }

    return 0;
  }

  public static SymbolInterface getOrigin() {
    return SUBIDO;
  }

  public static int getMaxSymbolsDisplay(boolean hasUpgrade) {
    return hasUpgrade ? 8 : 6;
  }

  public static float getAnglePerGlyph() {
    return 1;
  }

  public static SymbolInterface getTopSymbol() {
    return SUBIDO;
  }

  private static final Map<Integer, SymbolPegasusEnum> ID_MAP = new HashMap<>();
  private static final Map<String, SymbolPegasusEnum> ENGLISH_NAME_MAP = new HashMap<>();

  static {
    for (SymbolPegasusEnum symbol : values()) {
      ID_MAP.put(symbol.id, symbol);
      ENGLISH_NAME_MAP.put(symbol.englishName.toLowerCase(), symbol);
    }
  }

  public static final SymbolPegasusEnum valueOf(int id) {
    return ID_MAP.get(id);
  }

  public static final SymbolPegasusEnum fromEnglishName(String englishName) {
    return ENGLISH_NAME_MAP.get(englishName.toLowerCase());
  }
}
