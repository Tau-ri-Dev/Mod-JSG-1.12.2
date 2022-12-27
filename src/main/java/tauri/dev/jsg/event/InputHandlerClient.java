package tauri.dev.jsg.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.gui.GuiSendCode;
import tauri.dev.jsg.gui.PageRenameGui;
import tauri.dev.jsg.gui.entry.NotebookEntryChangeGui;
import tauri.dev.jsg.gui.entry.OCEntryChangeGui;
import tauri.dev.jsg.gui.entry.UniverseEntryChangeGui;
import tauri.dev.jsg.item.JSGItems;
import tauri.dev.jsg.item.linkable.dialer.UniverseDialerActionEnum;
import tauri.dev.jsg.item.linkable.dialer.UniverseDialerActionPacketToServer;
import tauri.dev.jsg.item.linkable.dialer.UniverseDialerItem;
import tauri.dev.jsg.item.linkable.gdo.GDOActionEnum;
import tauri.dev.jsg.item.linkable.gdo.GDOActionPacketToServer;
import tauri.dev.jsg.item.linkable.gdo.GDOMode;
import tauri.dev.jsg.item.notebook.NotebookActionEnum;
import tauri.dev.jsg.item.notebook.NotebookActionPacketToServer;
import tauri.dev.jsg.packet.JSGPacketHandler;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

@EventBusSubscriber(value = Side.CLIENT)
public class InputHandlerClient {

    // Common bindings
    private static final KeyBinding MODE_SCROLL = new KeyBinding("config.jsg.mode_scroll", Keyboard.KEY_LCONTROL, "JSG");
    private static final KeyBinding ADDRESS_SCROLL = new KeyBinding("config.jsg.address_scroll", Keyboard.KEY_LSHIFT, "JSG");

    private static final KeyBinding MODE_UP = new KeyBinding("config.jsg.mode_up", 0, "JSG");
    private static final KeyBinding MODE_DOWN = new KeyBinding("config.jsg.mode_down", 0, "JSG");
    private static final KeyBinding ADDRESS_UP = new KeyBinding("config.jsg.address_up", 0, "JSG");
    private static final KeyBinding ADDRESS_DOWN = new KeyBinding("config.jsg.address_down", 0, "JSG");

    // Used to open common gui on Notebook/Universe dialer
    private static final KeyBinding ADDRESS_EDIT = new KeyBinding("config.jsg.address_edit", Keyboard.KEY_INSERT, "JSG");

    // Unpress
    private static final Method METHOD_UNPRESS = ObfuscationReflectionHelper.findMethod(KeyBinding.class, "func_74505_d", void.class);

    private static final KeyBinding[] KEY_BINDINGS = {
            // Common bindings
            MODE_SCROLL,
            ADDRESS_SCROLL,

            MODE_UP,
            MODE_DOWN,
            ADDRESS_UP,
            ADDRESS_DOWN,

            ADDRESS_EDIT
    };

    // Init function, call from preInit
    public static void registerKeybindings() {
        for (KeyBinding keyb : KEY_BINDINGS) {
            ClientRegistry.registerKeyBinding(keyb);
        }
    }


    // Get hand holding item
    @Nullable
    public static EnumHand getHand(Item item) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        EnumHand hand = null;

        if (player == null)
            return null;

        if (player.getHeldItemMainhand().getItem() == item)
            hand = EnumHand.MAIN_HAND;
        else if (player.getHeldItemOffhand().getItem() == item)
            hand = EnumHand.OFF_HAND;

