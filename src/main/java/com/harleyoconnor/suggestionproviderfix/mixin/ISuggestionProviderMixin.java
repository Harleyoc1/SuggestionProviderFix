package com.harleyoconnor.suggestionproviderfix.mixin;

import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Provides {@link Mixin} for the {@link ISuggestionProvider} class.
 *
 * @author Harley O'Connor
 */
@Mixin(ISuggestionProvider.class)
public interface ISuggestionProviderMixin {

    /**
     * Overwrites {@link ISuggestionProvider#filterResources(Iterable, String, Function, Consumer)}.
     * This is an {@link Overwrite} since mixin doesn't seem to allow for other kinds of annotated
     * methods to be {@code public} and {@code static} in interfaces, and the {@code private} access
     * modifier isn't allowed in {@code interfaces}.
     *
     * <p>Difference in this method to the original is that it negates a check for the namespace
     * being <tt>minecraft</tt> in the second {@code if} statement, meaning that if a namespace is
     * not specified it filters to any paths matching the {@code input} {@link String}.</p>
     *
     * @author Harley O'Connor
     * @see ISuggestionProvider#filterResources(Iterable, String, Function, Consumer)
     */
    @Overwrite
    static <T> void filterResources(Iterable<T> resources, String input, Function<T, ResourceLocation> resourceMapper, Consumer<T> resourceConsumer) {
        final boolean hasColon = input.indexOf(58) > -1;

        for (final T resource : resources) {
            final ResourceLocation resourceLocation = resourceMapper.apply(resource);
            if (hasColon) {
                final String resLocStr = resourceLocation.toString();

                if (ISuggestionProvider.matchesSubStr(input, resLocStr)) {
                    resourceConsumer.accept(resource);
                }
            } else if (ISuggestionProvider.matchesSubStr(input, resourceLocation.getNamespace()) || ISuggestionProvider.matchesSubStr(input, resourceLocation.getPath())) {
                resourceConsumer.accept(resource);
            }
        }

    }

}
