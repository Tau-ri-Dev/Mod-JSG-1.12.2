package tauri.dev.jsg.item.linkable.dialer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.item.JSGItems;
import tauri.dev.jsg.item.oc.ItemOCMessage;
import tauri.dev.jsg.item.renderer.ItemRenderHelper;
import tauri.dev.jsg.item.renderer.JSGFontRenderer;
import tauri.dev.jsg.loader.ElementEnum;
import tauri.dev.jsg.renderer.AncientRenderer;
import tauri.dev.jsg.renderer.biomes.BiomeOverlayEnum;
import tauri.dev.jsg.stargate.EnumStargateState;
import tauri.dev.jsg.stargate.network.*;
import tauri.dev.jsg.transportrings.TransportRings;
import tauri.dev.jsg.util.JSGMinecraftHelper;
import tauri.dev.jsg.util.JSGTextureLightningHelper;

import java.awt.*;
import java.util.ArrayList;

import static tauri.dev.jsg.item.linkable.dialer.UniverseDialerMode.NEARBY;


public class UniverseDialerTEISR extends TileEntityItemStackRenderer {

    private static StargateAddressDynamic addrFromBytes(NBTTagCompound compound, String baseName) {
        if (compound == null || baseName == null) return null;
        SymbolTypeEnum symbolType = SymbolTypeEnum.valueOf((int) compound.getByte(baseName + "_symbolType"));
        StargateAddressDynamic newAddress = new StargateAddressDynamic(symbolType);
        int addressLength = compound.getByte(baseName + "_addressLength");
        for (int i = 0; i < addressLength; i++) {
            int symbolId = compound.getByte(baseName + "_" + i);
            switch (symbolType) {
                case MILKYWAY:
                    newAddress.addSymbol(SymbolMilkyWayEnum.valueOf(symbolId));
                    break;
                case PEGASUS:
                    newAddress.addSymbol(SymbolPegasusEnum.valueOf(symbolId));
                    break;
                case UNIVERSE:
                    newAddress.addSymbol(SymbolUniverseEnum.valueOf(symbolId));
                    break;
                default:
                    break;
            }
        }
        return newAddress;
    }

    private static void drawStringWithShadow(float x, float y, String text, boolean active, boolean red) {
        drawStringWithShadow(x, y, text, active, red, false, false, EnumStargateState.IDLE);
    }

    private static void drawStringWithShadow(float x, float y, String text, boolean isActive, boolean redDef, boolean isAddress, boolean dialing, EnumStargateState stargateState) {

        boolean isEngaged = stargateState.engaged() || stargateState.initiating();
        boolean isEngagedInitiating = stargateState.initiating();
        boolean isIncoming = stargateState.incoming();
        boolean isFailing = stargateState.failing();

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.rotate(180, 0, 0, 1);
        GlStateManager.scale(0.015f, 0.015f, 0.015f);

        int color;

        float red = 1f;
        float green = 1f;
        float blue = 1f;
        float alpha = 1f;

        if (!isActive || isIncoming || (!isEngagedInitiating && isEngaged))
            alpha = 0.3f;

        if (dialing) {
            red = 0.5f;
            green = 0.7f;
            blue = 1f;
        }
        if (isEngaged && dialing) {
            red = 0.0f;
            green = 1f;
            blue = 0.5f;
        }
        if (isFailing && dialing) {
            red = 1f;
            green = 0.0f;
            blue = 0.3f;
        }
        if (isIncoming || (!isEngagedInitiating && isEngaged)) {
            red = 1f;
            green = 0.7f;
            blue = 0.0f;
        }


        if (!isAddress)
            color = isActive ? 0xFFFFFF : 0x0060FF;
        else
            color = new Color(red, green, blue, alpha).getRGB();

        JSGFontRenderer.getFontRenderer().drawString(text, -6, 19, color, false);

        if (isActive) {
            GlStateManager.translate(-0.4, 0.6, -0.1);
            JSGFontRenderer.getFontRenderer().drawString(text, -6, 19, color, false);
        }

        GlStateManager.popMatrix();
    }

