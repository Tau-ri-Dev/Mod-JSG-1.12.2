package tauri.dev.jsg.config.stargate;

import tauri.dev.jsg.util.JSGAxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum StargateSizeEnum {
  SMALL(0, "Small", 0.75, -0.95,
          new JSGAxisAlignedBB(-2.5, 1.5, -0.1, 2.5, 6.6, 0.2),
          new JSGAxisAlignedBB(-1, 3, 0, 1, 5.5, 5),
          5,
          Arrays.asList(
                  new JSGAxisAlignedBB(-1.5, 2.0, -0.5, 1.5, 7, 0.5),
                  new JSGAxisAlignedBB(-2.5, 2.0, -0.5, -1.5, 6, 0.5),
                  new JSGAxisAlignedBB(2.5, 2.0, -0.5, 1.5, 6, 0.5)
          )
          ),

  MEDIUM(1, "Medium", 0.83, -0.62,
          new JSGAxisAlignedBB(-2.5, 1.5, -0.1, 2.5, 7.5, 0.2),
          new JSGAxisAlignedBB(-1, 3, 0, 1, 5.5, 5), 5,
          Arrays.asList(
                  new JSGAxisAlignedBB(-1.5, 2.0, -0.5, 1.5, 7, 0.5),
                  new JSGAxisAlignedBB(-2.5, 2.0, -0.5, -1.5, 6, 0.5),
                  new JSGAxisAlignedBB(2.5, 2.0, -0.5, 1.5, 6, 0.5))),

  LARGE(2, "Large", 1, 0,
          new JSGAxisAlignedBB(-3.5, 1.5, -0.1, 3.5, 8.6, 0.2),
          new JSGAxisAlignedBB(-1.5, 3.5, 0.5, 1.5, 7, 6.5), 6,
          Arrays.asList(
                  new JSGAxisAlignedBB(-2.5, 2.0, -0.5, 2.5, 9, 0.5),
                  new JSGAxisAlignedBB(-3.5, 2.0, -0.5, -2.5, 8, 0.5),
                  new JSGAxisAlignedBB(3.5, 2.0, -0.5, 2.5, 8, 0.5)));

  public int id;
  public String name;
  public double renderScale;
  public double renderTranslationY;
  public JSGAxisAlignedBB teleportBox;
  public JSGAxisAlignedBB killingBox;
  public int horizonSegmentCount;
  public List<JSGAxisAlignedBB> gateVaporizingBoxes;



  private StargateSizeEnum(int id, String name, double renderScale, double renderTranslationY, JSGAxisAlignedBB teleportBox, JSGAxisAlignedBB killingBox, int horizonSegmentCount, List<JSGAxisAlignedBB> gateVaporizingBoxes) {
    this.id = id;
    this.name = name;
    this.renderScale = renderScale;
    this.renderTranslationY = renderTranslationY;
    this.teleportBox = teleportBox;
    this.killingBox = killingBox;
    this.horizonSegmentCount = horizonSegmentCount;
    this.gateVaporizingBoxes = gateVaporizingBoxes;
  }

  @Override
  public String toString() {
    return name;
  }

  public static StargateSizeEnum fromId(int id) {
    return StargateSizeEnum.values()[id];
  }

  public static BlockPos[] getIrisBLocksPatter(StargateSizeEnum size) {
    List<BlockPos> patter = new ArrayList<>();
    switch (size) {
      case SMALL:
      case MEDIUM:
        return IRIS_PATTER_SMALL;
      case LARGE:
        return IRIS_PATTER_LARGE;
      default:
        return null;
    }
  }

  public static void init() {

    // Small
    // Medium
    List<BlockPos> small = new ArrayList<>();
    for (int i = 0; i < 6; i++) {
      for (int j = -2; j < 3; j++) {
        if ((i == 0 || i == 5) && (j == -2 || j == 2)) continue;
        small.add(new BlockPos(j, i+1 , 0));
      }
    }

    // Large
    List<BlockPos> large = new ArrayList<>();
    int startX = -2;
    for (int i = 0; i < 8; i++) {

      for (int j = startX; j < 1-startX; j++) {
        large.add(new BlockPos(j, i+1, 0));
      }
      if (i < 3 && i != 1) startX--;
      if (i >= 4 && i != 5) startX++;
    }

    IRIS_PATTER_SMALL = small.toArray(new BlockPos[0]);
    IRIS_PATTER_LARGE = large.toArray(new BlockPos[0]);
  }


  public static BlockPos[] IRIS_PATTER_SMALL;
  public static BlockPos[] IRIS_PATTER_LARGE;

}
