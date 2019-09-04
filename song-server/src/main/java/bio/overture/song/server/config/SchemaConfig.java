package bio.overture.song.server.config;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.val;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaClient;
import org.everit.json.schema.loader.SchemaLoader;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

import static bio.overture.song.core.utils.JsonUtils.readTree;
import static bio.overture.song.server.utils.JsonObjects.convertToJSONObject;
import static bio.overture.song.server.utils.Resources.getResourceContent;

@Getter
@Setter
@Validated
@Component
@Configuration
@ConfigurationProperties("schemas")
public class SchemaConfig {

  public static final Path SCHEMA_PATH = Paths.get("schemas");
  public static final Path SCHEMA_ANALYSIS_PATH = SCHEMA_PATH.resolve("analysis");
  private static final Schema ANALYSIS_TYPE_META_SCHEMA = buildAnalysisTypeMetaSchema();

  @NotNull
  private Boolean useLatest;

  @Bean
  public String analysisPayloadBaseJson() throws IOException {
    val schemaRelativePath = SCHEMA_ANALYSIS_PATH.resolve("analysisPayload.json").toString();
    return getResourceContent(schemaRelativePath);
  }

  @Bean
  public String analysisUpdateBaseJson() throws IOException {
    val schemaRelativePath = SCHEMA_ANALYSIS_PATH.resolve("analysisUpdate.json").toString();
    return getResourceContent(schemaRelativePath);
  }

  // [rtisma] NOTE: When this is transformed into a bean, a stackoverflow occurs, probably due to
  // some spring integration issue https://github.com/everit-org/json-schema/issues/191. The
  // workaround is to create a bean that is a callback to the schema
  @Bean
  @SneakyThrows
  public Supplier<Schema> analysisTypeMetaSchemaSupplier() {
    return () -> ANALYSIS_TYPE_META_SCHEMA;
  }

  private static JsonNode getSchemaAsJson(String schemaFilename) throws IOException {
    val schemaRelativePath = SCHEMA_ANALYSIS_PATH.resolve(schemaFilename);
    return readTree(getResourceContent(schemaRelativePath.toString()));
  }

  @SneakyThrows
  private static Schema buildAnalysisTypeMetaSchema() {
    val filename = "analysisType.metaschema.json";
    val jsonSchema = convertToJSONObject(getSchemaAsJson(filename));
    return SchemaLoader.builder()
        .schemaClient(SchemaClient.classPathAwareClient())
        .draftV7Support()
        .schemaJson(jsonSchema)
        .build()
        .load()
        .build();
  }
}
