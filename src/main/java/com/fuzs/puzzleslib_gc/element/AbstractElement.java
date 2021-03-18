package com.fuzs.puzzleslib_gc.element;

import com.fuzs.puzzleslib_gc.PuzzlesLib;
import com.fuzs.puzzleslib_gc.config.ConfigManager;
import com.fuzs.puzzleslib_gc.element.side.IClientElement;
import com.fuzs.puzzleslib_gc.element.side.ICommonElement;
import com.fuzs.puzzleslib_gc.element.side.IServerElement;
import com.fuzs.puzzleslib_gc.element.side.ISidedElement;
import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.lifecycle.ModLifecycleEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * all features a mod adds are structured into elements which are then registered, this is an abstract version
 */
@SuppressWarnings("unused")
public abstract class AbstractElement extends EventListener implements IConfigurableElement {

    /**
     * all events registered by this element
     */
    private final List<EventStorage<? extends Event>> eventListeners = Lists.newArrayList();
    /**
     * is this element enabled (are events registered)
     * 1 and 0 for enable / disable, -1 for force disable where reloading the config doesn't have any effect
     */
    private int enabled = this.getDefaultState() ? 1 : 0;

    /**
     * @return name of this set in elements registry
     */
    protected String getRegistryName() {

        return ElementRegistry.getRegistryName(this).getPath();
    }

