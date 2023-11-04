package com.imranrashid.configium;

import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;

import java.io.IOException;

public class SimpleJiraDemo {

    public static String baseURL = "https://configium.atlassian.net/rest/api/3/";

    public static void main(String[] args) throws IOException {
        HttpResponse<JsonNode> response = JiraAuth.addAuth(Unirest.get(baseURL + "search"))
          .header("Accept", "application/json")
          .queryString("jql", "project=TI")
          .asJson();

        System.out.println(response.getBody());
    }

}
