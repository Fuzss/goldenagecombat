package com.fuzs.goldenagecombat.registry;

import com.fuzs.materialmaster.api.SyncProvider;
import com.fuzs.materialmaster.api.builder.AttributeMapBuilder;
import com.fuzs.materialmaster.api.provider.AbstractPropertyProvider;
import com.fuzs.goldenagecombat.GoldenAgeCombat;
import com.fuzs.goldenagecombat.config.CommonConfigHandler;
import com.google.common.collect.Multimap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unused")
@SyncProvider
public class GoldenAgePropertyProvider extends AbstractPropertyProvider {

    @Override
    public boolean isEnabled() {

        return CommonConfigHandler.COMBAT_DAMAGE_VALUES.get();
    }

    @Override
    public String getName() {

        return GoldenAgeCombat.MODID;
    }

    @Override
    public Map<Item, Multimap<String, AttributeModifier>> getAttributes() {

        return AttributeMapBuilder.create(this.getMainhandModifierId(), this.getArmorModifierIds())
                .putAttackDamage(Items.DIAMOND_SWORD, 1.0)
                .putAttackDamage(Items.DIAMOND_AXE, -2.0)
                .putAttackDamage(Items.DIAMOND_PICKAXE, 1.0)
                .putAttackDamage(Items.DIAMOND_SHOVEL, -0.5)
                .putAttackDamage(Items.DIAMOND_HOE, 3.0)
                .putAttackDamage(Items.IRON_SWORD, 1.0)
                .putAttackDamage(Items.IRON_AXE, -3.0)
                .putAttackDamage(Items.IRON_PICKAXE, 1.0)
                .putAttackDamage(Items.IRON_SHOVEL, -0.5)
                .putAttackDamage(Items.IRON_HOE, 2.0)
                .putAttackDamage(Items.STONE_SWORD, 1.0)
                .putAttackDamage(Items.STONE_AXE, -4.0)
                .putAttackDamage(Items.STONE_PICKAXE, 1.0)
                .putAttackDamage(Items.STONE_SHOVEL, -0.5)
                .putAttackDamage(Items.STONE_HOE, 1.0)
                .putAttackDamage(Items.WOODEN_SWORD, 1.0)
                .putAttackDamage(Items.WOODEN_AXE, -3.0)
                .putAttackDamage(Items.WOODEN_PICKAXE, 1.0)
                .putAttackDamage(Items.WOODEN_SHOVEL, -0.5)
                .putAttackDamage(Items.GOLDEN_SWORD, 1.0)
                .putAttackDamage(Items.GOLDEN_AXE, -3.0)
                .putAttackDamage(Items.GOLDEN_PICKAXE, 1.0)
                .putAttackDamage(Items.GOLDEN_SHOVEL, -0.5)
                .build();
    }

    @Override
    protected UUID getMainhandModifierId() {

        return UUID.fromString("C67E5959-C58C-4A7C-A51D-B9297E31F314");
    }

    @Override
    protected UUID[] getArmorModifierIds() {

        return new UUID[]{UUID.fromString("1D8FC628-9A5F-45BE-8346-C8B384CF38B9"), UUID.fromString("D66D5C7A-2958-4C84-9537-D8DF9E9F3B53"), UUID.fromString("6F5D1DFA-2B51-4F65-874E-F5E047E4930A"), UUID.fromString("05696464-7127-496D-A102-D63E1203AB4C")};
    }

}
