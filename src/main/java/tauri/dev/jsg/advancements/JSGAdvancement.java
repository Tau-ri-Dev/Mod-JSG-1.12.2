package tauri.dev.jsg.advancements;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import tauri.dev.jsg.JSG;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class JSGAdvancement implements ICriterionTrigger<JSGAdvancement.Instance> {
    private final ResourceLocation RL;
    private final Map<PlayerAdvancements, JSGAdvancement.Listeners> listeners = Maps.newHashMap();

    public JSGAdvancement(String parString) {
        super();
        RL = new ResourceLocation(JSG.MOD_ID, parString);
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return RL;
    }

    @Override
    public void addListener(@Nonnull PlayerAdvancements playerAdvancementsIn, @Nonnull ICriterionTrigger.Listener<JSGAdvancement.Instance> listener) {
        JSGAdvancement.Listeners myCustomTrigger$listeners = listeners.get(playerAdvancementsIn);

        if (myCustomTrigger$listeners == null) {
            myCustomTrigger$listeners = new JSGAdvancement.Listeners(playerAdvancementsIn);
            listeners.put(playerAdvancementsIn, myCustomTrigger$listeners);
        }

        myCustomTrigger$listeners.add(listener);
    }

    @Override
    public void removeListener(@Nonnull PlayerAdvancements playerAdvancementsIn, @Nonnull ICriterionTrigger.Listener<JSGAdvancement.Instance> listener) {
        JSGAdvancement.Listeners listeners1 = listeners.get(playerAdvancementsIn);

        if (listeners1 != null) {
            listeners1.remove(listener);

            if (listeners1.isEmpty()) {
                listeners.remove(playerAdvancementsIn);
            }
        }
    }

    @Override
    public void removeAllListeners(@Nonnull PlayerAdvancements playerAdvancementsIn) {
        listeners.remove(playerAdvancementsIn);
    }

    /**
     * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
     *
     * @param json    the json
     * @param context the context
     * @return the tame bird trigger. instance
     */
    @Nonnull
    @Override
    public JSGAdvancement.Instance deserializeInstance(@Nonnull JsonObject json, @Nonnull JsonDeserializationContext context) {
        return new JSGAdvancement.Instance(getId());
    }

    public void trigger(EntityPlayerMP parPlayer) {
        JSGAdvancement.Listeners listeners1 = listeners.get(parPlayer.getAdvancements());

        if (listeners1 != null) {
            listeners1.trigger();
        }
    }

    public static class Instance implements ICriterionInstance {
        private final ResourceLocation parRL;

        /**
         * Instantiates a new instance.
         */
        public Instance(ResourceLocation parRL) {
            this.parRL = parRL;
        }

        /**
         * Test.
         *
         * @return true, if successful
         */
        public boolean test() {
            return true;
        }

        @Nonnull
        @Override
        public ResourceLocation getId() {
            return parRL;
        }
    }

    static class Listeners {
        private final PlayerAdvancements playerAdvancements;
        private final Set<ICriterionTrigger.Listener<JSGAdvancement.Instance>> listeners = Sets.newHashSet();

        /**
         * Instantiates a new listeners.
         *
         * @param playerAdvancementsIn the player advancements in
         */
        public Listeners(PlayerAdvancements playerAdvancementsIn) {
            playerAdvancements = playerAdvancementsIn;
        }

        /**
         * Checks if is empty.
         *
         * @return true, if is empty
         */
        public boolean isEmpty() {
            return listeners.isEmpty();
        }

        /**
         * Adds the listener.
         *
         * @param listener the listener
         */
        public void add(ICriterionTrigger.Listener<JSGAdvancement.Instance> listener) {
            listeners.add(listener);
        }

        /**
         * Removes the listener.
         *
         * @param listener the listener
         */
        public void remove(ICriterionTrigger.Listener<JSGAdvancement.Instance> listener) {
            listeners.remove(listener);
        }

        /**
         * Trigger.
         */
        public void trigger() {
            ArrayList<ICriterionTrigger.Listener<JSGAdvancement.Instance>> list = null;

            for (ICriterionTrigger.Listener<JSGAdvancement.Instance> listener : listeners) {
                if (listener.getCriterionInstance().test()) {
                    if (list == null) {
                        list = Lists.newArrayList();
                    }

                    list.add(listener);
                }
            }

            if (list != null) {
                for (ICriterionTrigger.Listener<JSGAdvancement.Instance> listener1 : list) {
                    listener1.grantCriterion(playerAdvancements);
                }
            }
        }
    }
}
