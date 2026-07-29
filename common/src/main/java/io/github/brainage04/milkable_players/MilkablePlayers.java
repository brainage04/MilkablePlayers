package io.github.brainage04.milkable_players;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Shared bucket interaction behavior, registered by each loader's entrypoint. */
public final class MilkablePlayers {
	public static final String MOD_ID = "milkable_players";
	public static final String MOD_NAME = "Milkable Players";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private MilkablePlayers() {
	}

	public static InteractionResult milkPlayer(Player milker, Entity target, InteractionHand hand) {
		if (!(target instanceof Player milkedPlayer) || !milker.getItemInHand(hand).is(Items.BUCKET)) {
			return InteractionResult.PASS;
		}
		if (!milker.level().isClientSide()) {
			milker.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
			ItemStack milkBucket = Items.MILK_BUCKET.getDefaultInstance();
			milkBucket.applyComponents(DataComponentMap.builder()
					.set(DataComponents.ITEM_NAME, Component.literal("%s's Milk".formatted(milkedPlayer.getScoreboardName())))
					.build());
			milker.setItemInHand(hand, ItemUtils.createFilledResult(milker.getItemInHand(hand), milker, milkBucket));
		}
		return InteractionResult.SUCCESS;
	}
}
