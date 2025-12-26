package com.example.cw5_calculator

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder
import kotlin.math.E
import kotlin.math.PI

class MainActivity : AppCompatActivity() {
    private val operators: Array<Char> = arrayOf('+', '-', '*', '/', '%')
    private val seps: Array<String> = arrayOf("+", "-", "*", "/", "%", "(", ")", "sin(", "cos(", "tan", "log10(", "log(", "exp(")
    private var memoryVar: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun clearErr()
    {
        if (findViewById<TextView>(R.id.num_text).text.toString() == "Ошибка выражения")
            findViewById<TextView>(R.id.num_text).text = "0"
    }
    fun isOperator(op: Char): Boolean
    {
        var isOp: Boolean = false
        if (operators.contains(op))
            isOp = true
        return isOp
    }

    fun strIsNum(str: String): Boolean
    {
        var isNum: Boolean = false
        try {
            val num: Short = str.toShort()
            isNum = true
        }
        catch (e: Exception) {
            isNum = false
        }
        return isNum
    }

    fun insertChar(originalString: String, index: Int, charToInsert: Char): String {
        if (index < 0 || index > originalString.length) {
            return originalString
        }
        val part1 = originalString.substring(0, index)
        val part2 = originalString.substring(index)
        return (part1 + charToInsert + part2).trim()
    }

    fun onNumClick(sender: View)
    {
        clearErr()
        val numTextBox: TextView = findViewById(R.id.num_text)
        val num: String = findViewById<Button>(sender.id).text.toString()
        val numTxt: String = numTextBox.text.toString()
        val lastChr: Int = numTxt.length - 1

        if (numTxt == "0" || numTxt == "") {
            numTextBox.text = num
        }
        else if (numTxt[lastChr] != ')') {
            numTextBox.text = numTxt.plus(num)
        }
        return
    }
    fun addOperator(op: Char)
    {
        val numTextBox: TextView = findViewById(R.id.num_text)
        var numTxt: String = numTextBox.text.toString()
        if (numTxt.isEmpty())
            return
        val lastChr: Int = numTxt.length - 1
        val isOp: Boolean = isOperator(numTxt[lastChr])

        if (numTxt[lastChr] != op && !isOp)
            numTextBox.text = numTxt.plus(op)
        else if (isOp)
        {
            numTxt = numTxt.substring(0, lastChr)
            numTextBox.text = numTxt.plus(op)
        }
    }

    fun onOpClick(sender: View)
    {
        clearErr()
        val btnSender: Button = findViewById(sender.id)
        val op: Char = btnSender.text[0]
        addOperator(op)
    }

    fun isInt(num: String): Boolean
    {
        var isInt: Boolean = false
        try {
            val intNum: Int = num.toInt()
            isInt = true
        }
        catch (e: Exception)
        {
           isInt = false
        }
        return isInt
    }

    fun isDouble(num: String): Boolean
    {
        var isDouble: Boolean = false
        try {
            val intNum: Double = num.toDouble()
            isDouble = true
        }
        catch (e: Exception)
        {
            isDouble = false
        }
        return isDouble
    }

    fun eval(expression: String): String {

        var result: String? = ""
        try {
            val ex: Expression = ExpressionBuilder(expression).build()
            result = ex.evaluate().toString()
            if (isInt(result))
                result = result.toDouble().toInt().toString()
        }
        catch (e: ArithmeticException)
        {
            result = e.localizedMessage?.toString()
        }
        catch (e: Exception)
        {
            result = "Ошибка выражения"
        }
        if (result == null)
            result = ""
        return result
    }
    fun funCalc(): Double
    {
        var result: String = eval(findViewById<TextView>(R.id.num_text).text.toString()).toString()
        if (isDouble(result))
            return result.toDouble()
        else
            return 0.0
    }
    fun calc()
    {
        findViewById<TextView>(R.id.calc_text).text = findViewById<TextView>(R.id.num_text).text
        var result: String = eval(findViewById<TextView>(R.id.num_text).text.toString()).toString()
        findViewById<TextView>(R.id.num_text).text = result
    }

    fun insertFunc(fnc: String)
    {
        findViewById<TextView>(R.id.num_text).text = findViewById<TextView>(R.id.num_text).text.toString() + fnc + "("
    }

    fun addSeparator()
    {
        val regexString: String = seps.joinToString("|") { Regex.escape(it.toString()) }
        val regex = Regex(regexString)
        val expression: Array<String> = findViewById<TextView>(R.id.num_text).text.toString().split(regex).toTypedArray()
        if (strIsNum(expression[expression.lastIndex]))
            findViewById<TextView>(R.id.num_text).text = findViewById<TextView>(R.id.num_text).text.toString().plus('.')
    }

    fun UnaryMP()
    {
        val regexString: String = seps.joinToString("|") { Regex.escape(it.toString()) }
        val regex = Regex(regexString)
        val expression: Array<String> = findViewById<TextView>(R.id.num_text).text.toString().split(regex).toTypedArray()
        if (isDouble(expression[expression.lastIndex]))
        {
            var unChr: Char = '-'
            try
            {
                if (findViewById<TextView>(R.id.num_text).text.toString()[findViewById<TextView>(R.id.num_text).text.toString().lastIndexOf(expression[expression.lastIndex]) - 1] == '-')
                    unChr = ' '
            }
            catch (e: Exception)
            {
                unChr = '-'
            }
            val funcStr: String = (unChr + expression[expression.lastIndex]).trim()
            if (unChr == '-')
                findViewById<TextView>(R.id.num_text).text = findViewById<TextView>(
                    R.id.num_text).text.toString().substring(0, findViewById<TextView>(R.id.num_text).text.toString().lastIndexOf(expression[expression.lastIndex])) + funcStr
            else
                findViewById<TextView>(
                    R.id.num_text).text = findViewById<TextView>(R.id.num_text).text.toString().substring(0,
                    findViewById<TextView>(R.id.num_text).text.toString().lastIndexOf(expression[expression.lastIndex]) - 1) + funcStr
        }
    }

    fun onFuncClick(sender: View)
    {
        clearErr()
        val numTextBox: TextView = findViewById(R.id.num_text)
        val command: String = findViewById<Button>(sender.id).text.toString()
        when(command)
        {
            "C" ->
            {
                numTextBox.text = ""
                findViewById<TextView>(R.id.calc_text).text = " "
            }
            "⌫" -> if (!numTextBox.text.isEmpty()) numTextBox.text = numTextBox.text.substring(0, numTextBox.text.length - 1)
            "." -> addSeparator()
            "√" -> insertFunc("sqrt")
            "±" -> UnaryMP()
            "MS" -> memoryVar = funCalc()
            "MC" -> memoryVar = 0.0
            "MR" -> {
                if (numTextBox.text.equals("0"))
                    numTextBox.text = memoryVar.toString()
                else
                    numTextBox.text = numTextBox.text.toString().plus(memoryVar.toString())
            }
            "M+" -> memoryVar += funCalc()
            "M-" -> memoryVar -= funCalc()
            "=" -> calc()
        }

    }
}