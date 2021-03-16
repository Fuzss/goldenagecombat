package com.fuzs.puzzleslib_gc.config;


import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

import javax.annotation.Nullable;

/**
 * internal storage for builders, specs and file names
 */
class ConfigTypeData {

    /**
     * type extension for config name
     */
    private final ModConfig.Type type;
    /**
     * file path, only specified when config is in a separate folder
     */
    private String path = "";
    /**
     * spec for this type
     */
    private ForgeConfigSpec spec;
    /**
     * builder for this type
     */
    private ForgeConfigSpec.Builder builder;

    /**
     * @param type type of config for file name
     */
    ConfigTypeData(ModConfig.Type type) {

        this.type = type;
    }

    /**
     * has the spec for this type been built yet (has {@link #getSpec} been called)
     * @return has spec been built
     */
    boolean isSpecNotBuilt() {

        return this.spec == null;
    }

    /**
     * @return can a new spec be built due to the presence of a builder
     */
    boolean canBuildSpec() {

        return this.builder != null;
    }

    /**
     * add a spec when building config manually
     * @param spec spec to add
     * @return was adding successful (spec not present yet)
     */
    boolean addSpec(ForgeConfigSpec spec) {

        if (this.isSpecNotBuilt()) {

            this.spec = spec;
            return true;
        }

        return false;
    }

    /**
     * get spec, build from builder if absent
     * @return config spec for type
     */
    @Nullable
    ForgeConfigSpec getSpec() {

        if (this.isSpecNotBuilt()) {

            if (!this.canBuildSpec()) {

                return null;
            }

            this.spec = this.builder.build();
        }

        return this.spec;
    }

    /**
     * @return builder for type of this entry
     */
    ForgeConfigSpec.Builder getBuilder() {

        if (this.builder == null) {

            this.builder = new ForgeConfigSpec.Builder();
        }

        return this.builder;
    }

    /**
     * @param prefix path for this config
     */
    void setPrefix(String prefix) {

        this.path = prefix;
    }

    /**
     * get file name for config
     * @param context context for supplying modid
     * @return full config name with path as prefix
     */
    String getName(ModLoadingContext context) {

        String modId = context.getActiveContainer().getModId();
        return this.path + ConfigManager.getConfigName(modId, this.type);
    }

}
