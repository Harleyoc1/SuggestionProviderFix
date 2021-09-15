package com.harleyoconnor.suggestionproviderfix;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

/**
 * @author Harley O'Connor
 */
@Mod(SuggestionProviderFix.MOD_ID)
public final class SuggestionProviderFix {

    public static final String MOD_ID = "suggestionproviderfix";

    public SuggestionProviderFix() {
        // If not in production, register a block for testing.
        if (!FMLEnvironment.production) {
            FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, this::registerBlock);
        }
    }

    public void registerBlock(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(new Block(BlockBehaviour.Properties.copy(Blocks.AIR)).setRegistryName(MOD_ID + ":air"));
    }

}
