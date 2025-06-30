package com.freshworks.rag.haystack;

import com.freshworks.rag.haystack.tools.IndexerTool;
import com.freshworks.rag.haystack.util.ConsoleAssistantBot;
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

import java.io.IOException;

/**
 * Haystack Assistant - Console Application
 * 
 * This class provides a console-based interface for the Haystack Assistant.
 * For a web-based interface, run the Spring Boot application using HaystackApplication.
 * 
 * Web Interface: http://localhost:8080
 * Console Interface: Run this main method
 */
public class HaystackAssistant {

    private static final String key = System.getenv("CLOUDVERSE_TOKEN");

    public static void main(String[] args) throws IOException {
        System.out.println("=== Haystack Assistant Console ===");
        System.out.println("Note: A web interface is also available at http://localhost:8080");
        System.out.println("Run the Spring Boot application (HaystackApplication) to access the web UI.");
        System.out.println("=====================================\n");
        
        // Initialize embedding model
        EmbeddingModel embeddingModel = new AllMiniLmL6V2QuantizedEmbeddingModel();
        // Create embedding store
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        IndexerTool tool = new IndexerTool(embeddingModel, embeddingStore);

        // Initialize Ollama model
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
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(openAiChatModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(1000))
                .contentRetriever(contentRetriever)
                .tools(tool)
                .build();

        // Ask questions about the documents
        ConsoleAssistantBot.chat(assistant::answer);
    }


}
