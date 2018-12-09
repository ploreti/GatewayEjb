
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
    "endCharacter",
    "endSentenceCharacter",
    "entityProbability",
    "entity",
    "ontologyUri",
    "sentenceEndCharacter",
    "sentenceStartCharacter",
    "startCharacter",
    "startSentenceCharacter",
    "type",
    "uri",
    "version"
})
public class Properties_____ {

    @JsonProperty("endCharacter")
    private EndCharacter_ endCharacter;
    @JsonProperty("endSentenceCharacter")
    private EndSentenceCharacter endSentenceCharacter;
    @JsonProperty("entityProbability")
    private EntityProbability entityProbability;
    @JsonProperty("entity")
    private Entity entity;
    @JsonProperty("ontologyUri")
    private OntologyUri ontologyUri;
    @JsonProperty("sentenceEndCharacter")
    private SentenceEndCharacter_ sentenceEndCharacter;
    @JsonProperty("sentenceStartCharacter")
    private SentenceStartCharacter_ sentenceStartCharacter;
    @JsonProperty("startCharacter")
    private StartCharacter_ startCharacter;
    @JsonProperty("startSentenceCharacter")
    private StartSentenceCharacter startSentenceCharacter;
    @JsonProperty("type")
    private Type type;
    @JsonProperty("uri")
    private Uri__ uri;
    @JsonProperty("version")
    private Version____ version;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("endCharacter")
    public EndCharacter_ getEndCharacter() {
        return endCharacter;
    }

    @JsonProperty("endCharacter")
    public void setEndCharacter(EndCharacter_ endCharacter) {
        this.endCharacter = endCharacter;
    }

    @JsonProperty("endSentenceCharacter")
    public EndSentenceCharacter getEndSentenceCharacter() {
        return endSentenceCharacter;
    }

    @JsonProperty("endSentenceCharacter")
    public void setEndSentenceCharacter(EndSentenceCharacter endSentenceCharacter) {
        this.endSentenceCharacter = endSentenceCharacter;
    }

    @JsonProperty("entityProbability")
    public EntityProbability getEntityProbability() {
        return entityProbability;
    }

    @JsonProperty("entityProbability")
    public void setEntityProbability(EntityProbability entityProbability) {
        this.entityProbability = entityProbability;
    }

    @JsonProperty("entity")
    public Entity getEntity() {
        return entity;
    }

    @JsonProperty("entity")
    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    @JsonProperty("ontologyUri")
    public OntologyUri getOntologyUri() {
        return ontologyUri;
    }

    @JsonProperty("ontologyUri")
    public void setOntologyUri(OntologyUri ontologyUri) {
        this.ontologyUri = ontologyUri;
    }

    @JsonProperty("sentenceEndCharacter")
    public SentenceEndCharacter_ getSentenceEndCharacter() {
        return sentenceEndCharacter;
    }

    @JsonProperty("sentenceEndCharacter")
    public void setSentenceEndCharacter(SentenceEndCharacter_ sentenceEndCharacter) {
        this.sentenceEndCharacter = sentenceEndCharacter;
    }

    @JsonProperty("sentenceStartCharacter")
    public SentenceStartCharacter_ getSentenceStartCharacter() {
        return sentenceStartCharacter;
    }

    @JsonProperty("sentenceStartCharacter")
    public void setSentenceStartCharacter(SentenceStartCharacter_ sentenceStartCharacter) {
        this.sentenceStartCharacter = sentenceStartCharacter;
    }

    @JsonProperty("startCharacter")
    public StartCharacter_ getStartCharacter() {
        return startCharacter;
    }

    @JsonProperty("startCharacter")
    public void setStartCharacter(StartCharacter_ startCharacter) {
        this.startCharacter = startCharacter;
    }

    @JsonProperty("startSentenceCharacter")
    public StartSentenceCharacter getStartSentenceCharacter() {
        return startSentenceCharacter;
    }

    @JsonProperty("startSentenceCharacter")
    public void setStartSentenceCharacter(StartSentenceCharacter startSentenceCharacter) {
        this.startSentenceCharacter = startSentenceCharacter;
    }

    @JsonProperty("type")
    public Type getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(Type type) {
        this.type = type;
    }

    @JsonProperty("uri")
    public Uri__ getUri() {
        return uri;
    }

    @JsonProperty("uri")
    public void setUri(Uri__ uri) {
        this.uri = uri;
    }

    @JsonProperty("version")
    public Version____ getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(Version____ version) {
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
