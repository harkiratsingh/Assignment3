package edu.sjsu.cmpe.cache.client;

import com.google.common.hash.Funnel;
import com.google.common.hash.Funnels;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Distributed cache service
 *
 */
public class RendezvousCacheService implements CacheServiceInterface {
    private String cacheServerUrl;
    private ArrayList<String> servers;
    private static final HashFunction hfunc = Hashing.murmur3_128();
    private static final Funnel<CharSequence> strFunnel = Funnels.stringFunnel(Charset.defaultCharset());
    private static final Funnel<Long> keyFunnel = Funnels.longFunnel();



    public RendezvousCacheService() {
        servers = new ArrayList<String>();
        servers.add("http://localhost:3000");
        servers.add("http://localhost:3001");
        servers.add("http://localhost:3002");
    }


    /**
     * @see CacheServiceInterface#get(long)
     */
    @Override
    public String get(long key) {
        HttpResponse<JsonNode> response = null;
        try {

            cacheServerUrl = getBucket(key);
            response = Unirest.get(this.cacheServerUrl + "/cache/{key}")
                    .header("accept", "application/json")
                    .routeParam("key", Long.toString(key)).asJson();
        } catch (UnirestException e) {
            System.err.println(e);
        }
        String value = response.getBody().getObject().getString("value");

        return value;
    }

    /**
     * @see CacheServiceInterface#put(long,
     *      String)
     */
    @Override
    public void put(long key, String value) {

        HttpResponse<JsonNode> response = null;
        System.out.println("put("+key+" => "+value+")");

        try {
            cacheServerUrl = getBucket(key);
            System.out.println("******" + cacheServerUrl);
            response = Unirest
                    .put(this.cacheServerUrl + "/cache/{key}/{value}")
                    .header("accept", "application/json")
                    .routeParam("key", Long.toString(key))
                    .routeParam("value", value).asJson();
        } catch (UnirestException e) {
            System.err.println(e);
        }

        if (response.getCode() != 200) {
            System.out.println("Failed to add to the cache.");
        }

    }


    public String getBucket(Long key){
        RendezvousHash<Long, String> r = new RendezvousHash(hfunc, keyFunnel, strFunnel, servers);
         return r.get(key);

    }


}
