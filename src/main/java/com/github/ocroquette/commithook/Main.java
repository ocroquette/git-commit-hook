package com.github.ocroquette.commithook;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class Main {
    final static String jiraUrl = "https://jira.company.com";
    final static String jiraUsername = "jira-user";
    final static String jiraPassword = "jira-password";

    public static void main(String[] args) throws URISyntaxException, IOException {
        // The commit-message hook is called by Git with a single parameter, which is the file where the
        // commit message is stored and should be overwritten with the new content
        File file = new File(args[0]);

        CommitMessage cm = new CommitMessage(readFile(file, StandardCharsets.UTF_8));

        new CommitMessageUpdaterBundle(
                getJiraSettings(),
                getWordWrapSettings()
        ).update(cm);

        writeFile(file, cm.generate());
    }

    static String readFile(File file, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(file.getCanonicalPath()));
        return new String(encoded, encoding);
    }

    static void writeFile(File file, String content) throws FileNotFoundException {
        try (PrintWriter out = new PrintWriter(file)) {
            out.print(content);
        }
    }

    static Properties getSettingsProperties() {
        Properties properties = new Properties();
        InputStream in = Main.class.getResourceAsStream("settings.properties");
        try {
            properties.load(in);
        } catch (IOException e) {
            // Don't throw an exception, we will use the default values
            e.printStackTrace();
            return null;
        }
        try {
            in.close();
        } catch (IOException e) {
            // Nothing we can do about this
            e.printStackTrace();
        }
        return properties;
    }

    static CommitMessageUpdaterJira.JiraSettings getJiraSettings() throws URISyntaxException {
        Properties properties = getSettingsProperties();
        URI jiraUri = (properties.getProperty("jiraUrl") != null ?
                new URI(properties.getProperty("jiraUrl"))
                : null);

        return new CommitMessageUpdaterJira.JiraSettings(
                jiraUri,
                properties.getProperty("jiraUser"),
                properties.getProperty("jiraPassword"));
    }

    static CommitMessageUpdaterWordWrap.WordWrapSettings getWordWrapSettings() throws URISyntaxException {
        Properties properties = getSettingsProperties();

        final int defaultWrapLength = 79;
        int wrapLength;
        String wrapLengthPropertyValue = properties.getProperty("wrapLength");
        if (wrapLengthPropertyValue == null || wrapLengthPropertyValue.isEmpty()) {
            wrapLength = defaultWrapLength;
        }
        else {
            // will throw an unchecked exception if the string cannot be parsed as integer
            wrapLength = Integer.parseInt(wrapLengthPropertyValue);
        }

        return new CommitMessageUpdaterWordWrap.WordWrapSettings(wrapLength);
    }
}
