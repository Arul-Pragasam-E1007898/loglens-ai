package com.freshworks.rag.haystack;

import com.freshworks.rag.haystack.tools.IndexerTool;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2q.AllMiniLmL6V2QuantizedEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class HaystackService {

    private static final String key = System.getenv("CLOUDVERSE_TOKEN");

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
        ChatModel openAiChatModel = OpenAiChatModel.builder()
                .baseUrl("https://cloudverse.freshworkscorp.com/api/v1")
                .modelName("Azure-GPT-4.1")
                .apiKey(key)
                .build();

        EmbeddingStoreContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(100)
                .build();

        // Create RAG assistant
        assistant = AiServices.builder(Assistant.class)
                .chatModel(openAiChatModel)
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