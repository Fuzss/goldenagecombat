package com.fuzs.puzzleslib_gc.config;

import com.fuzs.puzzleslib_gc.PuzzlesLib;
import com.fuzs.puzzleslib_gc.config.json.JsonConfigFileUtil;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

import javax.annotation.Nullable;
import java.io.File;
import java.util.EnumMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class ConfigBuilder {

    /**
     * enum map of config type entries for storing and managing builders, specs and file names for every type
     */
    private final EnumMap<ModConfig.Type, ConfigTypeData> configTypeEntries = Stream.of(ModConfig.Type.values())
            .collect(Collectors.toMap(Function.identity(), ConfigTypeData::new, (key1, key2) -> key1, () -> new EnumMap<>(ModConfig.Type.class)));

    /**
     * config type of category currently being built
     */
    private ModConfig.Type activeType;

    /**
     * add a spec when building config manually
     * @param spec spec to add
     * @param type type to add
     * @return was adding successful (spec not present yet)
     */
    public boolean addSpec(ModConfig.Type type, ForgeConfigSpec spec) {

        return this.configTypeEntries.get(type).addSpec(spec);
    }

    /**
     * get spec, build from builder if absent
     * @param type type to get spec for
     * @return config spec for type
     */
    @Nullable
    public ForgeConfigSpec getSpec(ModConfig.Type type) {

        return this.configTypeEntries.get(type).getSpec();
    }

    /**
     * has the spec for this type been built yet (has {@link ConfigTypeData#getSpec} been called)
     * @param type type to check
     * @return has spec not been built
     */
    public boolean isSpecNotBuilt(ModConfig.Type type) {

        return this.configTypeEntries.get(type).isSpecNotBuilt();
    }

    /**
     * has the spec for this type been built yet and has it been loaded by Forge
     * @param type type to check
     * @return has spec been built and loaded
     */
    @SuppressWarnings("ConstantConditions")
    public boolean isSpecNotValid(ModConfig.Type type) {

        return this.isSpecNotBuilt(type) || !this.configTypeEntries.get(type).getSpec().isLoaded();
    }

    /**
     * wrap creation of a new category
     * @param category name of new category
     * @param options builder for category
     * @param type config type this category is for
     * @param comments comments to add to category
     */
    public void create(String category, Consumer<ForgeConfigSpec.Builder> options, ModConfig.Type type, String... comments) {

        this.activeType = type;

        ForgeConfigSpec.Builder builder = this.configTypeEntries.get(type).getBuilder();
        if (comments.length != 0) {

            builder.comment(comments);
        }

        builder.push(category);
        options.accept(builder);
        builder.pop();

        this.activeType = null;
    }

    /**
     * register all configs from non-empty builders
     * @param context active mod container context
     */
    public void registerConfigs(ModLoadingContext context) {

        for (ModConfig.Type type : ModConfig.Type.values()) {

            ConfigTypeData typeEntry = this.configTypeEntries.get(type);
            if (typeEntry.canBuildSpec()) {

                context.registerConfig(type, typeEntry.getSpec(), typeEntry.getName(context));
            }
        }
    }

    /**
     * make sure folders have actually been created
     * @param folderName folder structure to place config files into
     */
    public void moveToFolder(String... folderName) {

        if (folderName.length > 0) {

            String prefix = String.join(File.separator, folderName);
            JsonConfigFileUtil.mkdirs(prefix);
            this.configTypeEntries.values().forEach(typeEntry -> typeEntry.setPrefix(prefix + File.separator));
        } else {

            PuzzlesLib.LOGGER.error("Unable to move config files to folder" + ":" + "Invalid path");
        }
    }

    /**
     * @return type of category currently being built
     */
    public ModConfig.Type getActiveType() {

        return this.activeType;
    }

}
