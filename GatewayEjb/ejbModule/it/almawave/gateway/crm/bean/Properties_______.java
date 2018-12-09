
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
    "classificationLogicUsed",
    "label",
    "levelScores",
    "tuple",
    "version",
    "classification",
    "rankPosition",
    "speakerRole",
    "frequency"
})
public class Properties_______ {

    @JsonProperty("classificationLogicUsed")
    private ClassificationLogicUsed classificationLogicUsed;
    @JsonProperty("label")
    private Label__ label;
    @JsonProperty("levelScores")
    private LevelScores levelScores;
    @JsonProperty("tuple")
    private Tuple tuple;
    @JsonProperty("version")
    private Version_______ version;
    @JsonProperty("classification")
    private Classification classification;
    @JsonProperty("rankPosition")
    private RankPosition_ rankPosition;
    @JsonProperty("speakerRole")
    private SpeakerRole_ speakerRole;
    @JsonProperty("frequency")
    private Frequency frequency;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("classificationLogicUsed")
    public ClassificationLogicUsed getClassificationLogicUsed() {
        return classificationLogicUsed;
    }

    @JsonProperty("classificationLogicUsed")
    public void setClassificationLogicUsed(ClassificationLogicUsed classificationLogicUsed) {
        this.classificationLogicUsed = classificationLogicUsed;
    }

    @JsonProperty("label")
    public Label__ getLabel() {
        return label;
    }

    @JsonProperty("label")
    public void setLabel(Label__ label) {
        this.label = label;
    }

    @JsonProperty("levelScores")
    public LevelScores getLevelScores() {
        return levelScores;
    }

    @JsonProperty("levelScores")
    public void setLevelScores(LevelScores levelScores) {
        this.levelScores = levelScores;
    }

    @JsonProperty("tuple")
    public Tuple getTuple() {
        return tuple;
    }

    @JsonProperty("tuple")
    public void setTuple(Tuple tuple) {
        this.tuple = tuple;
    }

    @JsonProperty("version")
    public Version_______ getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(Version_______ version) {
        this.version = version;
    }

    @JsonProperty("classification")
    public Classification getClassification() {
        return classification;
    }

    @JsonProperty("classification")
    public void setClassification(Classification classification) {
        this.classification = classification;
    }

    @JsonProperty("rankPosition")
    public RankPosition_ getRankPosition() {
        return rankPosition;
    }

    @JsonProperty("rankPosition")
    public void setRankPosition(RankPosition_ rankPosition) {
        this.rankPosition = rankPosition;
    }

    @JsonProperty("speakerRole")
    public SpeakerRole_ getSpeakerRole() {
        return speakerRole;
    }

    @JsonProperty("speakerRole")
    public void setSpeakerRole(SpeakerRole_ speakerRole) {
        this.speakerRole = speakerRole;
    }

    @JsonProperty("frequency")
    public Frequency getFrequency() {
        return frequency;
    }

    @JsonProperty("frequency")
    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
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
