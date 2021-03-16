package com.fuzs.puzzleslib_gc.config;

import com.fuzs.puzzleslib_gc.PuzzlesLib;
import com.fuzs.puzzleslib_gc.util.INamespaceLocator;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * main config manager for this mod
 */
@SuppressWarnings("unused")
public class ConfigManager implements INamespaceLocator {

    /**
     * singleton instance
     */
    private static ConfigManager instance;

    /**
     * config build helpers for each mod separately since they store the forge builders and specs
     */
    private final Map<String, ConfigBuilder> configBuilders = Maps.newHashMap();
    /**
     * all config entries as a set
     */
    private final Map<String, ConfigValueData<? extends ForgeConfigSpec.ConfigValue<?>, ?, ?>> configData = Maps.newHashMap();
    /**
     * listeners to call when a config is somehow loaded
     */
    private final Multimap<ModConfig.Type, Runnable> configListeners = HashMultimap.create();

    /**
     * this class is a singleton
     */
    private ConfigManager() {

    }

    /**
     * register configs from non-empty builders and add listener from active mod container to {@link #onModConfig}
     * @param path optional directory inside of main config dir
     */
    public void load(String... path) {

        if (path.length > 0) {

            this.getBuilder().moveToFolder(path);
        }

        this.getBuilder().registerConfigs(ModLoadingContext.get());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModConfig);
    }

    /**
     * fires on both loading and reloading, loading phase is required for initial setup
     * @param evt event provided by Forge
     */
    public void onModConfig(final ModConfig.ModConfigEvent evt) {

        String modid = evt.getConfig().getModId();
        ModConfig.Type type = evt.getConfig().getType();
        if (this.getBuilder(modid).isSpecNotValid(type)) {

            PuzzlesLib.LOGGER.error("Unable to get values from " + type.extension() + " config for " + modid + " during " + (evt instanceof ModConfig.Loading ? "loading" : "reloading") + " phase: " + "Config spec not present");
        } else {

            if (this.syncAll(modid, type) && evt instanceof ModConfig.Reloading) {

                PuzzlesLib.LOGGER.info("Reloading " + type.extension() + " config for mod " + modid);
            }
        }
    }

    /**
     * sync all config entries and notify all listeners
     * @param type config type for this listener
     */
    public void syncAll(ModConfig.Type type) {

        if (this.syncAll(null, type)) {

            PuzzlesLib.LOGGER.info("Reloading " + type.extension() + " config for all mods");
        }
    }

    /**
     * sync config entries for specific type of config
     * call listeners for type as the config has somehow been loaded
     * @param modid mod to get entries for
     * @param type config type for this listener
     * @return was any data found for syncing
     */
    private boolean syncAll(@Nullable String modid, ModConfig.Type type) {

        List<ConfigValueData<? extends ForgeConfigSpec.ConfigValue<?>, ?, ?>> typeData = this.getConfigData(modid)
                .filter(configValue -> configValue.getType() == type)
                .collect(Collectors.toList());

        if (!typeData.isEmpty()) {

            typeData.forEach(ConfigValueData::sync);
            this.configListeners.get(type).forEach(Runnable::run);

            return true;
        }

        return false;
    }

    /**
     * @param modid mod to get entries for
     * @return stream of entries only for this mod
     */
    private Stream<ConfigValueData<? extends ForgeConfigSpec.ConfigValue<?>, ?, ?>> getConfigData(@Nullable String modid) {

        return this.configData.values().stream().filter(entry -> modid == null || entry.getModId().equals(modid));
    }

    /**
     * @param paths individual parts of path for config value
     * @return the config value
     */
    public Object getValue(String... paths) {

        return this.getValue(String.join(".", paths));
    }

    /**
     * @param path path for config value
     * @return the config value
     */
    public Object getValue(String path) {

        return this.getConfigDataAtPath(path).<Object>map(ConfigValueData::getValue).orElse(null);

    }

    /**
     * @param paths individual parts of path for config value
     * @return config data
     */
    public Optional<ConfigValueData<? extends ForgeConfigSpec.ConfigValue<?>, ?, ?>> getConfigDataAtPath(String... paths) {

        return this.getConfigDataAtPath(String.join(".", paths));
    }

    /**
     * @param path path for config data entry
     * @return config data
     */
    public Optional<ConfigValueData<? extends ForgeConfigSpec.ConfigValue<?>, ?, ?>> getConfigDataAtPath(String path) {

        Optional<ConfigValueData<? extends ForgeConfigSpec.ConfigValue<?>, ?, ?>> optional;
        optional = Optional.ofNullable(this.configData.get(path));
        if (optional.isPresent()) {

            return optional;
        }

        PuzzlesLib.LOGGER.error("Unable to get config value for path \"" + path + "\": " + "No config value found for path");
        return Optional.empty();
    }

    /**
     * register config entry on both client and server
     * @param entry source config value object
     * @param action action to perform when value changes (is reloaded)
     * @param <S> config value of a certain type
     * @param <T> type for value
     */
    public <S extends ForgeConfigSpec.ConfigValue<T>, T> void registerCommonEntry(S entry, Consumer<T> action) {

        this.registerEntry(ModConfig.Type.COMMON, entry, action, Function.identity());
    }

    /**
     * register config entry on the client
     * @param entry source config value object
     * @param action action to perform when value changes (is reloaded)
     * @param <S> config value of a certain type
     * @param <T> type for value
     */
    public <S extends ForgeConfigSpec.ConfigValue<T>, T> void registerClientEntry(S entry, Consumer<T> action) {

        this.registerEntry(ModConfig.Type.CLIENT, entry, action, Function.identity());
    }

    /**
     * register config entry on the server
     * @param entry source config value object
     * @param action action to perform when value changes (is reloaded)
     * @param <S> config value of a certain type
     * @param <T> type for value
     */
    public <S extends ForgeConfigSpec.ConfigValue<T>, T> void registerServerEntry(S entry, Consumer<T> action) {

        this.registerEntry(ModConfig.Type.SERVER, entry, action, Function.identity());
    }

    /**
     * register config entry for active type
     * @param <S> config value of a certain type
     * @param <T> type for value
     * @param entry source config value object
     * @param action action to perform when value changes (is reloaded)
     */
    public <S extends ForgeConfigSpec.ConfigValue<T>, T> void registerEntry(S entry, Consumer<T> action) {

        this.registerEntry(entry, action, Function.identity());
    }

    /**
     * register config entry for active type
     * @param entry source config value object
     * @param action action to perform when value changes (is reloaded)
     * @param transformer transformation to apply when returning value
     * @param <S> config value of a certain type
     * @param <T> type for value
     * @param <R> final return type of config entry
     */
    public <S extends ForgeConfigSpec.ConfigValue<T>, T, R> void registerEntry(S entry, Consumer<R> action, Function<T, R> transformer) {

        ModConfig.Type activeType = this.getBuilder().getActiveType();
        if (activeType == null) {

            PuzzlesLib.LOGGER.error("Unable to register config entry: " + "Active builder is null");
        } else if (this.getBuilder().isSpecNotBuilt(activeType)) {

            this.registerEntry(activeType, entry, action, transformer);
        } else {

            PuzzlesLib.LOGGER.error("Unable to register config entry: " + "Config spec already built");
        }
    }

    /**
     * register config entry for given type
     * @param type type of config to register for
     * @param entry source config value object
     * @param action action to perform when value changes (is reloaded)
     * @param transformer transformation to apply when returning value
     * @param <S> config value of a certain type
     * @param <T> type for value
     * @param <R> final return type of config entry
     */
    private <S extends ForgeConfigSpec.ConfigValue<T>, T, R> void registerEntry(ModConfig.Type type, S entry, Consumer<R> action, Function<T, R> transformer) {

        this.configData.put(String.join(".", entry.getPath()), new ConfigValueData<>(type, entry, action, transformer, this.getActiveNamespace()));
    }

    /**
     * add a listener for when the config is somehow loaded
     * @param listener listener to add
     * @param type config type for this listener
     */
    public void addListener(Runnable listener, ModConfig.Type type) {

        this.configListeners.put(type, listener);
    }

    /**
     * @param type type of config
     * @param modId modid this config belongs to
     * @return config name as if it were generated by Forge itself
     */
    public static String getConfigName(String modId, ModConfig.Type type) {

        return String.format("%s-%s.toml", modId, type.extension());
    }

    /**
     * put config into it's own folder when there are multiples
     * @param type type of config
     * @param modId modid this config belongs to
     * @return name lead by folder
     */
    public static String getConfigNameInFolder(String modId, ModConfig.Type type) {

        return modId + File.separator + getConfigName(modId, type);
    }

    /**
     * @param entries entries to convert to string
     * @param <T> registry element type
     * @return entries as string list
     */
    @SafeVarargs
    public static <T extends IForgeRegistryEntry<T>> List<String> getKeyList(T... entries) {

        return Stream.of(entries)
                .map(IForgeRegistryEntry::getRegistryName)
                .filter(Objects::nonNull)
                .map(ResourceLocation::toString)
                .collect(Collectors.toList());
    }

    /**
     * get builder for active mod, create if not present
     * @return builder for active mod
     */
    private ConfigBuilder getBuilder() {

        return this.getBuilder(this.getActiveNamespace());
    }

    /**
     * get builder for a given mod, create if not present
     * @param modid modid to get builder for
     * @return builder for active mod
     */
    private ConfigBuilder getBuilder(String modid) {

        return this.configBuilders.computeIfAbsent(modid, key -> new ConfigBuilder());
    }

    /**
     * @return instance of this
     */
    public static ConfigManager get() {

        if (instance == null) {

            instance = new ConfigManager();
        }

        return instance;
    }

    /**
     * get builder directly
     * @return builder for active mod
     */
    public static ConfigBuilder builder() {

        return get().getBuilder();
    }

}
