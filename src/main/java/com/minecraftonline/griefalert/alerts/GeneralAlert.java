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

package com.minecraftonline.griefalert.alerts;

import com.google.common.collect.Lists;
import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.alerts.Detail;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.Alerts;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.Grammar;
import com.minecraftonline.griefalert.util.enums.Details;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

/**
 * An object to hold all information about an Alert caused by an
 * event matching a {@link GriefProfile}.
 *
 * @author PietElite
 */
public abstract class GeneralAlert implements Alert {

  private final GriefProfile griefProfile;

  private final List<Detail<Alert>> details = Lists.newLinkedList();
  private boolean silent = false;
  private final Date created;

  protected GeneralAlert(GriefProfile griefProfile) {
    this.griefProfile = griefProfile;
    created = new Date();
    details.add(Details.player());
    details.add(Details.event());
    details.add(Details.target());
    details.add(Details.location());
    details.add(Details.time());
    details.add(Details.inHand());
  }

  @Nonnull
  @Override
  public final GriefProfile getGriefProfile() {
    return griefProfile;
  }

  @Nonnull
  @Override
  public Date getCreated() {
    return created;
  }

  @Override
  @Nonnull
  public final Text getMessage() {
    Text.Builder builder = Text.builder();
    builder.append(Text.of(
        Format.userName(Alerts.getGriefer(this)),
        Format.space(),
        this.getGriefProfile()
            .getColored(GriefProfile.Colorable.EVENT)
            .orElse(Format.ALERT_EVENT_COLOR),
        Format.action(this.getGriefEvent()),
        Format.space(),
        this.getGriefProfile()
            .getColored(GriefProfile.Colorable.TARGET)
            .orElse(Format.ALERT_TARGET_COLOR),
        Grammar.addIndefiniteArticle(Format.item(this.getTarget())),
        TextColors.RED, " in the ",
        this.getGriefProfile()
            .getColored(GriefProfile.Colorable.DIMENSION)
            .orElse(Format.ALERT_DIMENSION_COLOR),
        Format.dimension(Alerts.getWorld(this).getDimension().getType())));
    Text.Builder ellipses = Text.builder().append(Format.bonus("(...)"));
    Text hoverText = Text.of(Format.prefix(), Format.endLine(), Text.joinWith(
        Format.endLine(),
        details.stream()
            .filter(detail -> !detail.isPrimary())
            .map(detail -> detail.get(this))
            .flatMap(optional -> optional.map(Stream::of).orElseGet(Stream::empty))
            .collect(Collectors.toList())));
    if (!hoverText.toPlain().isEmpty()) {
      ellipses.onHover(TextActions.showText(hoverText));
    }
    return builder.append(Format.space()).append(ellipses.build()).build();
  }

  @Nonnull
  @Override
  public final Text getSummary() {
    return Text.joinWith(
        Format.endLine(),
        details.stream()
            .map(detail -> detail.get(this))
            .flatMap(optional -> optional.map(Stream::of).orElseGet(Stream::empty))
            .collect(Collectors.toList()));
  }

  protected void addDetail(Detail<Alert> detail) {
    this.details.add(detail);
  }

  @Override
  public boolean isSilent() {
    return silent;
  }

  @Override
  public void setSilent(boolean silent) {
    this.silent = silent;
  }

  @Nonnull
  @Override
  public final GriefEvent getGriefEvent() {
    return griefProfile.getGriefEvent();
  }

  @Nonnull
  @Override
  public final String getTarget() {
    return griefProfile.getTarget();
  }

  public List<Detail<Alert>> getDetails() {
    return details;
  }


}
