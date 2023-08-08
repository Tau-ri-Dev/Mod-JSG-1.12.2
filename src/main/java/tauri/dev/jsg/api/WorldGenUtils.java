package tauri.dev.jsg.api;

import net.minecraft.tileentity.TileEntityChest;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import tauri.dev.jsg.tileentity.energy.ZPMHubTile;
import tauri.dev.jsg.util.ItemHandlerHelper;
import tauri.dev.jsg.worldgen.structures.JSGStructure;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class contains methods that are mostly use in JSG world generator
 */
@SuppressWarnings("unused")
public class WorldGenUtils {
    /**
     * Method is used to spawn ZPM in chest
     *
     * @param chest - tile entity of target chest
     * @param findAlreadySpawned
     *      - if true, method will only replace spawned ZPMs in the chest with new ones
     *      - if false, method will find all empty places (is stopOnFound is false) and fill them with ZPMs
     * @param energyPercent - energy percentage of the spawned ZPMs (set to null if u want random value between 10% - 70%)
     * @param stopOnFound - if true, method will spawn only one ZPM (if possible)
     */
    public static void spawnZPMInChest(@Nonnull TileEntityChest chest, boolean findAlreadySpawned, @Nullable Float energyPercent, boolean stopOnFound) {
        JSGStructure.spawnZPMInChest(chest, findAlreadySpawned, energyPercent, stopOnFound);
    }

    /**
     * Method will fill chest with ZPMs. Every empty slot will be filled with ZPM
     * @param chest - tile entity of target chest
     * @param energyPercent - energy percentage of the spawned ZPMs (set to null if u want random value between 10% - 70%)
     */
    public static void fillChestWithZPMs(@Nonnull TileEntityChest chest, @Nullable Float energyPercent){
        JSGStructure.spawnZPMInChest(chest, false, energyPercent, false);
    }

    /**
     * Method is used to spawn ZPM in ZPM Hub tile or ZPM Slot tile entity (ZPMSlotTile extends ZPMHubTile)
     *
     * @param zpmHubTile - tile entity of target zpm hub/slot
     * @param findAlreadySpawned
     *      - if true, method will only replace spawned ZPMs in the chest with new ones
     *      - if false, method will find all empty places (is stopOnFound is false) and fill them with ZPMs
     * @param energyPercent - energy percentage of the spawned ZPMs (set to null if u want random value between 10% - 70%)
     * @param stopOnFound - if true, method will spawn only one ZPM (if possible)
     */
    public static void spawnZPMInZPMHub(@Nonnull ZPMHubTile zpmHubTile, boolean findAlreadySpawned, @Nullable Float energyPercent, boolean stopOnFound) {
        JSGStructure.spawnZPMInZPMHub(zpmHubTile, findAlreadySpawned, energyPercent, stopOnFound);
    }

    /**
     * Method is used to fill ZPM Hub tile or ZPM Slot tile entity (ZPMSlotTile extends ZPMHubTile) with ZPMs
     *
     * @param zpmHubTile - tile entity of target zpm hub/slot
     * @param energyPercent - energy percentage of each spawned ZPMs (set each any value to null if u want random value between 10% - 70%)
     *                      - amount of items in this list is amount of ZPMs spawned
     */
    public static void fillZPMHubWithZPMs(@Nonnull ZPMHubTile zpmHubTile, @Nonnull Float... energyPercent){
        if(energyPercent.length == 0) return;
        IItemHandler handler = zpmHubTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if(handler == null) return;

        int count = Math.min(energyPercent.length, 3);
        ItemHandlerHelper.clearInventory(handler);
        for(int i = 0; i < count; i++){
            JSGStructure.spawnZPMInZPMHub(zpmHubTile, false, energyPercent[i], true);
        }
    }

    /**
     * Method is used to spawn ZPM in any item handler
     *
     * @param handler - target item handler (inventory)
     * @param findAlreadySpawned
     *      - if true, method will only replace spawned ZPMs in the chest with new ones
     *      - if false, method will find all empty places (is stopOnFound is false) and fill them with ZPMs
     * @param energyPercent - energy percentage of the spawned ZPMs (set to null if u want random value between 10% - 70%)
     * @param stopOnFound - if true, method will spawn only one ZPM (if possible)
     */
    public static void spawnZPMInHandler(@Nullable IItemHandler handler, boolean findAlreadySpawned, @Nullable Float energyPercent, boolean stopOnFound) {
        JSGStructure.spawnZPMInHandler(handler, findAlreadySpawned, energyPercent, stopOnFound);
    }

    /**
     * This method is used to insert suspicious page into chest's inventory.
     * If no possible address is found, it will spawn COBWEB instead
     *
     * @param chest - tile entity of target chest
     * @param findAlreadySpawned
     *      - if true, method will only replace spawned notebook pages in the chest with sus pages
     *      - if false, method will find all empty places (is stopOnFound is false) and fill them with sus pages
     * @param stopOnFound - if true, method will spawn only one SUS Page (if possible)
     */
    public static void spawnSusPageInChest(@Nonnull TileEntityChest chest, boolean findAlreadySpawned, boolean stopOnFound){
        JSGStructure.spawnSusPageInChest(chest, findAlreadySpawned, stopOnFound);
    }
}
