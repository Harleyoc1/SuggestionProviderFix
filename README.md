# Suggestion Provider Fix [![](http://cf.way2muchnoise.eu/versions/suggestion-provider-fix.svg) ![](http://cf.way2muchnoise.eu/full_suggestion-provider-fix_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/suggestion-provider-fix/)
A minor change to Minecraft's suggestion provider so that it auto-completes resource locations for all mod namespaces.

Whilst this is a very tiny change, if you use commands a lot in modded it is likely to save you a lot of time.

Note that this mod does not need to be installed on a dedicated server to work, though doing so is advisable so that the client and server mod list match.

## Links
- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/suggestion-provider-fix/)
- [Modrinth](https://modrinth.com/mod/suggestionproviderfix)

## Usage example
Let's say you want to use `/give` to spawn yourself an `oak_seed` from Dynamic Trees. With vanilla, you may begin to type `/give @p oak_seed`. However, no auto-complete suggestions come up, as it expects you to type  the fully qualified `dynamictrees:oak_seed` name:

![](without.gif)

When Suggestion Provider Fix is installed, beginning to type `oak_seed` will suggest any items with that name, regardless of which mod adds it. So, the oak seed from Dynamic Trees will be suggested:

![](with.gif)

This also works with other registries that use resource locations, including blocks, biomes, and even custom registries from other mods.

## Technical Description
This mod uses a Mixin to overwrite `SharedSuggestionProvider#filterResources(Iterable, String, Function, Consumer)`, doing exactly the same thing but negating a check on whether or not the namespace of the `ResourceLocation` is equal to `minecraft` (since this is the default when a path is typed without a namespace). This means that suggestion providers for `ResourceLocation` objects will suggest all paths matching the input string if no namespace was entered, instead of only those from Minecraft itself.

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