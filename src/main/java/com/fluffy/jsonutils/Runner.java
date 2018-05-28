package com.fluffy.jsonutils;


public class Runner {
    public static void main(String[] args) throws Exception {
        JsonReader reader = new JsonReader();
        reader.feedData("{\"f1\":90, \"f2\":123} 13 {\"f3\":{\"hello\":\"123\"}".getBytes());
        System.out.println(reader.parse());
        reader.feedData("}{".getBytes());
        System.out.println(reader.parse());
        System.out.println("feed again");
        reader.feedData("\"hello\":\"asdfasdfas\"".getBytes());
        System.out.println(reader.parse());
        reader.feedData("}".getBytes());
        System.out.println(reader.parse());
    }
}
