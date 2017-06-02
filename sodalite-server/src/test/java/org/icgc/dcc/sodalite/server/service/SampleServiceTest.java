package org.icgc.dcc.sodalite.server.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.flywaydb.test.annotation.FlywayTest;
import org.flywaydb.test.junit.FlywayTestExecutionListener;
import org.icgc.dcc.sodalite.server.model.entity.Sample;
import org.icgc.dcc.sodalite.server.model.enums.SampleType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import lombok.val;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, FlywayTestExecutionListener.class })
@FlywayTest
@ActiveProfiles("dev")
public class SampleServiceTest {

  @Autowired
  SampleService sampleService;

  @Test
  public void testReadSample() {
    val id = "SA1";
    val sample = sampleService.read(id);
    assertThat(sample.getSampleId()).isEqualTo(id);
    assertThat(sample.getSampleSubmitterId()).isEqualTo("T285-G7-A5");
    assertThat(sample.getSampleType()).isEqualTo(SampleType.DNA.value());
  }

  @Test
  public void testCreateAndDeleteSample() {
    val specimenId = "";
    val metadata = "";
    val s = Sample.create("", "101-IP-A", specimenId, SampleType.AMPLIFIED_DNA.value(), metadata);

    val status = sampleService.create("SP2", s);
    val id = s.getSampleId();

    assertThat(id).startsWith("SA");
    assertThat(status).isEqualTo("ok:" + id);

    Sample check = sampleService.read(id);
    assertThat(s).isEqualToComparingFieldByField(check);

    sampleService.delete(id);
    Sample check2 = sampleService.read(id);
    assertThat(check2).isNull();
  }

  @Test
  public void testUpdateSample() {
    val metadata = "";
    val specimenId = "";
    val s = Sample.create("", "102-CBP-A", specimenId, SampleType.RNA.value(), metadata);

    sampleService.create("SP2", s);

    val id = s.getSampleId();

    val s2 = Sample.create(id, "Sample 102", s.getSpecimenId(), SampleType.FFPE_DNA.value(), metadata);

    sampleService.update(s2);

    val s3 = sampleService.read(id);
    assertThat(s3).isEqualToComparingFieldByField(s2);
  }

}
