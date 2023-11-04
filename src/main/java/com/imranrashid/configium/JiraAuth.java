package com.imranrashid.configium;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.core.HttpRequest;

import java.io.File;
import java.io.IOException;

public class JiraAuth {

    static String DEFAULT_TOKEN_FILE_NAME = ".jira_token.json";

    /**
     * Token file searched for in priority order:
     * 1) JIRA_TOKEN_FILE env var
     * 2) "jira.token.file" java system prop
     * 3) ".jira_token.json" in cwd
     * 4) ".jira_token.json" in home dir
     * @return file name
     */
    public static String findTokenFile() {
        String tokenFile = resolveFileAndCheckExistence(System.getenv("JIRA_TOKEN_FILE"));
        if (tokenFile != null) {
            return tokenFile;
        }
        tokenFile = resolveFileAndCheckExistence(System.getProperty("jira.token.file"));
        if (tokenFile != null) {
            return tokenFile;
        }
        tokenFile = resolveFileAndCheckExistence(DEFAULT_TOKEN_FILE_NAME);
        if (tokenFile != null) {
            return tokenFile;
        }
        tokenFile = resolveFileAndCheckExistence("~/" + DEFAULT_TOKEN_FILE_NAME);
        if (tokenFile == null) {
            throw new RuntimeException("No jira token file found.  Checked for '" +
                    DEFAULT_TOKEN_FILE_NAME + "' in home dir and cwd.  Please create a jira" +
                    "security token (eg. at " +
                    "https://id.atlassian.com/manage-profile/security/api-tokens) and save a json" +
                    "file with \"user\" and \"token\" fields.");
        }
        return tokenFile;
    }

    /**
     * Expand home dir and check if file exists (return null if not)
     * @param file
     * @return
     */
    private static String resolveFileAndCheckExistence(String file) {
        if (file == null) {
            return null;
        }
        if (file.startsWith("~/")) {
            file = file.replaceFirst("~/", System.getProperty("user.home"));
        }
        if (new File(file).exists()) {
            return file;
        } else {
            throw new RuntimeException("No jira token file found.  Checked for '" +
                    DEFAULT_TOKEN_FILE_NAME + "' in home dir and cwd.  Please create a jira" +
                    "security token (eg. at " +
                    "https://id.atlassian.com/manage-profile/security/api-tokens) and save a json" +
                    "file with \"user\" and \"token\" fields.");
        }
    }

    public static <R extends HttpRequest<R>> R addAuth(R req) {
        AuthHolder auth = AuthHolder.getDefault();
        return req.basicAuth(auth.user, auth.token);
    }

    // Not a singleton just for testing purposes -- need to be abe to test w/ alternatives
    static class AuthHolder {
        final String user;
        final String token;

        AuthHolder(String user, String token) {
            this.user = user;
            this.token = token;
        }

        private static volatile AuthHolder DEFAULT = null;
        static AuthHolder getDefault() {
            if (DEFAULT == null) {
                synchronized (AuthHolder.class) {
                    if (DEFAULT == null) {
                        try {
                            DEFAULT = loadAuthFromTokenFile(findTokenFile());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
            return DEFAULT;
        }
    }

    static AuthHolder loadAuthFromTokenFile(String tokenFile) throws IOException {
        JsonNode j = new ObjectMapper().readTree(new File(tokenFile));
        return new AuthHolder(j.get("user").asText(), j.get("token").asText());
    }
}
