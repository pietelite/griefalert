package com.minecraftonline.griefalert.commands;

import com.google.common.collect.Lists;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.commands.AbstractCommand;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.*;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.world.DimensionType;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;

public class GriefAlertProfileCommand extends AbstractCommand {

  public GriefAlertProfileCommand() {
    super(Permissions.GRIEFALERT_COMMAND_PROFILE,
        Text.of("Perform alterations to the profiles used for flagging alerts"));
    addAlias("profile");
    addAlias("p");
    addChild(new AddCommand());
    addChild(new RemoveCommand());
    addChild(new CountCommand());
    addChild(new ListCommand());
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    sendHelp(src);
    return CommandResult.success();
  }

  public static class AddCommand extends AbstractCommand {

    AddCommand() {
      super(Permissions.GRIEFALERT_COMMAND_PROFILE, Text.of("Add a profile to the database"));
      addAlias("add");
      addAlias("a");
      addCommandElement(GenericArguments.seq(
          GenericArguments.catalogedElement(Text.of("event"), GriefEvent.class),
          GenericArguments.string(Text.of("target")),
          GenericArguments.flags()
              .valueFlag(GenericArguments.dimension(Text.of("dimension")), "-ignore", "i")
              .valueFlag(GenericArguments.catalogedElement(Text.of("event_color"), TextColor.class), "-event_color")
              .valueFlag(GenericArguments.catalogedElement(Text.of("target_color"), TextColor.class), "-target_color")
              .valueFlag(GenericArguments.catalogedElement(Text.of("dimension_color"), TextColor.class), "-dimension_color")
              .buildWith(GenericArguments.none())));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      DataContainer dataContainer = DataContainer.createNew();

      dataContainer.set(
          GriefProfileDataQueries.EVENT,
          ((GriefEvent) args.getOne("event").get()).getId());
      dataContainer.set(
          GriefProfileDataQueries.TARGET,
          args.getOne("target").map((s) -> General.ensureIdFormat((String) s)).get());

      if (args.getOne("event_color").isPresent()) {
        dataContainer.set(GriefProfileDataQueries.EVENT_COLOR, args.getOne("event_color").get());
      }

      if (args.getOne("target_color").isPresent()) {
        dataContainer.set(GriefProfileDataQueries.TARGET_COLOR, args.getOne("target_color").get());
      }

      if (args.getOne("dimension_color").isPresent()) {
        dataContainer.set(GriefProfileDataQueries.DIMENSION_COLOR, args.getOne("dimension_color").get());
      }

      dataContainer.set(GriefProfileDataQueries.IGNORED, Lists.newArrayList(args.getAll("dimension")));

      GriefProfile toAdd = GriefProfile.of(dataContainer);
      if (!toAdd.isValid()) {
        src.sendMessage(Format.error("It looks like you're missing some necessary components!"));
        return CommandResult.empty();
      }

      try {
        if (GriefAlert.getInstance().getProfileStorage().write(toAdd)) {
          src.sendMessage(Format.success("GriefProfile added"));
          GriefAlert.getInstance().getProfileCabinet().reload();
          return CommandResult.success();
        } else {
          src.sendMessage(Format.error("GriefProfile addition failed. Maybe this format already exists?"));
          return CommandResult.empty();
        }
      } catch (SQLException e) {
        GriefAlert.getInstance().getLogger().error("SQLException thrown when trying to add a profile: " + toAdd.printData());
        General.printStackTraceToDebugLogger(e);
        return CommandResult.empty();
      }

    }

  }

  public static class RemoveCommand extends AbstractCommand {

    RemoveCommand() {
      super(Permissions.GRIEFALERT_COMMAND_PROFILE, Text.of("Remove a profile from the database"));
      addAlias("remove");
      addAlias("r");
      addCommandElement(GenericArguments.seq(
          GenericArguments.catalogedElement(Text.of("event"), GriefEvent.class),
          GenericArguments.string(Text.of("target"))));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

      GriefEvent griefEvent = (GriefEvent) args.getOne("event").get();
      String target = args.getOne("target").map((s) -> General.ensureIdFormat((String) s)).get();

      try {
        if (GriefAlert.getInstance().getProfileStorage().remove(griefEvent, target)) {
          GriefAlert.getInstance().getProfileCabinet().reload();
          src.sendMessage(Format.success("Removed a Grief Profile"));
          return CommandResult.success();
        } else {
          src.sendMessage(Format.error("No Grief Profile was found with those parameters"));
          return CommandResult.empty();
        }
      } catch (SQLException e) {
        GriefAlert.getInstance().getLogger().error("SQLException thrown when trying to remove a profile: "
            + griefEvent.getId() + ", "
            + target);
        General.printStackTraceToDebugLogger(e);
        return CommandResult.empty();
      }

    }

  }

  public static class CountCommand extends AbstractCommand {

    CountCommand() {
      super(Permissions.GRIEFALERT_COMMAND_PROFILE, Text.of("Count how many profiles there are in use"));
      addAlias("count");
      addAlias("c");
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

      src.sendMessage(Format.success(String.format(
          "There are %s Grief Profiles in use",
          GriefAlert.getInstance().getProfileCabinet().getProfiles().size())));
      return CommandResult.success();

    }

  }

  public static class ListCommand extends AbstractCommand {

    ListCommand() {
      super(Permissions.GRIEFALERT_COMMAND_PROFILE, Text.of("List every profile in use"));
      addAlias("list");
      addAlias("l");
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

      if (!(src instanceof ConsoleSource)) {
        src.sendMessage(Format.error("Only the console can use this command"));
        return CommandResult.empty();
      }

      ConsoleSource console = (ConsoleSource) src;

      console.sendMessage(Format.heading("=== Grief Profiles ==="));
      for (GriefProfile profile : GriefAlert.getInstance().getProfileCabinet().getProfiles()) {
        console.sendMessage(Format.bonus(profile.printData()));
      }
      return CommandResult.success();
    }

  }

}