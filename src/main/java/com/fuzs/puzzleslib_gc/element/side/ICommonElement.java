package com.fuzs.puzzleslib_gc.element.side;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * implement this for elements with common capabilities
 */
public interface ICommonElement extends ISidedElement {

    /**
     * register common events and registry entry objects (blocks, items, etc.)
     */
    default void setupCommon() {

    }

    /**
     * setup for {@link net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent}
     * is always loaded no matter the element's state
     */
    default void initCommon() {

    }

    /**
     * load whenever the element's state changes to enabled
     * is not loaded when the element is disabled, changes are undone by {@link #unloadCommon()}
     */
    default void loadCommon() {

    }

    /**
     * reverse load whenever the element's state changes to disabled
     * should basically clean up changes made in {@link #loadCommon()}
     */
    default void unloadCommon() {

    }

    /**
     * build common config
     *
     * @param builder builder for common config
     */
    default void setupCommonConfig(ForgeConfigSpec.Builder builder) {

    }

    /**
     * @return description for this elements common config section
     */
    default String[] getCommonDescription() {

        return new String[0];
    }

}
