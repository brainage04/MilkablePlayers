package io.github.brainage04.milkable_players.neoforge;

import io.github.brainage04.milkable_players.MilkablePlayers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/** NeoForge event adapter for the shared bucket interaction. */
@Mod(MilkablePlayers.MOD_ID)
public final class MilkablePlayersNeoForge {
	public MilkablePlayersNeoForge(IEventBus modEventBus) {
		NeoForge.EVENT_BUS.addListener(this::onEntityInteract);
		MilkablePlayers.LOGGER.info("{} initialized on NeoForge.", MilkablePlayers.MOD_NAME);
	}

	private void onEntityInteract(PlayerInteractEvent.EntityInteractSpecific event) {
		if (event.getLevel().isClientSide()) {
			return;
		}
		if (MilkablePlayers.milkPlayer(event.getEntity(), event.getTarget(), event.getHand()).consumesAction()) {
			event.setCancellationResult(net.minecraft.world.InteractionResult.SUCCESS);
			event.setCanceled(true);
		}
	}
}
