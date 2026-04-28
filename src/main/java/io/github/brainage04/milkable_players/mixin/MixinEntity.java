package io.github.brainage04.milkable_players.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class MixinEntity {
    @Inject(
            at = @At("HEAD"),
            method = "interact"
    )
    private void interactInjected(Player player, InteractionHand hand, Vec3 hitPos, CallbackInfoReturnable<InteractionResult> cir) {
        Entity thisEntity = (Entity)(Object)this;

        if (thisEntity instanceof Player otherPlayer) {
            ItemStack itemStack = player.getItemInHand(hand);

            if (itemStack.getItem() == Items.BUCKET) {
                player.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);

                ItemStack stackToExchange = Items.MILK_BUCKET.getDefaultInstance();
                stackToExchange.applyComponents(DataComponentMap.builder()
                        .set(DataComponents.ITEM_NAME, Component.literal("%s's Milk".formatted(otherPlayer.getScoreboardName())))
                        .build());

                ItemStack itemStack2 = ItemUtils.createFilledResult(
                        itemStack,
                        player,
                        stackToExchange
                );

                player.setItemInHand(hand, itemStack2);
            }
        }
    }
}
