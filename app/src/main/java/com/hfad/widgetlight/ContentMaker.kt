package com.hfad.widgetlight

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun getContent(){
    var income = remember{mutableStateOf("")}
    var expense = remember { mutableStateOf("") }
    var balance = remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val borderColor = remember { mutableStateOf(Color(red = 25, green = 25, blue = 112)) }

    LaunchedEffect(Unit) {
        val savedBalance = readBalanceFromFile(context)
        balance.value = savedBalance
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(id = R.drawable.background_image),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.fillMaxWidth(0.8f)
                .background(Color.White.copy(alpha = 0.95f), shape = RoundedCornerShape(24.dp))
                .padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "Balance:", fontSize = 38.sp)
            Text(
                text = balance.value, fontSize = 50.sp, modifier = Modifier.padding(bottom = 30.dp)
                    .border(
                        width = 4.dp,
                        color = borderColor.value,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(10.dp)
            )

            Text("Income:", fontSize = 28.sp)
            TextField(
                income.value,
                { income.value = it },
                modifier = Modifier.clip(RoundedCornerShape(percent = 50)),
                textStyle = TextStyle(fontSize = 28.sp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color(
                        red = 65,
                        green = 105,
                        blue = 225
                    ).copy(alpha = 0.5f),
                    focusedContainerColor = Color(
                        red = 65,
                        green = 105,
                        blue = 225
                    ).copy(alpha = 0.5f),
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                    }
                )
            )
            Text("Expense:", fontSize = 28.sp)
            TextField(
                expense.value,
                { expense.value = it },
                modifier = Modifier.clip(RoundedCornerShape(percent = 50)),
                textStyle = TextStyle(fontSize = 28.sp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Red.copy(alpha = 0.5f),
                    focusedContainerColor = Color.Red.copy(alpha = 0.5f),
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                    }
                )
            )
            Button(
                onClick = {
                    val newBalance = calculateDifference(
                        income = income.value,
                        expense = expense.value
                    )

                    val currentBalance = balance.value.toDoubleOrNull() ?: 0.0
                    balance.value = (newBalance + currentBalance).toCurrencyFormat()

                    if (balance.value.toDouble() <= 0)
                        borderColor.value = Color.Red
                    else
                        borderColor.value = Color(red = 25, green = 25, blue = 112)

                    coroutineScope.launch {
                        writeBalanceToFile(context, balance.value)
                        updateWidget(context)
                    }
                    income.value = ""
                    expense.value = ""
                },
                modifier = Modifier.padding(top = 26.dp)
            ) {
                Text(text = "OK")
            }
        }
    }
}