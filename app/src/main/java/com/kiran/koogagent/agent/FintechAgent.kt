package com.kiran.koogagent.agent

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.reflect.asTools
import ai.koog.prompt.executor.clients.openai.OpenAIClientSettings
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.executor.llms.MultiLLMPromptExecutor
import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel
import com.kiran.koogagent.agent.tools.TransactionTools
import kotlin.time.ExperimentalTime

object FintechAgent {

    private val GROQ_MODEL = LLModel(
        provider = LLMProvider.OpenAI,
        id = "llama-3.3-70b-versatile",
        capabilities = listOf(
            LLMCapability.Completion,
            LLMCapability.Tools,
            LLMCapability.ToolChoice,
            LLMCapability.Temperature,
            LLMCapability.OpenAIEndpoint.Completions
        ),
        contextLength = 131072,
        maxOutputTokens = 32768
    )

    private const val SYSTEM_PROMPT =
        "You are a professional financial advisor AI assistant specializing in Indian taxation, " +
        "investments, and financial planning. You help with ITR filing queries, tax-saving strategies, " +
        "investment advice, stock market insights, and general financial guidance. " +
        "Provide accurate, actionable advice following Indian tax laws and regulations."

    @OptIn(ExperimentalTime::class)
    fun create(apiKey: String): AIAgent<String, String> {
        val groqClient = OpenAILLMClient(
            apiKey = apiKey,
            settings = OpenAIClientSettings(
                baseUrl = "https://api.groq.com",
                chatCompletionsPath = "openai/v1/chat/completions"
            )
        )

        val toolList = TransactionTools().asTools()

        return AIAgent(
            promptExecutor = MultiLLMPromptExecutor(groqClient),
            systemPrompt = SYSTEM_PROMPT,
            llmModel = GROQ_MODEL,
            toolRegistry = ToolRegistry {
                toolList.forEach { tool(it) }
            },
            maxIterations = 10
        )
    }
}
