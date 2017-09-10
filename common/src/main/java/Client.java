import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

public class Client {
    @JsonProperty
    String username;
    @JsonProperty
    List<File> files;

    public static String get(Client client){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(client);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Client get(String string){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(string, Client.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
