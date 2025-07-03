package fuzs.goldenagecombat.handler;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ToolMaterials {
    private static final Map<ToolMaterialPropertiesKey, ToolMaterial> TOOL_MATERIALS = new ConcurrentHashMap<>();

    public static void registerToolMaterial(ToolMaterial toolMaterial) {
        TOOL_MATERIALS.put(ToolMaterialPropertiesKey.of(toolMaterial), toolMaterial);
    }

    @Nullable
    public static ToolMaterial getToolMaterial(DataComponentMap dataComponents) {
        ToolMaterialPropertiesKey key = ToolMaterialPropertiesKey.of(dataComponents);
        if (key != null) {
            return TOOL_MATERIALS.get(key);
        } else {
            return null;
        }
    }

    private record ToolMaterialPropertiesKey(int durability, int enchantmentValue, TagKey<Item> repairItems) {

        static ToolMaterialPropertiesKey of(ToolMaterial toolMaterial) {
            return new ToolMaterialPropertiesKey(toolMaterial.durability(),
                    toolMaterial.enchantmentValue(),
                    toolMaterial.repairItems());
        }

        @Nullable
        static ToolMaterialPropertiesKey of(DataComponentMap dataComponents) {
            if (dataComponents.has(DataComponents.MAX_DAMAGE) && dataComponents.has(DataComponents.ENCHANTABLE) &&
                    dataComponents.has(DataComponents.REPAIRABLE)) {
                Optional<TagKey<Item>> optional = dataComponents.get(DataComponents.REPAIRABLE).items().unwrapKey();
                if (optional.isPresent()) {
                    return new ToolMaterialPropertiesKey(dataComponents.get(DataComponents.MAX_DAMAGE).intValue(),
                            dataComponents.get(DataComponents.ENCHANTABLE).value(),
                            optional.get());
                }
            }

            return null;
        }
    }
}
