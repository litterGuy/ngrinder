package org.ngrinder.groovy

import org.junit.Test
import redis.clients.jedis.Jedis

class justTest {

    public Jedis jedis;

    @Test
    void test(){
        List<Map<String,Object>> list = new ArrayList<>()
        Map<String,Object> objectMap = new HashMap<>()
        objectMap.put("a",123)
        list.add(objectMap)
        objectMap = new HashMap<>()
        objectMap.put("b",234)
        list.add(objectMap)
        println list
    }

    @Test
    void arrays(){
        String[] tmp = {};
        Arrays.fill(tmp, "123");
        println tmp[0]
        long start = System.currentTimeMillis()
        Thread.sleep(10000)
        long end = System.currentTimeMillis()
        println end - start
    }
}
