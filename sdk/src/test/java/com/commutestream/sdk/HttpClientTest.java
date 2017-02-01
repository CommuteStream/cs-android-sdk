package com.commutestream.sdk;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 23, manifest = "../sdk/src/main/AndroidManifest.xml")
public class HttpClientTest {

    @Test
    public void getAdSuccess() throws Exception {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "text/html")
                .setHeader("X-CS-AD-WIDTH", "320")
                .setHeader("X-CS-AD-HEIGHT", "50")
                .setHeader("X-CS-AD-KIND", "html")
                .setBody("<h1>banner</h1>"));
        HttpUrl baseUrl = server.url("/");
        HttpClient client = new HttpClient(baseUrl);
        AdRequest req = new AdRequest();
        AdResponseHandler handler = new AdResponseHandler() {
            @Override
            public void onFound(AdMetadata metadata, byte[] content) {
                assertThat(content.length, not(0));
                assertThat(metadata.clickUrl, not(isEmptyString()));
                assertThat(metadata.requestTime, greaterThan(0.0));
                synchronized (this) {
                    notifyAll();
                }
            }

            @Override
            public void onNotFound() {
                fail("Expected success response with an item returned got no match");
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
    public void getAdNotFound() throws Exception {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse()
                .setResponseCode(404));
        HttpUrl baseUrl = server.url("/");
        HttpClient client = new HttpClient(baseUrl);
        AdRequest req = new AdRequest();
        AdResponseHandler handler = new AdResponseHandler() {
            @Override
            public void onFound(AdMetadata metadata, byte[] content) {
                fail("Expected failure response");
                synchronized (this) {
                    notifyAll();
                }
            }

            @Override
            public void onNotFound() {
                synchronized (this) {
                    notifyAll();
                }
            }

            @Override
            public void onError(Throwable error) {
                fail("Expected Not Found response");
                assertThat(error, notNullValue());
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
    public void getAdTemporaryOutage() throws Exception {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse()
                .setResponseCode(503));
        HttpUrl baseUrl = server.url("/");
        HttpClient client = new HttpClient(baseUrl);
        AdRequest req = new AdRequest();
        AdResponseHandler handler = new AdResponseHandler() {
            @Override
            public void onFound(AdMetadata metadata, byte[] content) {
                fail("Expected not found response");
                synchronized (this) {
                    notifyAll();
                }
            }

            @Override
            public void onNotFound() {
                synchronized (this) {
                    notifyAll();
                }
            }

            @Override
            public void onError(Throwable error) {
                fail("Expected not found response");
                assertThat(error, notNullValue());
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
            public void onFound(AdMetadata metadata, byte[] content) {
                fail("Expected failure response");
                synchronized (this) {
                    notifyAll();
                }
            }

            @Override
            public void onNotFound() {
                fail("Expected failure response");
                synchronized (this) {
                    notifyAll();
                }
            }

            @Override
            public void onError(Throwable error) {
                assertThat(error, notNullValue());
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
