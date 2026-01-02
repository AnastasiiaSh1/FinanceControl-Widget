package com.hfad.widgetlight

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class BalanceWidget : AppWidgetProvider() {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        scope.launch {
            val balance = readBalanceFromFile(context)

            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId, balance)
            }
        }
    }
}

private fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
    balance: String
) {
    val views = RemoteViews(context.packageName, R.layout.widget_balance_layout)

    views.setTextViewText(R.id.balance_text, "Баланс: $balance")

    val intent = Intent(context, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent)

    appWidgetManager.updateAppWidget(appWidgetId, views)
}