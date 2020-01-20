/*
 * Copyright (c) 2018. Ontario Institute for Cancer Research
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

package bio.overture.song.server.model.analysis;

import static bio.overture.song.core.model.enums.AnalysisStates.UNPUBLISHED;
import static bio.overture.song.core.model.enums.AnalysisStates.resolveAnalysisState;
import static bio.overture.song.core.utils.JsonUtils.toMap;
import static bio.overture.song.server.service.AnalysisTypeService.resolveAnalysisTypeId;

import bio.overture.song.core.model.AnalysisTypeId;
import bio.overture.song.core.utils.JsonUtils;
import bio.overture.song.server.model.entity.AnalysisSchema;
import bio.overture.song.server.model.entity.FileEntity;
import bio.overture.song.server.model.entity.composites.CompositeEntity;
import bio.overture.song.server.model.enums.TableAttributeNames;
import bio.overture.song.server.model.enums.TableNames;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;

import java.lang.reflect.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = TableNames.ANALYSIS)
public class Analysis {

  @Id
  @Column(name = TableAttributeNames.ID, updatable = false, unique = true, nullable = false)
  private String analysisId;

  @Column(name = TableAttributeNames.STUDY_ID, nullable = false)
  private String studyId;

  @Column(name = TableAttributeNames.STATE, nullable = false)
  private String analysisState;

  @NotNull
  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @JoinColumn(name = TableAttributeNames.ANALYSIS_SCHEMA_ID, nullable = false)
  private AnalysisSchema analysisSchema;

  @OneToOne
  @JoinColumn(name = TableAttributeNames.ANALYSIS_DATA_ID)
  @JsonIgnore
  private AnalysisData analysisData;

  @Transient private List<CompositeEntity> samples;

  @Transient private List<FileEntity> files;

  // TODO: need to remove this, and replace anything that needs this with Payload object
  public AnalysisTypeId getAnalysisType() {
    return resolveAnalysisTypeId(analysisSchema);
  }

  @SneakyThrows
  @JsonAnyGetter
  public Map<String, Object> getData() {
    return toMap(JsonUtils.toJson(analysisData.getData()));
  }

  @JsonProperty("analysisState")
  public void setAnalysisState(String state) {
    this.analysisState = resolveAnalysisState(state).toString();
  }

  @JsonProperty("analysisState")
  public String getAnalysisState() {
    return this.analysisState;
  }

  public void printMembers() {
    Class thisClass = this.getClass();

    log.info("Printing analysis methods");
    Method[] thisMethods = thisClass.getDeclaredMethods();
    for(int i = 0; i < thisMethods.length; i++) {
      log.info("method = " + thisMethods[i].toString());
    }

    log.info("Printing analysis fields");
    Field[] thisFields = thisClass.getDeclaredFields();
    for(int i = 0; i < thisFields.length; i++) {
      log.info("Field = " + thisFields[i].toString());
    }
  }
}
