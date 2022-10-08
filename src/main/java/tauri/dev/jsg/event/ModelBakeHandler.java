package tauri.dev.jsg.event;

import tauri.dev.jsg.JSG;
import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.item.JSGItems;
import tauri.dev.jsg.item.renderer.CustomModelItemInterface;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(Side.CLIENT)
public class ModelBakeHandler {

  @SubscribeEvent
  public static void onModelBakeEvent(ModelBakeEvent event) {
    IRegistry<ModelResourceLocation, IBakedModel> registry = event.getModelRegistry();

    JSGBlocks.STARGATE_MILKY_WAY_MEMBER_BLOCK.registerCustomModel(registry);
    JSGBlocks.STARGATE_UNIVERSE_MEMBER_BLOCK.registerCustomModel(registry);
    JSGBlocks.STARGATE_PEGASUS_MEMBER_BLOCK.registerCustomModel(registry);

    for (Item item : JSGItems.getItems()) {
      if (item instanceof CustomModelItemInterface) {
        JSG.logger.debug("Registering custom model for: " + item);

        ((CustomModelItemInterface) item).registerCustomModel(registry);
      }
    }
  }
}
