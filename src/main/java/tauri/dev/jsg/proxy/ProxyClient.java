package tauri.dev.jsg.proxy;

import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.block.stargate.StargateClassicMemberBlockColor;
import tauri.dev.jsg.entity.friendly.TokraEntity;
import tauri.dev.jsg.entity.renderer.TokraRenderer;
import tauri.dev.jsg.event.InputHandlerClient;
import tauri.dev.jsg.fluid.JSGBlockFluid;
import tauri.dev.jsg.fluid.JSGFluids;
import tauri.dev.jsg.item.JSGItems;
import tauri.dev.jsg.item.color.PageMysteriousItemColor;
import tauri.dev.jsg.item.color.PageNotebookItemColor;
import tauri.dev.jsg.item.renderer.CustomModelItemInterface;
import tauri.dev.jsg.loader.ReloadListener;
import tauri.dev.jsg.renderer.BeamerRenderer;
import tauri.dev.jsg.renderer.dialhomedevice.DHDMilkyWayRenderer;
import tauri.dev.jsg.renderer.dialhomedevice.DHDPegasusRenderer;
import tauri.dev.jsg.renderer.stargate.*;
import tauri.dev.jsg.renderer.transportrings.TRControllerGoauldRenderer;
import tauri.dev.jsg.renderer.transportrings.TransportRingsGoauldRenderer;
import tauri.dev.jsg.renderer.transportrings.TransportRingsOriRenderer;
import tauri.dev.jsg.sound.JSGSoundHelperClient;
import tauri.dev.jsg.sound.SoundPositionedEnum;
import tauri.dev.jsg.tileentity.BeamerTile;
import tauri.dev.jsg.tileentity.dialhomedevice.DHDMilkyWayTile;
import tauri.dev.jsg.tileentity.dialhomedevice.DHDPegasusTile;
import tauri.dev.jsg.tileentity.stargate.StargateMilkyWayBaseTile;
import tauri.dev.jsg.tileentity.stargate.StargateOrlinBaseTile;
import tauri.dev.jsg.tileentity.stargate.StargatePegasusBaseTile;
import tauri.dev.jsg.tileentity.stargate.StargateUniverseBaseTile;
import tauri.dev.jsg.tileentity.transportrings.TRControllerGoauldTile;
import tauri.dev.jsg.tileentity.transportrings.TransportRingsGoauldTile;
import tauri.dev.jsg.tileentity.transportrings.TransportRingsOriTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ProxyClient implements IProxy {
    public void preInit(FMLPreInitializationEvent event) {
        registerRenderers();
        registerFluidRenderers();

        InputHandlerClient.registerKeybindings();
    }

    public void init(FMLInitializationEvent event) {
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new PageMysteriousItemColor(), JSGItems.PAGE_MYSTERIOUS_ITEM);
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new PageNotebookItemColor(), JSGItems.PAGE_NOTEBOOK_ITEM);

        Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(new StargateClassicMemberBlockColor(), JSGBlocks.STARGATE_MILKY_WAY_MEMBER_BLOCK, JSGBlocks.STARGATE_UNIVERSE_MEMBER_BLOCK, JSGBlocks.STARGATE_PEGASUS_MEMBER_BLOCK);
    }

    public void postInit(FMLPostInitializationEvent event) {
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(new ReloadListener());
    }

    public String localize(String unlocalized, Object... args) {
        return I18n.format(unlocalized, args);
    }

    private void registerRenderers() {
        OBJLoader.INSTANCE.addDomain("jsg");

        ClientRegistry.bindTileEntitySpecialRenderer(StargateMilkyWayBaseTile.class, new StargateMilkyWayRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(StargateUniverseBaseTile.class, new StargateUniverseRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(StargateOrlinBaseTile.class, new StargateOrlinRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(StargatePegasusBaseTile.class, new StargatePegasusRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(DHDMilkyWayTile.class, new DHDMilkyWayRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(DHDPegasusTile.class, new DHDPegasusRenderer());

        ClientRegistry.bindTileEntitySpecialRenderer(TransportRingsGoauldTile.class, new TransportRingsGoauldRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TransportRingsOriTile.class, new TransportRingsOriRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TRControllerGoauldTile.class, new TRControllerGoauldRenderer());

        ClientRegistry.bindTileEntitySpecialRenderer(BeamerTile.class, new BeamerRenderer());
        registerEntityRenderers();
    }

    private void registerEntityRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(TokraEntity.class, TokraRenderer::new);
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
}
