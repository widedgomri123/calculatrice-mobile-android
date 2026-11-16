
package com.example.calcul

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.mozilla.javascript.Context
import org.mozilla.javascript.Scriptable

class CalculatorViewModel : ViewModel() {


    private val _equationText = MutableLiveData("")

    val equationText: LiveData<String> = _equationText

    // Result
    private val _resultText = MutableLiveData("0")

    val resultText: LiveData<String> = _resultText

    // Button Click
    fun onButtonClick(btn: String) {

        Log.i("Clicked Button", btn)

        val currentEquation = _equationText.value.orEmpty()

        when (btn) {

            // Clear everything
            "AC" -> {
                _equationText.value = ""
                _resultText.value = "0"
            }

            // Delete last character
            "C" -> {
                if (currentEquation.isNotEmpty()) {
                    _equationText.value =
                        currentEquation.dropLast(1)
                }
            }

            // Calculate result
            "=" -> {
                if (currentEquation.isNotEmpty()) {
                    try {
                        _resultText.value =
                            calculateResult(currentEquation)

                    } catch (e: Exception) {
                        _resultText.value = "Erreur"
                        Log.e("Calculator", "Calculation error", e)
                    }
                }
            }

            // Add number or operator
            else -> {
                _equationText.value = currentEquation + btn
            }
        }
    }

    // Calculate Result
    fun calculateResult(equation: String): String {

        require(
            equation.matches(Regex("[0-9+\\-*/(). ]+"))
        ) {
            "Invalid expression"
        }

        val context = Context.enter()

        try {

            context.optimizationLevel = -1

            val scriptable: Scriptable =
                context.initStandardObjects()

            val finalResult = context.evaluateString(
                scriptable,
                equation,
                "JavaScript",
                1,
                null
            ).toString()

            return if (finalResult.endsWith(".0")) {
                finalResult.removeSuffix(".0")
            } else {
                finalResult
            }

        } finally {
            Context.exit()
        }
    }
}
