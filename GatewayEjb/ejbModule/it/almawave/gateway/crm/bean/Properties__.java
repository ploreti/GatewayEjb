
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
    "ontology",
    "ontologies",
    "parentURIs",
    "resourceType",
    "resourceURI",
    "sentenceEndCharacter",
    "sentenceStartCharacter",
    "sourceExtractedConcepts",
    "source",
    "startCharacter",
    "declaredDomainURIs",
    "declaredRangeURIs",
    "startTimeSentence",
    "endTimeSentence",
    "startTime",
    "endTime",
    "targetExtractedConcepts",
    "speakerRole",
    "version"
})
public class Properties__ {

    @JsonProperty("endCharacter")
    private EndCharacter endCharacter;
    @JsonProperty("ontology")
    private Ontology ontology;
    @JsonProperty("ontologies")
    private Ontologies ontologies;
    @JsonProperty("parentURIs")
    private ParentURIs parentURIs;
    @JsonProperty("resourceType")
    private ResourceType resourceType;
    @JsonProperty("resourceURI")
    private ResourceURI resourceURI;
    @JsonProperty("sentenceEndCharacter")
    private SentenceEndCharacter sentenceEndCharacter;
    @JsonProperty("sentenceStartCharacter")
    private SentenceStartCharacter sentenceStartCharacter;
    @JsonProperty("sourceExtractedConcepts")
    private SourceExtractedConcepts sourceExtractedConcepts;
    @JsonProperty("source")
    private Source source;
    @JsonProperty("startCharacter")
    private StartCharacter startCharacter;
    @JsonProperty("declaredDomainURIs")
    private DeclaredDomainURIs declaredDomainURIs;
    @JsonProperty("declaredRangeURIs")
    private DeclaredRangeURIs declaredRangeURIs;
    @JsonProperty("startTimeSentence")
    private StartTimeSentence startTimeSentence;
    @JsonProperty("endTimeSentence")
    private EndTimeSentence endTimeSentence;
    @JsonProperty("startTime")
    private StartTime startTime;
    @JsonProperty("endTime")
    private EndTime endTime;
    @JsonProperty("targetExtractedConcepts")
    private TargetExtractedConcepts targetExtractedConcepts;
    @JsonProperty("speakerRole")
    private SpeakerRole speakerRole;
    @JsonProperty("version")
    private Version___ version;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("endCharacter")
    public EndCharacter getEndCharacter() {
        return endCharacter;
    }

    @JsonProperty("endCharacter")
    public void setEndCharacter(EndCharacter endCharacter) {
        this.endCharacter = endCharacter;
    }

    @JsonProperty("ontology")
    public Ontology getOntology() {
        return ontology;
    }

    @JsonProperty("ontology")
    public void setOntology(Ontology ontology) {
        this.ontology = ontology;
    }

    @JsonProperty("ontologies")
    public Ontologies getOntologies() {
        return ontologies;
    }

    @JsonProperty("ontologies")
    public void setOntologies(Ontologies ontologies) {
        this.ontologies = ontologies;
    }

    @JsonProperty("parentURIs")
    public ParentURIs getParentURIs() {
        return parentURIs;
    }

    @JsonProperty("parentURIs")
    public void setParentURIs(ParentURIs parentURIs) {
        this.parentURIs = parentURIs;
    }

    @JsonProperty("resourceType")
    public ResourceType getResourceType() {
        return resourceType;
    }

    @JsonProperty("resourceType")
    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    @JsonProperty("resourceURI")
    public ResourceURI getResourceURI() {
        return resourceURI;
    }

    @JsonProperty("resourceURI")
    public void setResourceURI(ResourceURI resourceURI) {
        this.resourceURI = resourceURI;
    }

    @JsonProperty("sentenceEndCharacter")
    public SentenceEndCharacter getSentenceEndCharacter() {
        return sentenceEndCharacter;
    }

