package com.imranrashid.configium;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


import static org.junit.jupiter.api.Assertions.*;

class JiraAuthTest {

    private static final String baseInputDir = "src/test/resources/input/";

    @Test
    void testTokenLoading() throws Throwable {
        String tokenFile = baseInputDir + "ok_token_file.json";
        withSystemProp("jira.token.file", tokenFile, () -> {
            JiraAuth.AuthHolder auth = JiraAuth.loadAuthFromTokenFile(JiraAuth.findTokenFile());
            assertEquals("some_user", auth.user);
            assertEquals("some_token", auth.token);
        });
    }

    @Test
    void testErrorMsgsOnNoTokenFile() throws Throwable {
        withSystemProp("jira.token.file", "bogus_file", () -> {
            Exception ex = assertThrows(Exception.class, () -> JiraAuth.findTokenFile());
            assertTrue(ex.getMessage().contains(JiraAuth.DEFAULT_TOKEN_FILE_NAME));
        });
    }

    @Test
    void testErrorMsgsOnBadTokenFile() throws Throwable {
        withSystemProp("jira.token.file", baseInputDir + "bad_token_file.json", () -> {
            String tokenFile = JiraAuth.findTokenFile();
            Exception ex = assertThrows(Exception.class, () -> JiraAuth.loadAuthFromTokenFile(tokenFile));
            assertTrue(ex.getMessage().contains("did not have 'user' field"));
        });
    }


    void withSystemProp(String prop, String value, Executable f) throws Throwable {
        String initialValue = System.getProperty(prop);
        try {
            System.setProperty(prop, value);
            f.execute();
        } finally {
            if (initialValue == null) {
                System.clearProperty(prop);
            } else {
                System.setProperty(prop, initialValue);
            }
        }
    }
}
