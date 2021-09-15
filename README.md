# Suggestion Provider Fix [![](http://cf.way2muchnoise.eu/versions/suggestion-provider-fix.svg) ![](http://cf.way2muchnoise.eu/full_suggestion-provider-fix_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/suggestion-provider-fix/)
A minor change to Minecraft's suggestion provider so that it auto-completes resource locations for all mod namespaces. 

Whilst this is a very tiny change, if you use commands a lot in modded it is likely to save you a lot of time.

Note that this mod does not need to be installed on a dedicated server to work, though doing so is advisable so that the client and server mod list match. 

## Example
For example, let's say you want to use `/give` to spawn yourself an `oak_seed` from Dynamic Trees. With vanilla, you may begin to type `/give @p oak_seed` and no auto-complete suggestions will come up, as it expects you to type in `dynamictrees:oak_seed` since that is the full name for the item:

![](without.gif)

With this mod installed, you are able to start typing `oak_seed` and any items with that name (regardless of their namespace/mod ID) will be suggested:

![](with.gif)

This also works with other registries that use "resource locations", including blocks, biomes, and even custom registries from other mods. 

## Technical Description
This mod uses a coremod to remove instructions from `SharedSuggestionProvider#filterResources(Iterable, String, Function, Consumer)` which check the `namespace` of the `ResourceLocation` is `minecraft` (since this is the default when a path is typed without a namespace). This means that suggestion providers for `ResourceLocation` objects will suggest all paths matching the input string if no namespace was entered, instead of only from Minecraft itself.

## Gradle Setup
This is likely to be a useful tweak in the dev environment, so I've added it to my maven repository. First, add my maven repository to the `repositories` block:

#### Groovy `build.gradle`
```groovy
maven {
    url "https://harleyoconnor.com/maven"
}
```

#### Kotlin `build.gradle.kts`
```kotlin
maven("https://harleyoconnor.com/maven")
```

Next, add it as a runtime dependency in the `dependecies` block:

#### Groovy `build.gradle`
```groovy
runtimeOnly fg.deobf("com.harleyoconnor.suggestionproviderfix:SuggestionProviderFix:1.16.5-1.0.0")
```

#### Kotlin `build.gradle.kts`
```kotlin
runtimeOnly(fg.deobf("com.harleyoconnor.suggestionproviderfix:SuggestionProviderFix:1.16.5-1.0.0"))
```

Finally, you will need to add the following properties to each of your run tasks (under the `minecraft` -> `runs` -> `client` / `server` / `data` blocks):

#### Groovy `build.gradle`
```groovy
property 'mixin.env.remapRefMap', 'true'
property 'mixin.env.refMapRemappingFile', "${buildDir}/createSrgToMcp/output.srg"
```

#### Kotlin `build.gradle.kts`
```kotlin
property("mixin.env.remapRefMap", "true")
property("mixin.env.refMapRemappingFile", "${buildDir}/createSrgToMcp/output.srg")
```