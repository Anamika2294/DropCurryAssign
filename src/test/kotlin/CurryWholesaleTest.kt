import junit.framework.TestCase
import org.junit.Test
import java.io.File
import java.io.FileNotFoundException




class CurryWholesaleTest : TestCase() {
    var curryShop = CurryWholesale()
    var classLoader = javaClass.classLoader
   // var inputFilesFolder = "input_files" + File.separator
    @Test
    @Throws(Exception::class)
    fun test5curryValid() {
       val file = File(classLoader.getResource("test1.glt"
            ).toURI())

       val output = curryShop.mixCurry(file)
        assertEquals("V V V V M", output)
    }

    @Test
    @Throws(Exception::class)
    fun test2curryInValid() {
        val file = File(classLoader.getResource("test2.glt"
        ).toURI())

        val output = curryShop.mixCurry(file)
        assertEquals("No solution exists", output)
    }

    @Test
    @Throws(Exception::class)
    fun test2Curryvalid(){

        val file = File(classLoader.getResource("test3.glt"
        ).toURI())
        val output = curryShop.mixCurry(file)

        assertEquals("M M", output)
    }

    @Test
    @Throws(Exception::class)
    fun testMoreCurries(){

        val file = File(classLoader.getResource("test4.glt"
        ).toURI())
        val output = curryShop.mixCurry(file)

        assertEquals("V V V M V", output)
    }

    @Test
    @Throws(Exception::class)
    fun testInvalidFile(){

        try {
            curryShop.mixCurry(File("abc.glt"))
            fail()
        } catch (e: FileNotFoundException) {
            System.out.println("This file does not exist")
        } catch (e: java.lang.Exception) {
            fail()
        }
    }





}