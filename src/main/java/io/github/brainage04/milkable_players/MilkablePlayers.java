package io.github.brainage04.milkable_players;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MilkablePlayers implements ModInitializer {
	public static final String MOD_ID = "milkable_players";
	public static final String MOD_NAME = "Milkable Players";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("{} initialising...", MOD_NAME);

		LOGGER.info("{} initialised.", MOD_NAME);
	}
}
