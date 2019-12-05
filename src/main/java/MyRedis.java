import redis.clients.jedis.Jedis;

public class MyRedis {
    Jedis jedis;
    String host;
    String auth;
    int port;

    public MyRedis() {
        host = "localhost";
        port = 6379;
        connectRedis();
    }

    public MyRedis(String host, int port, String auth) {
        this.host = host;
        this.port = port;
        this.auth = auth;
        connectRedis();
    }

    private void connectRedis() {
        try {
            jedis = new Jedis(host, port);
            if (auth != null) {
                jedis.auth(auth);
            }
            System.out.println("Connected to the Redis server: " + host + ":" + port + ".");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
