package com.minecraftonline.griefalert.commands;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.griefevents.GriefEvent;
import com.minecraftonline.griefalert.tools.ClickableMessage;
import java.util.HashMap;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.annotation.NonnullByDefault;

public class GriefAlertRecentCommand extends AbstractCommand {

  GriefAlertRecentCommand(GriefAlert plugin) {
    super(
        plugin,
        GriefAlert.Permission.GRIEFALERT_COMMAND_RECENT,
        Text.of("Get information regarding recent cached grief alerts")
    );
    addAlias("recent");
    addAlias("n");
    HashMap<String, ?> filterMap = new HashMap<>();
    filterMap.put("player", null);
    addCommandElement(GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))));
  }

  @Override
  @NonnullByDefault
  public CommandResult execute(@NonnullByDefault CommandSource src,
                               @NonnullByDefault CommandContext args) throws CommandException {
    if (args.getOne("player").isPresent()) {
      Player player = (Player) args.getOne("player").get();
      ClickableMessage.Builder messageBuilder = ClickableMessage.builder(Text.of(
          TextColors.GRAY, "Alerts: ",
          TextColors.LIGHT_PURPLE, player.getName(), "\n"));
      for (GriefEvent event : plugin.getGriefEventCache().getListChronologically(true)) {
        if (event.getEvent().getGriefer().equals(player)) {
          messageBuilder.addClickableCommand(
              String.valueOf(event.getCacheCode()),
              "/griefalert check " + event.getCacheCode(),
              Text.of("Teleport here.\n", event.getSummary())
          );
        }
      }
      src.sendMessage(messageBuilder.build().toText());
    }
    return CommandResult.success();
  }
}
