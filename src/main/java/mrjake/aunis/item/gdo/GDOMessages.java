package mrjake.aunis.item.gdo;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;

/**
 * @author matousss
 */
public enum GDOMessages {
    OPENED(new TextComponentTranslation("item.aunis.gdo.iris_opened")),
    BUSY(new TextComponentTranslation("item.aunis.gdo.iris_busy")),
    CODE_ACCEPTED(new TextComponentTranslation("item.aunis.gdo.code_accepted")),
    CODE_REJECTED(new TextComponentTranslation("item.aunis.gdo.code_rejected"));


    public TextComponentTranslation textComponent;

    private GDOMessages(TextComponentTranslation textComponent) {
        this.textComponent = textComponent;

    }


    public void sendMessageIfFailed(EntityPlayer player) {
        player.sendStatusMessage(textComponent, true);

    }
}
