package com.minecraftonline.griefalert.storage;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.GriefProfileDataQueries;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.DimensionTypes;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MySqlProfileStorage {

  private static final String TABLE_NAME = "GriefAlertProfiles";
  private final String address;
  private Connection connection;

  public MySqlProfileStorage() throws SQLException {
    address = "jdbc:sqlite:" + GriefAlert.getInstance().getDataDirectory().getPath() + "/griefalert.db";
    createTable();
  }

  private void connect() throws SQLException {
    try {
      Class.forName("org.sqlite.JDBC");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    connection = DriverManager.getConnection(address);
  }

  private boolean close() throws SQLException {
    if (getConnection() == null || getConnection().isClosed()) {
      return false;
    }
    getConnection().close();
    return getConnection().isClosed();
  }

  private void createTable() throws SQLException {
    connect();
    String profiles = "CREATE TABLE IF NOT EXISTS "
        + TABLE_NAME + " ("
        + GriefProfileDataQueries.EVENT + " varchar(16) NOT NULL, "
        + GriefProfileDataQueries.TARGET + " varchar(255) NOT NULL, "
        + "ignore_overworld bit NOT NULL, "
        + "ignore_nether bit NOT NULL, "
        + "ignore_the_end bit NOT NULL, "
        + GriefProfileDataQueries.EVENT_COLOR + " varchar(16), "
        + GriefProfileDataQueries.TARGET_COLOR + " varchar(16), "
        + GriefProfileDataQueries.DIMENSION_COLOR + " varchar(16) "
        + ");";
    getConnection().prepareStatement(profiles).execute();
    close();
  }

  /**
   * Write a GriefProfile into the database. Requires connection.
   *
   * @param profile The GriefProfile to add
   * @return false if a grief profile already existed with the same target and grief event
   * @throws SQLException if error through SQL
   */
  public boolean write(GriefProfile profile) throws SQLException {
    String command = String.format(
        "INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s) values (?, ?, ?, ?, ?, ?, ?, ?);",
        TABLE_NAME,
        GriefProfileDataQueries.EVENT,
        GriefProfileDataQueries.TARGET,
        "ignore_overworld",
        "ignore_nether",
        "ignore_the_end",
        GriefProfileDataQueries.EVENT_COLOR,
        GriefProfileDataQueries.TARGET_COLOR,
        GriefProfileDataQueries.DIMENSION_COLOR);

    if (exists(profile.getGriefEvent(), profile.getTarget())) {
      return false;
    }

    connect();
    PreparedStatement statement = getConnection().prepareStatement(command);

    statement.setString(1, profile.getGriefEvent().getId());
    statement.setString(2, profile.getTarget());
    statement.setBoolean(3, profile.getDataContainer()
        .getList(GriefProfileDataQueries.IGNORED)
        .map((list) -> list.contains(DimensionTypes.OVERWORLD.getId())).orElse(false));
    statement.setBoolean(4, profile.getDataContainer()
        .getList(GriefProfileDataQueries.IGNORED)
        .map((list) -> list.contains(DimensionTypes.NETHER.getId())).orElse(false));
    statement.setBoolean(5, profile.getDataContainer()
        .getList(GriefProfileDataQueries.IGNORED)
        .map((list) -> list.contains(DimensionTypes.THE_END.getId())).orElse(false));
    statement.setString(6, profile.getDataContainer()
        .getString(GriefProfileDataQueries.EVENT_COLOR).orElse(null));
    statement.setString(7, profile.getDataContainer()
        .getString(GriefProfileDataQueries.TARGET_COLOR).orElse(null));
    statement.setString(8, profile.getDataContainer()
        .getString(GriefProfileDataQueries.DIMENSION_COLOR).orElse(null));

    statement.execute();
    close();
    return true;
  }

  private boolean exists(GriefEvent griefEvent, String target) throws SQLException {
    connect();
    String command = "SELECT * FROM "
        + TABLE_NAME + " WHERE "
        + GriefProfileDataQueries.EVENT + " = '" + griefEvent.getId() + "' AND "
        + GriefProfileDataQueries.TARGET + " = '" + target + "';";

    ResultSet rs = getConnection().prepareStatement(command).executeQuery();
    boolean hasResult = rs.next();

    close();
    return hasResult;

  }

  /**
   * Remove the GriefProfile with the given parameters. Requires connection.
   *
   * @param griefEvent The grief event of this profile to remove
   * @param target     The target of this profile to remove
   * @return false if a grief profile was not found
   * @throws SQLException if error through SQL
   */
  public boolean remove(GriefEvent griefEvent, String target) throws SQLException {
    if (!exists(griefEvent, target)) {
      return false;
    }

    connect();
    String command = "DELETE FROM "
        + TABLE_NAME + " WHERE "
        + GriefProfileDataQueries.EVENT + " = '" + griefEvent.getId() + "' AND "
        + GriefProfileDataQueries.TARGET + " = '" + target + "';";

    getConnection().prepareStatement(command).execute();
    close();
    return true;
  }

  /**
   * Get all <code>GriefProfile</code>s saved in the database. Required connection.
   *
   * @return a list of grief profiles
   * @throws SQLException if error through SQL
   */
  public List<GriefProfile> retrieve() throws SQLException {
    connect();
    List<GriefProfile> profiles = new LinkedList<>();

    String command = "SELECT * FROM " + TABLE_NAME + ";";
    ResultSet rs = connection.prepareStatement(command).executeQuery();

    while (rs.next()) {
      DataContainer container = DataContainer.createNew()
          .set(GriefProfileDataQueries.EVENT, rs.getString(1))
          .set(GriefProfileDataQueries.TARGET, rs.getString(2));

      List<String> ignored = new ArrayList<>();
      if (rs.getBoolean(3)) {
        ignored.add(DimensionTypes.OVERWORLD.getId());
      }
      if (rs.getBoolean(4)) {
        ignored.add(DimensionTypes.NETHER.getId());
      }
      if (rs.getBoolean(5)) {
        ignored.add(DimensionTypes.THE_END.getId());
      }
      container.set(GriefProfileDataQueries.IGNORED, ignored);
      if (rs.getString(6) != null) {
        container.set(GriefProfileDataQueries.EVENT_COLOR, rs.getString(6));
      }
      if (rs.getString(7) != null) {
        container.set(GriefProfileDataQueries.TARGET_COLOR, rs.getString(7));
      }
      if (rs.getString(8) != null) {
        container.set(GriefProfileDataQueries.DIMENSION_COLOR, rs.getString(8));
      }
      profiles.add(GriefProfile.of(container));
    }
    close();
    return profiles;

  }

  private Connection getConnection() {
    return connection;
  }

}