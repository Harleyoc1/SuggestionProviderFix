package com.harleyoconnor.suggestionproviderfix;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.registries.RegisterEvent;

/**
 * @author Harley O'Connor
 */
@Mod(SuggestionProviderFix.MOD_ID)
public final class SuggestionProviderFix {

    public static final String MOD_ID = "suggestionproviderfix";

    public SuggestionProviderFix(IEventBus modBus, ModContainer modContainer) {
        // If not in production, register a block for testing.
        if (!FMLEnvironment.production) {
            modBus.addListener(this::registerBlock);
        }
    }

    public void registerBlock(RegisterEvent event) {
        event.register(Registries.BLOCK, helper ->
                helper.register(ResourceLocation.fromNamespaceAndPath(MOD_ID, "air"),
                        new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.AIR)))
        );
    }

}
