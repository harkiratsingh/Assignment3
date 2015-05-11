package edu.sjsu.cmpe.cache.client;

public class Client {

    public static void main(String[] args) throws Exception {

        CacheServiceInterface cache = new DistributedCacheService();

        cache.put(1, "a");
        cache.put(2, "b");
        cache.put(3, "c");
        cache.put(4, "d");
        cache.put(5, "e");
        cache.put(6, "f");
        cache.put(7, "g");
        cache.put(8, "h");
        cache.put(9, "i");
        cache.put(10, "j");
        for(int i=1; i<=10; i++){
            System.out.println("get("+i+") => " + cache.get(i));
        }
        System.out.println("Stopping c Cache Client...");


        System.out.println("Starting R Cache Client...");

        CacheServiceInterface rCache = new RendezvousCacheService();

        rCache.put(1, "a");
        rCache.put(2, "b");
        rCache.put(3, "c");
        rCache.put(4, "d");
        rCache.put(5, "e");
        rCache.put(6, "f");
        rCache.put(7, "g");
        rCache.put(8, "h");
        rCache.put(9, "i");
        rCache.put(10, "j");
        for(int i=1; i<=10; i++){
            System.out.println("get("+i+") => " + rCache.get(i));
        }
        System.out.println("Stopping R Cache Client...");



    }


}