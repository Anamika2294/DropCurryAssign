class Customer {

    val CurryPreferences: MutableList<Curry> =  ArrayList<Curry>()

    fun addCurryPreference(color: Int, finish: Finish) {
        val Curry = Curry(color, finish)
        CurryPreferences.add(Curry)
    }

    fun numOfCurryPreferences(): Int {
        return CurryPreferences.size }


    fun getcurryPreferences(): MutableList<Curry> {

        return CurryPreferences;
    }
}