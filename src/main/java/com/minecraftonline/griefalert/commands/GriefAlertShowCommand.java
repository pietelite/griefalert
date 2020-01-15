package com.minecraftonline.griefalert.commands;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.commands.AbstractCommand;
import com.minecraftonline.griefalert.util.Errors;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.Permissions;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class GriefAlertShowCommand extends AbstractCommand {


  public GriefAlertShowCommand() {
    super(
        Permissions.GRIEFALERT_COMMAND_SHOW,
        Text.of("Create a Hologram at the location of grief"));
    addAlias("show");
    addAlias("s");
    setCommandElement(GenericArguments.onlyOne(GenericArguments.integer(Text.of("index"))));
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    if (src instanceof Player) {
      Player player = (Player) src;
      if (args.<Integer>getOne("index").isPresent()) {

        try {

          Alert alert = GriefAlert.getInstance()
              .getRotatingAlertList()
              .get(args.<Integer>getOne("index").get());
          GriefAlert.getInstance().getHologramManager().createTemporaryHologram(alert);
          return CommandResult.success();
        } catch (IndexOutOfBoundsException e) {
          player.sendMessage(Format.error("That alert could not be found."));
          return CommandResult.empty();
        }
      } else {
        player.sendMessage(Format.error(Text.of(
            TextColors.RED,
            "The alert code could not be parsed.")));
        return CommandResult.empty();
      }
    } else {
      Errors.sendPlayerOnlyCommand(src);
      return CommandResult.empty();
    }
  }
}