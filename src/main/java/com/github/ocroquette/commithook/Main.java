package com.github.ocroquette.commithook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    final static String jiraUrl = "https://jira.company.com";
    final static String jiraUsername = "jira-user";
    final static String jiraPassword = "jira-password";

    public static void main(String[] args) throws URISyntaxException, IOException {
        File file = new File(args[0]);

        CommitMessage cm = new CommitMessage(readFile(file, StandardCharsets.UTF_8));

        new CommitMessageUpdaterJira(new URI(jiraUrl), jiraUsername, jiraPassword).update(cm);
        new CommitMessageUpdaterGerritChangeId().update(cm);

        writeFile(file, cm.generate());
    }

    static String readFile(File file, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(file.getCanonicalPath()));
        return new String(encoded, encoding);
    }

    static void writeFile(File file, String content) throws FileNotFoundException {
        try(  PrintWriter out = new PrintWriter( file)  ){
            out.print( content);
        }
    }
}
