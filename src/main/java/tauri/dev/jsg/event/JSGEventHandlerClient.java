package tauri.dev.jsg.event;

import tauri.dev.jsg.JSG;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.util.main.JSGProps;
import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.gui.mainmenu.JSGMainMenu;
import tauri.dev.jsg.gui.mainmenu.JSGMainMenuOnLoad;
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

@EventBusSubscriber(Side.CLIENT)
public class JSGEventHandlerClient {

  @SubscribeEvent
  public static void onConfigChangedEvent(OnConfigChangedEvent event) {
    if (event.getModID().equals(JSG.MOD_ID)) {
      ConfigManager.sync(JSG.MOD_ID, Type.INSTANCE);
      tauri.dev.jsg.config.JSGConfig.resetCache();
    }
  }

  @SubscribeEvent
  public static void onDrawHighlight(DrawBlockHighlightEvent event) {
    RayTraceResult target = event.getTarget();

    if (target.typeOfHit == RayTraceResult.Type.BLOCK) {
      IBlockState blockState = event.getPlayer().world.getBlockState(target.getBlockPos());
      Block block = blockState.getBlock();

      boolean cancelled = false;

      cancelled |= block == JSGBlocks.DHD_BLOCK;
      cancelled |= block == JSGBlocks.DHD_PEGASUS_BLOCK;
      cancelled |= (block == JSGBlocks.STARGATE_MILKY_WAY_MEMBER_BLOCK || block == JSGBlocks.STARGATE_MILKY_WAY_BASE_BLOCK) && !blockState.getValue(JSGProps.RENDER_BLOCK);
      cancelled |= (block == JSGBlocks.STARGATE_UNIVERSE_MEMBER_BLOCK || block == JSGBlocks.STARGATE_UNIVERSE_BASE_BLOCK) && !blockState.getValue(JSGProps.RENDER_BLOCK);
      cancelled |= (block == JSGBlocks.STARGATE_PEGASUS_MEMBER_BLOCK || block == JSGBlocks.STARGATE_PEGASUS_BASE_BLOCK) && !blockState.getValue(JSGProps.RENDER_BLOCK);
      cancelled |= (block == JSGBlocks.STARGATE_ORLIN_MEMBER_BLOCK) && !blockState.getValue(JSGProps.RENDER_BLOCK);

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

  @SubscribeEvent
  public static void onGuiOpen(GuiOpenEvent event) {
    if (!JSGConfig.mainMenuConfig.disableJSGMainMenu) {
      if (!event.isCanceled() && event.getGui() instanceof GuiMainMenu && !(event.getGui() instanceof JSGMainMenu) && !(event.getGui() instanceof JSGMainMenuOnLoad)) {
        event.setCanceled(true);
        //Minecraft.getMinecraft().displayGuiScreen(new JSGMainMenu());
        Minecraft.getMinecraft().displayGuiScreen(new JSGMainMenuOnLoad());
      }
    }
  }

  @SubscribeEvent
  public static void onPlaySound(PlaySoundEvent event) {
    if(event.getSound().getSoundLocation().toString() == "minecraft:music.menu") {
      event.setCanceled(true);
    }
  }
}
