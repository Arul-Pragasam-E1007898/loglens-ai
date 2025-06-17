package com.freshworks.rag.haystack.util;

import okhttp3.*;

import java.io.IOException;

public class RestClient {
    public Response query(String payload, String cookie) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, payload);
        Request.Builder request = new Request.Builder()
                .url("https://logs.haystack.es/internal/_msearch")
                .method("POST", body);

        Request req = addHeaders(request)
                .addHeader("Cookie", "userEmail=arul.pragasam@freshworks.com; HAYSAuthSessionID-0=" + cookie)
                .build();
        return client.newCall(req).execute();
    }

    private Request.Builder addHeaders(Request.Builder builder) {
        return builder.addHeader("accept", "*/*")
                .addHeader("accept-language", "en-US,en;q=0.9")
                .addHeader("content-type", "application/json")
                .addHeader("origin", "https://logs.haystack.es")
                .addHeader("osd-version", "6.1.5-SNAPSHOT")
                .addHeader("priority", "u=1, i")
                .addHeader("referer", "https://logs.haystack.es/app/discover")
                .addHeader("sec-ch-ua", "\"Chromium\";v=\"136\", \"Google Chrome\";v=\"136\", \"Not.A/Brand\";v=\"99\"")
                .addHeader("sec-ch-ua-mobile", "?0")
                .addHeader("sec-ch-ua-platform", "\"macOS\"")
                .addHeader("sec-fetch-dest", "empty")
                .addHeader("sec-fetch-mode", "cors")
                .addHeader("sec-fetch-site", "same-origin")
                .addHeader("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Safari/537.36")
                .addHeader("x-env", "production");
    }
}
