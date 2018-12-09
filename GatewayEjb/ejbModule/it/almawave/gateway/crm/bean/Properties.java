
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
    "checksum",
    "contactId",
    "rankPosition",
    "matchingUriScore",
    "businessEvent",
    "documentAnnotations",
    "documentAttributes",
    "duplicated",
    "extractedConcepts",
    "language",
    "mimeType",
    "neRecognized",
    "documentKey",
    "plainText",
    "score",
    "size",
    "tupleScores",
    "uri",
    "version",
    "rawContent",
    "sessionDuration",
    "chageEvent"
})
public class Properties {

    @JsonProperty("checksum")
    private Checksum checksum;
    @JsonProperty("contactId")
    private ContactId contactId;
    @JsonProperty("rankPosition")
    private RankPosition rankPosition;
    @JsonProperty("matchingUriScore")
    private MatchingUriScore matchingUriScore;
    @JsonProperty("businessEvent")
    private BusinessEvent businessEvent;
    @JsonProperty("documentAnnotations")
    private DocumentAnnotations documentAnnotations;
    @JsonProperty("documentAttributes")
    private DocumentAttributes documentAttributes;
    @JsonProperty("duplicated")
    private Duplicated duplicated;
    @JsonProperty("extractedConcepts")
    private ExtractedConcepts extractedConcepts;
    @JsonProperty("language")
    private Language language;
    @JsonProperty("mimeType")
    private MimeType mimeType;
    @JsonProperty("neRecognized")
    private NeRecognized neRecognized;
    @JsonProperty("documentKey")
    private DocumentKey documentKey;
    @JsonProperty("plainText")
    private PlainText plainText;
    @JsonProperty("score")
    private Score score;
    @JsonProperty("size")
    private Size size;
    @JsonProperty("tupleScores")
    private TupleScores tupleScores;
    @JsonProperty("uri")
    private Uri___ uri;
    @JsonProperty("version")
    private Version________ version;
    @JsonProperty("rawContent")
    private RawContent rawContent;
    @JsonProperty("sessionDuration")
    private SessionDuration sessionDuration;
    @JsonProperty("chageEvent")
    private ChageEvent chageEvent;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("checksum")
    public Checksum getChecksum() {
        return checksum;
    }

    @JsonProperty("checksum")
    public void setChecksum(Checksum checksum) {
        this.checksum = checksum;
    }

    @JsonProperty("contactId")
    public ContactId getContactId() {
        return contactId;
    }

    @JsonProperty("contactId")
    public void setContactId(ContactId contactId) {
        this.contactId = contactId;
    }

    @JsonProperty("rankPosition")
    public RankPosition getRankPosition() {
        return rankPosition;
    }

    @JsonProperty("rankPosition")
    public void setRankPosition(RankPosition rankPosition) {
        this.rankPosition = rankPosition;
    }

    @JsonProperty("matchingUriScore")
    public MatchingUriScore getMatchingUriScore() {
        return matchingUriScore;
    }

    @JsonProperty("matchingUriScore")
    public void setMatchingUriScore(MatchingUriScore matchingUriScore) {
        this.matchingUriScore = matchingUriScore;
    }

    @JsonProperty("businessEvent")
    public BusinessEvent getBusinessEvent() {
        return businessEvent;
    }

    @JsonProperty("businessEvent")
    public void setBusinessEvent(BusinessEvent businessEvent) {
        this.businessEvent = businessEvent;
    }

    @JsonProperty("documentAnnotations")
    public DocumentAnnotations getDocumentAnnotations() {
        return documentAnnotations;
    }

    @JsonProperty("documentAnnotations")
    public void setDocumentAnnotations(DocumentAnnotations documentAnnotations) {
        this.documentAnnotations = documentAnnotations;
    }

    @JsonProperty("documentAttributes")
    public DocumentAttributes getDocumentAttributes() {
        return documentAttributes;
    }

    @JsonProperty("documentAttributes")
    public void setDocumentAttributes(DocumentAttributes documentAttributes) {
        this.documentAttributes = documentAttributes;
    }

    @JsonProperty("duplicated")
    public Duplicated getDuplicated() {
        return duplicated;
    }

    @JsonProperty("duplicated")
    public void setDuplicated(Duplicated duplicated) {
        this.duplicated = duplicated;
    }

    @JsonProperty("extractedConcepts")
    public ExtractedConcepts getExtractedConcepts() {
        return extractedConcepts;
    }

    @JsonProperty("extractedConcepts")
    public void setExtractedConcepts(ExtractedConcepts extractedConcepts) {
        this.extractedConcepts = extractedConcepts;
    }

    @JsonProperty("language")
    public Language getLanguage() {
        return language;
    }

    @JsonProperty("language")
    public void setLanguage(Language language) {
        this.language = language;
    }

    @JsonProperty("mimeType")
    public MimeType getMimeType() {
        return mimeType;
    }

    @JsonProperty("mimeType")
    public void setMimeType(MimeType mimeType) {
        this.mimeType = mimeType;
    }

    @JsonProperty("neRecognized")
    public NeRecognized getNeRecognized() {
        return neRecognized;
    }

    @JsonProperty("neRecognized")
    public void setNeRecognized(NeRecognized neRecognized) {
        this.neRecognized = neRecognized;
    }

    @JsonProperty("documentKey")
    public DocumentKey getDocumentKey() {
        return documentKey;
    }

    @JsonProperty("documentKey")
    public void setDocumentKey(DocumentKey documentKey) {
        this.documentKey = documentKey;
    }

    @JsonProperty("plainText")
    public PlainText getPlainText() {
        return plainText;
    }

    @JsonProperty("plainText")
    public void setPlainText(PlainText plainText) {
        this.plainText = plainText;
    }

    @JsonProperty("score")
    public Score getScore() {
        return score;
    }

    @JsonProperty("score")
    public void setScore(Score score) {
        this.score = score;
    }

    @JsonProperty("size")
    public Size getSize() {
        return size;
    }

    @JsonProperty("size")
    public void setSize(Size size) {
        this.size = size;
    }

    @JsonProperty("tupleScores")
    public TupleScores getTupleScores() {
        return tupleScores;
    }

    @JsonProperty("tupleScores")
    public void setTupleScores(TupleScores tupleScores) {
        this.tupleScores = tupleScores;
    }

    @JsonProperty("uri")
    public Uri___ getUri() {
        return uri;
    }

    @JsonProperty("uri")
    public void setUri(Uri___ uri) {
        this.uri = uri;
    }

    @JsonProperty("version")
    public Version________ getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(Version________ version) {
        this.version = version;
    }

    @JsonProperty("rawContent")
    public RawContent getRawContent() {
        return rawContent;
    }

    @JsonProperty("rawContent")
    public void setRawContent(RawContent rawContent) {
        this.rawContent = rawContent;
    }

    @JsonProperty("sessionDuration")
    public SessionDuration getSessionDuration() {
        return sessionDuration;
    }

    @JsonProperty("sessionDuration")
    public void setSessionDuration(SessionDuration sessionDuration) {
        this.sessionDuration = sessionDuration;
    }

    @JsonProperty("chageEvent")
    public ChageEvent getChageEvent() {
        return chageEvent;
    }

    @JsonProperty("chageEvent")
    public void setChageEvent(ChageEvent chageEvent) {
        this.chageEvent = chageEvent;
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
