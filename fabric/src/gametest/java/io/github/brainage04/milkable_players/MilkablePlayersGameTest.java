package io.github.brainage04.milkable_players;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

public final class MilkablePlayersGameTest {
	@GameTest
	public void bucketInteractionProducesNamedMilkButOtherItemsAreUntouched(GameTestHelper context) {
		MilkablePlayersServerGameTestSuite.bucketInteractionProducesNamedMilkButOtherItemsAreUntouched(context);
	}
}
