import java.io.*
import java.util.ArrayList
import java.util.HashMap
import java.util.logging.Level
import java.util.logging.Logger

class CurryWholesale {

    private val customerComparator =  Comparator<Customer> { c1, c2 ->
        c1.numOfCurryPreferences()

            .compareTo(c2.numOfCurryPreferences())
    }
    private var numOfCurry = 0
    private val customers = ArrayList<Customer>()
    private val fixedFinishes = HashMap<Int, Finish>()

    @Throws(
        Exception::class,
        IOException::class,
        FileNotFoundException::class,
        NumberFormatException::class
    )
    fun mixCurry(inputFile: File): String {

        readCustomersAndColorsFromFile(inputFile)

        // we want to iterate through the customers ordered by number of Curry preferences
        customers.sortWith(customerComparator)

        for (customer in customers) {
            if (customer.numOfCurryPreferences() === 1) {
                // if the customer has only one Curry preference, that must be in the output
                val fixedCurry = decideCurryForCustomer(customer, null) ?: return NO_SOLUTION_EXISTS

                fixedFinishes.put(fixedCurry.curryType, fixedCurry.finish);
            } else {
                // if the customer has more Curry preferences, the method below will also
                // fill a list with candidate Currys and return a fixed Curry or null
                val CurryCandidates = ArrayList<Curry>()
                val fixedCurry = decideCurryForCustomer(customer, CurryCandidates)

                if (fixedCurry != null) {
                    // there is a Curry already fixed which is good for this customer,
                    // nothing left to do, she is already satisfied
                    continue
                } else if (CurryCandidates.isEmpty()) {
                    // all the Curry preferences of this customer is in conflict
                    // with the ones already fixed - no solution exists
                    return NO_SOLUTION_EXISTS
                }

                // we need to select one Curry for this customer
                // it should be gloss, since it's cheaper, but
                // if there isn't one, we'll go with the first
                var CurryToSelect = CurryCandidates[0]
                for (Curry in CurryCandidates) {
                    if (Curry.finish.equals(Finish.VEG)) {
                        CurryToSelect = Curry
                    }
                }

                fixedFinishes[CurryToSelect.curryType] = CurryToSelect.finish
            }
        }

        return createOutput(fixedFinishes, numOfCurry)
    }

    @Throws(Exception::class)
    private fun readCustomersAndColorsFromFile(inputFile: File) {

        var firstLineParsed = false

        // first we need to read the input file and parse the colors and customers
        BufferedReader(FileReader(inputFile)).use { br ->

            var line: String? = null
            while ({ line = br.readLine(); line }() != null) {
                if (!firstLineParsed) {
                    numOfCurry = Integer.parseInt(line!!.trim { it <= ' ' })
                    firstLineParsed = true
                } else {
                    val customer = parseCustomer(line)
                    if(customer != null){
                        customers.add(customer)

                    }
                }
            }
        }
    }



    private fun decideCurryForCustomer(
        customer: Customer,
        CurryCandidates: MutableList<Curry>?
    ): Curry? {

        for (Curry in customer.getcurryPreferences()) {
            val curry = Curry.curryType
            val finish = Curry.finish
            val fixedFinish = fixedFinishes[curry]

            if (customer.numOfCurryPreferences() === 1) {
                return if (fixedFinish == null || fixedFinish!!.equals(finish)) {
                    // there is no finish fixed for this color yet,
                    // or it's the same as the preference of this customer
                    Curry
                } else {
                    // there is another finish needed for this color already,
                    // there is no solution.
                    null
                }
            } else {
                if (fixedFinish == null) {
                    // 'color' is not in the fixed finishes yet,
                    // let's remember it as a candidate
                    CurryCandidates!!.add(Curry)
                } else if (fixedFinish!!.equals(finish)) {
                    // we found one of the Curry preferences of this customer
                    // in the already fixed Currys, let's return it!
                    return Curry
                }
            }
        }

        // for a customer with multiple Curry preferences, there were no match
        // in the already fixed Currys; that's not a problem.
        return null
    }

    /**
     * Creates the string for the output of the program.
     * If a color is missing from the keys of the fixedFinishes map,
     * than it is considered gloss, since it's cheaper than matte.
     *
     * @param fixedFinishes
     * @param numOfColors
     * @return
     */
    private fun createOutput(fixedFinishes: Map<Int, Finish>, numOfColors: Int): String {

        val sb = StringBuilder()
        for (i in 1..numOfColors) {
            if (sb.length > 0) {
                sb.append(" ")
            }
            var finish = fixedFinishes[i]
            if (finish == null) {
                // If no specific need for a finish, let's make it gloss,
                // because it's cheaper. This can happen if there are
                // more colors than customers.
                finish = Finish.VEG
            }
            sb.append(finish!!.getAbbrevation())
        }

        return sb.toString()
    }


    @Throws(Exception::class, NumberFormatException::class)
    private fun parseCustomer(line: String?): Customer? {

        if (line == null || line == "") {
            return null
        }

        val customer = Customer()

        // 'colors' will be similar to: [1,M,2,G,5,M]
        val currys = line.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var i = 0
        while (i < currys.size) {

            val curry = Integer.parseInt(currys[i])
            val finishStr = currys[i + 1]
            val finish = Finish.parseFinish(finishStr)

            customer.addCurryPreference(curry, finish)
            i += 2
        }

        return customer
    }

    companion object {

        private val NO_SOLUTION_EXISTS = "No solution exists"
        private val LOG = Logger.getLogger(CurryWholesale::class.java.name)

        @JvmStatic
        fun main(args: Array<String>) {

            if (args.size != 1) {
                System.err.println("Number of arguments is incorrect. " + "Please provide the path for the input file as the first argument!")
                System.exit(1)
            }

            try {

                val CurryShop = CurryWholesale()
                val file = File(args[0])
                val finishes = CurryShop.mixCurry(file)
                println(finishes)

            } catch (e: NumberFormatException) {
                LOG.log(Level.SEVERE, "The program could not parse the input file.", e)
                System.exit(2)
            } catch (e: FileNotFoundException) {
                LOG.log(
                    Level.SEVERE, "The specified input file does not exist, "
                            + "or cannot be read: " + args[0], e
                )
                System.exit(3)
            } catch (e: IOException) {
                LOG.log(Level.SEVERE, "An error occured while reading the input file.", e)
                System.exit(4)
            } catch (e: Exception) {
                LOG.log(Level.SEVERE, "An error occured during runtime.", e)
                System.exit(5)
            }

            System.exit(0)
        }
    }
}
