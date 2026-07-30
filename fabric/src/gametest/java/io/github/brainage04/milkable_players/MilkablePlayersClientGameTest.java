package io.github.brainage04.milkable_players;

import io.github.brainage04.fabricmoddingconventions.ClientGameTestRecorder;
import io.github.brainage04.fabricmoddingconventions.ClientGameTestServers;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestDedicatedServerContext;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public final class MilkablePlayersClientGameTest implements FabricClientGameTest {
	private static final int STAGE_Y = 64;
	private static final String MILKER_NAME = "mpCameraMilker";
	private static final String TARGET_NAME = "mpCameraTarget";
	private static final Vec3 MILKER_POSITION = new Vec3(0.5D, STAGE_Y, 1.5D);
	private static final Vec3 TARGET_POSITION = new Vec3(0.5D, STAGE_Y, -1.0D);

	@Override
	public void runTest(ClientGameTestContext context) {
		ClientGameTestServers.withDedicatedServer(context, "MilkablePlayers recording GameTest", server -> {
			UUID observerId = server.computeOnServer(minecraftServer ->
					minecraftServer.getPlayerList().getPlayers().getFirst().getUUID());
			server.runOnServer(minecraftServer -> prepareStage(requireObserver(minecraftServer.getPlayerList().getPlayer(observerId))));
			waitForActors(context, server, observerId);
			pointSpectatorCamera(context);

			try {
				context.waitTicks(20);
				ClientGameTestRecorder.startRecording(context);
				ClientGameTestRecorder.showStep(
						context,
						"milkableplayers.stage",
						"MilkablePlayers",
						"A spectator observes two independently controlled Carpet players"
				);
				context.waitTicks(35);

				ClientGameTestRecorder.showStep(
						context,
						"milkableplayers.interact",
						"Bucket interaction",
						MILKER_NAME + " uses an empty bucket on " + TARGET_NAME
				);
				server.runOnServer(minecraftServer -> {
					ServerLevel level = requireObserver(minecraftServer.getPlayerList().getPlayer(observerId)).level();
					ServerPlayer milker = MilkablePlayersFakePlayerScenario.requirePlayer(level, MILKER_NAME);
					ServerPlayer target = MilkablePlayersFakePlayerScenario.requirePlayer(level, TARGET_NAME);
					MilkablePlayersFakePlayerScenario.interactWithBucket(milker, target);
				});
				context.waitTicks(30);

				server.runOnServer(minecraftServer -> {
					ServerLevel level = requireObserver(minecraftServer.getPlayerList().getPlayer(observerId)).level();
					MilkablePlayersFakePlayerScenario.assertNamedMilk(
							MilkablePlayersFakePlayerScenario.requirePlayer(level, MILKER_NAME),
							MilkablePlayersFakePlayerScenario.requirePlayer(level, TARGET_NAME)
					);
				});
				ClientGameTestRecorder.showStep(
						context,
						"milkableplayers.complete",
						"Named milk bucket created",
						"The real interaction path produced “" + TARGET_NAME + "'s Milk”"
				);
				context.waitTicks(35);
			} finally {
				server.runOnServer(minecraftServer -> {
					ServerPlayer observer = minecraftServer.getPlayerList().getPlayer(observerId);
					if (observer != null) {
						MilkablePlayersFakePlayerScenario.kill(observer.level(), MILKER_NAME);
						MilkablePlayersFakePlayerScenario.kill(observer.level(), TARGET_NAME);
					}
				});
			}
		});
	}

	private static void prepareStage(ServerPlayer observer) {
		ServerLevel level = observer.level();
		for (BlockPos position : BlockPos.betweenClosed(-5, STAGE_Y - 1, -5, 5, STAGE_Y + 4, 5)) {
			level.setBlock(position, position.getY() == STAGE_Y - 1
					? Blocks.SMOOTH_STONE.defaultBlockState()
					: Blocks.AIR.defaultBlockState(), 3);
		}

		observer.setGameMode(GameType.SPECTATOR);
		observer.teleportTo(6.5D, STAGE_Y + 4.0D, 6.5D);
		observer.setDeltaMovement(Vec3.ZERO);
		MilkablePlayersFakePlayerScenario.spawn(level, MILKER_NAME, MILKER_POSITION);
		MilkablePlayersFakePlayerScenario.spawn(level, TARGET_NAME, TARGET_POSITION);
	}

	private static void waitForActors(
			ClientGameTestContext context,
			TestDedicatedServerContext server,
			UUID observerId
	) {
		for (int tick = 0; tick < 100; tick++) {
			boolean ready = server.computeOnServer(minecraftServer -> {
				ServerLevel level = requireObserver(minecraftServer.getPlayerList().getPlayer(observerId)).level();
				return MilkablePlayersFakePlayerScenario.isReady(level, MILKER_NAME)
						&& MilkablePlayersFakePlayerScenario.isReady(level, TARGET_NAME);
			});
			if (ready) {
				return;
			}
			context.waitTick();
		}
		throw new AssertionError("Timed out waiting for the MilkablePlayers Carpet actors.");
	}

	private static void pointSpectatorCamera(ClientGameTestContext context) {
		context.runOnClient(client -> {
			if (client.player == null) {
				throw new AssertionError("Expected a connected spectator for the recording.");
			}
			client.player.setYRot(135.0F);
			client.player.setXRot(18.0F);
		});
	}

	private static ServerPlayer requireObserver(ServerPlayer observer) {
		if (observer == null) {
			throw new AssertionError("Expected the recording spectator to remain connected.");
		}
		return observer;
	}
}
