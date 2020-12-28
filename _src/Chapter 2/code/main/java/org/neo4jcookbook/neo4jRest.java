package org.neo4jcookbook;


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;


public class neo4jRest {
    public neo4jRest() {
    }

    public static void main(String[] args) {
        final String ROOT_URI = "http://localhost:7474/db/data/";
        final String nodeEntry = ROOT_URI + "node";
        WebResource res = Client.create().resource( nodeEntry );
        ClientResponse response = res.get(ClientResponse.class);
        System.out.println( String.format( "GET on [%s], status code [%d]",
                ROOT_URI, response.getStatus() ) );
        response.close();

    }

}
