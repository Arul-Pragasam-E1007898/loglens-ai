package com.freshworks.rag.haystack;

import com.freshworks.rag.haystack.tools.IndexerTool;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2q.AllMiniLmL6V2QuantizedEmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;

@Service
public class HaystackService {

    @Value("${gemini.api.key:#{environment.GEMINI_API_KEY}}")
    private String geminiApiKey;

    private Assistant assistant;
    private IndexerTool indexerTool;

    @PostConstruct
    public void initialize() {
        // Initialize embedding model
        EmbeddingModel embeddingModel = new AllMiniLmL6V2QuantizedEmbeddingModel();
        
        // Create embedding store
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        // Initialize indexer tool
        indexerTool = new IndexerTool(embeddingModel, embeddingStore);

        // Initialize Gemini model
        ChatLanguageModel chatModel = GoogleAiGeminiChatModel.builder()
                .modelName("gemini-2.0-flash")
                .apiKey(geminiApiKey)
                .build();

        EmbeddingStoreContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(100)
                .build();

        // Create RAG assistant
        assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(chatModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(1000))
                .contentRetriever(contentRetriever)
                .tools(indexerTool)
                .build();
    }

    public String askQuestion(String question) {
        return assistant.answer(question);
    }

    public void indexLogs(String query) throws IOException {
        indexerTool.index(query);
    }

    public void clearLogs() {
        indexerTool.clear();
    }
} 