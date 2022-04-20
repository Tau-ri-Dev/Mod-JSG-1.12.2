package mrjake.aunis.transportrings;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;

public enum TransportResult {
	OK(null),
	BUSY(new TextComponentTranslation("tile.aunis.transportrings_block.busy")),
	BUSY_TARGET(new TextComponentTranslation("tile.aunis.transportrings_block.busy_target")),
	OBSTRUCTED(new TextComponentTranslation("tile.aunis.transportrings_block.obstructed")),
	OBSTRUCTED_TARGET(new TextComponentTranslation("tile.aunis.transportrings_block.obstructed_target")),
	NO_SUCH_ADDRESS(new TextComponentTranslation("tile.aunis.transportrings_block.non_existing_address")),
	NOT_IN_GRID(new TextComponentTranslation("tile.aunis.transportrings_block.rings_not_in_grid")),
	NOT_ENOUGH_POWER(new TextComponentTranslation("tile.aunis.transportrings_block.not_enough_power")),
	ACTIVATED(null),
	ALREADY_ACTIVATED(null);

	@Nullable
	public TextComponentTranslation textComponent;

	private TransportResult(TextComponentTranslation textComponent) {
		this.textComponent = textComponent;
		
	}
	
	public boolean ok() {
		return this == OK;
	}

	public void sendMessageIfFailed(EntityPlayer player) {
		if (textComponent != null) {
			player.sendStatusMessage(textComponent, true);
		}
	}
}