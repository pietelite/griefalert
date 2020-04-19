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

package com.minecraftonline.griefalert.util;

import com.minecraftonline.griefalert.api.alerts.Alert;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;

public final class Alerts {

  private Alerts() {
  }

  public static User getGriefer(@Nonnull Alert alert) {
    return Sponge.getServiceManager().provide(UserStorageService.class)
        .flatMap(users -> users.get(alert.getGrieferUuid()))
        .orElseThrow(() -> new RuntimeException("Alert stores an invalid User UUID"));
  }

  public static Transform<World> buildTransform(@Nonnull Alert alert) {
    return new Transform<>(
        getWorld(alert),
        alert.getGrieferPosition(),
        alert.getGrieferRotation());
  }

  public static World getWorld(@Nonnull Alert alert) {
    return Sponge.getServer()
        .getWorld(alert.getWorldUuid())
        .orElseThrow(() -> new RuntimeException("Alert stores an invalid World UUID"));
  }

  public static Location<World> getGriefLocation(Alert alert) {
    return new Location<>(Alerts.getWorld(alert), alert.getGriefPosition());
  }
}
