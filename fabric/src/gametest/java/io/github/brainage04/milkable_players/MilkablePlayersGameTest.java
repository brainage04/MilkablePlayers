package io.github.brainage04.milkable_players;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerLoadedPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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

		spawnFakePlayer(context, milkerName, milkerPosition);
		spawnFakePlayer(context, targetName, targetPosition);
		context.runBeforeTestEnd(() -> {
			killFakePlayer(context, milkerName);
			killFakePlayer(context, targetName);
		});

		context.startSequence()
				.thenWaitUntil(() -> {
					assertCarpetFakePlayer(context, milkerName);
					assertCarpetFakePlayer(context, targetName);
				})
				.thenExecute(() -> {
					ServerPlayer milker = fakePlayer(context, milkerName);
					ServerPlayer target = fakePlayer(context, targetName);
					milker.connection.handleAcceptPlayerLoad(new ServerboundPlayerLoadedPacket());
					milker.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BUCKET));
					Vec3 relativeHitPosition = target.getBoundingBox().getCenter().subtract(target.position());
					milker.connection.handleInteract(new ServerboundInteractPacket(
							target.getId(),
							InteractionHand.MAIN_HAND,
							relativeHitPosition,
							false
					));
				})
				.thenIdle(1)
				.thenExecute(() -> {
					ServerPlayer milker = fakePlayer(context, milkerName);
					ServerPlayer target = fakePlayer(context, targetName);
					ItemStack milk = milker.getItemInHand(InteractionHand.MAIN_HAND);
					context.assertTrue(milk.is(Items.MILK_BUCKET),
							"Expected Carpet's fake-player connection to process the entity interaction packet");
					context.assertValueEqual(
							milk.get(DataComponents.ITEM_NAME),
							Component.literal("%s's Milk".formatted(target.getScoreboardName())),
							"Expected the real interaction path to name the milk for the fake target"
					);
				})
				.thenSucceed();
	}

	private static void spawnFakePlayer(GameTestHelper context, String name, Vec3 position) {
		executeCarpetCommand(context, position, "player %s spawn in survival".formatted(name));
	}

	private static void killFakePlayer(GameTestHelper context, String name) {
		if (fakePlayer(context, name) != null) {
			executeCarpetCommand(context, Vec3.ZERO, "player %s kill".formatted(name));
		}
	}

	private static void assertCarpetFakePlayer(GameTestHelper context, String name) {
		ServerPlayer player = fakePlayer(context, name);
		context.assertTrue(player != null, "Waiting for Carpet fake player " + name);
		context.assertValueEqual(
				player.getClass().getName(),
				"carpet.patches.EntityPlayerMPFake",
				"Expected /player spawn to create Carpet's fake-player implementation"
		);
	}

	private static ServerPlayer fakePlayer(GameTestHelper context, String name) {
		return context.getLevel().getServer().getPlayerList().getPlayerByName(name);
	}

	private static void executeCarpetCommand(GameTestHelper context, Vec3 position, String command) {
		CommandSourceStack source = context.getLevel().getServer().createCommandSourceStack()
				.withLevel(context.getLevel())
				.withPosition(position)
				.withPermission(PermissionSet.ALL_PERMISSIONS);
		context.getLevel().getServer().getCommands().performPrefixedCommand(source, command);
	}
}
