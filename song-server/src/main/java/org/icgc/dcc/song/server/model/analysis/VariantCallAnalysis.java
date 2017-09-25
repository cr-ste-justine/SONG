package org.icgc.dcc.song.server.model.analysis;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.val;
import org.icgc.dcc.song.server.model.enums.AccessTypes;
import org.icgc.dcc.song.server.model.experiment.VariantCall;
@EqualsAndHashCode(callSuper=false)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VariantCallAnalysis extends Analysis {
    VariantCall experiment;

    @JsonGetter
    public String getAnalysisType() {
        return "variantCall";
    }

    public static VariantCallAnalysis create(String id, String study, String submitter_id, String state,
        AccessTypes access) {
        val s = new VariantCallAnalysis();

        s.setAnalysisId(id);
        s.setStudy(study);
        s.setAnalysisSubmitterId(submitter_id);
        s.setAnalysisState(state);
        s.setAnalysisAccess(access);

        return s;
    }
}
