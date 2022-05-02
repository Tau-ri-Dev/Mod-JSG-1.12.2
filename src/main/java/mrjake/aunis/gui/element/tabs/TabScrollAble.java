package mrjake.aunis.gui.element.tabs;

public abstract class TabScrollAble extends Tab {
    protected TabScrollAble(TabBuilder builder) {
        super(builder);
    }

    protected static final int SCROLL_AMOUNT = 5;

    public abstract void scroll(int k);
    public abstract boolean canRenderEntry(int x, int y);
    public abstract boolean canContinueScrolling(int k);
}