    private static void renderSymbol(int row, int col, SymbolInterface symbol, boolean dialing, boolean isActive, boolean is9Chevron, boolean engage, EnumStargateState stargateState) {

        boolean isEngaged = stargateState.engaged() || stargateState.initiating();
        boolean isEngagedInitiating = stargateState.initiating();
        boolean isIncoming = stargateState.incoming();
        boolean isFailing = stargateState.failing();

        float x = col * 0.09f - 0.05f;
        float y = -row * 0.32f - 0.16f;
        float scale = 0.7f;
        float w = 0.19f * scale;
        float h = 0.40f * scale;

        if (!is9Chevron)
            x += 0.09f;

        Minecraft.getMinecraft().getTextureManager().bindTexture(symbol.getIconResource(BiomeOverlayEnum.NORMAL, 0));
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();

        float red = 1f;
        float green = 1f;
        float blue = 1f;
        float alpha = 1f;

        if (!isActive || isIncoming || (!isEngagedInitiating && isEngaged))
            alpha = 0.3f;

        if (dialing && !isEngaged && !engage) {
            red = 0.5f;
            green = 0.7f;
            blue = 1f;
        }
        if (isEngaged && engage) {
            red = 0.0f;
            green = 1f;
            blue = 0.5f;
        }
        if (isFailing && engage) {
            red = 1f;
            green = 0.0f;
            blue = 0.3f;
        }
        if (isIncoming || (!isEngagedInitiating && isEngaged)) {
            red = 1f;
            green = 0.7f;
            blue = 0.0f;
        }


        GlStateManager.color(red, green, blue, alpha);

        drawTexturedRect(x, y, 0, w, h);
        float shadow = 0.008f;

        GlStateManager.color(0, 0, 0, 0.15f);
        drawTexturedRect(x + shadow, y - shadow, -0.01f, w, h);
    }

    private static void drawTexturedRect(float x, float y, float z, float w, float h) {
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(1, 1);
        GL11.glVertex3f(x, y, z);
        GL11.glTexCoord2f(0, 1);
        GL11.glVertex3f(x + w, y, z);
        GL11.glTexCoord2f(0, 0);
        GL11.glVertex3f(x + w, y + h, z);
        GL11.glTexCoord2f(1, 0);
        GL11.glVertex3f(x, y + h, z);
        GL11.glEnd();
    }

    private static void renderArms(EnumHandSide handSide, float angle, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(20, 20, 20);
        ItemRenderHelper.applyBobbing(partialTicks);

        if (handSide == EnumHandSide.RIGHT) {
            GlStateManager.translate(-0.3, -0.4, 0.0);
            GlStateManager.rotate(25, 0, 0, 1);

            GlStateManager.translate(-0.15 * angle, -0.5 * angle, 0.0);
            GlStateManager.rotate(10 * angle, 0, 0, 1);
        } else {
            GlStateManager.translate(0.3, -0.4, 0.0);
            GlStateManager.rotate(-25, 0, 0, 1);

            GlStateManager.translate(0.15 * angle, -0.5 * angle, 0.0);
            GlStateManager.rotate(-10 * angle, 0, 0, 1);
        }

        ItemRenderHelper.renderArmFirstPersonSide(0, handSide, 0, null);
        GlStateManager.popMatrix();
    }

    private final ArrayList<Integer> switchStates = new ArrayList<>();

