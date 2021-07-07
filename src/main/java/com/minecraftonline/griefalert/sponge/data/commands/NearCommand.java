/*
 * This file is part of Prism, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2015 Helion3 http://helion3.com/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.minecraftonline.griefalert.sponge.data.commands;

import com.minecraftonline.griefalert.SpongeGriefAlert;
import com.minecraftonline.griefalert.common.data.query.ConditionGroup;
import com.minecraftonline.griefalert.common.data.query.QuerySession;
import com.minecraftonline.griefalert.sponge.data.util.AsyncUtil;
import com.minecraftonline.griefalert.sponge.data.util.Format;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

/**
 * @author viveleroi
 */
// TODO remove
@Deprecated
public class NearCommand {
  private NearCommand() {
  }

  public static CommandSpec getCommand() {
    return CommandSpec.builder()
        .description(Text.of("Alias of /pr l r:(default radius)"))
        .permission("prism.lookup")
        .executor((source, args) -> {
          int radius = SpongeGriefAlert.getSpongeInstance().getConfig().getDefaultCategory().getRadius();

          source.sendMessage(Format.heading("Querying records..."));

          // Create a new query session
          final QuerySession session = new QuerySession(source);
          session.newQuery().addCondition(ConditionGroup.from(((Player) source).getLocation(), radius));

          // Pass off to an async lookup helper
          AsyncUtil.lookup(session);
          return CommandResult.success();
        }).build();
  }
}
