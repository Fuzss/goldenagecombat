package com.fuzs.puzzleslib_gc.config;


import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * internal storage for registered config entries
 * @param <S> config value of a certain type
 * @param <T> type for value
 * @param <R> return type after applying transformer
 */
public class ConfigValueData<S extends ForgeConfigSpec.ConfigValue<T>, T, R> {

    /**
     * config type of this entry
     */
    final ModConfig.Type configType;
    /**
     * config value entry
     */
    final S configValue;
    /**
     * action to perform when the entry is updated
     */
    final Consumer<R> syncAction;
    /**
     * transformation to apply when returning value, usually {@link Function#identity}
     */
    final Function<T, R> valueTransformer;
    /**
     * source mod this entry belongs to
     */
    final String parentModid;

    /**
     * new entry storage
     */
    ConfigValueData(ModConfig.Type configType, S configValue, Consumer<R> syncAction, Function<T, R> valueTransformer, String parentModid) {

        this.configType = configType;
        this.configValue = configValue;
        this.syncAction = syncAction;
        this.valueTransformer = valueTransformer;
        this.parentModid = parentModid;
    }

    /**
     * get type for filtering purposes
     * @return type of this
     */
    ModConfig.Type getType() {

        return this.configType;
    }

    /**
     * get modid for filtering purposes
     * @return modid associated with this
     */
    String getModId() {

        return this.parentModid;
    }

    /**
     * modify a config value so the config file is updated as well and sync afterwards
     * @param operator action to apply to config value
     */
    public void modifyConfigValue(UnaryOperator<T> operator) {

        this.configValue.set(operator.apply(this.getRawValue()));
        this.sync();
    }

    /**
     * @return current value from entry
     */
    public R getValue() {

        return this.valueTransformer.apply(this.configValue.get());
    }

    /**
     * @return current raw value from entry
     */
    public T getRawValue() {

        return this.configValue.get();
    }

    /**
     * get value from config value and supply it to consumer
     */
    void sync() {

        this.syncAction.accept(this.getValue());
    }

}
