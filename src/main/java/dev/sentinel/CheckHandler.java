package dev.sentinel;

import com.sun.net.httpserver.*;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.io.*;

public class CheckHandler implements HttpHandler {
    private final RateLimiter rateLimiter;
    private final JsonMapper jsonMapper;

    public CheckHandler(RateLimiter rateLimiter, JsonMapper jsonMapper) {
        this.rateLimiter = rateLimiter;
        this.jsonMapper = jsonMapper;
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            InputStream inputStream = exchange.getRequestBody();
            CheckRequest request = jsonMapper.readValue(inputStream, CheckRequest.class);

            LimitKey limitKey = new LimitKey(request.application(), request.resource(), request.principal());
            Decision decisionReq = rateLimiter.check(limitKey, request.cost());

            CheckResponse checkResponse = CheckResponse.from(decisionReq);
            byte[] responseBytes = jsonMapper.writeValueAsBytes(checkResponse);

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, responseBytes.length);


            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(responseBytes);
            }
        } catch (JacksonException | IllegalArgumentException e){
            exchange.sendResponseHeaders(400, -1);
            exchange.close();
        } catch (UnknownPolicyException e) {
            exchange.sendResponseHeaders(404, -1);
            exchange.close();
        }

    }
}
