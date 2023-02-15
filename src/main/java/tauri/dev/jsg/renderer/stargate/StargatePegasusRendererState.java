package tauri.dev.jsg.renderer.stargate;

import io.netty.buffer.ByteBuf;
import tauri.dev.jsg.config.stargate.StargateSizeEnum;
import tauri.dev.jsg.stargate.StargatePegasusSpinHelper;

import java.util.HashMap;
import java.util.Map;

public class StargatePegasusRendererState extends StargateClassicRendererState {
    public StargatePegasusRendererState() {
    }

    private StargatePegasusRendererState(StargatePegasusRendererStateBuilder builder) {
        super(builder);
        this.spinHelper = new StargatePegasusSpinHelper(builder.symbolType, builder.currentRingSymbol, builder.spinDirection, builder.isSpinning, builder.targetRingSymbol, builder.spinStartTime);
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

    // Chevrons
    // Not saved
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
    public void fromBytes(ByteBuf buf) {
        stargateSize = StargateSizeEnum.fromId(buf.readInt());
        super.fromBytes(buf, StargatePegasusSpinHelper.class);
    }


    // ------------------------------------------------------------------------
    // Builder

    public static StargatePegasusRendererStateBuilder builder() {
        return new StargatePegasusRendererStateBuilder();
    }

    public static class StargatePegasusRendererStateBuilder extends StargateClassicRendererState.StargateClassicRendererStateBuilder {
        public StargatePegasusRendererStateBuilder() {
        }

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

        @Override
        public StargatePegasusRendererState build() {
            return new StargatePegasusRendererState(this);
        }
    }
}