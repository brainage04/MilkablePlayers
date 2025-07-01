package io.github.brainage04.milkable_players.mixin;

import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class MixinEntity {
    @Inject(
            at = @At("HEAD"),
            method = "Lnet/minecraft/entity/Entity;interact(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;"
    )
    private void interactInjected(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        Entity thisEntity = (Entity)(Object)this;

        if (thisEntity instanceof PlayerEntity otherPlayer) {
            ItemStack itemStack = player.getStackInHand(hand);

            if (itemStack.isOf(Items.BUCKET)) {
                player.playSound(SoundEvents.ENTITY_COW_MILK, 1.0F, 1.0F);

                ItemStack stackToExchange = Items.MILK_BUCKET.getDefaultStack();
                stackToExchange.applyComponentsFrom(ComponentMap.builder()
                        .add(DataComponentTypes.ITEM_NAME, Text.literal("%s's Milk".formatted(otherPlayer.getNameForScoreboard())))
                        .build());

                ItemStack itemStack2 = ItemUsage.exchangeStack(
                        itemStack,
                        player,
                        stackToExchange
                );

                player.setStackInHand(hand, itemStack2);
            }
        }
    }
}
