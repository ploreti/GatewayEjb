
package it.almawave.gateway.bean;

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
    "plainText",
    "tuples",
    "km",
    "isUrgent",
    "details"
})
public class GatewayResponse {

    @JsonProperty("plainText")
    private String plainText;
    @JsonProperty("tuples")
    private List<Tuple> tuples = null;
    @JsonProperty("km")
    private Integer km;
    @JsonProperty("isUrgent")
    private Boolean isUrgent;
    @JsonProperty("details")
    private Details details;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("plainText")
    public String getPlainText() {
        return plainText;
    }

    @JsonProperty("plainText")
    public void setPlainText(String plainText) {
        this.plainText = plainText;
    }

    @JsonProperty("tuples")
    public List<Tuple> getTuples() {
        return tuples;
    }

    @JsonProperty("tuples")
    public void setTuples(List<Tuple> tuples) {
        this.tuples = tuples;
    }

    @JsonProperty("km")
    public Integer getKm() {
        return km;
    }

    @JsonProperty("km")
    public void setKm(Integer km) {
        this.km = km;
    }

    @JsonProperty("isUrgent")
    public Boolean getIsUrgent() {
        return isUrgent;
    }

    @JsonProperty("isUrgent")
    public void setIsUrgent(Boolean isUrgent) {
        this.isUrgent = isUrgent;
    }

    @JsonProperty("details")
    public Details getDetails() {
        return details;
    }

    @JsonProperty("details")
    public void setDetails(Details details) {
        this.details = details;
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
