package com.kiran.koogagent.agent.tools

import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.ToolSet

class TransactionTools : ToolSet {
    @Tool
    @LLMDescription("Categorizes financial transactions for tax and budgeting. Returns category and potential tax deductions under Indian tax law.")
    fun categorizeTransaction(
        @LLMDescription("Transaction description (e.g., 'LIC Premium', 'Salary Credit', 'SIP Investment')")
        description: String,
        @LLMDescription("Transaction amount in INR")
        amount: Double
    ): String {
        val desc = description.lowercase()

        val category = when {
            desc.contains("salary") || desc.contains("income") || desc.contains("payroll") -> "Income - Salary"
            desc.contains("dividend") || desc.contains("stock") || desc.contains("mutual fund") -> "Income - Investment"
            desc.contains("freelance") || desc.contains("consulting") -> "Income - Business"
            desc.contains("rent") || desc.contains("emi") || desc.contains("loan") -> "Fixed Expenses"
            desc.contains("insurance") || desc.contains("premium") -> "Insurance"
            desc.contains("tax") || desc.contains("tds") || desc.contains("gst") -> "Tax Payment"
            desc.contains("investment") || desc.contains("sip") || desc.contains("fd") -> "Investment"
            desc.contains("medical") || desc.contains("hospital") || desc.contains("medicine") -> "Healthcare"
            desc.contains("education") || desc.contains("tuition") || desc.contains("course") -> "Education"
            amount > 100000 -> "Large Transaction - Review Required"
            else -> "Other"
        }

        val taxInfo = when {
            desc.contains("insurance") || desc.contains("medical") -> "Potentially 80D deductible"
            desc.contains("education") || desc.contains("tuition") -> "Potentially 80E deductible"
            desc.contains("home loan") || desc.contains("housing loan") -> "Potentially 24(b) & 80C deductible"
            desc.contains("investment") || desc.contains("ppf") || desc.contains("elss") -> "Potentially 80C deductible"
            else -> "Check with tax advisor"
        }

        return "Category: $category | Amount: ₹${String.format("%.2f", amount)} | Tax Info: $taxInfo"
    }
}
