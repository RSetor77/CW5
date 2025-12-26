package com.example.cw5_calculator

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder

class MainActivity : AppCompatActivity() {
    private val operators: Array<Char> = arrayOf('+', '-', '*', '/')
    private var memoryVar: Double = 0.0

    private fun initViews()
    {
        val mainLayout: TableLayout = findViewById(R.id.tableLayout)
        val childCount: Int = mainLayout.childCount
        for(i in 0 until childCount)
        {

            val childView: TableRow = (mainLayout.getChildAt(i) as TableRow)
            for(child in childView.children)
            {
                if (child is Button)
                    if (arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0").contains(child.text.toString()))
                        child.setOnClickListener { onNumClick(child) }
                    else if (operators.contains(child.text.toString()[0]))
                        child.setOnClickListener { onNumClick(child) }
                    else
                        child.setOnClickListener { onFuncClick(child) }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        initViews()
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
        var isOp = false
        if (operators.contains(op))
            isOp = true
        return isOp
    }

    fun strIsNum(str: String): Boolean
    {
        var isNum = false
        try {
            str.toShort()
            isNum = true
        }
        catch (e: Exception) {
        }
        return isNum
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
        var isInt = false
        try {
            num.toInt()
            isInt = true
        }
        catch (e: Exception) { }
        return isInt
    }

    fun isDouble(num: String): Boolean
    {
        var isDouble = false
        try {
            num.toDouble()
            isDouble = true
        }
        catch (e: Exception) { }
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
        val result: String = eval(findViewById<TextView>(R.id.num_text).text.toString())
        var number: Double
        if (isDouble(result))
            number = result.toDouble()
        else
            number = 0.0
        return number
    }
    fun calc()
    {
        findViewById<TextView>(R.id.calc_text).text = findViewById<TextView>(R.id.num_text).text.toString().plus("=")
        var result: String = eval(findViewById<TextView>(R.id.num_text).text.toString())
        findViewById<TextView>(R.id.num_text).text = result
    }

    fun insertFunc(fnc: String)
    {
        findViewById<TextView>(R.id.num_text).text = findViewById<TextView>(R.id.num_text).text.toString() + fnc + "("
    }

    fun addSeparator()
    {
        val regexString: String = arrayOf("+", "-", "*", "/", "sqrt(").joinToString("|") { Regex.escape(it) }
        val regex = Regex(regexString)
        val expression: Array<String> = findViewById<TextView>(R.id.num_text).text.toString().split(regex).toTypedArray()
        if (strIsNum(expression[expression.lastIndex]))
            findViewById<TextView>(R.id.num_text).text = findViewById<TextView>(R.id.num_text).text.toString().plus('.')
    }

    fun unaryMP()
    {
        val regexString: String = arrayOf("+", "-", "*", "/", "sqrt(").joinToString("|") { Regex.escape(it) }
        val regex = Regex(regexString)
        val expression: Array<String> = findViewById<TextView>(R.id.num_text).text.toString().split(regex).toTypedArray()
        if (isDouble(expression[expression.lastIndex]))
        {
            var unChr = '-'
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
                    R.id.num_text).text = findViewById<TextView>(R.id.num_text).text.toString().substring(0, findViewById<TextView>(R.id.num_text).text.toString().lastIndexOf(expression[expression.lastIndex]) - 1) + funcStr
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
            "±" -> unaryMP()
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