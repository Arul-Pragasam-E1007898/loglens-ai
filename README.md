# RAG-Based Log Analysis System
## Core Concept
A system that indexes logs by trace ID in a vector store, enabling users to analyze application logs through natural language queries powered by LLMs.

## Architecture
Log Retrieval → Vector Indexing → RAG Retrieval → LLM Analysis

## Key Components
* Trace Indexer: Retrieves logs from Haystack based on a given trace ID.
* Vector Store: Embeds log content semantically, enabling similarity-based retrieval. Stores both original logs and their vector representations linked to trace IDs.
* RAG Engine: Retrieves relevant log segments based on user queries. Combines exact trace matches with semantic similarity search.
* LLM Interface: Analyzes retrieved logs to answer user questions, identify patterns, and provide insights about system behavior.

## Capabilities
* Natural Language Queries: "What caused errors?" or "Show slow queries executed more than a second"
* Root Cause Analysis: Connects symptoms to underlying causes across distributed systems

## Key Benefits
* Intelligent Search: Semantic retrieval finds relevant logs beyond keyword matching
* Rapid Troubleshooting: Natural language interface reduces time to insight
* Pattern Recognition: Detects systemic issues invisible in isolated log entries

## Use Cases
* Incident Response: Quick root cause analysis during outages
* Performance Monitoring: Identify slow operations and bottlenecks
* Operational Intelligence: Understand system behavior and usage patterns

## Implementation Essentials
* Log ingestion with trace ID extraction
* Efficient vector storage and retrieval mechanisms
* Secure access controls for sensitive log data
* This system transforms log analysis from manual log-diving into intelligent, conversational troubleshooting.


## Technology Stack

- **Backend**: LangChain, Java
- **Large Language Model**: Gemini (Pluggable)
- **Build Tool**: Maven

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Gemini API Key

## Setup Instructions

### 1. Clone the Repository

```bash
git clone <repository-url>
cd loglens-ai
```

### 2. Configure OpenAI API Key

You have several options to set your Gemini API key:

#### Option A: Environment Variable (Recommended)
```bash
export GEMINI_API_KEY=your-actual-gemini-api-key-here
```

Grab your keys from https://aistudio.google.com/
