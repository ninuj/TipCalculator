/*
 * Copyright (c) 2022 Razeware LLC
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 * 
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.androidchef.android.tipcalculator

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.androidchef.android.tipcalculator.databinding.ActivityMainBinding
import java.text.NumberFormat
import kotlin.math.roundToInt

/**
 * Main Screen
 */
class MainActivity : AppCompatActivity() {

  private lateinit var binding: ActivityMainBinding
  var currentBillAmount = DEFAULT_MIN_AMOUNT

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    // initialize the listeners
    with(binding) {
      minusButton.setOnClickListener { decrementTipPercentage() }
      plusButton.setOnClickListener { incrementTipPercentage() }
      billAmountInputEditTextView.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
          currentBillAmount = if (!s.isNullOrEmpty() && s.toString() != ".") s.toString().toDouble() else DEFAULT_MIN_AMOUNT
          calculateTipAmount(billAmount = currentBillAmount)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
      })
    }
    updateTotalBillAmount()
  }

  private fun incrementTipPercentage() {
    val tipPercentage = binding.tipPercentageValueTextView.text.toString().toInt() + 1
    updateTipPercentage(tipPercentage)
  }

  private fun decrementTipPercentage() {
    val tipPercentage = binding.tipPercentageValueTextView.text.toString().toInt() - 1
    updateTipPercentage(tipPercentage)
  }

  private fun updateTipPercentage(tipPercentage: Int) {
    if (tipPercentage in 0..100) {
      binding.tipPercentageValueTextView.text = tipPercentage.toString()
      calculateTipAmount(tipPercentage.toDouble())
    }
  }

  private fun calculateTipAmount(tipPercentage: Double = binding.tipPercentageValueTextView.text.toString().toDouble(),
                                 billAmount: Double = currentBillAmount) {
    val tipAmountRounded = ((tipPercentage / 100) * billAmount).also {
      roundToTwoDecimalPlaces(it)
    }
    val billAmountRounded = roundToTwoDecimalPlaces(billAmount)
    updateTotalBillAmount(tipAmountRounded, billAmountRounded)
  }

  private fun updateTotalBillAmount(tipAmount: Double = DEFAULT_MIN_AMOUNT, billAmount: Double = DEFAULT_MIN_AMOUNT) {
    val totalBillAmount = tipAmount + billAmount
    with(binding) {
      tipAmountTextView.text = getString(R.string.tip_amount, NumberFormat.getCurrencyInstance().format(tipAmount))
      totalBillTextView.text = getString(R.string.total_bill, NumberFormat.getCurrencyInstance().format(totalBillAmount))
    }
  }

  private fun roundToTwoDecimalPlaces(number: Double): Double = ((number * 100.0).roundToInt()) / 100.0

  companion object {
    const val DEFAULT_MIN_AMOUNT = 0.00
  }
}
