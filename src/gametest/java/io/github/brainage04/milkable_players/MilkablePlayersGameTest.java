package io.github.brainage04.milkable_players;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;

public final class MilkablePlayersGameTest {
    @GameTest
    public void bucketInteractionProducesMilkButOtherItemsAreUntouched(GameTestHelper context) {
        ServerPlayer milker = makeSurvivalPlayer(context);
        ServerPlayer target = makeSurvivalPlayer(context);

        milker.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BUCKET));
        target.interact(milker, InteractionHand.MAIN_HAND, target.position());
        assertItem(Items.MILK_BUCKET, milker.getItemInHand(InteractionHand.MAIN_HAND), "Expected a bucket interaction with a player to produce milk");

        milker.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.STICK));
        target.interact(milker, InteractionHand.MAIN_HAND, target.position());
        assertItem(Items.STICK, milker.getItemInHand(InteractionHand.MAIN_HAND), "Expected a non-bucket interaction to leave the held item untouched");

        context.succeed();
    }

    private static ServerPlayer makeSurvivalPlayer(GameTestHelper context) {
        return (ServerPlayer) context.makeMockServerPlayer(GameType.SURVIVAL);
    }

    private static void assertItem(net.minecraft.world.item.Item expected, ItemStack actual, String message) {
        if (!actual.is(expected)) {
            throw new AssertionError(message + ": expected " + expected + ", found " + actual.getItem());
        }
    }
}
