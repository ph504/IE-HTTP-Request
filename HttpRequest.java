package main;

import java.io.*;
import java.net.*;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HttpRequest {
    private String url;
    private HttpRequestMethod httpRequestMethod;
    private HashMap<String, String> headers;
    private HashMap<String, String> params;
    private HashMap<String, String> body;

    private static final String CRLF = "\r\n"; // carriage return.

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

        HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();

        // setting method.
        connection.setRequestMethod(httpRequestMethod.toString());
        connection.setDoInput(true);

        // setting headers.
        Iterator<String> iterator = headers.keySet().iterator();
        while (iterator.hasNext()) {
            String header = iterator.next();
            connection.addRequestProperty(header, headers.get(header));
        }

        // clearing parameters and body if not necessary.
        clearBody();
        clearQParams();

        connection.setDoOutput(true);
        DataOutputStream dos = new DataOutputStream(connection.getOutputStream());

        String queryParameters = toQParams();
        dos.writeBytes(queryParameters);

        String jsonBody = toJSON();
        dos.writeBytes(jsonBody);

        dos.flush();
        dos.close();

        // fetchig response
        int responseStatus = connection.getResponseCode();
        if(responseStatus>=400 && responseStatus<=499){
            throw new HttpException("Error 400s family is from client-side", new HttpResponse(connection.getResponseCode(), connection.getHeaderFields(), ""));
        }
        else if(responseStatus>=500 && responseStatus<=599){
            throw new HttpException("Error 500s family is from server-side", new HttpResponse(connection.getResponseCode(), connection.getHeaderFields(), ""));
        }
        HttpResponse httpResponse = new HttpResponse(connection.getResponseCode(), connection.getHeaderFields(), setResponseBody(connection));


        return httpResponse;

    }

    private String setResponseBody(HttpURLConnection connection) throws IOException {
        String msgLine;
        StringBuilder responseMassage = new StringBuilder();
        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {

            while ((msgLine = br.readLine()) != null) {

                responseMassage.append(msgLine.trim());
                responseMassage.append(CRLF);
            }

        }
        return responseMassage.toString();
    }

    private String toQParams() throws UnsupportedEncodingException {
        StringBuilder queryParams = new StringBuilder();
        Iterator<String> iterator = params.keySet().iterator();
        String key = null;
        if(iterator.hasNext()){
            key = iterator.next();
            queryParams.append(URLEncoder.encode(key, StandardCharsets.UTF_8.toString()));
            queryParams.append("=");
            queryParams.append(URLEncoder.encode(params.get(key), StandardCharsets.UTF_8.toString()));
        }
        while(iterator.hasNext()) {
            key = iterator.next();
            queryParams.append("&");
            queryParams.append(URLEncoder.encode(key, StandardCharsets.UTF_8.toString()));
            queryParams.append("=");
            queryParams.append(URLEncoder.encode(params.get(key), StandardCharsets.UTF_8.toString()));

        }
        return queryParams.toString();
    }

    private String toJSON(){
        StringBuilder requestBody = new StringBuilder("{");
        Iterator<String> iterator = body.keySet().iterator();
        if(iterator.hasNext())
            requestBody.append(CRLF + iterator.next());
        while (iterator.hasNext()){
            String jsonElement = iterator.next();
            requestBody.append(requestBody.append("," + CRLF)
                    .append(jsonElement));

            requestBody.append(requestBody.append(":")
                    .append(body.get(jsonElement)));
        }
        requestBody.append(CRLF + "}");
        return requestBody.toString();
    }

    private void clearBody(){ // clear the body if it is a GET request method.
        if(httpRequestMethod.equals(HttpRequestMethod.GET))
            body = new HashMap<>();
        // else nothing.
    }

    private void clearQParams(){
        // for the get method.

        if(!httpRequestMethod.equals(HttpRequestMethod.GET))
            params = new HashMap<>();

    }
}
