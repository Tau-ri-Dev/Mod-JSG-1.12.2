package tauri.dev.jsg.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.lwjgl.opengl.Display;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.block.JSGBlock;
import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.block.stargate.StargateClassicMemberBlockColor;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.config.JSGConfigUtil;
import tauri.dev.jsg.entity.friendly.TokraEntity;
import tauri.dev.jsg.entity.renderer.TokraRenderer;
import tauri.dev.jsg.event.EventTickClient;
import tauri.dev.jsg.event.InputHandlerClient;
import tauri.dev.jsg.fluid.JSGBlockFluid;
import tauri.dev.jsg.fluid.JSGFluids;
import tauri.dev.jsg.gui.mainmenu.GuiCustomMainMenu;
import tauri.dev.jsg.item.JSGItems;
import tauri.dev.jsg.item.color.PageNotebookItemColor;
import tauri.dev.jsg.item.renderer.CustomModelItemInterface;
import tauri.dev.jsg.loader.ReloadListener;
import tauri.dev.jsg.renderer.effect.DestinyFTL;
import tauri.dev.jsg.renderer.stargate.StargateAbstractRendererState;
import tauri.dev.jsg.renderer.stargate.StargateOrlinRenderer;
import tauri.dev.jsg.sound.JSGSoundHelperClient;
import tauri.dev.jsg.sound.SoundPositionedEnum;

import static tauri.dev.jsg.block.JSGBlocks.BLOCKS;

@SuppressWarnings({"rawtypes", "unused", "unchecked"})
public class ProxyClient implements IProxy {
    public void preInit(FMLPreInitializationEvent event) {
        Display.setTitle(Display.getTitle() + " w/" + JSG.MOD_NAME + " " + JSG.MOD_VERSION.replaceAll(JSG.MC_VERSION + "-", ""));
        registerRenderers();
        registerFluidRenderers();

        InputHandlerClient.registerKeybindings();

        MinecraftForge.EVENT_BUS.register(new JSGConfigUtil());
    }

    public void init(FMLInitializationEvent event) {
        if(JSGConfig.General.mainMenuConfig.loadingMusic)
            playPositionedSoundClientSide(new BlockPos(0, 0, 0), SoundPositionedEnum.LOADING_MUSIC, true);

        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new PageNotebookItemColor(), JSGItems.PAGE_NOTEBOOK_ITEM);

        Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(new StargateClassicMemberBlockColor(), JSGBlocks.STARGATE_MILKY_WAY_MEMBER_BLOCK, JSGBlocks.STARGATE_UNIVERSE_MEMBER_BLOCK, JSGBlocks.STARGATE_PEGASUS_MEMBER_BLOCK);
    }

    public void postInit(FMLPostInitializationEvent event) {
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(new ReloadListener());
        MinecraftForge.EVENT_BUS.register(new EventTickClient());
    }

    public String localize(String unlocalized, Object... args) {
        return I18n.format(unlocalized, args);
    }

    private void registerRenderers() {
        OBJLoader.INSTANCE.addDomain("jsg");

        for (JSGBlock block : BLOCKS) {
            Class<? extends TileEntity> tileClass = block.getTileEntityClass();
            TileEntitySpecialRenderer renderer = block.getTESR();
            if (tileClass != null && renderer != null)
                ClientRegistry.bindTileEntitySpecialRenderer(tileClass, renderer);
        }
        registerEntityRenderers();
    }

    private void registerEntityRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(TokraEntity.class, TokraRenderer::new);
        //RenderingRegistry.registerEntityRenderingHandler(EntityKino.class, KinoRenderer::new);
    }


    private void registerFluidRenderers() {
        for (JSGBlockFluid blockFluid : JSGFluids.blockFluidMap.values()) {
            ModelLoader.setCustomStateMapper(blockFluid, new StateMap.Builder().ignore(JSGBlockFluid.LEVEL).build());
        }
    }

    @Override
    public EntityPlayer getPlayerInMessageHandler(MessageContext ctx) {
        return Minecraft.getMinecraft().player;
    }

    @Override
    public void setTileEntityItemStackRenderer(Item item) {
        item.setTileEntityItemStackRenderer(((CustomModelItemInterface) item).createTEISR());
    }

    @Override
    public EntityPlayer getPlayerClientSide() {
        return Minecraft.getMinecraft().player;
    }

    @Override
    public void addScheduledTaskClientSide(Runnable runnable) {
        Minecraft.getMinecraft().addScheduledTask(runnable);
    }

    @Override
    public void orlinRendererSpawnParticles(World world, StargateAbstractRendererState rendererState) {
        StargateOrlinRenderer.spawnParticles(world, rendererState);
    }

    @Override
    public void playPositionedSoundClientSide(BlockPos pos, SoundPositionedEnum soundEnum, boolean play) {
        JSGSoundHelperClient.playPositionedSoundClientSide(pos, soundEnum, play);
    }

    @Override
    public void openGui(GuiScreen gui) {
        Minecraft.getMinecraft().displayGuiScreen(gui);
    }

    @Override
    public void shutDown() {
        DestinyFTL.jumpingOut = false;
        DestinyFTL.jumpingIn = false;
        Minecraft.getMinecraft().gameSettings.setOptionFloatValue(GameSettings.Options.FOV, DestinyFTL.defaultFov);
    }

    @Override
    public void loadCompleted() {
        if(JSGConfig.General.mainMenuConfig.loadingMusic)
            playPositionedSoundClientSide(new BlockPos(0, 0, 0), SoundPositionedEnum.LOADING_MUSIC, false);
    }
}
