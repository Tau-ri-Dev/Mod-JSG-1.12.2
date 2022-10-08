package tauri.dev.jsg.item.linkable.gdo;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;

/**
 * @author matousss
 */
public enum GDOMessages {
    OPENED(new TextComponentTranslation("item.jsg.gdo.iris_opened")),
    BUSY(new TextComponentTranslation("item.jsg.gdo.iris_busy")),
    CODE_ACCEPTED(new TextComponentTranslation("item.jsg.gdo.code_accepted")),
    CODE_REJECTED(new TextComponentTranslation("item.jsg.gdo.code_rejected")),

    CODE_NOT_SET(new TextComponentTranslation("item.jsg.gdo.code_not_set")),
    SEND_TO_COMPUTER(new TextComponentTranslation("item.jsg.gdo.computer_handled"));

    public TextComponentTranslation textComponent;

    private GDOMessages(TextComponentTranslation textComponent) {
        this.textComponent = textComponent;

    }


    public void sendMessageIfFailed(EntityPlayer player) {
        player.sendStatusMessage(textComponent, true);

    }
}
