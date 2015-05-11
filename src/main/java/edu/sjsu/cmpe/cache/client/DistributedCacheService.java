package edu.sjsu.cmpe.cache.client;

import com.google.common.hash.Hashing;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.ArrayList;

/**
 * Distributed cache service
 *
 */
public class DistributedCacheService implements CacheServiceInterface {
    private String cacheServerUrl;
    private ArrayList<String> servers;

    public DistributedCacheService() {
        servers = new ArrayList<String>();
        servers.add("http://localhost:3000");
        servers.add("http://localhost:3001");
        servers.add("http://localhost:3002");
    }


    /**
     * @see edu.sjsu.cmpe.cache.client.CacheServiceInterface#get(long)
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
     * @see edu.sjsu.cmpe.cache.client.CacheServiceInterface#put(long,
     *      java.lang.String)
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


    public String getBucket(long key){

        int bucket = Hashing.consistentHash(Hashing.md5().hashLong(key), servers.size());
        String cacheUrl = "";

        if(bucket == 0){
            cacheUrl = "http://localhost:3000";
        }else if(bucket == 1){
            cacheUrl = "http://localhost:3001";
        }else{
            cacheUrl = "http://localhost:3002";
        }

        return cacheUrl;

    }

}
