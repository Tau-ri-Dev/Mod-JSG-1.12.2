package tauri.dev.jsg.item.renderer;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.Item;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tauri.dev.jsg.JSG;

import java.util.Objects;

/**
 * This interface allows for automatic registration of
 * TEISR.
 *
 * @author MrJake222
 */
public interface CustomModelItemInterface {

    default void registerCustomModel(IRegistry<ModelResourceLocation, IBakedModel> registry) {
        ModelResourceLocation modelResourceLocation = new ModelResourceLocation(Objects.requireNonNull(((Item) this).getRegistryName()), "inventory");

        IBakedModel defaultModel = registry.getObject(modelResourceLocation);
        CustomModel customModel = new CustomModel(defaultModel);
        setCustomModel(customModel);

        registry.putObject(modelResourceLocation, customModel);
    }

    default void setCustomModelLocation() {
        ModelLoader.setCustomModelResourceLocation((Item) this, 0, new ModelResourceLocation(Objects.requireNonNull(((Item) this).getRegistryName()), "inventory"));
    }

    default void setTEISR() {
        JSG.proxy.setTileEntityItemStackRenderer((Item) this);
    }

    void setCustomModel(CustomModel customModel);

    /**
     * @return New TEISR instance.
     */
    @SideOnly(Side.CLIENT)
    TileEntityItemStackRenderer createTEISR();
}
