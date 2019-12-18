import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.junit.Before;
import org.junit.Test;

public class MyElasticsearchTest {
    private MyElasticsearch myElasticsearch;
    private Client client;

    @Before
    public void setElasticsearch() {
        myElasticsearch = new MyElasticsearch("34.74.169.35", 9300, "docker-cluster");
        client = myElasticsearch.client;
    }

    @Test
    public void queryTest() {
        long startTime = System.nanoTime();
        SearchResponse searchResponse = client.prepareSearch("books")
                .setQuery(QueryBuilders.matchQuery("title.keyword", "With Schwarzkopf: Life Lessons of The Bear"))
                .get();
        long endTime = System.nanoTime();

        for (SearchHit searchHit : searchResponse.getHits()) {
            System.out.println(searchHit.getSourceAsString());
        }

        System.out.println("Time used: " + (endTime - startTime) + "ns");
    }
}
