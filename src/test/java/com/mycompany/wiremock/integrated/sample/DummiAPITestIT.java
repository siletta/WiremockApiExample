/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.wiremock.integrated.sample;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import org.testng.annotations.Test;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Assert;

/**
 *
 * @author adistratis
 */
public class DummiAPITestIT {
    WireMockServer wireMockServer = new WireMockServer(wireMockConfig().port(8089));
    @Test
    public void dummyApiTestMethod() throws IOException {
        //int port = wireMockRule.port();
        WireMock.configureFor("localhost", 8089);
        wireMockServer.start();

        System.out.println("Port is <<<<<<<: " + wireMockServer.port());

        createAStub(200, "Hello, I am a happy mock!");
       
        HttpResponse httpResponse = makeAnHTTPRequest("http://localhost:8089/PIPPO");
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        String body = convertInputStreamToString(httpResponse.getEntity().getContent());
        System.out.println("Body is: " + body + "status code is " + statusCode);
        Assert.assertEquals(200, statusCode);
        Assert.assertEquals("Hello, I am a happy mock!", body);
        
        wireMockServer.stop();
    }

    private String convertInputStreamToString(InputStream content) {
        Scanner scanner = new Scanner(content, "UTF-8").useDelimiter("\\Z");
        return scanner.hasNext() ? scanner.next() : "";    
    }
    
    private void createAStub(int statusCode, String aMessage) {
        stubFor(get(urlPathMatching("/PIPPO"))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", "application/json")
                        .withBody(aMessage)));
    }
    
    private HttpResponse makeAnHTTPRequest(String url) throws IOException{
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(url);
        HttpResponse httpResponse = httpClient.execute(request);
        return httpResponse;
    }
}
