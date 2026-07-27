# Milkable Players

Milkable Players lets players use an empty bucket on another player to receive a named milk bucket.

Available for Fabric and NeoForge. The mod is server-side, so vanilla clients can join a modded server.

## Migrating from the Fabric-only release

Install exactly one loader-specific JAR: the Fabric `milkable_players-<version>.jar` or NeoForge `milkable_players-neoforge-<version>.jar`. Remove the old JAR before switching loaders. Both variants retain the `milkable_players` mod ID and do not store mod-specific configuration or world data, so no data migration is required. Install the selected JAR on the server only; vanilla clients do not need it. Fabric requires Fabric Loader and Fabric API, while NeoForge requires NeoForge. A root `./gradlew build` creates both release artifacts in `build/libs`.
