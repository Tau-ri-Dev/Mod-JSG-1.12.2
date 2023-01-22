package tauri.dev.jsg.proxy;

import tauri.dev.jsg.renderer.stargate.StargateAbstractRendererState;
import tauri.dev.jsg.sound.SoundPositionedEnum;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public interface IProxy {
	void preInit(FMLPreInitializationEvent event);
	void init(FMLInitializationEvent event);
	void postInit(FMLPostInitializationEvent event);
	
	String localize(String unlocalized, Object... args);
	
	EntityPlayer getPlayerInMessageHandler(MessageContext ctx);
	void setTileEntityItemStackRenderer(Item item);
	EntityPlayer getPlayerClientSide();
	void addScheduledTaskClientSide(Runnable runnable);
	
	void orlinRendererSpawnParticles(World world, StargateAbstractRendererState rendererState);
	void playPositionedSoundClientSide(BlockPos pos, SoundPositionedEnum soundEnum, boolean play);
	
	void openGui(GuiScreen gui);
	void shutDown();
	void loadCompleted();
}
