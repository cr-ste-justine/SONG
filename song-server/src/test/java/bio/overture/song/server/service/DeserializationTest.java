/*
 * Copyright (c) 2019. Ontario Institute for Cancer Research
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package bio.overture.song.server.service;

import static bio.overture.song.core.utils.JsonUtils.fromJson;
import static bio.overture.song.core.utils.JsonUtils.toJsonNode;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import bio.overture.song.core.model.AnalysisTypeId;
import bio.overture.song.core.utils.JsonUtils;
import bio.overture.song.server.model.dto.Payload;
import bio.overture.song.server.utils.TestFiles;
import lombok.val;
import org.junit.Test;

public class DeserializationTest {

  @Test
  public void testAnalysisTypeId() {
    val j1 = JsonUtils.mapper().createObjectNode();
    j1.put("name", "something");
    j1.put("version", 33);
    val e1 = AnalysisTypeId.builder().name("something").version(33).build();
    assertEquals(fromJson(j1, AnalysisTypeId.class), e1);

    val j2 = JsonUtils.mapper().createObjectNode();
    j2.put("name", "something");
    val e2 = AnalysisTypeId.builder().name("something").build();
    assertEquals(fromJson(j2, AnalysisTypeId.class), e2);

    val j3 = JsonUtils.mapper().createObjectNode();
    j3.put("version", 33);
    val e3 = AnalysisTypeId.builder().version(33).build();
    assertEquals(fromJson(j3, AnalysisTypeId.class), e3);

    val j4 = JsonUtils.mapper().createObjectNode();
    val e4 = AnalysisTypeId.builder().build();
    assertEquals(fromJson(j4, AnalysisTypeId.class), e4);
  }

  @Test
  public void testVariantCallDeserialization() {
    val payload1 =
        fromJson(
            TestFiles.getJsonNodeFromClasspath(
                "documents/deserialization/variantcall-deserialize1.json"),
            Payload.class);
    val rootNode1 = toJsonNode(payload1.getData());
    val experimentNode1 = rootNode1.path("experiment");
    assertFalse(experimentNode1.hasNonNull("matchedNormalSampleSubmitterId"));
    assertFalse(experimentNode1.hasNonNull("variantCallingTool"));
    assertFalse(experimentNode1.hasNonNull("random"));

    val payload2 =
        fromJson(
            TestFiles.getJsonNodeFromClasspath(
                "documents/deserialization/variantcall-deserialize2.json"),
            Payload.class);

    val rootNode2 = toJsonNode(payload2.getData());
    val experimentNode2 = rootNode2.path("experiment");
    assertTrue(rootNode2.has("experiment"));
    assertFalse(experimentNode2.has("matchedNormalSampleSubmitterId"));
    assertFalse(experimentNode2.has("variantCallingTool"));
  }

  @Test
  public void testSequencingReadDeserialization() {
    val payload1 =
        fromJson(
            TestFiles.getJsonNodeFromClasspath(
                "documents/deserialization/sequencingread-deserialize1.json"),
            Payload.class);

    val rootNode1 = toJsonNode(payload1.getData());
    val experimentNode1 = rootNode1.path("experiment");
    assertFalse(experimentNode1.has("aligned"));
    assertFalse(experimentNode1.has("alignmentTool"));
    assertFalse(experimentNode1.has("insertSize"));
    assertEquals(experimentNode1.path("libraryStrategy").textValue(), "WXS");
    assertFalse(experimentNode1.hasNonNull("pairedEnd"));
    assertFalse(experimentNode1.hasNonNull("referenceGenome"));
    assertFalse(experimentNode1.path("info").hasNonNull("random"));

    val payload2 =
        fromJson(
            TestFiles.getJsonNodeFromClasspath(
                "documents/deserialization/sequencingread-deserialize2.json"),
            Payload.class);

    val rootNode2 = toJsonNode(payload2.getData());
    val experimentNode2 = rootNode2.path("experiment");
    assertFalse(experimentNode2.has("aligned"));
    assertFalse(experimentNode2.has("alignmentTool"));
    assertFalse(experimentNode2.hasNonNull("insertSize"));
    assertEquals(experimentNode2.path("libraryStrategy").textValue(), "WXS");
    assertTrue(experimentNode2.path("pairedEnd").booleanValue());
    assertFalse(experimentNode2.hasNonNull("referenceGenome"));
  }
}