    @JsonProperty("sentenceEndCharacter")
    public void setSentenceEndCharacter(SentenceEndCharacter sentenceEndCharacter) {
        this.sentenceEndCharacter = sentenceEndCharacter;
    }

    @JsonProperty("sentenceStartCharacter")
    public SentenceStartCharacter getSentenceStartCharacter() {
        return sentenceStartCharacter;
    }

    @JsonProperty("sentenceStartCharacter")
    public void setSentenceStartCharacter(SentenceStartCharacter sentenceStartCharacter) {
        this.sentenceStartCharacter = sentenceStartCharacter;
    }

    @JsonProperty("sourceExtractedConcepts")
    public SourceExtractedConcepts getSourceExtractedConcepts() {
        return sourceExtractedConcepts;
    }

    @JsonProperty("sourceExtractedConcepts")
    public void setSourceExtractedConcepts(SourceExtractedConcepts sourceExtractedConcepts) {
        this.sourceExtractedConcepts = sourceExtractedConcepts;
    }

    @JsonProperty("source")
    public Source getSource() {
        return source;
    }

    @JsonProperty("source")
    public void setSource(Source source) {
        this.source = source;
    }

    @JsonProperty("startCharacter")
    public StartCharacter getStartCharacter() {
        return startCharacter;
    }

    @JsonProperty("startCharacter")
    public void setStartCharacter(StartCharacter startCharacter) {
        this.startCharacter = startCharacter;
    }

    @JsonProperty("declaredDomainURIs")
    public DeclaredDomainURIs getDeclaredDomainURIs() {
        return declaredDomainURIs;
    }

    @JsonProperty("declaredDomainURIs")
    public void setDeclaredDomainURIs(DeclaredDomainURIs declaredDomainURIs) {
        this.declaredDomainURIs = declaredDomainURIs;
    }

    @JsonProperty("declaredRangeURIs")
    public DeclaredRangeURIs getDeclaredRangeURIs() {
        return declaredRangeURIs;
    }

    @JsonProperty("declaredRangeURIs")
    public void setDeclaredRangeURIs(DeclaredRangeURIs declaredRangeURIs) {
        this.declaredRangeURIs = declaredRangeURIs;
    }

    @JsonProperty("startTimeSentence")
    public StartTimeSentence getStartTimeSentence() {
        return startTimeSentence;
    }

    @JsonProperty("startTimeSentence")
    public void setStartTimeSentence(StartTimeSentence startTimeSentence) {
        this.startTimeSentence = startTimeSentence;
    }

    @JsonProperty("endTimeSentence")
    public EndTimeSentence getEndTimeSentence() {
        return endTimeSentence;
    }

    @JsonProperty("endTimeSentence")
    public void setEndTimeSentence(EndTimeSentence endTimeSentence) {
        this.endTimeSentence = endTimeSentence;
    }

    @JsonProperty("startTime")
    public StartTime getStartTime() {
        return startTime;
    }

    @JsonProperty("startTime")
    public void setStartTime(StartTime startTime) {
        this.startTime = startTime;
    }

    @JsonProperty("endTime")
    public EndTime getEndTime() {
        return endTime;
    }

    @JsonProperty("endTime")
    public void setEndTime(EndTime endTime) {
        this.endTime = endTime;
    }

    @JsonProperty("targetExtractedConcepts")
    public TargetExtractedConcepts getTargetExtractedConcepts() {
        return targetExtractedConcepts;
    }

    @JsonProperty("targetExtractedConcepts")
    public void setTargetExtractedConcepts(TargetExtractedConcepts targetExtractedConcepts) {
        this.targetExtractedConcepts = targetExtractedConcepts;
    }

    @JsonProperty("speakerRole")
    public SpeakerRole getSpeakerRole() {
        return speakerRole;
    }

    @JsonProperty("speakerRole")
    public void setSpeakerRole(SpeakerRole speakerRole) {
        this.speakerRole = speakerRole;
    }

    @JsonProperty("version")
    public Version___ getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(Version___ version) {
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
