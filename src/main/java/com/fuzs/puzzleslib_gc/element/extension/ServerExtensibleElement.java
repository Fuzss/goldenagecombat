package com.fuzs.puzzleslib_gc.element.extension;

import com.fuzs.puzzleslib_gc.element.side.IServerElement;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.function.Function;

/**
 * an element that can be extended to the server
 * @param <T> extension class
 */
public abstract class ServerExtensibleElement<T extends ElementExtension<?> & IServerElement> extends ExtensibleElement<T> implements IServerElement {

    /**
     * @param extension provider for extension
     */
    public ServerExtensibleElement(Function<ExtensibleElement<?>, T> extension) {

        super(extension, Dist.DEDICATED_SERVER);
    }

    @Override
    public final void setupServer() {

        this.extension.setupServer();
    }

    @Override
    public final void initServer() {

        this.extension.initServer();
    }

    @Override
    public final void loadServer() {

        this.extension.loadServer();
    }

    @Override
    public final void unloadServer() {

        this.extension.unloadServer();
    }

    @Override
    public final void setupServerConfig(ForgeConfigSpec.Builder builder) {

        this.extension.setupServerConfig(builder);
    }

    @Override
    public final String[] getServerDescription() {

        return this.extension.getServerDescription();
    }

}
