package tauri.dev.jsg.datafixer;

import tauri.dev.jsg.JSG;
import tauri.dev.jsg.block.JSGBlocks;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.datafix.IFixableData;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * {@link TileEntity} data fixer for transition of block names
 * between AUNIS/JSG 1.5 and 1.6.
 * Data version 5 to 6.
 * 
 * @author MrJake222
 *
 */
public class TileNamesFixer implements IFixableData {
	@Override
	public int getFixVersion() {
		return 6;
	}

	@Nonnull
	@Override
	public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
		Block newBlock = JSGBlocks.remapBlock(compound.getString("id"), true);
		
		if (newBlock != null) {
			JSG.debug("Fixing block id " + compound.getString("id") + ", now: " + newBlock.getRegistryName());
			
			compound.setString("id", Objects.requireNonNull(newBlock.getRegistryName()).toString());
			compound.setInteger("DataVersion", getFixVersion());
		}
		
		return compound;
	}
}
