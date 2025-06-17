package com.freshworks.rag.haystack.tools;

import com.freshworks.rag.haystack.HaystackAssistant;
import com.freshworks.rag.haystack.util.ResponseParser;
import com.freshworks.rag.haystack.util.RestClient;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import okhttp3.Response;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

public class IndexerTool {
    private static final String COOKIE = System.getenv("HAYSTACK_COOKIE");

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> store;

    public IndexerTool(EmbeddingModel embeddingModel, EmbeddingStore<TextSegment> store) {
        this.embeddingModel = embeddingModel;
        this.store = store;
    }

    @Tool(name = "LogIndexer")
    public void index(String query) throws IOException {
        RestClient restClient = new RestClient();
        String payload = load("./query.json");
        payload = payload.replace("%Query%", query);
        payload = payload.replace("%GTE%", Instant.now().minus(24, ChronoUnit.HOURS).toString());
        payload = payload.replace("%LTE%", Instant.now().toString());

        Response response = restClient.query(payload, COOKIE);
        List<String> logLines = ResponseParser.parse(response);
        List<Document> logDocuments = logLines.stream().map(Document::from).collect(Collectors.toList());
        index(logDocuments);
        System.out.println("Indexed " + logLines.size() + " documents related to - " + query);
    }

    @Tool(name = "ClearLogs")
    public void clear() {
        store.removeAll();
    }

    private void index(List<Document> logLines) {
        DocumentSplitter splitter = DocumentSplitters.recursive(500, 50);

        // Process all documents
        for (Document line : logLines) {
            List<TextSegment> segments = splitter.split(line);
            List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
            store.addAll(embeddings, segments);
        }
    }

    public static String load(String file) {
        try (InputStream inputStream = HaystackAssistant.class.getClassLoader()
                .getResourceAsStream(file)) {

            if (inputStream == null) {
                throw new IllegalArgumentException("file not found: " + file);
            }

            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load : " + file, e);
        }
    }
}
