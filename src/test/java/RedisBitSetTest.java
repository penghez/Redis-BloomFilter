
import bitset.JavaBitSet;
import bitset.RedisBitSet;
import common.BloomFilter;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.io.BufferedReader;
import java.io.FileReader;

public class RedisBitSetTest {
    private MyRedis myRedis;
    private Jedis jedis;

    @Before
    public void setJedis() {
//        myRedis = new MyRedis("34.74.72.142", 6379, "mypass");
        myRedis = new MyRedis("127.0.0.1",6379,null);
        jedis = myRedis.jedis;

    }
    @Test
    public void redisTest(){


        BloomFilter<String> filter = new BloomFilter<String>(8.0,300000000,8);
        jedis.flushDB();
        filter.bind(new RedisBitSet(jedis, "bloomfilter:key:name"));
        if (filter.isEmpty()) {
            try {

                BufferedReader reader = new BufferedReader(new FileReader("/Users/haomengchao/Desktop/CDN/proj/Redis-BloomFilter/data/generated/data.csv"));//换成你的文件名
                String line = null;
                int counter = 0;
                while((line=reader.readLine())!=null){
                    filter.add(line);
                    if (counter ++ == 10000) break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        long startTime=System.nanoTime();
        System.out.println(filter.contains("With Schwarzkopf: Life Lessons of The Bear"));
        System.out.println(filter.contains("\"Mosby's Oncology Nursing Advisor: A Comprehensive Guide to Clinical Practice, 1e\""));
        System.out.println(filter.contains("Employment Law In Ireland: A Guide in Plain English for Employers and Employees"));
        long endTime=System.nanoTime();
        System.out.println("bloomfilter 耗时：" + (endTime - startTime) + "ns");


        try {

            BufferedReader reader = new BufferedReader(new FileReader("/Users/haomengchao/Desktop/CDN/proj/Redis-BloomFilter/data/generated/data.csv"));//换成你的文件名
            String line = null;
            int counter = 0;
            while((line=reader.readLine())!=null){
                jedis.set(line,"true");
                if (counter ++ == 10000) break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

         startTime=System.nanoTime();
        System.out.println(jedis.get("With Schwarzkopf: Life Lessons of The Bear"));
        System.out.println(jedis.get("\"Mosby's Oncology Nursing Advisor: A Comprehensive Guide to Clinical Practice, 1e\""));
        System.out.println(jedis.get("Employment Law In Ireland: A Guide in Plain English for Employers and Employees"));
         endTime=System.nanoTime();
        System.out.println("redis直接耗时：" + (endTime - startTime) + "ns");


        // True positive test


    }
    @Test
    public void falsePositiveTest() {
        BloomFilter<String> filter = new BloomFilter<String>(8.0,300000000,8);
        jedis.flushDB();
        filter.bind(new RedisBitSet(jedis, "bloomfilter:key:name"));
        try {
            BufferedReader reader;
            reader = new BufferedReader(new FileReader("/Users/haomengchao/Desktop/CDN/proj/Redis-BloomFilter/data/generated/data.csv"));
            String line = null;
            int counter = 0;
            while ((line = reader.readLine()) != null) {
                filter.add(line);
                if (counter++ == 10000) break;
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
            System.out.println("误报率:" + rate);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
