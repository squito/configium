package com.imranrashid.configium;

import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SimpleJiraDemo {

    public static String baseURL = "https://configium.atlassian.net/rest/api/3/";
    public static String tokenFile = ".jira_testing_token";
    public static String jiraUser= "imran+atlassian_test@therashids.com";

    public static void main(String[] args) throws IOException {
        HttpResponse<JsonNode> response = Unirest.get(baseURL + "search")
          .basicAuth(jiraUser, loadJiraToken())
          .header("Accept", "application/json")
          .queryString("jql", "project=TI")
          .asJson();

        System.out.println(response.getBody());
    }

    public static String loadJiraToken() throws IOException {
        return new String(Files.readAllBytes(Paths.get(tokenFile))).strip();
    }
}
