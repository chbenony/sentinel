package dev.sentinel;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Optional;
import java.util.function.LongSupplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CheckHandlerTest {
    private HttpServer server;
    private LongSupplier clock = () -> 0L;

    Policy policy = new Policy(10, 1.0);
    PolicyKey policyKey = new PolicyKey("app_1", "res_1");

    PolicyRegistry registry = new InMemoryPolicyRegistry(Map.of(policyKey, policy));
    InMemoryBucketStore store = new InMemoryBucketStore(clock);

    RateLimiter rateLimiter = new RateLimiter(registry, store);


    @BeforeEach
    void setup() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/v1/limits/check", new CheckHandler(rateLimiter, JsonMapper.builder().build()));
        server.start();
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
    }

    @Test
    void allowedRequestReturns200() throws IOException, InterruptedException {
        String url = "http://127.0.0.1:" + server.getAddress().getPort() + "/v1/limits/check";
        String jsonBody = """
                {
                    "application": "app_1",
                    "resource": "res_1",
                    "principal": "principal_1",
                    "cost": 1
                }
                """;
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    void malformedJsonReturns400() throws IOException, InterruptedException {
        String url = "http://127.0.0.1:" + server.getAddress().getPort() + "/v1/limits/check";
        String jsonBody = """
                {
                    "not json"
                }
                """;
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void unknownPolicyReturns404() throws IOException, InterruptedException {
        String url = "http://127.0.0.1:" + server.getAddress().getPort() + "/v1/limits/check";
        String jsonBody = """
                {
                    "application": "app_2",
                    "resource": "res_1",
                    "principal": "principal_1",
                    "cost": 1
                }
                """;
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    void invalidCostReturns400() throws IOException, InterruptedException {
        String url = "http://127.0.0.1:" + server.getAddress().getPort() + "/v1/limits/check";
        String jsonBody = """
                {
                    "application": "app_1",
                    "resource": "res_1",
                    "principal": "principal_1",
                    "cost": 0
                }
                """;
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void getRequestReturns405() throws IOException, InterruptedException {
        String url = "http://127.0.0.1:" + server.getAddress().getPort() + "/v1/limits/check";
        String jsonBody = """
                {
                    "application": "app_1",
                    "resource": "res_1",
                    "principal": "principal_1",
                    "cost": 0
                }
                """;
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(Optional.of("POST"), response.headers().firstValue("Allow"));
        assertEquals(405, response.statusCode());
    }

    @Test
    void missingPrincipalReturns400() throws IOException, InterruptedException {
        String url = "http://127.0.0.1:" + server.getAddress().getPort() + "/v1/limits/check";
        String jsonBody = """
                {
                    "application": "app_1",
                    "resource": "res_1",
                    "cost": 10
                }
                """;
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void missingApplicationReturns400() throws IOException, InterruptedException {
        String url = "http://127.0.0.1:" + server.getAddress().getPort() + "/v1/limits/check";
        String jsonBody = """
                {
                    "principal": "princ_1",
                    "resource": "res_1",
                    "cost": 10
                }
                """;
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void missingResourceReturns400() throws IOException, InterruptedException {
        String url = "http://127.0.0.1:" + server.getAddress().getPort() + "/v1/limits/check";
        String jsonBody = """
                {
                    "principal": "princ_1",
                    "application": "app_1",
                    "cost": 10
                }
                """;
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void missingCostReturns400() throws IOException, InterruptedException {
        String url = "http://127.0.0.1:" + server.getAddress().getPort() + "/v1/limits/check";
        String jsonBody = """
                {
                    "principal": "princ_1",
                    "application": "app_1",
                    "resource": "res_1"
                }
                """;
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void nullBodyReturns400() throws IOException, InterruptedException {
        String url = "http://127.0.0.1:" + server.getAddress().getPort() + "/v1/limits/check";
        String jsonBody = "null";
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }
}
