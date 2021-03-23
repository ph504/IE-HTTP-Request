package main;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HttpRequest {
    private String url;
    private HttpRequestMethod httpRequestMethod;
    private HashMap<String, String> headers;
    private HashMap<String, String> params;
    private HashMap<String, String> body;

    private static final String CLRF = "\r\n";

    public HttpRequest(String url) {
        this(url, HttpRequestMethod.GET, new HashMap<>(), new HashMap<>(), new HashMap<>());
    }

    public HttpRequest(String url, HttpRequestMethod httpRequestMethod) {
        this(url, httpRequestMethod, new HashMap<>(), new HashMap<>(), new HashMap<>());
    }

    public HttpRequest(String url, HttpRequestMethod httpRequestMethod, HashMap<String, 
            String> headers, HashMap<String, String> params, HashMap<String, String> body) {
        this.url = url;
        this.httpRequestMethod = httpRequestMethod;
        this.headers = headers;
        this.params = params;
        this.body = body;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HttpRequestMethod getHttpRequestMethod() {
        return this.httpRequestMethod;
    }

    public void setHttpRequestMethod(HttpRequestMethod httpRequestMethod) {
        this.httpRequestMethod = httpRequestMethod;
    }

    public HashMap<String, String> getHeaders() {
        return this.headers;
    }

    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }

    public HashMap<String, String> getParams() {
        return this.params;
    }

    public void setParams(HashMap<String, String> params) {
        this.params = params;
    }

    public HashMap<String, String> getBody() {
        return this.body;
    }

    public void setBody(HashMap<String, String> body) {
        this.body = body;
    }

    public HttpResponse request() throws HttpException, IOException {
        // TODO

        // request line --> general form of : Method<sp>Path<sp>Version<CLRF>
        httpRequestMethod.toString() + url.
        clearBody();

        queryParams();
        String jsonBody = toJSON();



    }

    private String toJSON(){
        StringBuilder requestBody = new StringBuilder("{");
        Iterator<String> iterator = body.keySet().iterator();
        if(iterator.hasNext())
            requestBody.append(CLRF + iterator.next());
        while (iterator.hasNext()){
            String jsonElement = iterator.next();
            requestBody.append(requestBody.append("," + CLRF)
                    .append(jsonElement));

            requestBody.append(requestBody.append(":")
                    .append(body.get(jsonElement)));
        }
        requestBody.append(CLRF + "}");
        return requestBody.toString();
    }

    private void clearBody(){ // clear the body if it is a GET request method.
        if(httpRequestMethod.equals(HttpRequestMethod.GET))
            body = new HashMap<>();
        // else nothing.
    }

    private void queryParams(){
        // for the get method.

        if(!httpRequestMethod.equals(HttpRequestMethod.GET))
            params = new HashMap<>();

    }
}
