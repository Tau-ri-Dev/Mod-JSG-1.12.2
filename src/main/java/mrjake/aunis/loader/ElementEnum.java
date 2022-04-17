package mrjake.aunis.loader;

import mrjake.aunis.Aunis;
import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.loader.model.ModelLoader;
import mrjake.aunis.loader.texture.TextureLoader;
import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum ElementEnum {

  // --------------------------------------------------------------------------------------------
  // Main Menu

  MILKYWAY_GATE_MAINMENU("mainmenu/gate.obj", "mainmenu/gatering7"),
  MILKYWAY_RING_MAINMENU("mainmenu/ring.obj", "mainmenu/gatering7"),

  MILKYWAY_CHEVRON_LIGHT_MAINMENU("mainmenu/chevronLight.obj", "mainmenu/chevron0"),
  MILKYWAY_CHEVRON_FRAME_MAINMENU("mainmenu/chevronFrame.obj", "mainmenu/gatering7"),
  MILKYWAY_CHEVRON_MOVING_MAINMENU("mainmenu/chevronMoving.obj", "mainmenu/chevron0"),

  MILKYWAY_CHEVRON_LIGHT_ACTIVE_MAINMENU("mainmenu/chevronLight.obj", "mainmenu/chevron10"),
  MILKYWAY_CHEVRON_MOVING_ACTIVE_MAINMENU("mainmenu/chevronMoving.obj", "mainmenu/chevron10"),

  // --------------------------------------------------------------------------------------------
  // Milky Way

  MILKYWAY_DHD("milkyway/DHD.obj", "milkyway/dhd"),
  MILKYWAY_GATE("milkyway/gate.obj", "milkyway/gatering7"),
  MILKYWAY_RING("milkyway/ring.obj", "milkyway/gatering7"),

  MILKYWAY_CHEVRON_LIGHT("milkyway/chevronLight.obj", "milkyway/chevron0"),
  MILKYWAY_CHEVRON_FRAME("milkyway/chevronFrame.obj", "milkyway/gatering7"),
  MILKYWAY_CHEVRON_MOVING("milkyway/chevronMoving.obj", "milkyway/chevron0"),
  MILKYWAY_CHEVRON_BACK("milkyway/chevronBack.obj", "milkyway/gatering7"),

  ORLIN_GATE("orlin/gate_orlin.obj", "orlin/gate_orlin"),


  // --------------------------------------------------------------------------------------------
  // Universe

  UNIVERSE_GATE("universe/universe_gate.obj", "universe/universe_gate"),
  UNIVERSE_CHEVRON("universe/universe_chevron.obj", "universe/universe_chevron10"),
  UNIVERSE_DIALER("universe/universe_dialer.obj", "universe/universe_dialer"),
  UNIVERSE_DIALER_BROKEN("universe/universe_dialer.obj", "universe/universe_dialer_broken"),

  // --------------------------------------------------------------------------------------------
  // Pegasus

  PEGASUS_DHD("pegasus/DHD.obj", "pegasus/dhd"),
  PEGASUS_GATE("pegasus/gate.obj", "pegasus/gatering7"),
  PEGASUS_RING("pegasus/ring_atlantis.obj", "pegasus/gatering7"),

  PEGASUS_CHEVRON_LIGHT("pegasus/chevronLight.obj", "pegasus/chevron0"),
  PEGASUS_CHEVRON_FRAME("pegasus/chevronFrame.obj", "pegasus/gatering7"),
  PEGASUS_CHEVRON_MOVING("pegasus/chevronMoving.obj", "pegasus/chevron0"),
  PEGASUS_CHEVRON_BACK("pegasus/chevronBack.obj", "pegasus/gatering7"),


  // --------------------------------------------------------------------------------------------
  // Transport rings

  RING_BLACK("transportrings/rings_ancient.obj", "transportrings/rings_ancient"),
  RING_ORI("transportrings/rings_ori.obj", "transportrings/rings_ori"),

  RINGSCONTROLLER_GOAULD("transportrings/controller/goauld/plate_goauld.obj", "transportrings/controller/goauld/goauld_panel"),
  RINGSCONTROLLER_GOAULD_LIGHT("transportrings/controller/goauld/indicator_lights.obj", "transportrings/controller/goauld/goauld_light"),

  // --------------------------------------------------------------------------------------------
  // ZPM things

  ZPM_HUB("zpm/zpmhub.obj", "zpm/hub/zpmhub"),
  //ZPM("zpm/zpm.obj", "zpm/item/zpm_1"),

  // --------------------------------------------------------------------------------------------
  // Irises/Shields

  SHIELD("iris/shield.obj", "iris/shield"),
  IRIS("iris/iris_blade.obj", "iris/iris_blade"),

  GDO("iris/gdo.obj", "iris/gdo.png");

  // --------------------------------------------------------------------------------------------

    public ResourceLocation modelResource;
  public Map<BiomeOverlayEnum, ResourceLocation> biomeTextureResourceMap = new HashMap<>();

  private ElementEnum(String model, String texture) {
    if (model.startsWith("mainmenu/") && AunisConfig.mainMenuConfig.disableAunisMainMenu) {
      modelResource = null;
      return;
    }
    this.modelResource = ModelLoader.getModelResource(model);

    for (BiomeOverlayEnum biomeOverlay : BiomeOverlayEnum.values())
      if (texture.endsWith(".png")) {
        biomeTextureResourceMap.put(biomeOverlay, TextureLoader.getTextureResource(texture));
      } else {
        biomeTextureResourceMap.put(biomeOverlay, TextureLoader.getTextureResource(texture + biomeOverlay.suffix + ".jpg"));
      }
  }

  public void render() {
    ModelLoader.getModel(modelResource).render();
  }

  private List<BiomeOverlayEnum> nonExistingReported = new ArrayList<>();

  public void bindTexture(BiomeOverlayEnum biomeOverlay) {
    ResourceLocation resourceLocation = biomeTextureResourceMap.get(biomeOverlay);
    bindTexture(biomeOverlay, resourceLocation);
  }
  public void bindTexture(BiomeOverlayEnum biomeOverlay, ResourceLocation resourceLocation) {
    if (!TextureLoader.isTextureLoaded(resourceLocation)) {
      // Probably doesn't exist

      if (!nonExistingReported.contains(biomeOverlay)) {
        Aunis.logger.error(this + " tried to use BiomeOverlay " + biomeOverlay + " but it doesn't exist. (" + resourceLocation + ")");
        nonExistingReported.add(biomeOverlay);
      }

      resourceLocation = biomeTextureResourceMap.get(BiomeOverlayEnum.NORMAL);
    }

    TextureLoader.getTexture(resourceLocation).bindTexture();
  }

  public void bindTextureAndRender(BiomeOverlayEnum biomeOverlay) {
    bindTexture(biomeOverlay);
    render();
  }
}
