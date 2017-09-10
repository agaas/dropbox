import com.fasterxml.jackson.annotation.JsonProperty;

public class File {
    @JsonProperty
    String name;
    @JsonProperty
    int size; //TODO should be long and for files >2gb byte[] should handled differently
}
