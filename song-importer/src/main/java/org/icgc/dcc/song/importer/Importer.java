package org.icgc.dcc.song.importer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.icgc.dcc.song.importer.convert.SpecimenSampleConverter.SpecimenSampleTuple;
import org.icgc.dcc.song.importer.model.DataContainer;
import org.icgc.dcc.song.importer.model.PortalDonorMetadata;
import org.icgc.dcc.song.importer.model.PortalFileMetadata;
import org.icgc.dcc.song.server.model.entity.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.groupingBy;
import static org.icgc.dcc.common.core.util.stream.Collectors.toImmutableSet;
import static org.icgc.dcc.song.importer.Factory.DONOR_CONVERTER;
import static org.icgc.dcc.song.importer.Factory.FILE_CONVERTER;
import static org.icgc.dcc.song.importer.Factory.SAMPLE_SET_CONVERTER;
import static org.icgc.dcc.song.importer.Factory.SPECIMEN_SAMPLE_CONVERTER;
import static org.icgc.dcc.song.importer.Factory.STUDY_CONVERTER;
import static org.icgc.dcc.song.importer.Factory.buildPersistenceFactory;
import static org.icgc.dcc.song.importer.convert.AnalysisConverter.createAnalysisConverter;
import static org.icgc.dcc.song.importer.dao.DonorDao.createDonorDao;

@Slf4j
@RequiredArgsConstructor
@Component
public class Importer implements  Runnable {

  private static final String DATA_CONTAINER_PERSISTENCE_FN = "dataContainer.dat";

  @Autowired
  private RepositoryDao repositoryDao;

  @Autowired
  private Factory factory;

  @Override
  public void run() {

    log.info("Building DataFetcher...");
    val dataFetcher = factory.buildDataFetcher();

    log.info("Building PersistenceFactory...");
    val persistenceFactory = buildPersistenceFactory(dataFetcher::fetchData);

    log.info("Getting DataContainer object {}...", DATA_CONTAINER_PERSISTENCE_FN);
    val dataContainer = persistenceFactory.getObject(DATA_CONTAINER_PERSISTENCE_FN);

    val portalFileMetadatas = dataContainer.getPortalFileMetadataList();
    val portalDonorMetadatas = dataContainer.getPortalDonorMetadataSet();
    val dccMetadataFiles = dataContainer.getDccMetadataFiles();

    processStudies(portalDonorMetadatas);
    processDonors(portalDonorMetadatas);
    processSpecimensAndSamples(portalFileMetadatas);
    processAnalysis(dataContainer);
    processSampleSets(portalFileMetadatas);
    processFiles(portalFileMetadatas, dccMetadataFiles);

  }

  private void processStudies( Set<PortalDonorMetadata> portalDonorMetadataSet){
    log.info("Converting Studies...");
    val studies = STUDY_CONVERTER.convertStudies(portalDonorMetadataSet);

    log.info("Updating StudyRepository with {} studies", studies.size());
    studies.forEach(repositoryDao::createStudy);
  }

  private void processDonors(Set<PortalDonorMetadata> portalDonorMetadataSet){
    log.info("Converting donors...");
    val donors = DONOR_CONVERTER.convertDonors(portalDonorMetadataSet);

    log.info("Updating DonorRepository with {} donors", donors.size());
    donors.forEach(repositoryDao::createDonor);
  }

  private void processSpecimensAndSamples(List<PortalFileMetadata> portalFileMetadataList){

    log.info("Converting Specimen and Samples...");
    val specimenSampleTuples = SPECIMEN_SAMPLE_CONVERTER.convertSpecimenSampleTuples(portalFileMetadataList);

    // Aggregating specimens
    val specimens = specimenSampleTuples.stream()
        .collect(
            groupingBy(SpecimenSampleTuple::getSpecimen))
        .keySet();

    log.info("Updating SpecimenRepository with {} specimens", specimens.size());
    specimens.forEach(repositoryDao::createSpecimen);

    // Aggregating samples
    val samples = specimenSampleTuples.stream()
        .collect(
            groupingBy(x -> x.getSample().getSampleId()))
        .entrySet()
        .stream()
        .map(x -> x.getValue().get(0).getSample()) //TODO: why??
        .collect(toImmutableSet());

    log.info("Updating SampleRepository with {} samples", samples.size());
    samples.forEach(repositoryDao::createSample);

  }

  private void processFiles(List<PortalFileMetadata> portalFileMetadataList,
      List<File>  dccMetadataFiles ){
    log.info("Converting Files...");
    val files = FILE_CONVERTER.convertFiles(portalFileMetadataList);

    log.info("Updating FileRepository with {} files", files.size());
    files.forEach(repositoryDao::createFile);

    log.info("Updating FileRepository with {} dccMetadata files", dccMetadataFiles.size());
    dccMetadataFiles.forEach(repositoryDao::createFile);

  }

  private void processAnalysis(DataContainer dataContainer){
    log.info("Creating DonorDao...");
    val donorDao = createDonorDao(dataContainer.getPortalDonorMetadataList());

    log.info("Creating AnalysisConverter using DonorDao");
    val analysisConverter = createAnalysisConverter(donorDao);

    log.info("Converting SequencingReads...");
    val seqReadAnalysisList = analysisConverter.convertSequencingReads(dataContainer.getPortalFileMetadataList());

    log.info("Updating analysisRepository with {} Sequencing Reads", seqReadAnalysisList.size());
    seqReadAnalysisList.forEach(repositoryDao::createSequencingReadAnalysis);

    log.info("Converting VariantCalls...");
    val variantCallList = analysisConverter.convertVariantCalls(dataContainer.getPortalFileMetadataList());

    log.info("Updating analysisRepository with {} VariantCalls", variantCallList.size());
    variantCallList.forEach(repositoryDao::createVariantCallAnalysis);
  }

  private void processSampleSets(List<PortalFileMetadata> portalFileMetadataList){
    log.info("Converting SampleSets...");
    val sampleSets = SAMPLE_SET_CONVERTER.convertSampleSets(portalFileMetadataList);

    log.info("Updating analysisRespositry with {} SampleSets", sampleSets.size());
    sampleSets.forEach(repositoryDao::createSampleSet);
  }

}
