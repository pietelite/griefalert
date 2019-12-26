package com.minecraftonline.griefalert.commands;

import com.minecraftonline.griefalert.GriefAlert;

import java.util.Optional;

import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.commands.AbstractCommand;
import com.minecraftonline.griefalert.util.Permissions;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.annotation.NonnullByDefault;

public class GriefAlertInfoCommand extends AbstractCommand {

  GriefAlertInfoCommand() {
    super(
        Permissions.GRIEFALERT_COMMAND_INFO,
        Text.of("Get info about a given grief alert")
    );
    addAlias("info");
    addAlias("i");
    addCommandElement(GenericArguments.onlyOne(GenericArguments.integer(Text.of("alert code"))));
  }

  @Override
  @NonnullByDefault
  public CommandResult execute(@NonnullByDefault CommandSource src,
                               @NonnullByDefault CommandContext args) throws CommandException {
    Player player = (Player) src;
    if (args.<Integer>getOne("alert code").isPresent()) {
      Alert alert = GriefAlert.getInstance().getAlertQueue().get(args.<Integer>getOne("alert code").get());
      player.sendMessage(alert.getMessageText());
    } else {
      player.sendMessage(Text.of(
          TextColors.RED,
          "The queried Grief Event code could not be parsed")
      );
    }
    return CommandResult.success();
  }
}