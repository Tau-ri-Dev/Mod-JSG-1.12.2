package mrjake.aunis.event;

import mrjake.aunis.Aunis;
import mrjake.aunis.AunisProps;
import mrjake.aunis.block.AunisBlocks;
import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.renderer.AunisMainMenu;
import mrjake.aunis.sound.SoundEventEnum;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundList;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreenOptionsSounds;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.client.event.sound.SoundEvent;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(Side.CLIENT)
public class AunisEventHandlerClient {

  public static boolean isInMainMenu = false;

  @SubscribeEvent
  public static void onConfigChangedEvent(OnConfigChangedEvent event) {
    if (event.getModID().equals(Aunis.ModID)) {
      ConfigManager.sync(Aunis.ModID, Type.INSTANCE);
      AunisConfig.resetCache();
    }
  }

  @SubscribeEvent
  public static void onDrawHighlight(DrawBlockHighlightEvent event) {
    RayTraceResult target = event.getTarget();

    if (target.typeOfHit == RayTraceResult.Type.BLOCK) {
      IBlockState blockState = event.getPlayer().world.getBlockState(target.getBlockPos());
      Block block = blockState.getBlock();

      boolean cancelled = false;

      cancelled |= block == AunisBlocks.DHD_BLOCK;
      cancelled |= block == AunisBlocks.DHD_PEGASUS_BLOCK;
      cancelled |= (block == AunisBlocks.STARGATE_MILKY_WAY_MEMBER_BLOCK || block == AunisBlocks.STARGATE_MILKY_WAY_BASE_BLOCK) && !blockState.getValue(AunisProps.RENDER_BLOCK);
      cancelled |= (block == AunisBlocks.STARGATE_UNIVERSE_MEMBER_BLOCK || block == AunisBlocks.STARGATE_UNIVERSE_BASE_BLOCK) && !blockState.getValue(AunisProps.RENDER_BLOCK);
      cancelled |= (block == AunisBlocks.STARGATE_PEGASUS_MEMBER_BLOCK || block == AunisBlocks.STARGATE_PEGASUS_BASE_BLOCK) && !blockState.getValue(AunisProps.RENDER_BLOCK);
      cancelled |= (block == AunisBlocks.STARGATE_ORLIN_MEMBER_BLOCK) && !blockState.getValue(AunisProps.RENDER_BLOCK);

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
    if (event.getGui() instanceof GuiMainMenu) {
      isInMainMenu = true;
      event.setGui(new AunisMainMenu());
    }
    else isInMainMenu = false;
  }
  /*@SubscribeEvent
  public static void onSounds(PlaySoundEvent event) {
    if(isInMainMenu) event.setResultSound(null);
  }*/
}
