var ASM_API = Java.type('net.minecraftforge.coremod.api.ASMAPI');
var Opcodes = Java.type('org.objectweb.asm.Opcodes');
var InsnList = Java.type('org.objectweb.asm.tree.InsnList');
var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');
var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
var TypeInsnNode = Java.type('org.objectweb.asm.tree.TypeInsnNode');
var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
var LdcInsnNode = Java.type('org.objectweb.asm.tree.LdcInsnNode');
var InvokeDynamicInsnNode = Java.type('org.objectweb.asm.tree.InvokeDynamicInsnNode');
var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
var FrameNode = Java.type('org.objectweb.asm.tree.FrameNode');
var LineNumberNode = Java.type('org.objectweb.asm.tree.LineNumberNode');

function initializeCoreMod() {

    return {

        // more damage from sharpness
        // make item projectiles cause knockback + damage animation and sound
        'player_entity_patch': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.entity.player.PlayerEntity'
            },
            'transformer': function(classNode) {
                patchMethod([{
                    obfName: "func_71059_n",
                    name: "attackTargetEntityWithCurrentItem",
                    desc: "(Lnet/minecraft/entity/Entity;)V",
                    patches: [patchPlayerEntityAttackTargetEntityWithCurrentItem1, patchPlayerEntityAttackTargetEntityWithCurrentItem2]
                }, {
                    obfName: "func_70097_a",
                    name: "attackEntityFrom",
                    desc: "(Lnet/minecraft/util/DamageSource;F)Z",
                    patches: [patchPlayerEntityAttackEntityFrom]
                }], classNode, "PlayerEntity");
                return classNode;
            }
        },

        // armor layer shows damage
        'armor_layer_patch': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.client.renderer.entity.layers.ArmorLayer'
            },
            'transformer': function(classNode) {
                patchMethod([{
                    obfName: "func_229129_a_",
                    name: "renderArmorPart",
                    desc: "(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;Lnet/minecraft/entity/LivingEntity;FFFFFFLnet/minecraft/inventory/EquipmentSlotType;I)V",
                    patches: [patchArmorLayerRenderArmorPart]
                }], classNode, "ArmorLayer", true);
                return classNode;
            }
        },

        // old fishing bobber behaviour
        'fishing_bobber_entity_patch': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.entity.projectile.FishingBobberEntity'
            },
            'transformer': function(classNode) {
                patchMethod([{
                    obfName: "func_190624_r",
                    name: "checkCollision",
                    desc: "()V",
                    patches: [patchFishingBobberEntityCheckCollision]
                }, {
                    obfName: "func_184527_k",
                    name: "bringInHookedEntity",
                    desc: "()V",
                    patches: [patchFishingBobberEntityBringInHookedEntity]
                }], classNode, "FishingBobberEntity");
                return classNode;
            }
        },

        // custom arm position
        'biped_model_patch': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.client.renderer.entity.model.BipedModel'
            },
            'transformer': function(classNode) {
                patchMethod([{
                    obfName: "func_225597_a_",
                    name: "setRotationAngles",
                    desc: "(Lnet/minecraft/entity/LivingEntity;FFFFF)V",
                    patches: [patchBipedModelSetRotationAngles]
                }], classNode, "BipedModel");
                return classNode;
            }
        }
    };
}

function patchMethod(entries, classNode, name, complete) {

    log("Patching " + name + "...");
    for (var i = 0; i < entries.length; i++) {

        var entry = entries[i];
        var method = findMethod(classNode.methods, entry);
        var flag = !!method;
        if (flag) {

            var obfuscated = !method.name.equals(entry.name);
            for (var j = 0; j < entry.patches.length; j++) {

                var patch = entry.patches[j];
                if (!patchInstructions(method, patch.filter, patch.action, obfuscated, complete)) {

                    flag = false;
                }
            }
        }

        log("Patching " + name + "#" + entry.name + (flag ? " was successful" : " failed"));
    }
}

function findMethod(methods, entry) {

    for (var i = 0; i < methods.length; i++) {

        var method = methods[i];
        if ((method.name.equals(entry.obfName) || method.name.equals(entry.name)) && method.desc.equals(entry.desc)) {

            return method;
        }
    }
}

