package io.github.brainage04.milkable_players.neoforge;

import io.github.brainage04.milkable_players.MilkablePlayers;
import io.github.brainage04.milkable_players.MilkablePlayersServerGameTestSuite;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

/** Registers the shared production tests with NeoForge's test registry. */
@EventBusSubscriber(modid = MilkablePlayers.MOD_ID)
public final class NeoForgeServerGameTests {
	private NeoForgeServerGameTests() {
	}

	@SubscribeEvent
	public static void registerTestFunctions(RegisterEvent event) {
		for (MilkablePlayersServerGameTestSuite.TestCase test : MilkablePlayersServerGameTestSuite.tests()) {
			Identifier id = Identifier.fromNamespaceAndPath(MilkablePlayers.MOD_ID, test.path());
			event.register(BuiltInRegistries.TEST_FUNCTION.key(), id, test::function);
		}
	}
}
