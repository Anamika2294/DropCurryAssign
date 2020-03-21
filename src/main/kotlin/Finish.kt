enum class Finish {
    VEG,
    MEAT;


    companion object {
        @Throws(Exception::class)
        fun parseFinish(abbreviation: String): Finish {
            when (abbreviation) {
                "M" -> return MEAT
                "V" -> return VEG
                else -> throw Exception("Invalid abbreviation for Finish: $abbreviation")
            }
        }
    }

    fun getAbbrevation(): String{
        return toString().substring(0,1);

    }

}
