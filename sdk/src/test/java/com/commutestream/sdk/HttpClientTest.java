package com.commutestream.sdk;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@RunWith(JUnit4.class)
public class HttpClientTest {

    @Test
    public void getAdSuccess() throws Exception {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("{\"item_returned\":true, \"html\":\"<html></html>\", \"url\":\"https://google.com\"}"));
        HttpUrl baseUrl = server.url("/");
        HttpClient client = new HttpClient(baseUrl);
        AdRequest req = new AdRequest();
        AdResponseHandler handler = new AdResponseHandler() {
            @Override
            public void onSuccess(AdMetadata metadata, byte[] content) {
                assertThat(content).isNotEmpty();
                assertThat(metadata.clickUrl).isNotEmpty();
                assertThat(metadata.requestTime).isGreaterThan(0.0);
                synchronized (this) {
                    notifyAll();
                }
            }

            @Override
            public void onError(Throwable error) {
                fail("Expected success response with an item returned got: " + error.toString());
                synchronized (this) {
                    notifyAll();
                }
            }
        };
        client.getAd(req, handler);
        synchronized (handler) {
            handler.wait(200);
        }
    }

    @Test
    public void getAdNone() throws Exception {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("{\"item_returned\":false}"));
        HttpUrl baseUrl = server.url("/");
        HttpClient client = new HttpClient(baseUrl);
        AdRequest req = new AdRequest();
        AdResponseHandler handler = new AdResponseHandler() {
            @Override
            public void onSuccess(AdMetadata metadata, byte[] content) {
                assertThat(metadata.requestTime).isGreaterThan(0.0);
                synchronized (this) {
                    notifyAll();
                }
            }

            @Override
            public void onError(Throwable error) {
                fail("Expected success response without an item returned got: " + error.toString());
                synchronized (this) {
                    notifyAll();
                }
            }
        };
        client.getAd(req, handler);
        synchronized (handler) {
            handler.wait(200);
        }
    }

    @Test
    public void getAdError() throws Exception {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse()
                .setResponseCode(500));
        HttpUrl baseUrl = server.url("/");
        HttpClient client = new HttpClient(baseUrl);
        AdRequest req = new AdRequest();
        AdResponseHandler handler = new AdResponseHandler() {
            @Override
            public void onSuccess(AdMetadata metadata, byte[] content) {
                fail("Expected failure response");
                synchronized (this) {
                    notifyAll();
                }
            }

            @Override
            public void onError(Throwable error) {
                assertThat(error).isNotNull();
                synchronized (this) {
                    notifyAll();
                }
            }
        };
        client.getAd(req, handler);
        synchronized (handler) {
            handler.wait(200);
        }
    }
}
