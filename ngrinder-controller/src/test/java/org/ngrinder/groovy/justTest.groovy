package org.ngrinder.groovy

import org.junit.Test
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig

class justTest {

    public Jedis jedis;

    @Test
    public void testJedis() {
        if (jedis == null) {
            jedis = new Jedis("127.0.0.1", 6379)

            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig()
            jedisPoolConfig.setMaxIdle(10)
            jedisPoolConfig.setMaxWaitMillis(40000)
            jedisPoolConfig.setTestOnBorrow(true)
            JedisPool jedisPool = new JedisPool(jedisPoolConfig, "127.0.0.1", 6379, 200, "")
            jedis = jedisPool.getResource()
        }

        println jedis.ping()

        long num = jedis.incr("123")
        println num
        if (jedis.exists("123")) {
            num = Long.parseLong(jedis.get("123"))
        }

    }


    @Test
    void test() {
        List<Map<String, Object>> list = new ArrayList<>()
        Map<String, Object> objectMap = new HashMap<>()
        objectMap.put("a", 123)
        list.add(objectMap)
        objectMap = new HashMap<>()
        objectMap.put("b", 234)
        list.add(objectMap)
        println list
    }

    @Test
    void arrays() {
        String[] tmp = {};
        Arrays.fill(tmp, "123");
        println tmp[0]
        long start = System.currentTimeMillis()
        pause()
        long end = System.currentTimeMillis()
        println end - start
    }

    public void pause() {
        Thread.sleep(1, 000 * 1000)
    }

    @Test
    public void whileTest(){
        int i = 0;
        while (i < 20){
            Thread.sleep(1000)
            println i
            if(i>15){
                break
            }
            i++
        }
    }

}
