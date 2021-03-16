package com.fuzs.puzzleslib_gc.util;

import com.google.common.base.CaseFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModLoadingContext;

/**
 * helper for namespace related things
 */
@SuppressWarnings("unused")
public interface INamespaceLocator {

    /**
     * get active modid so entries can still be associated with the mod
     * @return active modid
     */
    default String getActiveNamespace() {

        String namespace = ModLoadingContext.get().getActiveNamespace();
        if (namespace.equals("minecraft")) {

            throw new RuntimeException("Invalid active namespace");
        }

        return namespace;
    }

    /**
     * @param path path to create location for
     * @return resource location for active modid
     */
    default ResourceLocation getLocation(String path) {

        return new ResourceLocation(this.getActiveNamespace(), path);
    }

    /**
     * most useful for capabilities
     * @param name name to format
     * @return name formatted as upper camel
     */
    static String format(String name) {

        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name);
    }

}
