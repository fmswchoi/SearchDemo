package core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
public class HttpLoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final String REQUEST_PREFIX = "==>";
    private static final String RESPONSE_PREFIX = "<==";

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        requestLogging(request, body);

        ClientHttpResponse response = execution.execute(request, body);

        return responseLogging(response);
    }

    private ClientHttpResponse responseLogging(ClientHttpResponse response) throws IOException {
        ClientHttpResponse wrapper = new BufferingClientHttpResponseWrapper(response);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(wrapper.getBody(), UTF_8))) {
            HttpHeaders resHeaders = wrapper.getHeaders();
            log.debug("{} {}, headers={}", RESPONSE_PREFIX, wrapper.getStatusCode(), resHeaders);

            String resBody = reader.lines().collect(Collectors.joining("\n"));
            log.debug("body={}", resBody);
        }

        return wrapper;
    }

    private void requestLogging(HttpRequest request, byte[] body) {
        HttpHeaders reqHeaders = request.getHeaders();
        String reqBody = new String(body, UTF_8);
        log.debug("{} {} {}, headers={}", REQUEST_PREFIX, request.getMethod(), request.getURI(), reqHeaders);
        log.debug("body={}", reqBody);
    }

    private static class BufferingClientHttpResponseWrapper implements ClientHttpResponse {
        private final ClientHttpResponse response;

        @Nullable
        private byte[] body;

        public BufferingClientHttpResponseWrapper(ClientHttpResponse response) {
            this.response = response;
        }

        @Override
        public HttpStatus getStatusCode() throws IOException {
            return this.response.getStatusCode();
        }

        @Override
        public int getRawStatusCode() throws IOException {
            return this.response.getRawStatusCode();
        }

        @Override
        public String getStatusText() throws IOException {
            return this.response.getStatusText();
        }

        @Override
        public HttpHeaders getHeaders() {
            return this.response.getHeaders();
        }

        @Override
        public InputStream getBody() throws IOException {
            if (this.body == null) {
                this.body = StreamUtils.copyToByteArray(this.response.getBody());
            }
            return new ByteArrayInputStream(this.body);
        }

        @Override
        public void close() {
            this.response.close();
        }
    }
}
