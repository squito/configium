package com.imranrashid.configium;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.*;

class JiraAuthTest {

    @Test
    void testTokenLoading() throws Throwable {
        String tokenFile = "src/test/resources/input/ok_token_file.json";
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
