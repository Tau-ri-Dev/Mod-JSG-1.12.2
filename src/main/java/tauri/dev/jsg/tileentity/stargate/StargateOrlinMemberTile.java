package tauri.dev.jsg.tileentity.stargate;

import tauri.dev.jsg.block.JSGBlocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.util.main.JSGProps;

import java.util.List;
import java.util.Random;

public class StargateOrlinMemberTile extends StargateAbstractMemberTile {
	
	// ---------------------------------------------------------------------------------
	// Broken state
	
	private int openCount = 0;
	
	public boolean isBroken() {
		return openCount == JSGConfig.Stargate.mechanics.stargateOrlinMaxOpenCount;
	}
	
	public void incrementOpenCount() {
		openCount++;
		if(openCount >= JSGConfig.Stargate.mechanics.stargateOrlinMaxOpenCount){
			world.setBlockState(pos, world.getBlockState(pos).withProperty(JSGProps.ORLIN_BROKEN, true));
		}
		markDirty();
	}
	
	public int getOpenCount() {
		return openCount;
	}
	
	public void addDrops(List<ItemStack> drops) {
		
		if (isBroken()) {
			Random rand = new Random();
			
			drops.add(new ItemStack(Items.IRON_INGOT, 1 + rand.nextInt(2)));
			drops.add(new ItemStack(Items.REDSTONE, 2 + rand.nextInt(3)));
		}
			
		else {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setInteger("openCount", openCount);
			
			ItemStack stack = new ItemStack(Item.getItemFromBlock(JSGBlocks.STARGATE_ORLIN_MEMBER_BLOCK));
			stack.setTagCompound(compound);
			
			drops.add(stack);
		}
	}
	
	public void initializeFromItemStack(ItemStack stack) {
		if (stack.hasTagCompound()) {
			NBTTagCompound compound = stack.getTagCompound();
			
			if (compound.hasKey("openCount")) {
				openCount = compound.getInteger("openCount");
			}
		}
	}
	
	// ---------------------------------------------------------------------------------
	// NBT
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setInteger("openCount", openCount);
		
		return super.writeToNBT(compound);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		openCount = compound.getInteger("openCount");
		
		super.readFromNBT(compound);
	}
}
