package mo;

import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.MediaType;
import org.mockserver.model.PortBinding;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class MockServerTools {

    private String host;
    private int port;
    private String method;
    private String path;
    private int statusCode;
    private MediaType contentType;
    private String body;
    private ClientAndServer mockServer;

    private MockServerTools(){}

    public MockServerTools setHost(String host){
        this.host = host;
        return this;
    }

    public MockServerTools setPort(int port){
        this.port = port;
        return this;
    }

    public MockServerTools setMethod(String method){
        this.method = method;
        return this;
    }

    public MockServerTools setPath(String path){
        this.path = path;
        return this;
    }

    public MockServerTools setStatusCode(int statusCode){
        this.statusCode = statusCode;
        return this;
    }

    public MockServerTools setContentType(MediaType contentType){
        this.contentType = contentType;
        return this;
    }

    public MockServerTools setBody(String body){
        this.body = body;
        return this;
    }

    public static MockServerTools getInstance(){
        return new MockServerTools();
    }

    public MockServerTools startup(){
        mockServer = ClientAndServer.startClientAndServer(port);
        new MockServerClient(host, port)
                .when(
                        request()
                                .withMethod(method)
                                .withPath(path)
                )
                .respond(
                        response()
                                .withStatusCode(statusCode)
                                .withContentType(contentType)
                                .withBody(body)
                );
        System.out.println("Mock Server Start Up!");
        return this;
    }

    public void shutdown(){
        mockServer.stop();
        System.out.println("Mock Server Shut Down!");
    }

}
