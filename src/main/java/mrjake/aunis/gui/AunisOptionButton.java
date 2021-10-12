package mrjake.aunis.gui;

import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class AunisOptionButton extends AunisGuiButton {
    private GameSettings.Options enumOptions;
    public AunisOptionButton(int p_i45011_1_, int p_i45011_2_, int p_i45011_3_, String p_i45011_4_)
    {
        super(p_i45011_1_, p_i45011_2_, p_i45011_3_, p_i45011_4_);
    }

    public AunisOptionButton(int id, int x, int y, GameSettings.Options option, String string)
    {
        super(id, x, y, string);
        this.enumOptions = option;
    }

    public GameSettings.Options getOption()
    {
        return this.enumOptions;
    }
}