    @Override
    public final String getDisplayName() {

        return Stream.of(this.getRegistryName().split("_")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
    }

    @Override
    public boolean getDefaultState() {

        return true;
    }

    @Override
    public String[] isIncompatibleWith() {

        return new String[0];
    }

    /**
     * @return has an incompatible mod been found
     */
    protected final boolean isIncompatibilityPresent() {

        return Stream.of(this.isIncompatibleWith()).anyMatch(modId -> ModList.get().isLoaded(modId));
    }

    @Override
    public final void setupGeneralConfig(ForgeConfigSpec.Builder builder) {

        addToConfig(builder.comment(this.getDescription()).define(this.getDisplayName(), this.getDefaultState()), this::setEnabled);
    }

    /**
     * build element config and get event listeners
     */
    public final void setup() {

        this.setupConfig(this.getRegistryName());
        this.setupEvents();
    }

    /**
     * setup config for all sides
     * @param elementId id of this element for config section
     */
    private void setupConfig(String elementId) {

        Consumer<ICommonElement> commonConfig = element -> ConfigManager.builder().create(elementId, element::setupCommonConfig, ModConfig.Type.COMMON, element.getCommonDescription());
        Consumer<IClientElement> clientConfig = element -> ConfigManager.builder().create(elementId, element::setupClientConfig, ModConfig.Type.CLIENT, element.getClientDescription());
        Consumer<IServerElement> serverConfig = element -> ConfigManager.builder().create(elementId, element::setupServerConfig, ModConfig.Type.SERVER, element.getServerDescription());
        this.setupAllSides(commonConfig, clientConfig, serverConfig);
    }

    /**
     * setup events for all sides
     */
    private void setupEvents() {

        this.setupAllSides(ICommonElement::setupCommon, IClientElement::setupClient, IServerElement::setupServer);
    }

    /**
     * @param commonSetup consumer if implements {@link ICommonElement}
     * @param clientSetup consumer if implements {@link IClientElement}
     * @param serverSetup consumer if implements {@link IServerElement}
     */
    private void setupAllSides(Consumer<ICommonElement> commonSetup, Consumer<IClientElement> clientSetup, Consumer<IServerElement> serverSetup) {

        if (this instanceof ICommonElement) {

            commonSetup.accept(((ICommonElement) this));
        }

        if (FMLEnvironment.dist.isClient() && this instanceof IClientElement) {

            clientSetup.accept(((IClientElement) this));
        }

        if (FMLEnvironment.dist.isDedicatedServer() && this instanceof IServerElement) {

            serverSetup.accept(((IServerElement) this));
        }
    }

    /**
     * call sided load methods and register Forge events from internal storage
     * no need to check physical side as the setup event won't be called by Forge anyways
     * @param evt setup event this is called from
     */
    public final void load(ModLifecycleEvent evt) {

        // don't load anything if an incompatible mod is detected
        if (this.isIncompatibilityPresent()) {

            this.enabled = -1;
            return;
        }

        this.initSide(evt);
        if (this instanceof ICommonElement) {

            if (evt instanceof FMLCommonSetupEvent) {

                this.reload(true);
            }
        } else if (evt instanceof FMLClientSetupEvent || evt instanceof FMLDedicatedServerSetupEvent) {

            this.reload(true);
        }
    }

    /**
     * initialize sided content, this will always happen, even when the element is not loaded
     * @param evt setup event this is called from
     */
    private void initSide(ModLifecycleEvent evt) {

        if (evt instanceof FMLCommonSetupEvent && this instanceof ICommonElement) {

            ((ICommonElement) this).initCommon();
        } else if (evt instanceof FMLClientSetupEvent && this instanceof IClientElement) {

            ((IClientElement) this).initClient();
        } else if (evt instanceof FMLDedicatedServerSetupEvent && this instanceof IServerElement) {

            ((IServerElement) this).initServer();
        }
    }

    /**
     * update status of all reloadable components such as events and everything specified in sided load methods
     * @param firstLoad should unregistering not happen, as nothing has been loaded yet anyways
     */
    private void reload(boolean firstLoad) {

        if (this.isEnabled() || this.isAlwaysEnabled()) {

            this.reloadEventListeners(true);
            this.reloadSides(true);
        } else if (!firstLoad) {

            this.reloadEventListeners(false);
            this.reloadSides(false);
        }
    }

    /**
     * update status of all stored events
     * @param enable should events be loaded, otherwise they're unloaded
     */
    private void reloadEventListeners(boolean enable) {

        if (enable) {

            this.getEventListeners().forEach(EventStorage::register);
        } else {

            this.getEventListeners().forEach(EventStorage::unregister);
        }
    }

    /**
     * call proper load or unload methods depending on sided element type
     * @param enable should element contents be loaded, otherwise they're unloaded
     */
    private void reloadSides(boolean enable) {

        Consumer<ICommonElement> reloadCommon = element -> this.reloadSpecificSide(element, enable, ICommonElement::loadCommon, ICommonElement::unloadCommon);
        Consumer<IClientElement> reloadClient = element -> this.reloadSpecificSide(element, enable, IClientElement::loadClient, IClientElement::unloadClient);
        Consumer<IServerElement> reloadServer = element -> this.reloadSpecificSide(element, enable, IServerElement::loadServer, IServerElement::unloadServer);
        this.setupAllSides(reloadCommon, reloadClient, reloadServer);
    }

    /**
     * call proper load or unload method for given element type
     * @param element casted element for calling methods on
     * @param enable should element contents be loaded, otherwise they're unloaded
     * @param load element consumer for load
     * @param unload element consumer for unload
     * @param <T> type of this element
     */
    private <T extends ISidedElement> void reloadSpecificSide(T element, boolean enable, Consumer<T> load, Consumer<T> unload) {

        if (enable) {

            load.accept(element);
        } else {

            unload.accept(element);
        }
    }

    @Override
    public final boolean isEnabled() {

        return this.enabled == 1;
    }

    /**
     * are contents from this mod always active
     * @return is always enabled
     */
    public boolean isAlwaysEnabled() {

        return false;
    }

    /**
     * set {@link #enabled} state, reload when changed
     * @param enabled enabled
     */
    private void setEnabled(boolean enabled) {

        this.setEnabled(enabled ? 1 : 0);
    }

    /**
     * set {@link #enabled} state, reload when changed
     * @param enabled enabled as int
     */
    private void setEnabled(int enabled) {

        if (this.enabled != -1 && this.enabled != enabled) {

            this.enabled = enabled;
            this.reload(false);
        }
    }

    /**
     * something went wrong using this element, disable until game is restarted
     */
    protected void setDisabled() {

        this.setEnabled(-1);
        PuzzlesLib.LOGGER.warn("Detected issue in {} element: {}", this.getDisplayName(), "Disabling until game restart");
    }

    @Override
    public final List<EventStorage<? extends Event>> getEventListeners() {

        return this.eventListeners;
    }

}
