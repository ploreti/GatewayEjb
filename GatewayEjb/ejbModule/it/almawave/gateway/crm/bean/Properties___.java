
package it.almawave.gateway.crm.bean;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "description",
    "enabled",
    "filename",
    "applicationNamespace",
    "basenamespace",
    "id",
    "importedBasenamespaces",
    "insertDate",
    "label",
    "ontologyVersion",
    "originalFilename",
    "uri",
    "version"
})
public class Properties___ {

    @JsonProperty("description")
    private Description description;
    @JsonProperty("enabled")
    private Enabled enabled;
    @JsonProperty("filename")
    private Filename filename;
    @JsonProperty("applicationNamespace")
    private ApplicationNamespace applicationNamespace;
    @JsonProperty("basenamespace")
    private Basenamespace basenamespace;
    @JsonProperty("id")
    private Id id;
    @JsonProperty("importedBasenamespaces")
    private ImportedBasenamespaces importedBasenamespaces;
    @JsonProperty("insertDate")
    private InsertDate insertDate;
    @JsonProperty("label")
    private Label label;
    @JsonProperty("ontologyVersion")
    private OntologyVersion ontologyVersion;
    @JsonProperty("originalFilename")
    private OriginalFilename originalFilename;
    @JsonProperty("uri")
    private Uri uri;
    @JsonProperty("version")
    private Version_ version;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("description")
    public Description getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(Description description) {
        this.description = description;
    }

    @JsonProperty("enabled")
    public Enabled getEnabled() {
        return enabled;
    }

    @JsonProperty("enabled")
    public void setEnabled(Enabled enabled) {
        this.enabled = enabled;
    }

    @JsonProperty("filename")
    public Filename getFilename() {
        return filename;
    }

    @JsonProperty("filename")
    public void setFilename(Filename filename) {
        this.filename = filename;
    }

    @JsonProperty("applicationNamespace")
    public ApplicationNamespace getApplicationNamespace() {
        return applicationNamespace;
    }

    @JsonProperty("applicationNamespace")
    public void setApplicationNamespace(ApplicationNamespace applicationNamespace) {
        this.applicationNamespace = applicationNamespace;
    }

    @JsonProperty("basenamespace")
    public Basenamespace getBasenamespace() {
        return basenamespace;
    }

    @JsonProperty("basenamespace")
    public void setBasenamespace(Basenamespace basenamespace) {
        this.basenamespace = basenamespace;
    }

    @JsonProperty("id")
    public Id getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Id id) {
        this.id = id;
    }

    @JsonProperty("importedBasenamespaces")
    public ImportedBasenamespaces getImportedBasenamespaces() {
        return importedBasenamespaces;
    }

    @JsonProperty("importedBasenamespaces")
    public void setImportedBasenamespaces(ImportedBasenamespaces importedBasenamespaces) {
        this.importedBasenamespaces = importedBasenamespaces;
    }

    @JsonProperty("insertDate")
    public InsertDate getInsertDate() {
        return insertDate;
    }

    @JsonProperty("insertDate")
    public void setInsertDate(InsertDate insertDate) {
        this.insertDate = insertDate;
    }

    @JsonProperty("label")
    public Label getLabel() {
        return label;
    }

    @JsonProperty("label")
    public void setLabel(Label label) {
        this.label = label;
    }

    @JsonProperty("ontologyVersion")
    public OntologyVersion getOntologyVersion() {
        return ontologyVersion;
    }

    @JsonProperty("ontologyVersion")
    public void setOntologyVersion(OntologyVersion ontologyVersion) {
        this.ontologyVersion = ontologyVersion;
    }

    @JsonProperty("originalFilename")
    public OriginalFilename getOriginalFilename() {
        return originalFilename;
    }

    @JsonProperty("originalFilename")
    public void setOriginalFilename(OriginalFilename originalFilename) {
        this.originalFilename = originalFilename;
    }

    @JsonProperty("uri")
    public Uri getUri() {
        return uri;
    }

    @JsonProperty("uri")
    public void setUri(Uri uri) {
        this.uri = uri;
    }

    @JsonProperty("version")
    public Version_ getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(Version_ version) {
        this.version = version;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
