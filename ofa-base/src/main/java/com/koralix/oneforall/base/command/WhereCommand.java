package com.koralix.oneforall.base.command;

import com.koralix.oneforall.base.settings.CommandSettings;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class WhereCommand {
    private static final String[] DIM_RAW = new String[] { "overworld", "the_nether", "the_end" };
    public static final Text[] DIMENSIONS = new Text[] {
            Text.literal("Overworld").setStyle(Style.EMPTY.withColor(Formatting.DARK_GREEN)),
            Text.literal("Nether").setStyle(Style.EMPTY.withColor(Formatting.DARK_RED)),
            Text.literal("End").setStyle(Style.EMPTY.withColor(Formatting.DARK_PURPLE))
    };

    public static void register(
            @NotNull CommandDispatcher<ServerCommandSource> dispatcher,
            @NotNull CommandRegistryAccess registryAccess,
            @NotNull CommandManager.RegistrationEnvironment environment
    ) {
        LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = literal("where")
                .requires(source -> CommandSettings.COMMAND_WHERE.get())
                .then(argument("player", EntityArgumentType.player())
                        .executes(context -> locate(context.getSource(), EntityArgumentType.getPlayer(context, "player")))
                );

        dispatcher.register(literalArgumentBuilder);
    }

    private static int locate(@NotNull ServerCommandSource source, @NotNull ServerPlayerEntity player) {
        BlockPos coords = player.getBlockPos();

        MutableText playerText = (MutableText) player.getDisplayName();
        playerText
                .setStyle(playerText.getStyle().withClickEvent(new ClickEvent.SuggestCommand("/tell " + player.getGameProfile().getName())))
                .setStyle(playerText.getStyle().withColor(Formatting.YELLOW));

        String dimStr = player.getWorld().getRegistryKey().getValue().getPath();
        Text dimensionText = DIMENSIONS[ArrayUtils.indexOf(DIM_RAW, dimStr)];
        MutableText coordsText = Text.literal(" [x:" + coords.getX() + ", y:" + coords.getY() + ", z:" + coords.getZ() + "]");
        coordsText.setStyle(coordsText.getStyle().withColor(Formatting.AQUA));

        MutableText finalText = Text.empty();

        finalText.append(playerText).append(" @ ").append(dimensionText).append(coordsText);
        if (dimStr.equals("overworld")) {
            BlockPos netherCoords = new BlockPos(coords.getX() >> 3, coords.getY(), coords.getZ() >> 3);
            Text netherCoordsText = Text.literal(" (" + netherCoords.getX() + ", " + netherCoords.getY() + ", " + netherCoords.getZ() + ")");
            finalText.append(" -> ").append(DIMENSIONS[1]).append(netherCoordsText);
        }

        source.sendFeedback(() -> finalText, false);
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 15 * 20, 0, false, false, false));

        return 1;
    }
}
