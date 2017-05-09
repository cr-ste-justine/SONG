package org.icgc.dcc.sodalite.server.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.icgc.dcc.sodalite.server.model.Donor;
import org.icgc.dcc.sodalite.server.model.DonorGender;
import org.icgc.dcc.sodalite.server.model.Specimen;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.val;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DonorServiceTest {

  @Autowired
  DonorService service;
  @Autowired
  SpecimenService specimenService;

  @Test
  public void testReadDonor() {
    // check for data that we know exists in the H2 database already
    val d = service.getById("ABC123", "DO1");
    assertThat(d != null);
    assertThat(d.getDonorId()).isEqualTo("DO1");
    assertThat(d.getDonorGender()).isEqualTo(DonorGender.MALE);
    assertThat(d.getDonorSubmitterId()).isEqualTo("Subject-X23Alpha7");
    assertThat(d.getSpecimens().size()).isEqualTo(2);

    // Just check that each specimen object that we get is the same as the one we get from the
    // specimen service. Let the specimen service tests verify that the contents are right.
    d.getSpecimens().forEach(specimen -> assertThat(specimen.equals(getMatchingSpecimen(specimen))));

  }

  Specimen getMatchingSpecimen(Specimen specimen) {
    return specimenService.getById(specimen.getSpecimenId());
  }

  @Test
  public void testCreateAndDeleteDonor() {
    val d = new Donor()
        .withDonorGender(DonorGender.UNSPECIFIED)
        .withDonorSubmitterId("Subject X21-Alpha")
        .withSpecimens(new ArrayList<Specimen>());
    assertThat(d.getDonorId()).isNull();

    val status = service.create("XYZ234", d);
    val id = d.getDonorId();

    assertThat(id).startsWith("DO");
    assertThat(status).isEqualTo("ok:" + id);

    Donor check = service.getById("XYZ234", id);
    assertThat(d).isEqualToComparingFieldByField(check);

    service.delete("XYZ234", id);
    Donor check2 = service.getById("XYZ234", id);
    assertThat(check2).isNull();
  }

  @Test
  public void testUpdateDonor() {
    val d = new Donor()
        .withDonorGender(DonorGender.MALE)
        .withDonorSubmitterId("Triangle-Arrow-S")
        .withSpecimens(new ArrayList<Specimen>());

    service.create("ABC123", d);

    val id = d.getDonorId();

    val d2 = new Donor()
        .withDonorId(id)
        .withDonorGender(DonorGender.FEMALE)
        .withDonorSubmitterId("X21-Beta-17")
        .withSpecimens(new ArrayList<Specimen>());

    service.update("ABC123", d2);

    val d3 = service.getById("ABC123", id);
    assertThat(d3).isEqualToComparingFieldByField(d2);
  }

}