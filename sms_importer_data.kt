package molikto.bean.data

import com.google.code.regexp.Pattern


val defaultNumberRegex = """(?<out_number>[0-9]+\.[0-9]+)""" // have a "." in this case

val englishCurrencyConverter = mapOf(
    "$" to "USD"
)


val chineseCurrencyConverter = mapOf(
    "人民币" to "CNY",
    "美元" to "USD"
)

val chineseCurrencyRegex = currencyRegex(chineseCurrencyConverter)

sealed class SmsImporter {
    abstract val currencyConverter: Map<String, String>

    class Exact(
        val number: String,
        val regexes: List<Pattern>,
        override val currencyConverter: Map<String, String>
    ): SmsImporter()

    class Generic(
        val regexes: List<Pattern>,
        override val currencyConverter: Map<String, String>
    ): SmsImporter()
}

val smsImporters: List<SmsImporter> = listOf(
    SmsImporter.Exact( // China Merchant Bank Checking
        "95555",
        regexes(
            """您账户(?<account>[0-9]+)于(?<month>[0-9]+)月(?<date>[0-9]+)日(?<hour>[0-9]+):(?<minute>[0-9]+)在【(?<payee>[^】]+)】发生快捷支付扣款，(?<currency>[^0-9\.]+)(?<out_number>[0-9\.]+)[^\[]*\[招商银行\]""",
            """您账户(?<account>[0-9]+)于(?<month>[0-9]+)月(?<date>[0-9]+)日(?<hour>[0-9]+):(?<minute>[0-9]+)发生三方存管/银行活期转保证金(?<currency>[^0-9\.]+)(?<out_number>[0-9\.]+)[^\[]*\[招商银行\]""",
            """您账户(?<account>[0-9]+)于(?<month>[0-9]+)月(?<date>[0-9]+)日(?<hour>[0-9]+):(?<minute>[0-9]+)入账款项，(?<currency>[^0-9\.]+)(?<in_number>[0-9\.]+)[^\[]*\[招商银行\]""",
            """您账户(?<account>[0-9]+)于(?<month>[0-9]+)月(?<date>[0-9]+)日(?<hour>[0-9]+):(?<minute>[0-9]+)扣收账务变动通知（短信）手续费(?<currency>[^0-9\.]+)(?<out_number>[0-9\.]+)[^\[]*\[招商银行\]"""
        ),
        chineseCurrencyConverter
    ),
    SmsImporter.Exact( // China Merchant Bank Credit
        "01065795555",
        regexes(
            """您尾号(?<account>[0-9]+)的信用卡(?<date>[0-9]+)日(?<hour>[0-9]+):(?<minute>[0-9]+)网上交易(?<currency>[^0-9\.]+)(?<out_number>[0-9\.]+)元。[^\[]*\[招商银行\]"""
        ),
        chineseCurrencyConverter
    ),
    SmsImporter.Generic( // generic Chinese SMS
        regexes(
            """(?<month>[0-9]+)月(?<date>[0-9]+)日""",
            """(?<hour>[0-9]+):(?<minute>[0-9]+)""",
            """消费$chineseCurrencyRegex(?<out_number>[0-9\.]+)""",
            """入账$chineseCurrencyRegex(?<in_number>[0-9\.]+)""",
            chineseCurrencyRegex,
            """(?<out_number>[0-9\.]+)元""",
            defaultNumberRegex
        ),
        chineseCurrencyConverter
    ),
    SmsImporter.Generic(
        regexes(
            defaultNumberRegex
        ),
        englishCurrencyConverter
    )
)



//
//
//
// helpers
//
//
//


private fun regexes(vararg strs: String): List<Pattern> {
    return strs.map { Pattern.compile(it) }
}

private fun currencyRegex(map: Map<String, String>): String = run {
    val ors = map.keys.joinToString("|")
    """(?<currency>$ors)"""
}
