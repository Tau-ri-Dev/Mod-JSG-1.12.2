package tauri.dev.jsg.event;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.block.JSGBlock;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.config.JSGConfigUtil;
import tauri.dev.jsg.gui.mainmenu.GuiCustomMainMenu;

import static net.minecraft.init.SoundEvents.MUSIC_MENU;

@EventBusSubscriber(Side.CLIENT)
public class JSGEventHandlerClient {

    @SubscribeEvent
    public static void onDrawHighlight(DrawBlockHighlightEvent event) {
        RayTraceResult target = event.getTarget();

        if (target.typeOfHit == RayTraceResult.Type.BLOCK) {
            IBlockState blockState = event.getPlayer().world.getBlockState(target.getBlockPos());
            Block block = blockState.getBlock();

            boolean cancelled = false;

            if (block instanceof JSGBlock)
                cancelled = !((JSGBlock) block).renderHighlight(blockState);

            event.setCanceled(cancelled);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onInitGui(InitGuiEvent event) {
        if (event.getGui() instanceof GuiScreenOptionsSounds && event.getButtonList().size() > 13) {
            for (GuiButton button : event.getButtonList().subList(11, event.getButtonList().size())) {
                button.y += 24;
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onGuiOpen(GuiOpenEvent event) {
        if (!JSGConfig.General.mainMenuConfig.disableJSGMainMenu) {
            if (!event.isCanceled() && event.getGui() instanceof GuiMainMenu) {
                event.setCanceled(true);
                Minecraft.getMinecraft().displayGuiScreen(new GuiCustomMainMenu());
            }
        }
    }

    @SubscribeEvent
    public static void onSounds(PlaySoundEvent event) {
        if(event.getSound().getSoundLocation().equals(MUSIC_MENU.getSoundName())){
            event.setResultSound(null);
        }
    }
}
