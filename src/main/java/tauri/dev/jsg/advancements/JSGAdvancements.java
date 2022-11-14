package tauri.dev.jsg.advancements;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import tauri.dev.jsg.JSG;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class JSGAdvancements {
    public static final JSGAdvancement CHEVRON_SEVEN_LOCKED = new JSGAdvancement("chevron_seven_locked");


    public static final JSGAdvancement[] TRIGGER_ARRAY = new JSGAdvancement[]{
            CHEVRON_SEVEN_LOCKED
    };

    public static void register(){
        Method method = ReflectionHelper.findMethod(CriteriaTriggers.class, "register", "func_192118_a", ICriterionTrigger.class);
        method.setAccessible(true);
        for (int i = 0; i < JSGAdvancements.TRIGGER_ARRAY.length; i++) {
            try {
                method.invoke(null, JSGAdvancements.TRIGGER_ARRAY[i]);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        JSG.info("Advancements successfully loaded!");
    }
}
