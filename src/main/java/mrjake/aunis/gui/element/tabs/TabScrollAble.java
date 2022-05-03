package mrjake.aunis.gui.element.tabs;

public abstract class TabScrollAble extends Tab {
    protected static final int SCROLL_AMOUNT = 5;
    protected int scrolled = 0;

    protected TabScrollAble(TabBuilder builder) {
        super(builder);
    }

    public abstract void scroll(int k);

    public abstract boolean canRenderEntry(int x, int y);

    public abstract boolean canContinueScrolling(int k);
}
