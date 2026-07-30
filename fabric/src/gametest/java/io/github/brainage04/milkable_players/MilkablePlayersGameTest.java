package io.github.brainage04.milkable_players;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public final class MilkablePlayersGameTest {
	private static int fakePlayerPairCounter;

	@GameTest
	public void bucketInteractionProducesNamedMilkButOtherItemsAreUntouched(GameTestHelper context) {
		MilkablePlayersServerGameTestSuite.bucketInteractionProducesNamedMilkButOtherItemsAreUntouched(context);
	}

	@GameTest
	public void carpetFakePlayerConnectionProcessesEntityInteraction(GameTestHelper context) {
		int pairId = ++fakePlayerPairCounter;
		String milkerName = "mpMilker" + pairId;
		String targetName = "mpTarget" + pairId;
		Vec3 milkerPosition = context.absoluteVec(new Vec3(1.5, 1.0, 1.5));
		Vec3 targetPosition = context.absoluteVec(new Vec3(1.5, 1.0, 3.5));

		MilkablePlayersFakePlayerScenario.spawn(context.getLevel(), milkerName, milkerPosition);
		MilkablePlayersFakePlayerScenario.spawn(context.getLevel(), targetName, targetPosition);
		context.runBeforeTestEnd(() -> {
			MilkablePlayersFakePlayerScenario.kill(context.getLevel(), milkerName);
			MilkablePlayersFakePlayerScenario.kill(context.getLevel(), targetName);
		});

		context.startSequence()
				.thenWaitUntil(() -> {
					context.assertTrue(
							MilkablePlayersFakePlayerScenario.isReady(context.getLevel(), milkerName),
							"Waiting for Carpet fake player " + milkerName
					);
					context.assertTrue(
							MilkablePlayersFakePlayerScenario.isReady(context.getLevel(), targetName),
							"Waiting for Carpet fake player " + targetName
					);
				})
				.thenExecute(() -> {
					ServerPlayer milker = MilkablePlayersFakePlayerScenario.requirePlayer(context.getLevel(), milkerName);
					ServerPlayer target = MilkablePlayersFakePlayerScenario.requirePlayer(context.getLevel(), targetName);
					MilkablePlayersFakePlayerScenario.interactWithBucket(milker, target);
				})
				.thenIdle(1)
				.thenExecute(() -> {
					ServerPlayer milker = MilkablePlayersFakePlayerScenario.requirePlayer(context.getLevel(), milkerName);
					ServerPlayer target = MilkablePlayersFakePlayerScenario.requirePlayer(context.getLevel(), targetName);
					MilkablePlayersFakePlayerScenario.assertNamedMilk(milker, target);
				})
				.thenSucceed();
	}
}
