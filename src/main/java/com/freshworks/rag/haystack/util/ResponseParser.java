package com.freshworks.rag.haystack.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ResponseParser {

    public static List<String> parse(Response response) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(response.body().string());
        JsonNode hits = jsonNode.get("body").get("responses").get(0).get("hits").get("hits");
        return process((ArrayNode) hits);
    }

    private static List<String> process(ArrayNode hits) {
        List<String> collection = new ArrayList<>();
        for(int i=0;i<hits.size();i++) {
            JsonNode hit = hits.get(i);
            String message = hit.get("_source").get("message").textValue();
            String timestamp = hit.get("_source").get("@timestamp").textValue();
            collection.add(timestamp + " " + message);
        }
        return collection;
    }
}
