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

package com.minecraftonline.griefalert.alerts.prism;

import com.helion3.prism.api.data.PrismEvent;
import com.helion3.prism.api.records.PrismRecord;
import com.helion3.prism.api.services.PrismService;
import com.helion3.prism.api.services.Request;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import org.spongepowered.api.Sponge;

import java.util.Date;

public class DeathAlert extends PrismAlert {

  public DeathAlert(GriefProfile griefProfile, PrismRecord prismRecord) {
    super(griefProfile, prismRecord);
  }

  @Override
  protected Request getRollbackRequest() {
    Request.Builder builder = Request.builder();

    builder.addPlayerUuid(getGrieferUuid());
    builder.addTarget(getTarget().replace("minecraft:", ""));
    builder.setEarliest(Date.from(getCreated().toInstant().minusSeconds(1)));
    builder.setLatest(Date.from(getCreated().toInstant().plusSeconds(1)));
    builder.addEvent(Sponge.getRegistry().getType(PrismEvent.class, getGriefEvent().getId()).orElseThrow(() ->
            new RuntimeException("PrismAlert stored an invalid GriefEvent: " + getGriefEvent().getId())));
    builder.addWorldUuid(getWorldUuid());
    builder.setxRange(getGriefPosition().getX() - 1, getGriefPosition().getX() + 1);
    builder.setyRange(getGriefPosition().getY() - 1, getGriefPosition().getY() + 1);
    builder.setzRange(getGriefPosition().getZ() - 1, getGriefPosition().getZ() + 1);

    return builder.build();
  }

  //  @Override
//  protected void addQueryConditionsTo(Query query) {
//    query.addCondition(FieldCondition.of(
//        DataQueries.Player,
//        MatchRule.EQUALS,
//        this.getGriefer().getUniqueId().toString()));
//    query.addCondition(FieldCondition.of(
//        DataQueries.Target,
//        MatchRule.EQUALS,
//        Pattern.compile(this.getTarget().replace('_', ' ').replace("minecraft:", ""))));
//    query.addCondition(FieldCondition.of(
//        DataQueries.Created,
//        MatchRule.GREATER_THAN_EQUAL,
//        Date.from(this.getCreated().toInstant().minusSeconds(1))));
//    query.addCondition(FieldCondition.of(
//        DataQueries.Created,
//        MatchRule.LESS_THAN_EQUAL,
//        Date.from(this.getCreated().toInstant().plusSeconds(1))));
//    query.addCondition(FieldCondition.of(
//        DataQueries.EventName,
//        MatchRule.EQUALS,
//        this.getGriefEvent().getId()));
//    query.addCondition(FieldCondition.of(
//        DataQueries.Location.then(DataQueries.WorldUuid),
//        MatchRule.EQUALS,
//        this.getGriefLocation().getExtent().getUniqueId().toString()));
//    query.addCondition(FieldCondition.of(
//        DataQueries.Location.then(DataQueries.X),
//        MatchRule.EQUALS,
//        this.getGriefLocation().getPosition().getFloorX()));
//    query.addCondition(FieldCondition.of(
//        DataQueries.Location.then(DataQueries.Y),
//        MatchRule.EQUALS,
//        this.getGriefLocation().getPosition().getFloorY()));
//    query.addCondition(FieldCondition.of(
//        DataQueries.Location.then(DataQueries.Z),
//        MatchRule.EQUALS,
//        this.getGriefLocation().getPosition().getFloorZ()));
//  }
}
