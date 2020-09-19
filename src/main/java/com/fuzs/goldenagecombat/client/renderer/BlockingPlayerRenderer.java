package com.fuzs.goldenagecombat.client.renderer;

import com.fuzs.goldenagecombat.client.renderer.entity.layers.SwordBlockingLayer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class BlockingPlayerRenderer {

    public BlockingPlayerRenderer() {

        this.replaceHeldItemLayer();
    }

    private void replaceHeldItemLayer() {

        Map<String, PlayerRenderer> skinMap = Minecraft.getInstance().getRenderManager().getSkinMap();
        for (PlayerRenderer renderer : skinMap.values()) {

            renderer.layerRenderers.removeIf(it -> it instanceof HeldItemLayer);
            renderer.addLayer(new SwordBlockingLayer(renderer));
        }
    }

    @SuppressWarnings("deprecation")
    public static void applyTransformReverse(net.minecraft.client.renderer.model.ItemTransformVec3f vec, boolean leftHand, MatrixStack matrixStackIn) {

        if (vec != net.minecraft.client.renderer.model.ItemTransformVec3f.DEFAULT) {

            float x = vec.rotation.getX();
            float y = leftHand ? -vec.rotation.getY() : vec.rotation.getY();
            float z = leftHand ? -vec.rotation.getZ() : vec.rotation.getZ();
            Quaternion quat = new Quaternion(x, y, z, true);
            quat.conjugate();

            matrixStackIn.scale(1.0F / vec.scale.getX(), 1.0F / vec.scale.getY(), 1.0F / vec.scale.getZ());
            matrixStackIn.rotate(quat);
            matrixStackIn.translate((leftHand ? -1.0F : 1.0F) * -vec.translation.getX(), -vec.translation.getY(), -vec.translation.getZ());
        }
    }

}