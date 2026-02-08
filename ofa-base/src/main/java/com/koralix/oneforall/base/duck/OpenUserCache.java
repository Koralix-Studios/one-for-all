package com.koralix.oneforall.base.duck;

import com.mojang.authlib.GameProfile;

import java.util.stream.Stream;

public interface OpenUserCache {
    Stream<GameProfile> profiles();
}
