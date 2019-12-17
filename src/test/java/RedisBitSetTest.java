import bitset.JavaBitSet;
import bitset.RedisBitSet;
import common.BloomFilter;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class RedisBitSetTest {
    private MyRedis myRedis;
    private Jedis jedis;

    @Before
    public void setJedis() {
        myRedis = new MyRedis("34.74.169.35", 6379, "mypass");
        // myRedis = new MyRedis("127.0.0.1",6379,null);
        jedis = myRedis.jedis;
    }

    @Test
    public void redisTest(){
        // use bloomfilter
        BloomFilter<String> filter = new BloomFilter<String>(8.0,300000000,8);
        jedis.flushDB();
        filter.bind(new RedisBitSet(jedis, "bloomfilter:key:name"));
        filter.clear();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("./data/generated/data.csv"));
            List<String> list = new ArrayList<String>();
            String line = null;
            int counter = 0;
            while ((line = reader.readLine()) != null) {
                list.add(line);
                if ((counter ++) == 100000) { break; }
            }
            filter.addWithPipeline(list);
        } catch (Exception e) {
            e.printStackTrace();
        }

        long startTime = System.nanoTime();
        System.out.println("Using bloomfilter:");
        System.out.println(filter.contains("With Schwarzkopf: Life Lessons of The Bear"));
        // System.out.println(filter.contains("Mosby's Oncology Nursing Advisor: A Comprehensive Guide to Clinical Practice, 1e"));
        // System.out.println(filter.contains("Employment Law In Ireland: A Guide in Plain English for Employers and Employees"));
        long endTime = System.nanoTime();
        System.out.println("Time used: " + (endTime - startTime) + "ns\n");

        // no bloomfilter
        jedis.flushDB();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("./data/generated/data.csv"));
            String line = null;
            Pipeline pipeline = jedis.pipelined();
            int counter = 0;
            while((line = reader.readLine()) != null){
                pipeline.set(line, "true");
                if ((counter ++) == 100000) { break; }
            }
            pipeline.sync();
        } catch (Exception e) {
            e.printStackTrace();
        }

        startTime = System.nanoTime();
        System.out.println("Raw input:");
        System.out.println(jedis.get("With Schwarzkopf: Life Lessons of The Bear"));
        // System.out.println(jedis.get("Mosby's Oncology Nursing Advisor: A Comprehensive Guide to Clinical Practice, 1e"));
        // System.out.println(jedis.get("Employment Law In Ireland: A Guide in Plain English for Employers and Employees"));
        endTime = System.nanoTime();
        System.out.println("Time used: " + (endTime - startTime) + "ns");
    }

    @Test
    public void falsePositiveTest() {
        BloomFilter<String> filter = new BloomFilter<String>(8.0,300000000,8);
        jedis.flushDB();
        filter.bind(new RedisBitSet(jedis, "bloomfilter:key:name"));
        try {
            BufferedReader reader;
            reader = new BufferedReader(new FileReader("./data/generated/data.csv"));
            String line = null;
            int counter = 0;
            while ((line = reader.readLine()) != null) {
                filter.add(line);
                if (counter++ == 50000) break;
            }
            double falsePositiveRate = 0;
            line = null;
            counter = 0;
            while ((line = reader.readLine()) != null) {
                if (filter.contains(line)) {
                    falsePositiveRate += 1;
                }
                if (counter++ == 10000) break;
            }
            double rate = falsePositiveRate / 10000;
            System.out.println("False positive rate: " + rate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
