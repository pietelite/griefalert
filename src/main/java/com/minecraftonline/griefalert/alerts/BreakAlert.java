package com.minecraftonline.griefalert.alerts;

import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.util.Format;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class BreakAlert extends Alert {

  private final TextColor eventColor = Format.ALERT_ACTION_COLOR;
  private final TextColor targetColor = Format.ALERT_TARGET_COLOR;
  private final TextColor dimensionColor = Format.ALERT_DIMENSION_COLOR;

  public BreakAlert(int cacheCode) {
    super(cacheCode);
  }

  @Override
  public Text getMessageText() {
    // TODO: Write message text for BreakAlert
    return Text.of("BreakAlert text");
  }

  @Override
  public Transform<World> getTransform() {
    // TODO: Write getTransform
    return null;
  }

}