function patchInstructions(method, filter, action, obfuscated, complete) {

    var nodes = [];
    var instructions = method.instructions.toArray();
    for (var i = 0; i < instructions.length; i++) {

        var node = filter(instructions[i], obfuscated);
        if (!!node) {

            nodes.push(node);
            if (!complete) {

                break;
            }
        }
    }

    for (var j = 0; j < nodes.length; j++) {

        action(nodes[j], method.instructions, obfuscated);
    }

    if (j > 0) {

        return true;
    }
}

var patchBipedModelSetRotationAngles = {
    filter: function(node, obfuscated) {
        if (node instanceof VarInsnNode && node.getOpcode().equals(Opcodes.ALOAD) && node.var.equals(0)) {
            var nextNode = node.getNext();
            if (matchesField(nextNode, "net/minecraft/client/renderer/entity/model/BipedModel", obfuscated ? "field_217112_c" : "swingProgress", "F")) {
                nextNode = nextNode.getNext();
                if (nextNode instanceof InsnNode && nextNode.getOpcode().equals(Opcodes.FCONST_0)) {
                    nextNode = nextNode.getNext();
                    if (nextNode instanceof InsnNode && nextNode.getOpcode().equals(Opcodes.FCMPL)) {
                        return node.getPrevious();
                    }
                }
            }
        }
    },
    action: function(node, instructions, obfuscated) {
        var insnList = new InsnList();
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 1));
        insnList.add(generateHook("applyRotations", "(Lnet/minecraft/client/renderer/entity/model/BipedModel;Lnet/minecraft/entity/LivingEntity;)V"));
        instructions.insert(node, insnList);
    }
};

var patchArmorLayerRenderArmorPart = {
    filter: function(node, obfuscated) {
        if (matchesMethod(node, "net/minecraft/client/renderer/entity/layers/ArmorLayer", obfuscated ? "renderArmor" : "renderArmor", "(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;IZLnet/minecraft/client/renderer/entity/model/BipedModel;FFFLnet/minecraft/util/ResourceLocation;)V")) {
            return node;
        }

    },
    action: function(node, instructions, obfuscated) {
        var insnList = new InsnList();
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 3));
        insnList.add(generateHook("renderArmor", "(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;IZLnet/minecraft/client/renderer/entity/model/BipedModel;FFFLnet/minecraft/util/ResourceLocation;Lnet/minecraft/entity/LivingEntity;)V"));
        insnList.add(new InsnNode(Opcodes.POP));
        instructions.insert(node, insnList);
        instructions.remove(node);
    }
};

var patchPlayerEntityAttackTargetEntityWithCurrentItem2 = {
    filter: function(node, obfuscated) {
        if (node instanceof JumpInsnNode && node.getOpcode().equals(Opcodes.IFEQ)) {
            var nextNode = node.getNext();
            if (nextNode instanceof VarInsnNode && nextNode.getOpcode().equals(Opcodes.ALOAD) && nextNode.var.equals(0)) {
                nextNode = nextNode.getNext();
                if (matchesMethod(nextNode, "net/minecraft/entity/player/PlayerEntity", obfuscated ? "func_70051_ag" : "isSprinting", "()Z")) {
                    return nextNode;
                }
            }
        }
    },
    action: function(node, instructions, obfuscated) {
        var insnList = new InsnList();
        insnList.add(generateHook("allowCriticalSprinting", "(Z)Z"));
        instructions.insert(node, insnList);
    }
};

function matchesMethod(node, owner, name, desc) {

    return node instanceof MethodInsnNode && matchesNode(node, owner, name, desc);
}

function matchesField(node, owner, name, desc) {

    return node instanceof FieldInsnNode && matchesNode(node, owner, name, desc);
}

function matchesNode(node, owner, name, desc) {

    return node.owner.equals(owner) && node.name.equals(name) && node.desc.equals(desc);
}

function generateHook(name, desc) {

    return new MethodInsnNode(Opcodes.INVOKESTATIC, "com/fuzs/goldenagecombat/asm/Hooks", name, desc, false);
}

function getNthNode(node, n) {

    for (var i = 0; i < Math.abs(n); i++) {

        if (n < 0) {

            node = node.getPrevious();
        } else {

            node = node.getNext();
        }
    }

    return node;
}

function log(message) {

    print("[Golden Age Combat Transformer]: " + message);
}