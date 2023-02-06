package tauri.dev.jsg.stargate.network;

import tauri.dev.jsg.JSG;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.loader.model.ModelLoader;
import net.minecraft.util.ResourceLocation;
import tauri.dev.jsg.renderer.biomes.BiomeOverlayEnum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public enum SymbolPegasusEnum implements SymbolInterface {
  ROEHI(37, "Roehi", "37.obj", 11),
  ONCEEL(36, "Once El", "36.obj", 35),
  BASELAI(35, "Baselai", "35.obj", 33),
  SANDOVI(34, "Sandovi", "34.obj", 10),
  ILLUME(33, "Illume", "33.obj", 25),
  AMIWILL(32, "Amiwill", "32.obj", 30),
  SIBBRON(31, "Sibbron", "31.obj", 0), //36
  GILLTIN(30, "Gilltin", "30.obj", 9),
  // no texture exist, but it is showing ROEHI because while rendering pegasus gate idle cycle is going through 0 - 35
  UNKNOW2(29, "Unknow2", "29.obj", 11),
  RAMNON(28, "Ramnon", "28.obj", 24),
  OLAVII(27, "Olavii", "27.obj", 14),
  HACEMILL(26, "Hacemill", "26.obj", 16),
  POCORE(25, "Poco Re", "25.obj", 13),
  ABRIN(24, "Abrin", "24.obj", 12),
  SALMA(23, "Salma", "23.obj", 17),
  HAMLINTO(22, "Hamlinto", "22.obj", 15),
  ELENAMI(21, "Elenami", "21.obj", 7),
  TAHNAN(20, "Tahnan", "20.obj", 32),
  ZEO(19, "Zeo", "19.obj", 4),
  // same as UNKNOW2
  UNKNOW1(18, "Unknow1", "18.obj", 35),
  ROBANDUS(17, "Robandus", "17.obj", 1),
  RECKTIC(16, "Recktic", "16.obj", 6),
  ZAMILLOZ(15, "Zamilloz", "15.obj", 19),
  SUBIDO(14, "Subido", "14.obj", 3), // origin
  DAWNRE(13, "Dawnre", "13.obj", 8),
  ACJESIS(12, "Acjesis", "12.obj", 29),
  LENCHAN(11, "Lenchan", "11.obj", 22),
  ALURA(10, "Alura", "10.obj", 21),
  CAPO(9, "Ca Po", "9.obj", 28),
  LAYLOX(8, "Laylox", "8.obj", 34),
  ECRUMIG(7, "Ecrumig", "7.obj", 20),
  AVONIV(6, "Avoniv", "6.obj", 23),
  BYDO(5, "Bydo", "5.obj", 2),
  AAXEL(4, "Aaxel", "4.obj", 26),
  ALDENI(3, "Aldeni", "3.obj", 5),
  SETAS(2, "Setas", "2.obj", 31),
  ARAMI(1, "Arami", "1.obj", 27),
  DANAMI(0, "Danami", "0.obj", 18),

  BBB(38, "Bright Blue Button", "BRB.obj", -39);

  public final int id;
  public final int textureSlot;
  public final String englishName;
  public final String translationKey;
  public final ResourceLocation iconResource;
  public final ResourceLocation modelResource;

  private SymbolPegasusEnum(int id, String englishName, String model, int textureSlot) {
    this.id = id;
    this.textureSlot = textureSlot;
    this.englishName = englishName;
    this.translationKey = "glyph.jsg.pegasus." + englishName.toLowerCase().replace(" ", "_");
    this.iconResource = new ResourceLocation(JSG.MOD_ID, "textures/gui/symbol/pegasus/" + englishName.toLowerCase() + ".png");

    this.modelResource = ModelLoader.getModelResource("pegasus/" + model);
  }

  @Override
  public boolean origin() {
    return this == SUBIDO;
  }

  public boolean brb() {
    return this == BBB;
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
  public ResourceLocation getIconResource(BiomeOverlayEnum overlay, int dimensionId, int configOrigin) {
    return iconResource;
  }

  @Override
  public String localize() {
    return JSG.proxy.localize(translationKey);
  }

  @Override
  public SymbolTypeEnum getSymbolType() {
    return SymbolTypeEnum.PEGASUS;
  }

  public static SymbolInterface getSymbolByAngle(float angle){
    return getOrigin();
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

    boolean eightChevrons = JSGConfig.Stargate.mechanics.pegAndMilkUseEightChevrons;

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
