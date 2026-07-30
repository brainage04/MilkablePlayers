package io.github.brainage04.milkable_players;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerLoadedPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

final class MilkablePlayersFakePlayerScenario {
	private static final String CARPET_FAKE_PLAYER_CLASS = "carpet.patches.EntityPlayerMPFake";

	private MilkablePlayersFakePlayerScenario() {
	}

	static void spawn(ServerLevel level, String name, Vec3 position) {
		executeCarpetCommand(level, position, "player %s spawn in survival".formatted(name));
	}

	static void kill(ServerLevel level, String name) {
		if (player(level, name) != null) {
			executeCarpetCommand(level, Vec3.ZERO, "player %s kill".formatted(name));
		}
	}

	static boolean isReady(ServerLevel level, String name) {
		ServerPlayer player = player(level, name);
		return player != null && CARPET_FAKE_PLAYER_CLASS.equals(player.getClass().getName());
	}

	static ServerPlayer requirePlayer(ServerLevel level, String name) {
		ServerPlayer player = player(level, name);
		if (player == null) {
			throw new AssertionError("Expected Carpet fake player " + name + ".");
		}
		if (!CARPET_FAKE_PLAYER_CLASS.equals(player.getClass().getName())) {
			throw new AssertionError("Expected " + name + " to use Carpet's fake-player implementation.");
		}
		return player;
	}

	static void interactWithBucket(ServerPlayer milker, ServerPlayer target) {
		milker.connection.handleAcceptPlayerLoad(new ServerboundPlayerLoadedPacket());
		milker.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BUCKET));
		Vec3 relativeHitPosition = target.getBoundingBox().getCenter().subtract(target.position());
		milker.connection.handleInteract(new ServerboundInteractPacket(
				target.getId(),
				InteractionHand.MAIN_HAND,
				relativeHitPosition,
				false
		));
		milker.swing(InteractionHand.MAIN_HAND, true);
	}

	static ItemStack assertNamedMilk(ServerPlayer milker, ServerPlayer target) {
		ItemStack milk = milker.getItemInHand(InteractionHand.MAIN_HAND);
		if (!milk.is(Items.MILK_BUCKET)) {
			throw new AssertionError("Expected the real interaction path to replace the bucket with milk.");
		}
		Component expectedName = Component.literal("%s's Milk".formatted(target.getScoreboardName()));
		if (!expectedName.equals(milk.get(DataComponents.ITEM_NAME))) {
			throw new AssertionError("Expected the milk bucket to be named for " + target.getScoreboardName() + ".");
		}
		return milk;
	}

	private static ServerPlayer player(ServerLevel level, String name) {
		return level.getServer().getPlayerList().getPlayerByName(name);
	}

	private static void executeCarpetCommand(ServerLevel level, Vec3 position, String command) {
		CommandSourceStack source = level.getServer().createCommandSourceStack()
				.withLevel(level)
				.withPosition(position)
				.withPermission(PermissionSet.ALL_PERMISSIONS);
		level.getServer().getCommands().performPrefixedCommand(source, command);
	}
}
