import redis.clients.jedis.Jedis;

public class MyRedis {
    Jedis jedis;
    String host;
    int port;

    public MyRedis() {
        host = "localhost";
        port = 6379;
        connectRedis();
    }

    public MyRedis(String host, int port) {
        this.host = host;
        this.port = port;
        connectRedis();
    }

    private void connectRedis() {
        try {
            jedis = new Jedis(host, port);
            System.out.println("Connected to the Redis server: " + host + ":" + port + ".");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
