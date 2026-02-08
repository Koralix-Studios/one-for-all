package com.koralix.oneforall.base.mixin.statscore;

import com.koralix.oneforall.base.duck.OpenUserCache;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.authlib.GameProfile;
import net.minecraft.util.UserCache;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Mixin(UserCache.class)
@Implements(@Interface(iface = OpenUserCache.class, prefix = "open$"))
public abstract class UserCacheMixin implements OpenUserCache {
    @Unique
    private final List<GameProfile> open$entries = new ArrayList<>();

    @ModifyExpressionValue(
            method = "add(Lnet/minecraft/util/UserCache$Entry;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/UserCache$Entry;getProfile()Lcom/mojang/authlib/GameProfile;"
            )
    )
    private GameProfile addEntry(GameProfile original) {
        this.open$entries.add(original);
        return original;
    }

    @Unique
    public Stream<GameProfile> open$profiles() {
        return this.open$entries.stream();
    }
}
