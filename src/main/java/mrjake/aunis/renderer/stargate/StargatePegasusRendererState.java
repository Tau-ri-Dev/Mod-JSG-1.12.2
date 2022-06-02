package mrjake.aunis.renderer.stargate;

import io.netty.buffer.ByteBuf;
import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.config.stargate.StargateSizeEnum;
import mrjake.aunis.stargate.StargatePegasusSpinHelper;

import java.util.HashMap;
import java.util.Map;

public class StargatePegasusRendererState extends StargateClassicRendererState {
  public StargatePegasusRendererState() {
  }

  private StargatePegasusRendererState(StargatePegasusRendererStateBuilder builder) {
    super(builder);
    this.stargateSize = builder.stargateSize;
    this.spinHelper = new StargatePegasusSpinHelper(builder.symbolType, builder.currentRingSymbol, builder.spinDirection, builder.isSpinning, builder.targetRingSymbol, builder.spinStartTime, 0);
  }

  public Map<Integer, Integer> slotToGlyphMap = new HashMap<Integer, Integer>();

  public int slotFromChevron(ChevronEnum chevron) {
    return new int[]{9, 5, 1, 33, 29, 25, 21, 17, 13}[chevron.rotationIndex];
  }

  public void setGlyphAtSlot(int glyphId, int slot) {
    if (slot > 36) return;

    slotToGlyphMap.put(slot, glyphId);
  }

  public void lockChevron(int glyphId, ChevronEnum chevron) {
    setGlyphAtSlot(glyphId, slotFromChevron(chevron));
  }

  @Override
  public void clearChevrons(long time) {
    super.clearChevrons(time);
    this.clearGlyphs();
  }

  public void clearGlyphs() {
    slotToGlyphMap.clear();
  }

  // Gate
  // Saved
  public StargateSizeEnum stargateSize = AunisConfig.stargateSize;

  // Chevrons
  // Not saved
  public boolean chevronOpen;
  public long chevronActionStart;
  public boolean chevronOpening;
  public boolean chevronClosing;

  public void openChevron(long totalWorldTime) {
    chevronActionStart = totalWorldTime;
    chevronOpening = true;
  }

  public void closeChevron(long totalWorldTime) {
    chevronActionStart = totalWorldTime;
    chevronClosing = true;
  }

  @Override
  protected String getChevronTextureBase() {
    return "pegasus/chevron";
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(stargateSize.id);

    super.toBytes(buf);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    stargateSize = StargateSizeEnum.fromId(buf.readInt());
    super.fromBytes(buf, StargatePegasusSpinHelper.class);
  }


  // ------------------------------------------------------------------------
  // Builder

  public static StargatePegasusRendererStateBuilder builder() {
    return new StargatePegasusRendererStateBuilder();
  }

  public static class StargatePegasusRendererStateBuilder extends mrjake.aunis.renderer.stargate.StargateClassicRendererState.StargateClassicRendererStateBuilder {
    public StargatePegasusRendererStateBuilder() {
    }

    private StargateSizeEnum stargateSize;

    public StargatePegasusRendererStateBuilder(StargateClassicRendererStateBuilder superBuilder) {
      super(superBuilder);
      setSymbolType(superBuilder.symbolType);
      setActiveChevrons(superBuilder.activeChevrons);
      setFinalActive(superBuilder.isFinalActive);
      setCurrentRingSymbol(superBuilder.currentRingSymbol);
      setSpinDirection(superBuilder.spinDirection);
      setSpinning(superBuilder.isSpinning);
      setTargetRingSymbol(superBuilder.targetRingSymbol);
      setSpinStartTime(superBuilder.spinStartTime);
      setBiomeOverride(superBuilder.biomeOverride);
      setIrisState(superBuilder.irisState);
      setIrisType(superBuilder.irisType);
      setIrisAnimation(superBuilder.irisAnimation);
      setPlusRounds(0);
    }

    public StargatePegasusRendererStateBuilder setStargateSize(StargateSizeEnum stargateSize) {
      this.stargateSize = stargateSize;
      return this;
    }

    @Override
    public StargatePegasusRendererState build() {
      return new StargatePegasusRendererState(this);
    }
  }
}