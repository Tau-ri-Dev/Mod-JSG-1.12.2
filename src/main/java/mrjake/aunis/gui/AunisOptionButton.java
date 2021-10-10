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

    public AunisOptionButton(int p_i45013_1_, int p_i45013_2_, int p_i45013_3_, GameSettings.Options p_i45013_4_, String p_i45013_5_)
    {
        super(p_i45013_1_, p_i45013_2_, p_i45013_3_, p_i45013_5_);
        this.enumOptions = p_i45013_4_;
    }

    public GameSettings.Options getOption()
    {
        return this.enumOptions;
    }
}
