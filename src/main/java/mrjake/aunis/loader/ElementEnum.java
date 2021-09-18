package mrjake.aunis.loader;

import mrjake.aunis.Aunis;
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

  RINGS_BLACK("transportrings/rings_black.obj", "transportrings/rings_black"),
  RINGSCONTROLLER_GOAULD("transportrings/plate_goauld.obj", "transportrings/goauld_panel"),
  RINGSCONTROLLER_GOAULD_BUTTONS("transportrings/buttons_goauld.obj", "transportrings/goauld_buttons"),


  // --------------------------------------------------------------------------------------------
  // Irises/Shields

  SHIELD("iris/shield.obj", "iris/shield"),
  IRIS("iris/iris_blade.obj", "iris/iris_blade"),

  GDO("iris/gdo.obj", "iris/gdo");

  // --------------------------------------------------------------------------------------------

    public ResourceLocation modelResource;
  public Map<BiomeOverlayEnum, ResourceLocation> biomeTextureResourceMap = new HashMap<>();

  private ElementEnum(String model, String texture) {
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
