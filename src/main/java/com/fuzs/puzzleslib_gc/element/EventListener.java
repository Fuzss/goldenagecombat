package com.fuzs.puzzleslib_gc.element;

import com.fuzs.puzzleslib_gc.config.ConfigManager;
import com.fuzs.puzzleslib_gc.config.serialization.EntryCollectionBuilder;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * add ability to listen to Forge events
 */
@SuppressWarnings("unused")
public abstract class EventListener {

    /**
     * @param entry config entry to add
     * @param action consumer for updating value when changed
     * @param <S> type of config value
     * @param <T> field type
     */
    public static <S extends ForgeConfigSpec.ConfigValue<T>, T> void addToConfig(S entry, Consumer<T> action) {

        ConfigManager.get().registerEntry(entry, action);
    }

    /**
     * @param entry config entry to add
     * @param action consumer for updating value when changed
     * @param transformer transformation to apply when returning value
     * @param <S> type of config value
     * @param <T> field type
     * @param <R> final return type of config entry
     */
    public static <S extends ForgeConfigSpec.ConfigValue<T>, T, R> void addToConfig(S entry, Consumer<R> action, Function<T, R> transformer) {

        ConfigManager.get().registerEntry(entry, action, transformer);
    }

    /**
     * deserialize string <code>data</code> into entries of a <code>registry</code>
     * @param data data as string list as provided by Forge config
     * @param registry registry to get entries from
     * @param <T> type of registry
     * @return deserialized data as set
     */
    public static <T extends IForgeRegistryEntry<T>> Set<T> deserializeToSet(List<String> data, IForgeRegistry<T> registry) {

        return new EntryCollectionBuilder<>(registry).buildEntrySet(data);
    }

    /**
     * deserialize string <code>data</code> into entries of a <code>registry</code>
     * @param data data as string list as provided by Forge config
     * @param registry registry to get entries from
     * @param <T> type of registry
     * @return deserialized data as map
     */
    public static <T extends IForgeRegistryEntry<T>> Map<T, double[]> deserializeToMap(List<String> data, IForgeRegistry<T> registry) {

        return new EntryCollectionBuilder<>(registry).buildEntryMap(data);
    }

    /**
     * @return event storage list
     */
    protected abstract List<EventStorage<? extends Event>> getEventListeners();

    /**
     * Add a consumer listener with {@link EventPriority} set to {@link EventPriority#NORMAL}
     * @param consumer Callback to invoke when a matching event is received
     * @param <T> The {@link Event} subclass to listen for
     */
    protected final <T extends Event> void addListener(Consumer<T> consumer) {

        this.addListener(consumer, EventPriority.NORMAL);
    }

    /**
     * Add a consumer listener with {@link EventPriority} set to {@link EventPriority#NORMAL}
     * @param consumer Callback to invoke when a matching event is received
     * @param receiveCancelled Indicate if this listener should receive events that have been {@link Cancelable} cancelled
     * @param <T> The {@link Event} subclass to listen for
     */
    protected final <T extends Event> void addListener(Consumer<T> consumer, boolean receiveCancelled) {

        this.addListener(consumer, EventPriority.NORMAL, receiveCancelled);
    }

    /**
     * Add a consumer listener with the specified {@link EventPriority}
     * @param consumer Callback to invoke when a matching event is received
     * @param priority {@link EventPriority} for this listener
     * @param <T> The {@link Event} subclass to listen for
     */
    protected final <T extends Event> void addListener(Consumer<T> consumer, EventPriority priority) {

        this.addListener(consumer, priority, false);
    }

    /**
     * Add a consumer listener with the specified {@link EventPriority}
     * @param consumer Callback to invoke when a matching event is received
     * @param priority {@link EventPriority} for this listener
     * @param receiveCancelled Indicate if this listener should receive events that have been {@link Cancelable} cancelled
     * @param <T> The {@link Event} subclass to listen for
     */
    protected final <T extends Event> void addListener(Consumer<T> consumer, EventPriority priority, boolean receiveCancelled) {

        this.getEventListeners().add(new EventStorage<>(consumer, priority, receiveCancelled));
    }

    /**
     * storage for {@link net.minecraftforge.eventbus.api.Event} so we can register and unregister them as needed
     * @param <T> type of event
     */
    protected static class EventStorage<T extends Event> {

        /**
         * Callback to invoke when a matching event is received
         */
        private final Consumer<T> event;
        /**
         * {@link EventPriority} for this listener
         */
        private final EventPriority priority;
        /**
         * Indicate if this listener should receive events that have been {@link Cancelable} cancelled
         */
        private final boolean receiveCancelled;
        /**
         * has been registered or unregistered
         */
        private boolean active;

        /**
         * create new storage object with the same arguments as when calling {@link net.minecraftforge.eventbus.api.IEventBus#addListener}
         * @param priority {@link EventPriority} for this listener
         * @param receiveCancelled Indicate if this listener should receive events that have been {@link Cancelable} cancelled
         * @param consumer Callback to invoke when a matching event is received
         */
        private EventStorage(Consumer<T> consumer, EventPriority priority, boolean receiveCancelled) {

            this.event = consumer;
            this.priority = priority;
            this.receiveCancelled = receiveCancelled;
        }

        /**
         * check if storage object can be registered and do so if possible
         */
        protected void register() {

            if (this.isActive(true)) {

                MinecraftForge.EVENT_BUS.addListener(this.priority, this.receiveCancelled, this.event);
            }
        }

        /**
         * check if storage object can be unregistered and do so if possible
         */
        protected void unregister() {

            if (this.isActive(false)) {

                MinecraftForge.EVENT_BUS.unregister(this.event);
            }
        }

        /**
         * verify with {@link #active} if registering action can be performed
         * @param newState new active state after registering or unregistering
         * @return is an action permitted
         */
        private boolean isActive(boolean newState) {

            if (this.active != newState) {

                this.active = newState;
                return true;
            }

            return false;
        }
    }

}
