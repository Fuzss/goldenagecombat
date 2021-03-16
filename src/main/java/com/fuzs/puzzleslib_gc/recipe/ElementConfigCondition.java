package com.fuzs.puzzleslib_gc.recipe;

import com.fuzs.puzzleslib_gc.PuzzlesLib;
import com.fuzs.puzzleslib_gc.element.ElementRegistry;
import com.google.gson.JsonObject;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

import java.util.Optional;

public class ElementConfigCondition implements ICondition {

    private final ResourceLocation element;
    private final String path;

    public ElementConfigCondition(ResourceLocation element, String path) {

        this.element = element;
        this.path = path;
    }

    @Override
    public ResourceLocation getID() {

        return new ResourceLocation(PuzzlesLib.MODID, "element_config");
    }

    @Override
    public boolean test() {

        Optional<Boolean> configValue = ElementRegistry.getConfigValue(this.element, this.path);
        return configValue.isPresent() && configValue.get();
    }

    public static class Serializer implements IConditionSerializer<ElementConfigCondition> {

        @Override
        public void write(JsonObject json, ElementConfigCondition value) {

            json.addProperty("element", value.element.toString());
            json.addProperty("path", value.path);
        }

        @Override
        public ElementConfigCondition read(JsonObject json) {

            ResourceLocation element = new ResourceLocation(JSONUtils.getString(json, "element"));
            String path = JSONUtils.getString(json, "path");

            return new ElementConfigCondition(element, path);
        }

        @Override
        public ResourceLocation getID() {

            return new ResourceLocation(PuzzlesLib.MODID, "element_config");
        }

    }

}
