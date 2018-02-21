package org.icgc.dcc.song.importer.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.io.Serializable;
import java.util.Optional;

@Value
@Builder
public class PortalDonorMetadata implements Serializable{

  @NonNull private final String donorId;
  @NonNull private final String projectId;
  @NonNull private final String submittedDonorId;
  private final String projectName;
  private final String gender;

  public Optional<String> getGender() {
    return Optional.of(gender);
  }

  public Optional<String> getProjectName(){
    return Optional.of(projectName);
  }

}
