import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;


public class MyElasticsearch {
    String host;
    int port;
    String clusterName;
    Client client;

    public MyElasticsearch(String host, int port, String clusterName) {
        this.host = host;
        this.port = port;
        this.clusterName = clusterName;
        connectElasticsearch();
    }

    private void connectElasticsearch() {
        try {
            Settings settings = Settings.EMPTY;
            if (clusterName != null || clusterName.length() != 0) {
                 settings = Settings.builder()
                        .put("cluster.name", clusterName)
                        .build();
            }
            client = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new TransportAddress(InetAddress.getByName(host), port));
            System.out.println("Connected to Elasticsearch server: " + host + ":" + port + ".");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
