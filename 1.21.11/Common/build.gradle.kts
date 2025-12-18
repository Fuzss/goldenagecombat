plugins {
    id("fuzs.multiloader.multiloader-convention-plugins-common")
}

dependencies {
    modCompileOnlyApi(libs.puzzleslib.common)
}

multiloader {
    mixins {
        mixin("EntityMixin", "FishingHookMixin", "FoodDataMixin", "PlayerMixin", "ToolMaterialMixin")
        clientMixin("CameraMixin", "GuiMixin", "ItemInHandRendererMixin", "ItemStackMixin", "MinecraftMixin", "ParticleEngineMixin")
    }
}
