/* Created by PietElite */

package com.minecraftonline.griefalert.util.enums;

import com.google.common.collect.Lists;
import com.helion3.prism.util.PrismEvents;
import com.minecraftonline.griefalert.api.data.GriefEvent;

import java.util.Collection;
import java.util.Optional;
import javax.annotation.Nonnull;

import org.spongepowered.api.registry.CatalogRegistryModule;

public final class GriefEvents {

  /**
   * Ensure util class cannot be instantiated with private constructor.
   */
  private GriefEvents() {
  }

  public static final GriefEvent BREAK = GriefEvent.of(
      PrismEvents.BLOCK_BREAK,
      "Break a block");

  public static final GriefEvent PLACE = GriefEvent.of(
      PrismEvents.BLOCK_PLACE,
      "Place a block");

  public static final GriefEvent DEATH = GriefEvent.of(
      PrismEvents.ENTITY_DEATH,
      "An entity death directly caused by a player");

  public static final GriefEvent ITEM_USE = GriefEvent
      .of("use", "Item Use", "used",
          "Secondarily interact with the target item in main hand "
              + "while looking at nothing or at an entity.");

  public static final GriefEvent ITEM_APPLY = GriefEvent
      .of("apply", "Item Apply", "applied",
          "Secondarily interact with a block while holding the target item in main hand.");

  public static final GriefEvent INTERACT = GriefEvent
      .of("interact", "Interact", "interacted with",
          "Secondarily interact with a target object in the world with main hand");

  public static final GriefEvent ATTACK = GriefEvent
      .of("attack", "Entity Attack", "attacked",
          "Primarily interact with a target entity with main hand");

  public static final GriefEvent REPLACE = GriefEvent
      .of("replace", "Block Replace", "replaced",
          "Change a block from one non-air target block to a different non-air block");

  public static final CatalogRegistryModule<GriefEvent> REGISTRY_MODULE = new
      CatalogRegistryModule<GriefEvent>() {
        @Nonnull
        @Override
        public Optional<GriefEvent> getById(@Nonnull String id) {
          for (GriefEvent griefEvent : getAll()) {
            if (griefEvent.getId().equalsIgnoreCase(id)) {
              return Optional.of(griefEvent);
            }
          }
          return Optional.empty();
        }

        @Override
        public Collection<GriefEvent> getAll() {
          return Lists.newArrayList(
              BREAK,
              PLACE,
              REPLACE,
              DEATH,
              ITEM_USE,
              ITEM_APPLY,
              INTERACT,
              ATTACK
          );
        }
      };


}