import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class MyElasticsearchTest {
    private MyElasticsearch myElasticsearch;
    private Client client;

    @Before
    public void setElasticsearch() {
        myElasticsearch = new MyElasticsearch("35.229.112.5", 9300, "docker-cluster");
        client = myElasticsearch.client;
    }

    @Test
    public void queryTest() {
        for (int totalCaseNumber = 10000; totalCaseNumber <= 250000; totalCaseNumber += 10000) {
            int mod = totalCaseNumber / 100;
            List<String> testCases = new ArrayList<String>();
            try {
                BufferedReader reader = new BufferedReader(new FileReader("./data/generated/data.csv"));
                List<String> list = new ArrayList<String>();
                String line = null;
                int counter = 0;
                while ((line = reader.readLine()) != null) {
                    list.add(line);
                    if ((counter ++) == totalCaseNumber) { break; }
                    if (counter % mod == 0) {
                        testCases.add(line);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            long startTime = System.nanoTime();
            for (int i = 0; i < testCases.size(); i++) {
                SearchResponse searchResponse = client.prepareSearch("books")
                        .setQuery(QueryBuilders.matchQuery("title.keyword", testCases.get(i)))
                        .get();
            }
            long endTime = System.nanoTime();
            System.out.println("Number of testing records: " + Integer.toString(testCases.size()));
            System.out.println("Time used: " + (endTime - startTime) + "ns");
        }



//        for (SearchHit searchHit : searchResponse.getHits()) {
//            System.out.println(searchHit.getSourceAsString());
//        }
//        System.out.println("Number of testing records: " + Integer.toString(totalRecord));

    }
}
