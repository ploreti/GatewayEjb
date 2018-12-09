
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
    "index",
    "type",
    "id",
    "dbId",
    "version",
    "originalType"
})
public class Properties______ {

    @JsonProperty("index")
    private Index index;
    @JsonProperty("type")
    private Type_ type;
    @JsonProperty("id")
    private Id__ id;
    @JsonProperty("dbId")
    private DbId dbId;
    @JsonProperty("version")
    private Version_____ version;
    @JsonProperty("originalType")
    private OriginalType originalType;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("index")
    public Index getIndex() {
        return index;
    }

    @JsonProperty("index")
    public void setIndex(Index index) {
        this.index = index;
    }

    @JsonProperty("type")
    public Type_ getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(Type_ type) {
        this.type = type;
    }

    @JsonProperty("id")
    public Id__ getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Id__ id) {
        this.id = id;
    }

    @JsonProperty("dbId")
    public DbId getDbId() {
        return dbId;
    }

    @JsonProperty("dbId")
    public void setDbId(DbId dbId) {
        this.dbId = dbId;
    }

    @JsonProperty("version")
    public Version_____ getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(Version_____ version) {
        this.version = version;
    }

    @JsonProperty("originalType")
    public OriginalType getOriginalType() {
        return originalType;
    }

    @JsonProperty("originalType")
    public void setOriginalType(OriginalType originalType) {
        this.originalType = originalType;
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
