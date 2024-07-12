package com.smushytaco.better_withered_mobs
import com.google.common.collect.ImmutableList
import com.smushytaco.better_withered_mobs.bone_meal.WitheredBoneMeal
import com.smushytaco.better_withered_mobs.bone_meal.WitheredBoneMealDispenserBehavior
import com.smushytaco.better_withered_mobs.configuration_support.ModConfiguration
import com.smushytaco.better_withered_mobs.mixins.ItemEntryItemAccessor
import com.smushytaco.better_withered_mobs.mixins.LootTablePoolsAccessor
import eu.pb4.factorytools.api.item.FactoryBlockItem
import eu.pb4.factorytools.api.item.ModeledItem
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils
import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.annotation.Config
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.loot.v2.LootTableEvents
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder
import net.minecraft.block.*
import net.minecraft.block.enums.NoteBlockInstrument
import net.minecraft.item.*
import net.minecraft.loot.LootPool
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.function.EnchantedCountIncreaseLootFunction
import net.minecraft.loot.function.SetCountLootFunction
import net.minecraft.loot.provider.number.ConstantLootNumberProvider
import net.minecraft.loot.provider.number.UniformLootNumberProvider
import net.minecraft.registry.BuiltinRegistries
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.text.Text
import net.minecraft.util.Identifier
object BetterWitheredMobs : ModInitializer {
    const val MOD_ID = "better_withered_mobs"
    private lateinit var config: ModConfiguration
    override fun onInitialize() {
        AutoConfig.register(ModConfiguration::class.java) { definition: Config, configClass: Class<ModConfiguration> ->
            GsonConfigSerializer(definition, configClass)
        }
        config = AutoConfig.getConfigHolder(ModConfiguration::class.java).config

        PotionOfDecay.registerPotions()
        FabricBrewingRecipeRegistryBuilder.BUILD.register { builder ->
            PotionOfDecay.createPotionRecipes(builder)
        }
        Registry.register(Registries.BLOCK, Identifier.of(MOD_ID, "withered_bone_block"), WITHERED_BONE_BLOCK)
        val witheredBoneBlockItem = Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "withered_bone_block"), FactoryBlockItem(WITHERED_BONE_BLOCK, Item.Settings().fireproof(), Items.COAL))
        PolymerItemGroupUtils.registerPolymerItemGroup(
            Identifier.of(MOD_ID, MOD_ID),
            ItemGroup.Builder(ItemGroup.Row.BOTTOM, -1)
                .displayName(Text.literal("Better Withered Mobs"))
                .icon { ItemStack(WITHERED_BONE_BLOCK) }
                .entries { _, entries ->
                    entries.add(witheredBoneBlockItem)
                    entries.add(WITHERED_BONE)
                    entries.add(WITHERED_BONE_MEAL)
                }.build())

        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "withered_bone"), WITHERED_BONE)
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "withered_bone_meal"), WITHERED_BONE_MEAL)
        DispenserBlock.registerBehavior(WITHERED_BONE_MEAL, WitheredBoneMealDispenserBehavior)
        LootTableEvents.MODIFY.register { registryKey, builder, _ ->
            builder as LootTablePoolsAccessor
            if (!registryKey.value.equals(Identifier.of("minecraft", "entities/wither_skeleton")) && !registryKey.value.equals(Identifier.of("betternether", "entities/naga")) && !registryKey.value.equals(Identifier.of("betternether", "entities/skull"))) return@register
            val lootFunctionOne = SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F))
            val lootFunctionTwo = EnchantedCountIncreaseLootFunction.builder(BuiltinRegistries.createWrapperLookup(), UniformLootNumberProvider.create(0.0F, 1.0F))
            val poolBuilder = LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).with(ItemEntry.builder(WITHERED_BONE).apply(lootFunctionOne)).apply(lootFunctionTwo)
            builder.pool(poolBuilder)
            if (!config.witheredMobsDontDropRegularBones) return@register
            val pools = builder.pools.build().toMutableList()
            pools.removeIf { lootPool ->
                lootPool.entries.any { lootPoolEntry ->
                    lootPoolEntry is ItemEntry && Registries.ITEM.getRawId((lootPoolEntry as ItemEntryItemAccessor).item.value()) == Registries.ITEM.getRawId(Items.BONE)
                }
            }
            builder.pools = ImmutableList.builder<LootPool>().addAll(pools)
        }
        PolymerResourcePackUtils.addModAssets(MOD_ID)
    }
    private val WITHERED_BONE_MEAL = WitheredBoneMeal(Item.Settings().fireproof())
    private val WITHERED_BONE = ModeledItem(Item.Settings().fireproof())
    private val WITHERED_BONE_BLOCK = SimpleBlock(AbstractBlock.Settings.create().mapColor(MapColor.BLACK).instrument(NoteBlockInstrument.XYLOPHONE).requiresTool().strength(2.0F).sounds(BlockSoundGroup.BONE), "block/withered_bone_block")
}