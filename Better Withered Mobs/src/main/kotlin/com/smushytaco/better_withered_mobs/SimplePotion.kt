package com.smushytaco.better_withered_mobs
import eu.pb4.polymer.core.api.other.SimplePolymerPotion
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.potion.Potion
import net.minecraft.potion.Potions
import net.minecraft.server.network.ServerPlayerEntity
class SimplePotion(baseName: String?, statusEffectInstance: StatusEffectInstance) : SimplePolymerPotion(baseName, statusEffectInstance) {
    override fun getPolymerReplacement(player: ServerPlayerEntity?): Potion {
        return Potions.WEAKNESS.value()
    }
}