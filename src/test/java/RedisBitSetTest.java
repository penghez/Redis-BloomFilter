
import bitset.RedisBitSet;
import common.BloomFilter;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;

public class RedisBitSetTest {
    private MyRedis myRedis;
    private Jedis jedis;

    @Before
    public void setJedis() {
        myRedis = new MyRedis("34.74.72.142", 6379, "mypass");
        jedis = myRedis.jedis;
    }
    @Test
    public void redisTest(){

        BloomFilter<String> filter = new BloomFilter<String>(8.0,300000000,8);

        filter.bind(new RedisBitSet(jedis, "bloomfilter:key:name"));

        filter.add("filter");
        System.out.println(filter.contains("filter"));
        System.out.println(filter.contains("bloom"));
        filter.add("bitset");
        filter.add("redis");
        System.out.println(filter.contains("bitset"));
        System.out.println(filter.contains("redis"));
        System.out.println(filter.contains("mysql"));
        System.out.println(filter.contains("linux"));
        System.out.println(filter.count());
        System.out.println(filter.isEmpty());
        filter.clear();
        System.out.println(filter.isEmpty());
        System.out.println(filter.contains("filter"));
        System.out.println(filter.expectedFalsePositiveProbability());

    }
}
