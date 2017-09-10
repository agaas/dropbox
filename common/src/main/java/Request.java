import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.Socket;

public class Request {

    @JsonProperty
    public Method method;
    @JsonProperty
    public int port;
    @JsonProperty
    public File file;

    public static String get(Request request) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Request get(String string){
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(string, Request.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
