package io.github.brainage04.milkable_players;

import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;

import java.util.List;
import java.util.function.Consumer;

/** Loader-neutral production GameTest definitions. */
public final class MilkablePlayersServerGameTestSuite {
	private MilkablePlayersServerGameTestSuite() {
	}

	public static List<TestCase> tests() {
		return List.of(new TestCase("bucket_interaction_produces_named_milk", MilkablePlayersServerGameTestSuite::bucketInteractionProducesNamedMilkButOtherItemsAreUntouched));
	}

	public static void bucketInteractionProducesNamedMilkButOtherItemsAreUntouched(GameTestHelper context) {
		ServerPlayer milker = (ServerPlayer) context.makeMockServerPlayer(GameType.SURVIVAL);
		ServerPlayer target = (ServerPlayer) context.makeMockServerPlayer(GameType.SURVIVAL);
		milker.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BUCKET));
		MilkablePlayers.milkPlayer(milker, target, InteractionHand.MAIN_HAND);
		ItemStack milk = milker.getItemInHand(InteractionHand.MAIN_HAND);
		assertItem(Items.MILK_BUCKET, milk, "Expected a bucket interaction with a player to produce milk");
		if (!Component.literal("%s's Milk".formatted(target.getScoreboardName())).equals(milk.get(DataComponents.ITEM_NAME))) {
			throw new AssertionError("Expected milk bucket to be named for the milked player");
		}
		milker.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.STICK));
		MilkablePlayers.milkPlayer(milker, target, InteractionHand.MAIN_HAND);
		assertItem(Items.STICK, milker.getItemInHand(InteractionHand.MAIN_HAND), "Expected a non-bucket interaction to leave the held item untouched");
		context.succeed();
	}

	private static void assertItem(net.minecraft.world.item.Item expected, ItemStack actual, String message) {
		if (!actual.is(expected)) throw new AssertionError(message + ": expected " + expected + ", found " + actual.getItem());
	}

	public record TestCase(String path, Consumer<GameTestHelper> function) {
	}
}
