
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
    "id",
    "version",
    "insertDate",
    "applicationNamespace",
    "uri",
    "filename",
    "originalFilename",
    "ontologyVersion",
    "label",
    "enabled",
    "basenamespace",
    "description"
})
public class Properties____ {

    @JsonProperty("id")
    private Id_ id;
    @JsonProperty("version")
    private Version__ version;
    @JsonProperty("insertDate")
    private InsertDate_ insertDate;
    @JsonProperty("applicationNamespace")
    private ApplicationNamespace_ applicationNamespace;
    @JsonProperty("uri")
    private Uri_ uri;
    @JsonProperty("filename")
    private Filename_ filename;
    @JsonProperty("originalFilename")
    private OriginalFilename_ originalFilename;
    @JsonProperty("ontologyVersion")
    private OntologyVersion_ ontologyVersion;
    @JsonProperty("label")
    private Label_ label;
    @JsonProperty("enabled")
    private Enabled_ enabled;
    @JsonProperty("basenamespace")
    private Basenamespace_ basenamespace;
    @JsonProperty("description")
    private Description_ description;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("id")
    public Id_ getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Id_ id) {
        this.id = id;
    }

    @JsonProperty("version")
    public Version__ getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(Version__ version) {
        this.version = version;
    }

    @JsonProperty("insertDate")
    public InsertDate_ getInsertDate() {
        return insertDate;
    }

    @JsonProperty("insertDate")
    public void setInsertDate(InsertDate_ insertDate) {
        this.insertDate = insertDate;
    }

    @JsonProperty("applicationNamespace")
    public ApplicationNamespace_ getApplicationNamespace() {
        return applicationNamespace;
    }

    @JsonProperty("applicationNamespace")
    public void setApplicationNamespace(ApplicationNamespace_ applicationNamespace) {
        this.applicationNamespace = applicationNamespace;
    }

    @JsonProperty("uri")
    public Uri_ getUri() {
        return uri;
    }

    @JsonProperty("uri")
    public void setUri(Uri_ uri) {
        this.uri = uri;
    }

    @JsonProperty("filename")
    public Filename_ getFilename() {
        return filename;
    }

    @JsonProperty("filename")
    public void setFilename(Filename_ filename) {
        this.filename = filename;
    }

    @JsonProperty("originalFilename")
    public OriginalFilename_ getOriginalFilename() {
        return originalFilename;
    }

    @JsonProperty("originalFilename")
    public void setOriginalFilename(OriginalFilename_ originalFilename) {
        this.originalFilename = originalFilename;
    }

    @JsonProperty("ontologyVersion")
    public OntologyVersion_ getOntologyVersion() {
        return ontologyVersion;
    }

    @JsonProperty("ontologyVersion")
    public void setOntologyVersion(OntologyVersion_ ontologyVersion) {
        this.ontologyVersion = ontologyVersion;
    }

    @JsonProperty("label")
    public Label_ getLabel() {
        return label;
    }

    @JsonProperty("label")
    public void setLabel(Label_ label) {
        this.label = label;
    }

    @JsonProperty("enabled")
    public Enabled_ getEnabled() {
        return enabled;
    }

    @JsonProperty("enabled")
    public void setEnabled(Enabled_ enabled) {
        this.enabled = enabled;
    }

    @JsonProperty("basenamespace")
    public Basenamespace_ getBasenamespace() {
        return basenamespace;
    }

    @JsonProperty("basenamespace")
    public void setBasenamespace(Basenamespace_ basenamespace) {
        this.basenamespace = basenamespace;
    }

    @JsonProperty("description")
    public Description_ getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(Description_ description) {
        this.description = description;
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
