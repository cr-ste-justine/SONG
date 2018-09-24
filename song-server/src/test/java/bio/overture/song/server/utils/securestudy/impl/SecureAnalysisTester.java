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

package bio.overture.song.server.utils.securestudy.impl;

import bio.overture.song.core.utils.RandomGenerator;
import bio.overture.song.server.model.enums.AnalysisTypes;
import bio.overture.song.server.service.AnalysisService;
import bio.overture.song.server.service.StudyService;
import bio.overture.song.server.utils.securestudy.AbstractSecureTester;
import bio.overture.song.server.utils.securestudy.SecureTestData;
import lombok.NonNull;
import lombok.val;

import java.util.function.BiConsumer;

import static java.lang.String.format;
import static bio.overture.song.core.exceptions.ServerErrors.ANALYSIS_ID_NOT_FOUND;
import static bio.overture.song.server.utils.AnalysisGenerator.createAnalysisGenerator;

/**
 * Utility test class that tests study security associated with analysis entities. Ensures that
 * the analysisService method throws the correct error if it is called for an analysis
 * that is unrelated to the supplied studyId.
 */
public class SecureAnalysisTester extends AbstractSecureTester<AnalysisTypes> {

  @NonNull private final AnalysisService analysisService;

  private SecureAnalysisTester(RandomGenerator randomGenerator,
      StudyService studyService,
      AnalysisService analysisService) {
    super(randomGenerator, studyService, ANALYSIS_ID_NOT_FOUND);
    this.analysisService = analysisService;
  }

  @Override protected boolean isIdExist(String id){
    return analysisService.isAnalysisExist(id);
  }

  @Override protected String createId(String existingStudyId, AnalysisTypes analysisType){
    val analysisGenerator = createAnalysisGenerator(existingStudyId, analysisService, getRandomGenerator());

    if (analysisType == AnalysisTypes.SEQUENCING_READ){
      return analysisGenerator.createDefaultRandomSequencingReadAnalysis().getAnalysisId();
    } else if (analysisType == AnalysisTypes.VARIANT_CALL){
      return analysisGenerator.createDefaultRandomVariantCallAnalysis().getAnalysisId();
    } else {
      throw new IllegalStateException(format("The analysisType '%s' cannot be generated", analysisType));
    }
  }

  public static SecureAnalysisTester createSecureAnalysisTester(RandomGenerator randomGenerator,
      StudyService studyService,
      AnalysisService analysisService) {
    return new SecureAnalysisTester(randomGenerator, studyService, analysisService);
  }

  public SecureTestData runSecureTest(BiConsumer<String, String> biConsumer){
    return runSecureTest(biConsumer, getRandomGenerator().randomEnum(AnalysisTypes.class));
  }

}
