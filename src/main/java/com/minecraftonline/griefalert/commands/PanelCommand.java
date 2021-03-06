/*
 * MIT License
 *
 * Copyright (c) 2020 Pieter Svenson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.minecraftonline.griefalert.commands;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.commands.common.GeneralCommand;
import com.minecraftonline.griefalert.util.Errors;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.enums.Permissions;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

/**
 * A command to start a panel.
 */
public class PanelCommand extends GeneralCommand {

  PanelCommand() {
    super(Permissions.GRIEFALERT_COMMAND_CHECK,
        Text.of("GriefAlert menu interface"));
    addAlias("panel");
    addAlias("p");
  }


  @NotNull
  @Override
  public CommandResult execute(@NotNull CommandSource src,
                               @NotNull CommandContext args) throws CommandException {
    if (src instanceof Player) {
      if (GriefAlert.getInstance().getAlertService().openPanel((Player) src)) {
        return CommandResult.success();
      } else {
        src.sendMessage(Format.error("Use ", Format.bonus("/ga check"), " to select an Alert"));
      }
    } else {
      throw Errors.playerOnlyException();
    }
    return CommandResult.empty();
  }
}