        return hand;
    }

    @Nullable
    public static ItemStack getItemStack(EntityPlayer player, Item item) {
        EnumHand hand = getHand(item);

        if (hand != null) {
            return player.getHeldItem(hand);
        }

        return null;
    }

    // Check for item in both hands
    public static boolean checkForItem(Item item) {
        return getHand(item) != null;
    }

    // ------------------------------------------------------------------------------------
    // Events
    @SubscribeEvent
    public static void onMouseEvent(MouseEvent event) {

        if (event.getDwheel() == 0) {
            if (checkForItem(JSGItems.GDO)) {
                // opening code input gui
                if (Minecraft.getMinecraft().gameSettings.keyBindUseItem.isKeyDown()) {
                    ItemStack itemStack = getItemStack(Minecraft.getMinecraft().player, JSGItems.GDO);
					if (itemStack != null && itemStack.hasTagCompound() && Objects.requireNonNull(itemStack.getTagCompound()).hasKey("linkedGate")
							&& GDOMode.valueOf(itemStack.getTagCompound().getByte("mode")) == GDOMode.CODE_SENDER) {
						Minecraft.getMinecraft().displayGuiScreen(new GuiSendCode(getHand(JSGItems.GDO)));
					}
				}
            }
            return;
        }

        // NBT print

        boolean next = event.getDwheel() < 0;

        if (checkForItem(JSGItems.UNIVERSE_DIALER)) {
            EnumHand hand = getHand(JSGItems.UNIVERSE_DIALER);
            UniverseDialerActionEnum action = null;


			if (hand != null && Minecraft.getMinecraft().player.getHeldItem(hand).getItemDamage() != UniverseDialerItem.UniverseDialerVariants.BROKEN.meta) {
				if (MODE_SCROLL.isKeyDown())
					action = UniverseDialerActionEnum.MODE_CHANGE;

				else if (ADDRESS_SCROLL.isKeyDown())
					action = UniverseDialerActionEnum.ADDRESS_CHANGE;


				// ---------------------------------------------
				if (action != null) {
					event.setCanceled(true);
					JSGPacketHandler.INSTANCE.sendToServer(new UniverseDialerActionPacketToServer(action, hand, next));
				}
			}
		} else if (checkForItem(JSGItems.GDO)) {
            EnumHand hand = getHand(JSGItems.GDO);
            GDOActionEnum action = null;

            if (MODE_SCROLL.isKeyDown())
                action = GDOActionEnum.MODE_CHANGE;
            else if (ADDRESS_SCROLL.isKeyDown())
                action = GDOActionEnum.ADDRESS_CHANGE;

            // ---------------------------------------------
            if (action != null) {
                event.setCanceled(true);
                JSGPacketHandler.INSTANCE.sendToServer(new GDOActionPacketToServer(action, hand, next));
            }
        } else if (checkForItem(JSGItems.NOTEBOOK_ITEM)) {
            EnumHand hand = getHand(JSGItems.NOTEBOOK_ITEM);
            NotebookActionEnum action = null;

            if (ADDRESS_SCROLL.isKeyDown())
                action = NotebookActionEnum.ADDRESS_CHANGE;


            // ---------------------------------------------
            if (action != null) {
                event.setCanceled(true);
                JSGPacketHandler.INSTANCE.sendToServer(new NotebookActionPacketToServer(action, hand, next));
            }
        }

    }


    @SubscribeEvent
    public static void onKeyboardEvent(ClientTickEvent event) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (event.phase != Phase.END)
            return;

        EntityPlayer player = Minecraft.getMinecraft().player;

        if (checkForItem(JSGItems.UNIVERSE_DIALER)) {
            EnumHand hand = getHand(JSGItems.UNIVERSE_DIALER);
            UniverseDialerActionEnum action = null;
            boolean next = false;

            if (MODE_UP.isPressed()) {
                action = UniverseDialerActionEnum.MODE_CHANGE;
			} else if (MODE_DOWN.isPressed()) {
                action = UniverseDialerActionEnum.MODE_CHANGE;
                next = true;
            } else if (ADDRESS_UP.isPressed()) {
                action = UniverseDialerActionEnum.ADDRESS_CHANGE;
			} else if (ADDRESS_DOWN.isPressed()) {
                action = UniverseDialerActionEnum.ADDRESS_CHANGE;
                next = true;
            }

            // ---------------------------------------------
            if (action != null) {
                JSGPacketHandler.INSTANCE.sendToServer(new UniverseDialerActionPacketToServer(action, hand, next));
            }
        } else if (checkForItem(JSGItems.GDO)) {
            EnumHand hand = getHand(JSGItems.GDO);
            GDOActionEnum action = null;
            boolean next = false;

            if (MODE_UP.isPressed()) {
                action = GDOActionEnum.MODE_CHANGE;
			} else if (MODE_DOWN.isPressed()) {
                action = GDOActionEnum.MODE_CHANGE;
                next = true;
            } else if (ADDRESS_UP.isPressed()) {
                action = GDOActionEnum.ADDRESS_CHANGE;
			} else if (ADDRESS_DOWN.isPressed()) {
                action = GDOActionEnum.ADDRESS_CHANGE;
                next = true;
            }

            // ---------------------------------------------
            if (action != null) {
                JSGPacketHandler.INSTANCE.sendToServer(new GDOActionPacketToServer(action, hand, next));
            }
        } else if (checkForItem(JSGItems.NOTEBOOK_ITEM)) {
            EnumHand hand = getHand(JSGItems.NOTEBOOK_ITEM);
            NotebookActionEnum action = null;
            boolean next = false;

            if (ADDRESS_UP.isPressed()) {
                action = NotebookActionEnum.ADDRESS_CHANGE;
			} else if (ADDRESS_DOWN.isPressed()) {
                action = NotebookActionEnum.ADDRESS_CHANGE;
                next = true;
            }

            // ---------------------------------------------
            if (action != null) {
                JSGPacketHandler.INSTANCE.sendToServer(new NotebookActionPacketToServer(action, hand, next));
            }
        }

        if (ADDRESS_EDIT.isPressed()) {
            tryOpenAddressGui(player);
        }

        for (KeyBinding keyBinding : KEY_BINDINGS) {
            // Skip modifiers
            if (keyBinding == MODE_SCROLL || keyBinding == ADDRESS_SCROLL)
                continue;

            METHOD_UNPRESS.invoke(keyBinding);
        }
    }

    private static void tryOpenAddressGui(EntityPlayer player) {
        EnumHand hand = getHand(JSGItems.PAGE_NOTEBOOK_ITEM);
        if (hand != null) {
            ItemStack stack = player.getHeldItem(hand);

            if (stack.getMetadata() == 1) {
                // Full page (not empty)
                Minecraft.getMinecraft().displayGuiScreen(new PageRenameGui(hand, stack));
            }

            return;
        }

        hand = getHand(JSGItems.NOTEBOOK_ITEM);
        if (hand != null) {
            if(player.isSneaking())
                Minecraft.getMinecraft().displayGuiScreen(new PageRenameGui(hand, player.getHeldItem(hand)));
            else
                Minecraft.getMinecraft().displayGuiScreen(new NotebookEntryChangeGui(hand, player.getHeldItem(hand).getTagCompound()));
            return;
        }

        hand = getHand(JSGItems.UNIVERSE_DIALER);
        if (hand != null) {
            if(player.isSneaking())
                Minecraft.getMinecraft().displayGuiScreen(new PageRenameGui(hand, player.getHeldItem(hand)));
            else
                Minecraft.getMinecraft().displayGuiScreen(new UniverseEntryChangeGui(hand, player.getHeldItem(hand).getTagCompound(), player.world));
            return;
        }

        hand = getHand(JSGItems.GDO);
        if (hand != null) {
            if(player.isSneaking())
                Minecraft.getMinecraft().displayGuiScreen(new PageRenameGui(hand, player.getHeldItem(hand)));
            else if(JSG.ocWrapper.isModLoaded())
                Minecraft.getMinecraft().displayGuiScreen(new OCEntryChangeGui(hand, player.getHeldItem(hand).getTagCompound(), null));
		}
    }
}
