package com.fuzs.puzzleslib_gc.element.side;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.ForgeConfigSpec;

/**
 * implement this for elements with client-side capabilities
 */
public interface IClientElement extends ISidedElement {

    /**
     * register client events
     */
    default void setupClient() {

    }

    /**
     * setup for {@link net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent}
     * is always loaded no matter the element's state
     */
    default void initClient() {

    }

    /**
     * load whenever the element's state changes to enabled
     * is not loaded when the element is disabled, changes are undone by {@link #unloadClient()}
     */
    default void loadClient() {

    }

    /**
     * reverse load whenever the element's state changes to disabled
     * should basically clean up changes made in {@link #loadClient()}
     */
    default void unloadClient() {

    }

    /**
     * build client config
     *
     * @param builder builder for client config
     */
    default void setupClientConfig(ForgeConfigSpec.Builder builder) {

    }

    /**
     * @return description for this elements client config section
     */
    default String[] getClientDescription() {

        return new String[0];
    }

    /**
     * @return Minecraft client instance
     */
    static Minecraft getMc() {

        return Minecraft.getInstance();
    }

}
