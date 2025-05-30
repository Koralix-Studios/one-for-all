package com.koralix.oneforall.session;

import com.koralix.oneforall.OneForAll;
import com.koralix.oneforall.lang.Language;
import com.koralix.oneforall.session.component.LangComponent;
import com.koralix.oneforall.session.component.VersionComponent;
import com.koralix.oneforall.settings.ServerSettings;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.common.ClientOptionsC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class LoginManager {
    public static void init() {
        ServerLoginConnectionEvents.QUERY_START.register(LoginManager::onQueryStart);
    }

    private static @NotNull PacketByteBuf createHelloPacket() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(OneForAll.MOD_VERSION);
        buf.writeBoolean(ServerSettings.ENFORCE_PROTOCOL.value());
        return buf;
    }

    private static void onQueryStart(
            ServerLoginNetworkHandler handler,
            MinecraftServer server,
            LoginPacketSender sender,
            ServerLoginNetworking.LoginSynchronizer synchronizer
    ) {
        if (!ServerSettings.PROTOCOL_ENABLED.value()) return;

        CompletableFuture<Void> future = new CompletableFuture<>();

        ServerLoginNetworking.registerReceiver(handler, OneForAll.id("hello"), (server1, handler1, understood, buf, synchronizer1, responseSender) -> {
            Optional<Text> error = onQueryResponse(server1, handler1, understood, buf, synchronizer1, responseSender);

            error.ifPresent(handler1::disconnect);

            future.complete(null);
        });

        sender.sendPacket(OneForAll.id("hello"), createHelloPacket());

        synchronizer.waitFor(future);
    }

    private static Optional<Text> onQueryResponse(MinecraftServer server, ServerLoginNetworkHandler handler, boolean understood, PacketByteBuf buf, ServerLoginNetworking.LoginSynchronizer synchronizer, PacketSender sender) {
        Session session = ((SessionHolder) handler).get();

        session.on(ClientOptionsC2SPacket.class, packet -> {
            Language language = Language.fromCode(packet.options().language());
            if (language == null) session.remove(LangComponent.TYPE);
            else session.set(new LangComponent(language));
        });

        if (!understood && ServerSettings.ENFORCE_PROTOCOL.value()) {
            return Optional.of(Text.translatable("text." + OneForAll.MOD_ID + ".disconnect.enforce_protocol"));
        } else if (!understood) {
            return Optional.empty();
        }

        Optional<String> version = readSafe(buf, PacketByteBuf::readString);
        Optional<String> language = readSafe(buf, PacketByteBuf::readString);

        if (version.isEmpty() || language.isEmpty()) {
            return Optional.of(Text.translatable("text." + OneForAll.MOD_ID + ".disconnect.invalid_hello"));
        }

        try {
            session.set(new VersionComponent(version.get()));

            Language lang = Language.fromCode(language.get());
            if (lang != null) session.set(new LangComponent(lang));
        } catch (Exception e) {
            return Optional.of(Text.translatable("text." + OneForAll.MOD_ID + ".disconnect.invalid_query"));
        }

        return Optional.empty();
    }

    private static <T> Optional<T> readSafe(PacketByteBuf buf, Function<PacketByteBuf, T> function) {
        try {
            return Optional.of(function.apply(buf));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
