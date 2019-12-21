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
        long startTime = System.nanoTime();
//        int counter = 0;
//        int totalRecord = 100;
//        try {
//            BufferedReader reader;
//            reader = new BufferedReader(new FileReader("./data/generated/data.csv"));
//            String line = null;
//            while ((line = reader.readLine()) != null) {
//                SearchResponse searchResponse = client.prepareSearch("books")
//                        .setQuery(QueryBuilders.matchQuery("title.keyword", line))
//                        .get();
//                if (counter++ == totalRecord) break;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        SearchResponse searchResponse = client.prepareSearch("books")
                .setQuery(QueryBuilders.matchQuery("title.keyword", "With Schwarzkopf: Life Lessons of The Bear"))
                .get();
        long endTime = System.nanoTime();

//        for (SearchHit searchHit : searchResponse.getHits()) {
//            System.out.println(searchHit.getSourceAsString());
//        }
//        System.out.println("Number of testing records: " + Integer.toString(totalRecord));
        System.out.println("Time used: " + (endTime - startTime) + "ns");
    }
}
