package io.github.brainage04.milkable_players;

import net.fabricmc.api.ModInitializer;

import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MilkablePlayers implements ModInitializer {
	public static final String MOD_ID = "milkable_players";
	public static final String MOD_NAME = "Milkable Players";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("%s initialising...".formatted(MOD_NAME));

		LOGGER.info("%s initialised.".formatted(MOD_NAME));
	}
}