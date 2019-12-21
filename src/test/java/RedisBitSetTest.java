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
//        myRedis = new MyRedis("35.229.112.5", 6379, "mypass");
        myRedis = new MyRedis("127.0.0.1",6379,null);
        jedis = myRedis.jedis;
    }

    @Test
    public void redisTest(){
        // use bloomfilter
        BloomFilter<String> filter;
        for(int totalCaseNumber = 10000; totalCaseNumber <= 250000; totalCaseNumber += 10000) {
            filter = new BloomFilter<String>(8.0,totalCaseNumber,8);
            int mod = totalCaseNumber /  100;
            jedis.flushDB();
            filter.bind(new RedisBitSet(jedis, "bloomfilter:key:name"));
            filter.clear();
            List<String> testCases = new ArrayList<String>();
            try {
                BufferedReader reader = new BufferedReader(new FileReader("./data/generated/data.csv"));
                List<String> list = new ArrayList<String>();
                String line = null;
                int counter = 0;
                while ((line = reader.readLine()) != null) {
                    list.add(line);
                    if ((counter ++) == totalCaseNumber) { break; }
                    if (counter % mod== 0) {
                        testCases.add(line);
                    }
                }
                filter.addWithPipeline(list);
            } catch (Exception e) {
                e.printStackTrace();
            }

            long startTime = System.nanoTime();
            System.out.println("Number of records: " + Integer.toString(totalCaseNumber));
            System.out.println("Using bloomfilter:");
            for (int i = 0; i < testCases.size(); i++) {
                filter.contains(testCases.get(i));
            }
//            filter.contains(testCases.get(0));
            long endTime = System.nanoTime();
            System.out.println("Number of testing records: " + Integer.toString(testCases.size()));
            System.out.println("Time used: " + (endTime - startTime) + "ns");

            // no bloomfilter
            jedis.flushDB();
            try {
                BufferedReader reader = new BufferedReader(new FileReader("./data/generated/data.csv"));
                String line = null;
                Pipeline pipeline = jedis.pipelined();
                int counter = 0;
                while((line = reader.readLine()) != null){
                    pipeline.set(line, "true");
                    if ((counter ++) == totalCaseNumber) { break; }
                }
                pipeline.sync();
            } catch (Exception e) {
                e.printStackTrace();
            }

            startTime = System.nanoTime();
            System.out.println("Using Redis:");
            for (int i = 0; i < testCases.size(); i++) {
                jedis.get(testCases.get(i));
            }
            endTime = System.nanoTime();
            System.out.println("Number of testing records: " + Integer.toString(testCases.size()));
            System.out.println("Time used: " + (endTime - startTime) + "ns\n");
        }

    }

    @Test
    public void falsePositiveTest() {
        BloomFilter<String> filter = new BloomFilter<String>(16.0,250000,8);
        jedis.flushDB();
        filter.bind(new RedisBitSet(jedis, "bloomfilter:key:name"));
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("./data/generated/data.csv"));
            List<String> list = new ArrayList<String>();
            String line = null;
            int counter = 0;
            for (int totalCaseNumber = 10000; totalCaseNumber <= 250000; totalCaseNumber += 10000) {
                while ((line = reader.readLine()) != null) {
                    list.add(line);
                    if (counter++ == totalCaseNumber) break;
                }
                filter.addWithPipeline(list);
                double falsePositiveRate = 0;
                String testline = null;
                BufferedReader testReader = new BufferedReader(new FileReader("./data/generated/fpTestdata.csv"));
                while ((testline = testReader.readLine()) != null) {
                    if (filter.contains(testline)) {
                        falsePositiveRate += 1;
                    }
                }
                double rate = falsePositiveRate / 100;
                System.out.println("Current number of records: " + totalCaseNumber);
                System.out.println("False positive rate: " + rate);
                System.out.println(" ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Test
    public void falsePositiveParameterTest() {
        int totalCaseNumber = 250000;
        for (double k = 1.0; k <= 8.0; k += 1.0){
            BloomFilter<String> filter = new BloomFilter<String>(k,300000000,8);
            jedis.flushDB();
            filter.bind(new RedisBitSet(jedis, "bloomfilter:key:name"));
            try {
                BufferedReader reader;
                reader = new BufferedReader(new FileReader("./data/generated/data.csv"));
                List<String> list = new ArrayList<String>();
                String line = null;
                int counter = 0;
                while ((line = reader.readLine()) != null) {
                    list.add(line);
                    if (counter++ == totalCaseNumber) break;
                }
                filter.addWithPipeline(list);
                double falsePositiveRate = 0;
                line = null;
                reader = new BufferedReader(new FileReader("./data/generated/fpTestdata.csv"));
                while ((line = reader.readLine()) != null) {
                    if (filter.contains(line)) {
                        falsePositiveRate += 1;
                    }
                }
                double rate = falsePositiveRate / 1000;
                System.out.println("Current number of bits: " + k);
                System.out.println("False positive rate: " + rate);
                System.out.println(" ");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
