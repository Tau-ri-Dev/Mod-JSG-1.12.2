package tauri.dev.jsg.util;

import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;

import java.awt.*;

@SuppressWarnings("unused")
public class JSGColorUtil {
    public static int blendColors(int colorA, int colorB, float colorBRatio) {
        return blendColors(colorA, colorB, 1.0f - colorBRatio, colorBRatio);
    }

    public static int blendColors(int a, int b, float colorARation, float colorBRatio) {

        int aA = (a >> 24 & 0xff);
        int aR = ((a & 0xff0000) >> 16);
        int aG = ((a & 0xff00) >> 8);
        int aB = (a & 0xff);

        int bA = (b >> 24 & 0xff);
        int bR = ((b & 0xff0000) >> 16);
        int bG = ((b & 0xff00) >> 8);
        int bB = (b & 0xff);

        int alpha = ((int) (aA * colorARation) + (int) (bA * colorBRatio));
        int red = ((int) (aR * colorARation) + (int) (bR * colorBRatio));
        int green = ((int) (aG * colorARation) + (int) (bG * colorBRatio));
        int blue = ((int) (aB * colorARation) + (int) (bB * colorBRatio));

        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    public static Color toColor(int hex){
        return new Color(hex);
    }

    public static int fromColor(Color color){
        return color.getRGB();
    }

    public static int getColorFromDyeItem(ItemStack stack) {
        if (stack.getItem() instanceof ItemDye) {
            return getColorByMeta(stack.getMetadata());
        }
        return 0x000000;
    }

    public static int getColorByMeta(int metaIn) {
        if (metaIn < 0) metaIn = 0;
        if (metaIn > 15) metaIn = 15;
        return ItemDye.DYE_COLORS[metaIn];
    }
}
