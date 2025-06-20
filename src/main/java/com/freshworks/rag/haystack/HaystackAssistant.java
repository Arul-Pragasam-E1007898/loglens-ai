package com.freshworks.rag.haystack;

import com.freshworks.rag.haystack.tools.IndexerTool;
import com.freshworks.rag.haystack.util.ConsoleAssistantBot;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2q.AllMiniLmL6V2QuantizedEmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
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

    private static final String GEMINI_API_KEY = System.getenv("GEMINI_API_KEY");

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
        ChatLanguageModel chatModel = GoogleAiGeminiChatModel.builder()
                .modelName("gemini-2.0-flash")
                .apiKey(GEMINI_API_KEY)
                .build();

        EmbeddingStoreContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(100)
                .build();

        // Create RAG assistant
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(chatModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(1000))
                .contentRetriever(contentRetriever)
                .tools(tool)
                .build();

        // Ask questions about the documents
        ConsoleAssistantBot.chat(assistant::answer);
    }


}
