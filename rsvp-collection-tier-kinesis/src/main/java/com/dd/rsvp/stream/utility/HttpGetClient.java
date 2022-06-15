package com.dd.rsvp.stream.utility;

import com.dd.rsvp.stream.exception.ApplicationException;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.io.InputStream;

public class HttpGetClient {

    private HttpClient httpClient;

    public HttpGetClient() {
        this.httpClient = HttpClientBuilder.create().build();
    }

    public HttpGetClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public String get(String url, Header[] headers) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeaders(headers);

        HttpResponse response = httpClient.execute(httpGet);
        int statusCode = response.getStatusLine().getStatusCode();

        if (statusCode != HttpStatus.SC_OK) {
            throw new ApplicationException("Get api call failed with status code " + statusCode, null);
        }

        InputStream content = response.getEntity().getContent();
        return IOUtils.toString(content, "UTF-8");
    }
}
