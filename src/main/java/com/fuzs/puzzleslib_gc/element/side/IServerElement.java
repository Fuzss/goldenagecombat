package com.fuzs.puzzleslib_gc.element.side;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * implement this for elements with server-side capabilities
 */
public interface IServerElement extends ISidedElement {

    /**
     * register server events
     */
    default void setupServer() {

    }

    /**
     * setup for {@link net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent}
     * is always loaded no matter the element's state
     */
    default void initServer() {

    }

    /**
     * load whenever the element's state changes to enabled
     * is not loaded when the element is disabled, changes are undone by {@link #unloadServer()}
     */
    default void loadServer() {

    }

    /**
     * reverse load whenever the element's state changes to disabled
     * should basically clean up changes made in {@link #loadServer()}
     */
    default void unloadServer() {

    }

    /**
     * build server config
     *
     * @param builder builder for server config
     */
    default void setupServerConfig(ForgeConfigSpec.Builder builder) {

    }

    /**
     * @return description for this elements server config section
     */
    default String[] getServerDescription() {

        return new String[0];
    }

}
