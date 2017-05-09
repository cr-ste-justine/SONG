package org.icgc.dcc.sodalite.server.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.icgc.dcc.sodalite.server.model.File;
import org.icgc.dcc.sodalite.server.model.Sample;
import org.icgc.dcc.sodalite.server.model.SampleType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.val;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SampleServiceTest {

  @Autowired
  SampleService sampleService;
  @Autowired
  FileService fileService;

  @Test
  public void testReadSample() {
    val id = "SA1";
    val sample = sampleService.getById(id);
    assertThat(sample.getSampleId()).isEqualTo(id);
    assertThat(sample.getSampleSubmitterId()).isEqualTo("T285-G7-A5");
    assertThat(sample.getSampleType()).isEqualTo(SampleType.DNA);
    assertThat(sample.getFiles().size()).isEqualTo(2);

    // Verify that we got the same files as the file service says we should.
    sample.getFiles().forEach(file -> assertThat(file.equals(getFile(file.getObjectId()))));
  }

  private File getFile(String id) {
    return fileService.getById(id);
  }

  @Test
  public void testCreateAndDeleteSample() {
    val s = new Sample()
        .withSampleSubmitterId("101-IP-A")
        .withSampleType(SampleType.AMPLIFIED_DNA)
        .withFiles(new ArrayList<File>());

    val status = sampleService.create("SP2", s);
    val id = s.getSampleId();

    assertThat(id).startsWith("SA");
    assertThat(status).isEqualTo("ok:" + id);

    Sample check = sampleService.getById(id);
    assertThat(s).isEqualToComparingFieldByField(check);

    sampleService.delete(id);
    Sample check2 = sampleService.getById(id);
    assertThat(check2).isNull();
  }

  @Test
  public void testUpdateSample() {
    val s = new Sample()
        .withSampleSubmitterId("102-CBP-A")
        .withSampleType(SampleType.RNA)
        .withFiles(new ArrayList<File>());

    sampleService.create("SP2", s);

    val id = s.getSampleId();

    val s2 = new Sample()
        .withSampleId(id)
        .withSampleSubmitterId("Sample 102")
        .withSampleType(SampleType.FFPE_DNA)
        .withFiles(new ArrayList<File>());

    sampleService.update(s2);

    val s3 = sampleService.getById(id);
    assertThat(s3).isEqualToComparingFieldByField(s2);
  }

}