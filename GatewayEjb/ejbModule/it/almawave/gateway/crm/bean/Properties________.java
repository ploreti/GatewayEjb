
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
    "active",
    "id",
    "externalID",
    "description",
    "version",
    "validityStartDate",
    "validityEndDate"
})
public class Properties________ {

    @JsonProperty("active")
    private Active active;
    @JsonProperty("id")
    private Id___ id;
    @JsonProperty("externalID")
    private ExternalID externalID;
    @JsonProperty("description")
    private Description__ description;
    @JsonProperty("version")
    private Version______ version;
    @JsonProperty("validityStartDate")
    private ValidityStartDate validityStartDate;
    @JsonProperty("validityEndDate")
    private ValidityEndDate validityEndDate;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("active")
    public Active getActive() {
        return active;
    }

    @JsonProperty("active")
    public void setActive(Active active) {
        this.active = active;
    }

    @JsonProperty("id")
    public Id___ getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Id___ id) {
        this.id = id;
    }

    @JsonProperty("externalID")
    public ExternalID getExternalID() {
        return externalID;
    }

    @JsonProperty("externalID")
    public void setExternalID(ExternalID externalID) {
        this.externalID = externalID;
    }

    @JsonProperty("description")
    public Description__ getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(Description__ description) {
        this.description = description;
    }

    @JsonProperty("version")
    public Version______ getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(Version______ version) {
        this.version = version;
    }

    @JsonProperty("validityStartDate")
    public ValidityStartDate getValidityStartDate() {
        return validityStartDate;
    }

    @JsonProperty("validityStartDate")
    public void setValidityStartDate(ValidityStartDate validityStartDate) {
        this.validityStartDate = validityStartDate;
    }

    @JsonProperty("validityEndDate")
    public ValidityEndDate getValidityEndDate() {
        return validityEndDate;
    }

    @JsonProperty("validityEndDate")
    public void setValidityEndDate(ValidityEndDate validityEndDate) {
        this.validityEndDate = validityEndDate;
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
