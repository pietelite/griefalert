package com.minecraftonline.griefalert;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import javax.annotation.Nonnull;
import java.util.Optional;

import static org.spongepowered.api.text.format.TextColors.RED;
import static org.spongepowered.api.text.format.TextColors.WHITE;

public final class GriefAlertCommand implements CommandExecutor {
    private final AlertTracker tracker;

    public GriefAlertCommand(AlertTracker tracker) {
        this.tracker = tracker;
    }

    @Override
    @Nonnull
    public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) throws CommandException {
        if (src instanceof Player) {  // Check if a player is running the command
            Optional<Integer> arg = args.getOne("code");
            if (!arg.isPresent()) { // Missing player name
                throw new CommandException(Text.builder("ERROR: ").color(RED).append(Text.builder("Missing check number").color(WHITE).build()).build());
            }
            int code = arg.get();
            if (code > GriefAlert.readConfigInt("alertsCodeLimit") || code < 0) {
                throw new CommandException(Text.builder("GriefAlert ERROR: ").color(RED).append(Text.builder("Check number out of range").color(WHITE).build()).build());
            }
            Player checker = (Player) src;
            GriefAction action = tracker.get(arg.get());
            if (action == null) {
                throw new CommandException(Text.builder("GriefAlert ERROR: ").color(RED).append(Text.builder("There is no current alert at that code").color(WHITE).build()).build());
            }

            tracker.alertStaff(formatPlayerName(checker).toBuilder().append(
                    Text.builder(" is checking ").color(TextColors.YELLOW).build()).append(
                    Text.builder(Integer.toString(code)).color(TextColors.WHITE).build()).append(
                    Text.builder(" for grief.").color(TextColors.YELLOW).build()).build());
            GriefAction grief = tracker.get(code);
            checker.setLocationSafely(grief.griefer.getLocation().get());
            checker.setRotation(grief.rotation);
            return CommandResult.success();
        }
        throw new CommandException(Text.of("Only in game players can use this command!"));
    }

    private Text formatPlayerName(Player player) {
        return TextSerializers.FORMATTING_CODE.deserialize(player.getOption("prefix").orElse("") + player.getName() + player.getOption("suffix").orElse(""));
    }
}