package com.hfad.widgetlight
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

fun Double.toCurrencyFormat(): String {
    return String.format(Locale.US, "%.2f", this)
}

suspend fun readBalanceFromFile(context: Context): String {
    val fileName = "balance.txt"
    val file = File(context.filesDir, fileName)

    return withContext(Dispatchers.IO) {
        if (file.exists()) {
            try {
                val content = FileInputStream(file).bufferedReader().use { it.readText() }

                val cleanContent = content.replace(',', '.')

                cleanContent.toDoubleOrNull()?.toCurrencyFormat() ?: "0.00"
            } catch (e: IOException) {
                "0.00"
            }
        } else {
            "0.00"
        }
    }
}

suspend fun writeBalanceToFile(context: Context, data: String) {
    val fileName = "balance.txt"
    val file = File(context.filesDir, fileName)

    withContext(Dispatchers.IO) {
        try {
            FileOutputStream(file).use {
                it.write(data.toByteArray())
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

fun calculateDifference(income: String, expense: String): Double {
    val cleanIncome = income.replace(',', '.')
    val cleanExpense = expense.replace(',', '.')

    val incomeValue = cleanIncome.toDoubleOrNull() ?: 0.0
    val expenseValue = cleanExpense.toDoubleOrNull() ?: 0.0

    return incomeValue - expenseValue
}
fun updateWidget(context: Context) {
    val appWidgetManager = AppWidgetManager.getInstance(context)
    val componentName = ComponentName(context, BalanceWidget::class.java)

    val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)

    if (appWidgetIds.isNotEmpty()) {
        val intent = Intent(context, BalanceWidget::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
        }
        context.sendBroadcast(intent)
    }
}