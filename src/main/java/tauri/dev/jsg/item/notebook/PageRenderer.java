package tauri.dev.jsg.item.notebook;

import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.item.renderer.JSGFontRenderer;
import tauri.dev.jsg.item.renderer.ItemRenderHelper;
import tauri.dev.jsg.renderer.biomes.BiomeOverlayEnum;
import tauri.dev.jsg.stargate.network.StargateAddress;
import tauri.dev.jsg.stargate.network.SymbolInterface;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.transportrings.SymbolTypeTransportRingsEnum;
import tauri.dev.jsg.transportrings.TransportRingsAddress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.Objects;

public class PageRenderer {

	public static void renderSymbol(float x, float y, float w, float h, SymbolInterface symbol, int originId) {
		float z = 0.011f;
		GlStateManager.enableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.color(0, 0, 0, (float) JSGConfig.notebookOptions.glyphTransparency);
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(symbol.getIconResource(originId));
		GL11.glBegin(GL11.GL_QUADS);
		
		GL11.glTexCoord2f(0, 1); GL11.glVertex3f(0.04f + x, 0.79f - y, z);
		GL11.glTexCoord2f(1, 1); GL11.glVertex3f(0.04f + x + w, 0.79f - y, z);
		GL11.glTexCoord2f(1, 0); GL11.glVertex3f(0.04f + x + w, 0.79f - y + h, z); // 0.2
		GL11.glTexCoord2f(0, 0); GL11.glVertex3f(0.04f + x, 0.79f - y + h, z);
		
	    GL11.glEnd();
	}
	
	public static void renderByCompound(TransformType lastTransform, NBTTagCompound compound) {	
		float partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();
		GlStateManager.pushMatrix();
		
		if (lastTransform == TransformType.FIXED) {
			GlStateManager.rotate(180, 0, 1, 0);
			GlStateManager.translate(-0.91, -0.07, -0.5);
			float scale = 1.15f;
			GlStateManager.scale(scale, scale, scale);
		}
		
		else {
			boolean mainhand = lastTransform == TransformType.FIRST_PERSON_RIGHT_HAND;
			EnumHandSide handSide = mainhand ? EnumHandSide.RIGHT : EnumHandSide.LEFT;
			
			GlStateManager.pushMatrix();
			GlStateManager.scale(20,20,20);		
			ItemRenderHelper.applyBobbing(partialTicks);
			
			ItemRenderHelper.renderArmFirstPersonSide(0, handSide, 0, null);
		    GlStateManager.popMatrix();
		    
		    float narrow = tauri.dev.jsg.config.JSGConfig.notebookOptions.pageNarrowing;
		    GlStateManager.translate(mainhand ? 0.5f-narrow : -0.25f+narrow, 0.2f, 0);
		}
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("jsg:textures/gui/notebook_background.png"));		
	    GL11.glBegin(GL11.GL_QUADS);
		
	    GL11.glTexCoord2f(0, 0); GL11.glVertex3f(0.0f, 0.0f, 0.0f);
	    GL11.glTexCoord2f(0.5f, 0); GL11.glVertex3f(0.7f, 0.0f, 0.0f);
	    GL11.glTexCoord2f(0.5f, 0.71875f); GL11.glVertex3f(0.7f, 1.0f, 0.0f);
	    GL11.glTexCoord2f(0, 0.71875f); GL11.glVertex3f(0.0f, 1.0f, 0.0f);
		
	    GL11.glEnd();
	    if(!compound.hasKey("transportRings")) {
	    	// render stargate address
			SymbolTypeEnum symbolType = SymbolTypeEnum.valueOf(compound.getInteger("symbolType"));
			StargateAddress stargateAddress = new StargateAddress(compound.getCompoundTag("address"));
			int maxSymbols = symbolType.getMaxSymbolsDisplay(compound.getBoolean("hasUpgrade"));

			int originId = 0;
			if(compound.hasKey("originId"))
				originId = compound.getInteger("originId");

			for (int i=0; i<maxSymbols; i++) {
				float x = 0.21f*(i%3);
				float y = 0.20f*((int) Math.floor((float) i/3)) + 0.14f;

				if (symbolType == SymbolTypeEnum.UNIVERSE) {
					x += 0.04f;
					renderSymbol(x, y, 0.095f, 0.2f, stargateAddress.get(i), 0);
				}
				else {
					renderSymbol(x, y, 0.2f, 0.2f, stargateAddress.get(i), originId);
				}
			}

			float x = 0.21f*(10%3);
			float y = 0.20f*((int) Math.floor((float) 10/3)) + 0.14f;
			float w = 0.2f;
			if (symbolType == SymbolTypeEnum.UNIVERSE) {
				w = 0.095f;
				x += 0.04f;
			}

			renderSymbol(x, y, w, 0.2f, Objects.requireNonNull(symbolType.getOrigin()), originId);
		}
	    else{
	    	// render tr address
			SymbolTypeTransportRingsEnum symbolType = SymbolTypeTransportRingsEnum.valueOf(compound.getInteger("symbolType"));
			TransportRingsAddress address = new TransportRingsAddress(compound.getCompoundTag("address"));

			for (int i=0; i<address.size(); i++) {
				float x = 0.21f*(i%3);
				float y = 0.20f*((int) Math.floor((float) i/3)) + 0.14f;

				if (symbolType == SymbolTypeTransportRingsEnum.ANCIENT) {
					x += 0.04f;
					renderSymbol(x, y, 0.095f, 0.2f, address.get(i), 0);
				}
				else {
					renderSymbol(x, y, 0.2f, 0.2f, address.get(i), 0);
				}
			}

			float x = 0.21f*(10%3);
			float y = 0.20f*((int) Math.floor((float) 10/3)) + 0.14f;
			float w = 0.2f;
			if (symbolType == SymbolTypeTransportRingsEnum.ANCIENT) {
				w = 0.095f;
				x += 0.04f;
			}
			renderSymbol(x, y, w, 0.2f, Objects.requireNonNull(symbolType.getOrigin()), 0);
		}
		
		String name = PageNotebookItem.getNameFromCompound(compound);
		
		float scale = 0.009f;
		float width = Minecraft.getMinecraft().fontRenderer.getStringWidth(name) * scale;
		// 0.10 - 0.71 = 0.61 text space
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.10f + (0.61f - width)/2, 0.935f, 0.011f);
		GlStateManager.rotate(180, 0, 0, 1);
		GlStateManager.scale(scale, scale, scale);
		JSGFontRenderer.getFontRenderer().drawString(name, 0, 0, 0x383228, false);
		GlStateManager.popMatrix();
		
	    GlStateManager.popMatrix();
	}
}
