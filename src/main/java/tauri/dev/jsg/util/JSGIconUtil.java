package tauri.dev.jsg.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.Display;
import tauri.dev.jsg.JSG;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class JSGIconUtil {

    public static void setWindowIcon() {
        Util.EnumOS osType = Util.getOSType();
        if (osType != Util.EnumOS.OSX) {
            JSG.info("Setting up icons!");
            InputStream img1 = null;
            InputStream img2 = null;

            Minecraft mc = Minecraft.getMinecraft();

            try {
                img1 = mc.mcDefaultResourcePack.getInputStreamAssets(new ResourceLocation(JSG.MOD_ID, "icons/icon_16x16.png"));
                img2 = mc.mcDefaultResourcePack.getInputStreamAssets(new ResourceLocation(JSG.MOD_ID, "icons/icon_32x32.png"));

                if (img1 != null && img2 != null) {
                    Display.setIcon(new ByteBuffer[]{readImageToBuffer(img1), readImageToBuffer(img2)});
                }
            } catch (IOException ioexception) {
                JSG.logger.error("Couldn't set icon", ioexception);
            } finally {
                IOUtils.closeQuietly(img1);
                IOUtils.closeQuietly(img2);
            }
        }
    }

    public static ByteBuffer readImageToBuffer(InputStream imageStream) throws IOException {
        BufferedImage bufferedimage = ImageIO.read(imageStream);
        int[] rgb = bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), null, 0, bufferedimage.getWidth());
        ByteBuffer bytebuffer = ByteBuffer.allocate(4 * rgb.length);

        for (int i : rgb) {
            bytebuffer.putInt(i << 8 | i >> 24 & 255);
        }

        bytebuffer.flip();
        return bytebuffer;
    }
}