    @SideOnly(Side.CLIENT)
    @Override
    public void renderByItem(ItemStack stack) {
        float partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();
        TransformType transformType = JSGItems.UNIVERSE_DIALER.getLastTransform();

        boolean isBroken = stack.getItemDamage() == UniverseDialerItem.UniverseDialerVariants.BROKEN.meta;

        GlStateManager.pushMatrix();

        // Item frame
        if (transformType == TransformType.FIXED) {
            GlStateManager.translate(0.53, 0.50, 0.5);
            GlStateManager.rotate(90, 1, 0, 0);
            GlStateManager.rotate(180, 0, 0, 1);

            GlStateManager.scale(0.2f, 0.2f, 0.2f);
        } else {
            boolean mainhand = JSGItems.UNIVERSE_DIALER.getLastTransform() == TransformType.FIRST_PERSON_RIGHT_HAND;
            EnumHandSide handSide = mainhand ? EnumHandSide.RIGHT : EnumHandSide.LEFT;

            EntityPlayer player = Minecraft.getMinecraft().player;
            float pitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks;
            float angle = ItemRenderHelper.getMapAngleFromPitch(pitch);

            renderArms(handSide, angle, partialTicks);
            angle = 1 - angle;


            if (handSide == EnumHandSide.RIGHT) {
                GlStateManager.translate(0.8, 0, -0.5);
                GlStateManager.rotate(35, 1, 0, 0);
                GlStateManager.rotate(15, 0, 0, 1);

                GlStateManager.translate(0, 0.3 * angle, -0.1 * angle);
                GlStateManager.rotate(25 * angle, 1, 0, 0);
            } else {
                GlStateManager.translate(-0.2, 0, -0.55);
                GlStateManager.rotate(30, 1, 0, 0);
                GlStateManager.rotate(-20, 0, 0, 1);
                GlStateManager.rotate(25, 0, 1, 0);

                GlStateManager.translate(0, 0.3 * angle, -0.0 * angle);
                GlStateManager.rotate(25 * angle, 1, 0, 0);
            }

            GlStateManager.scale(0.3f, 0.3f, 0.3f);
        }

        if (!isBroken) {
            ElementEnum.UNIVERSE_DIALER.bindTextureAndRender(BiomeOverlayEnum.NORMAL);
        } else {
            ElementEnum.UNIVERSE_DIALER_BROKEN.bindTextureAndRender(BiomeOverlayEnum.NORMAL);
        }

        // Translate rendered text
        GlStateManager.translate(0, 0.20f, 0.1f);
        GlStateManager.rotate(-90, 1, 0, 0);

        // ---------------------------------------------------------------------------------------------
        // List rendering

        GlStateManager.enableBlend();
        JSGTextureLightningHelper.lightUpTexture(1f);

        if (stack.hasTagCompound() && !isBroken) {
            NBTTagCompound compound = stack.getTagCompound();
            UniverseDialerMode mode = UniverseDialerMode.valueOf(compound.getByte("mode"));

            drawStringWithShadow(-0.47f, 0.916f, mode.localize(), true, false);
            drawStringWithShadow(0.22f, 0.916f, mode.next().localize(), false, false);

            boolean notLinked = mode.linkable && !compound.hasKey(mode.tagPosName);
            boolean clocksDisabled = false;
            if (mode == UniverseDialerMode.COUNTDOWN) {
                long time = compound.getLong("timerCountTo");
                if (time == -1) clocksDisabled = true;
            }

            if (notLinked || clocksDisabled) {
                Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(JSG.MOD_ID, "textures/gui/universe_warning.png"));
                GlStateManager.enableTexture2D();
                GlStateManager.enableBlend();
                if (notLinked && mode == UniverseDialerMode.COUNTDOWN)
                    GlStateManager.color(0.7f, 1, 0.3f, 1f);
                else
                    GlStateManager.color(1f, 1, 1f, 1f);
                drawTexturedRect(0.72f, 0.26f, 0, 0.24f, 0.24f);
            }

            if (!notLinked || mode == UniverseDialerMode.MEMORY || mode == UniverseDialerMode.COUNTDOWN) {
                // static displays
                if (mode == UniverseDialerMode.GATE_INFO) {
                    EnumStargateState gateStatus = EnumStargateState.valueOf(compound.getInteger("gateStatus"));
                    String[] opened = compound.getString("gateOpenTime").split(" ");
                    String[] iris = compound.getString("gateIrisState").split(" ");
                    String[] lastSymbol = compound.getString("gateLastSymbol").replaceAll("Glyph ", "G").split(" ");

                    float top = 0.32f * 2;
                    float row = 0.20f;
                    float x = -0.42f;
                    float second = 0.90f;

                    drawStringWithShadow(x, top - (row * 0), "State:", false, false);
                    drawStringWithShadow(x, top - (row * 1), "Opened:", false, false);
                    drawStringWithShadow(x, top - (row * 2), "Iris:", false, false);
                    drawStringWithShadow(x, top - (row * 3), "Last glyph:", false, false);

                    String[] state = gateStatus.toString()
                            .replaceAll("ENGAGED", "ENGAGED INCOMING")
                            .replaceAll("ENGAGED INCOMING_INITIATING", "ENGAGED OUTGOING")
                            .replaceAll("_", " ").split(" ");

                    if (switchStates.size() < 4) {
                        switchStates.clear();
                        for (int i = 0; i < 4; i++)
                            switchStates.add(0);
                    }

                    for (int u = 0; u < 4; u++) {
                        int max = 0;
                        switch (u) {
                            default:
                                break;
                            case 0:
                                max = state.length;
                                break;
                            case 1:
                                max = opened.length;
                                break;
                            case 2:
                                max = iris.length;
                                break;
                            case 3:
                                max = lastSymbol.length;
                                break;
                        }
                        int current = (int) Math.floor(JSGMinecraftHelper.getClientTick() % (40 * max) / 40D);

                        switchStates.set(u, (current));
                    }

                    if (state.length > switchStates.get(0))
                        drawStringWithShadow(x + second, top - (row * 0), state[switchStates.get(0)].replaceAll("_", " "), true, false);
                    if (opened.length > switchStates.get(1))
                        drawStringWithShadow(x + second, top - (row * 1), opened[switchStates.get(1)].replaceAll("_", " "), true, false);
                    if (iris.length > switchStates.get(2))
                        drawStringWithShadow(x + second, top - (row * 2), iris[switchStates.get(2)].replaceAll("_", " "), true, false);
                    if (lastSymbol.length > switchStates.get(3))
                        drawStringWithShadow(x + second, top - (row * 3), lastSymbol[switchStates.get(3)].replaceAll("_", " "), true, false);

                } else if (mode == UniverseDialerMode.COUNTDOWN) {
                    if(notLinked) {
                        drawStringWithShadow(-0.46f, (0.32f * 2) - (0.2f * 3), I18n.format("item.jsg.universe_dialer.countdown_not_sync"), false, false);
                    }
                    if (!clocksDisabled) {
                        long time = compound.getLong("timerCountTo");
                        long countdown = (time - JSGMinecraftHelper.getPlayerTickClientSide());
                        long ticks = countdown;

                        if (countdown < 0) ticks = 0;

                        // must be there because client doesn't get message when timerCountTo reaches -1
                        if (countdown < -(20L * JSGConfig.countdownConfig.zeroDelay)) time = -1;

                        float y = (0.32f * 2) + (0.20f * 2) - 0.8f;
                        float x = -0.52f + 0.90f;

                        GlStateManager.pushMatrix();
                        GlStateManager.translate(x, y, 0);
                        GlStateManager.scale(0.03, 0.03, 0.03);
                        GlStateManager.rotate(180, 0, 1, 0);
                        GlStateManager.rotate(180, 0, 0, 1);
                        GlStateManager.pushMatrix();
                        GlStateManager.color(1, 1, 1);
                        AncientRenderer.renderClock(ticks, false, time, true);
                        GlStateManager.popMatrix();
                        GlStateManager.popMatrix();
                    }
                    else {
                        drawStringWithShadow(-0.42f, (0.32f * 2) - (0.2f * 1.5f), I18n.format("item.jsg.universe_dialer.countdown_not_active"), false, false);
                    }
                }
                // Lists of each tag list
                else {
                    int selected = compound.getByte("selected");
                    NBTTagList tagList = compound.getTagList(mode.tagListName, NBT.TAG_COMPOUND);

                    for (int offset = -1; offset <= 1; offset++) {
                        int index = selected + offset;
                        if (index >= 0 && index < tagList.tagCount()) {

                            boolean active = offset == 0;
                            NBTTagCompound entryCompound = tagList.getCompoundTagAt(index);

                            switch (mode) {
                                case MEMORY:
                                case NEARBY:
                                    drawStringWithShadow(-0.32f, 0.32f - 0.32f * offset, (index + 1) + ".", active, false);

                                    StargateAddress address = new StargateAddress(entryCompound);

                                    boolean hasUpgrade = entryCompound.getBoolean("hasUpgrade");

                                    int symbolCount = SymbolUniverseEnum.getMaxSymbolsDisplay((hasUpgrade && mode != NEARBY));

                                    // gate status (might be used in future)
                                    EnumStargateState gateStatus = EnumStargateState.valueOf(compound.getInteger("gateStatus"));
                                    boolean isIdle = gateStatus.idle();

                                    StargateAddressDynamic dialedAddress = addrFromBytes(compound, "dialedAddress");
                                    StargateAddressDynamic toDialAddress = addrFromBytes(compound, "toDialAddress");

                                    int dialed = -1;
                                    boolean isDialingThisAddr = false;
                                    if (toDialAddress != null && toDialAddress.equals(address)) {
                                        dialed = 0;
                                        isDialingThisAddr = true;
                                    }

                                    if (dialedAddress != null && dialed >= 0)
                                        dialed = dialedAddress.getSize();

                                    if (dialed == 0 && isIdle)
                                        dialed = -1;

                                    boolean engage_poo;

                                    if (dialed == -1) engage_poo = false;
                                    else if (hasUpgrade)
                                        engage_poo = (dialed == 9);
                                    else
                                        engage_poo = (dialed == 7);

                                    if (entryCompound.hasKey("name")) {
                                        String entryName = entryCompound.getString("name");
                                        if (dialed > -1)
                                            entryName += " (" + dialed + ")";
                                        drawStringWithShadow(-0.05f, 0.32f - 0.32f * offset, entryName, active, false, true, dialed >= 0, gateStatus);
                                    } else {
                                        for (int i = 0; i < symbolCount; i++) {
                                            boolean engage_s = (i < dialed);
                                            renderSymbol(offset, i, address.get(i), isDialingThisAddr, active, hasUpgrade, engage_s, gateStatus);
                                        }

                                        renderSymbol(offset, symbolCount, SymbolUniverseEnum.getOrigin(), isDialingThisAddr, active, hasUpgrade, engage_poo, gateStatus);
                                    }

                                    break;

                                case RINGS:
                                    TransportRings rings = new TransportRings(entryCompound);
                                    String name = rings.getName();
                                    if (name.equals("") || name.equals("[empty]")) name = index + "";
                                    drawStringWithShadow(-0.10f, 0.32f - 0.32f * offset, name, active, false);
                                    break;

                                case OC:
                                    ItemOCMessage message = new ItemOCMessage(entryCompound);
                                    drawStringWithShadow(-0.32f, 0.32f - 0.32f * offset, (index + 1) + ".", active, false);
                                    drawStringWithShadow(-0.10f, 0.32f - 0.32f * offset, message.name, active, false);
                                    break;
                            }
                        }
                    }
                }
            }
        }

        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}
