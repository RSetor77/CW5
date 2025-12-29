package com.example.cw5_calculator

import android.os.Bundle
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
                        child.setOnClickListener {
                            val numTextBox = findViewById<TextView>(R.id.num_text)
                            if (numTextBox.text.toString() == "0" || numTextBox.text.toString().isEmpty()) {
                                numTextBox.text = child.text
                            }
                            else numTextBox.text = numTextBox.text.toString().plus(child.text)
                        }
                    else if (operators.contains(child.text.toString()[0]))
                        child.setOnClickListener {
                            val numTextBox = findViewById<TextView>(R.id.num_text)
                            val lastChr: Char = numTextBox.text[numTextBox.text.lastIndex]

                            clearErr()
                            if (lastChr != child.text[0] && !operators.contains(lastChr))
                                numTextBox.text = numTextBox.text.toString().plus(child.text[0])
                            else if (operators.contains(child.text[0]) && !numTextBox.text.isEmpty()) {
                                val numSub = numTextBox.text.substring(0, numTextBox.text.lastIndex)
                                numTextBox.text =
                                    numSub.plus(child.text[0])
                            }
                        }
                    else
                        child.setOnClickListener {
                            clearErr()
                            val numTextBox = findViewById<TextView>(R.id.num_text)
                            val lastChr: Char = numTextBox.text[numTextBox.text.lastIndex]
                            val command: String = child.text.toString()
                            when(command)
                            {
                                "C" ->
                                {
                                    numTextBox.text = ""
                                    findViewById<TextView>(R.id.calc_text).text = " "
                                }
                                "⌫" -> if (!numTextBox.text.isEmpty()) numTextBox.text = numTextBox.text.substring(0, numTextBox.text.length - 1)
                                "." -> addSeparator()
                                "√" -> {
                                    if (!(numTextBox.text.isEmpty()) && !(operators.contains(lastChr)))
                                        numTextBox.text = numTextBox.text.toString().plus("^(1/2)")
                                }
                                "±" -> unaryMP(numTextBox)
                                "1/X" -> {
                                    if (!(numTextBox.text.isEmpty()) &&!(operators.contains(lastChr)) )
                                        numTextBox.text = numTextBox.text.toString().plus("^-1")
                                }
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
        }
    }

    fun clearErr()
    {
        if (findViewById<TextView>(R.id.num_text).text.toString() == "Ошибка выражения")
            findViewById<TextView>(R.id.num_text).text = "0"
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

        var result: String?
        try {
            val ex: Expression = ExpressionBuilder(expression).build()
            result = ex.evaluate().toString()
        }
        catch (e: ArithmeticException)
        {
            result = e.localizedMessage
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
        var number = 0.0
        if (isDouble(result))
            number = result.toDouble()
        return number
    }
    fun calc()
    {
        findViewById<TextView>(R.id.calc_text).text = findViewById<TextView>(R.id.num_text).text.toString().plus("=")
        val result: String = eval(findViewById<TextView>(R.id.num_text).text.toString())
        findViewById<TextView>(R.id.num_text).text = result
    }

    fun addSeparator()
    {
        val regexString: String = arrayOf("+", "-", "*", "/", "sqrt(").joinToString("|") { Regex.escape(it) }
        val regex = Regex(regexString)
        val expression: Array<String> = findViewById<TextView>(R.id.num_text).text.toString().split(regex).toTypedArray()
        if (strIsNum(expression[expression.lastIndex]))
            findViewById<TextView>(R.id.num_text).text = findViewById<TextView>(R.id.num_text).text.toString().plus('.')
    }

    fun unaryMP(numTextBox: TextView)
    {
        val regexString: String = arrayOf("+", "-", "*", "/", "sqrt(").joinToString("|") { Regex.escape(it) }
        val regex = Regex(regexString)
        val expression: Array<String> = numTextBox.text.toString().split(regex).toTypedArray()
        if (isDouble(expression[expression.lastIndex]))
        {
            var unChr = '-'
            try
            {
                if (numTextBox.text.toString()[numTextBox.text.toString().lastIndexOf(expression[expression.lastIndex]) - 1] == '-')
                    unChr = ' '
            }
            catch (e: Exception)
            {
                unChr = '-'
            }
            val funcStr: String = (unChr + expression[expression.lastIndex]).trim()
            if (unChr == '-')
                numTextBox.text = numTextBox.text.substring(0, numTextBox.text.lastIndexOf(expression[expression.lastIndex])) + funcStr
            else
                numTextBox.text = numTextBox.text.substring(0, numTextBox.text.lastIndexOf(expression[expression.lastIndex]) - 1) + funcStr
        }
    }
}