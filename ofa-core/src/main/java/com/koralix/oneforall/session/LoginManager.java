package com.koralix.oneforall.session;

import com.koralix.oneforall.CoreInit;
import com.koralix.oneforall.init.Initializer;
import com.koralix.oneforall.lang.Language;
import com.koralix.oneforall.session.component.LangComponent;
import com.koralix.oneforall.session.component.VersionComponent;
import com.koralix.oneforall.settings.ServerSettings;
import net.fabricmc.fabric.api.networking.v1.*;
import net.fabricmc.loader.api.SemanticVersion;
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

    private static @NotNull PacketByteBuf createHelloPacket(@NotNull MinecraftServer server) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(CoreInit.version().getFriendlyString());
        buf.writeBoolean(ServerSettings.ENFORCE_PROTOCOL.get());
        return buf;
    }

    private static void onQueryStart(
            ServerLoginNetworkHandler handler,
            MinecraftServer server,
            LoginPacketSender sender,
            ServerLoginNetworking.LoginSynchronizer synchronizer
    ) {
        if (!ServerSettings.PROTOCOL_ENABLED.get()) return;

        CompletableFuture<Void> future = new CompletableFuture<>();

        ServerLoginNetworking.registerReceiver(handler, CoreInit.id("hello"), (server1, handler1, understood, buf, synchronizer1, responseSender) -> {
            Optional<Text> error = onQueryResponse(server1, handler1, understood, buf, synchronizer1, responseSender);

            error.ifPresent(handler1::disconnect);

            future.complete(null);
        });

        sender.sendPacket(CoreInit.id("hello"), createHelloPacket(server));

        synchronizer.waitFor(future);
    }

    private static Optional<Text> onQueryResponse(MinecraftServer server, ServerLoginNetworkHandler handler, boolean understood, PacketByteBuf buf, ServerLoginNetworking.LoginSynchronizer synchronizer, PacketSender sender) {
        Session session = ((SessionHolder) handler).get();

        session.on(ClientOptionsC2SPacket.class, packet -> {
            if (session.has(LangComponent.TYPE)) return;

            Language language = Language.fromCode(packet.options().language());
            if (language == null) session.set(new LangComponent(Language.of(session)));
            else session.set(new LangComponent(language));
        });

        if (!understood && ServerSettings.ENFORCE_PROTOCOL.get()) {
            return Optional.of(Text.translatable("text." + Initializer.COMMON_ID + ".disconnect.enforce_protocol"));
        } else if (!understood) {
            return Optional.empty();
        }

        Optional<String> version = readSafe(buf, PacketByteBuf::readString);
        Optional<String> language = readSafe(buf, PacketByteBuf::readString);

        if (version.isEmpty() || language.isEmpty()) {
            return Optional.of(Text.translatable("text." + Initializer.COMMON_ID + ".disconnect.invalid_handshake"));
        }

        try {
            VersionComponent versionComponent = new VersionComponent(version.get());
            if (ServerSettings.ENFORCE_PROTOCOL.get() && !versionComponent.isCompatible()) {
                if (!(CoreInit.version() instanceof SemanticVersion semver)) {
                    return Optional.of(Text.translatable("text." + Initializer.COMMON_ID + ".disconnect.incompatible_version"));
                }
                int major = semver.getVersionComponent(0);
                int minor = semver.getVersionComponent(1);
                return Optional.of(Text.translatable(
                        "text." + Initializer.COMMON_ID + ".disconnect.incompatible_version.recommendation",
                        "%s.%s.*".formatted(major, minor),
                        "%s.*.*".formatted(major + 1)
                ));
            }
            session.set(versionComponent);

            Language lang = Language.fromCode(language.get());
            if (lang != null) session.set(new LangComponent(lang));
        } catch (Exception e) {
            return Optional.of(Text.translatable("text." + Initializer.COMMON_ID + ".disconnect.invalid_handshake"));
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
