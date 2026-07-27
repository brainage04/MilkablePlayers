package io.github.brainage04.milkable_players.fabric;

import io.github.brainage04.milkable_players.MilkablePlayers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;

/** Fabric event adapter for the shared bucket interaction. */
public final class MilkablePlayersFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> MilkablePlayers.milkPlayer(player, entity, hand));
		MilkablePlayers.LOGGER.info("{} initialized on Fabric.", MilkablePlayers.MOD_NAME);
	}
}
