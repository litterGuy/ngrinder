package org.ngrinder.groovy

import HTTPClient.NVPair
import org.junit.Test

class FileTest {

    @Test
    void test() {
        println System.getProperty("user.dir")
        println getClass().protectionDomain.codeSource.location.path


        File test = new File(getClass().protectionDomain.codeSource.location.path+"/src/test/java/org/ngrinder/groovy/123.txt")
        print(test.text)

        return

        File file = new File('E:\\tmp\\user.csv')

        file.eachLine("UTF-8", 1) { str,num ->
            println num + ":" +str
        }

        Map<String,List<String>> rstMap = new HashMap<>()
        for(int i=0; i< 10;i++){
            rstMap.put(i as String,new ArrayList<String>())
        }
    }
}
