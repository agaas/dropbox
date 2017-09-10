import config.Config;

import java.nio.file.Path;
import java.util.List;

public class RequestHandlerFactory {

    public static RequestHandler get(Request request){

        switch (request.method){
            case GET:
                return new GetHandler();
            case SEND:
                return new SendHandler();
            case END:
                return new EndHandler();
            default:
                throw new ClientException("Unknown server request method." + request.method);
        }
    }

    private RequestHandlerFactory(){

    }
}
