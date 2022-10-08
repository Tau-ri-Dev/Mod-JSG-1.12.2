package tauri.dev.jsg.tileentity.util;

import tauri.dev.jsg.stargate.EnumScheduledTask;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;

/**
 * Used with {@link EnumScheduledTask} to execute scheduled tasks.
 * 
 * @author MrJake
 */
public interface ScheduledTaskExecutorInterface {
	
	/**
	 * Adds given {@link ScheduledTask} to the list.
	 * 
	 * @param scheduledTask The task to be added.
	 */
	public void addTask(ScheduledTask scheduledTask);
	
	/**
	 * Executes given task.
	 * 
	 * @param scheduledTask The task.
	 * @param customData Custom data passed by the user.
	 */
	public void executeTask(EnumScheduledTask scheduledTask, @Nullable NBTTagCompound customData);
}
