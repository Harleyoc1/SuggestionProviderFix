package com.harleyoconnor.suggestionproviderfix.mixin;

import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Harley O'Connor
 */
@Mixin(SharedSuggestionProvider.class)
public interface SharedSuggestionProviderMixin {

    /**
     * Overwrites {@link SharedSuggestionProvider#filterResources(Iterable, String, Function, Consumer)}. This is an
     * {@link Overwrite} since mixin doesn't currently allow injectors in interfaces.
     *
     * <p>Difference in this method to the original is that it negates a check for the namespace being {@code
     * minecraft} in the second {@code if} statement, meaning that if a namespace is not specified it filters to any
     * paths matching the {@code input} {@link String}.</p>
     *
     * @author Harley O'Connor
     * @see SharedSuggestionProvider#filterResources(Iterable, String, Function, Consumer)
     */
    @Overwrite
    static <T> void filterResources(Iterable<T> resources, String input, Function<T, ResourceLocation> locationFunction,
                                    Consumer<T> resourceConsumer) {
        final boolean inputContainsColon = input.indexOf(58) > -1;

        for (final T resource : resources) {
            final ResourceLocation resourceLocation = locationFunction.apply(resource);
            if (inputContainsColon) {
                final String resourceLocationString = resourceLocation.toString();

                if (SharedSuggestionProvider.matchesSubStr(input, resourceLocationString)) {
                    resourceConsumer.accept(resource);
                }
            } else if (SharedSuggestionProvider.matchesSubStr(input, resourceLocation.getNamespace()) ||
                    SharedSuggestionProvider.matchesSubStr(input, resourceLocation.getPath())) {
                resourceConsumer.accept(resource);
            }
        }

    }

}
