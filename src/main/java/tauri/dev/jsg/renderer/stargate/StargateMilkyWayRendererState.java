package tauri.dev.jsg.renderer.stargate;

public class StargateMilkyWayRendererState extends StargateClassicRendererState {
    public StargateMilkyWayRendererState() {
    }

    private StargateMilkyWayRendererState(StargateMilkyWayRendererStateBuilder builder) {
        super(builder);
    }

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
        return "milkyway/chevron";
    }


    // ------------------------------------------------------------------------
    // Builder

    public static StargateMilkyWayRendererStateBuilder builder() {
        return new StargateMilkyWayRendererStateBuilder();
    }

    public static class StargateMilkyWayRendererStateBuilder extends StargateClassicRendererStateBuilder {
        public StargateMilkyWayRendererStateBuilder() {
        }

        public StargateMilkyWayRendererStateBuilder(StargateClassicRendererStateBuilder superBuilder) {
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
            setIrisCode(superBuilder.irisCode);
            setIrisMode(superBuilder.irisMode);
            setIrisAnimation(superBuilder.irisAnimation);
            setPlusRounds(superBuilder.plusRounds);
        }

        @Override
        public StargateMilkyWayRendererState build() {
            return new StargateMilkyWayRendererState(this);
        }
    }
}