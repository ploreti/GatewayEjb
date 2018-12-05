package it.almawave.gateway.db.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "textMessage",
    "classificationLogicList"
})
public class CRMRequest {

    @JsonProperty("textMessage")
    private String textMessage;
    @JsonProperty("classificationLogicList")
    private List<String> classificationLogicList = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("textMessage")
    public String getTextMessage() {
        return textMessage;
    }

    @JsonProperty("textMessage")
    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    @JsonProperty("classificationLogicList")
    public List<String> getClassificationLogicList() {
        return classificationLogicList;
    }

    @JsonProperty("classificationLogicList")
    public void setClassificationLogicList(List<String> classificationLogicList) {
        this.classificationLogicList = classificationLogicList;
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
