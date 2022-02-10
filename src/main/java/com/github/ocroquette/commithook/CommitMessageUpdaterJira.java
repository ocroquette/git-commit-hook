package com.github.ocroquette.commithook;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import io.atlassian.util.concurrent.Promise;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Add information from Jira to the commit message.
 * <p>
 * Jira issue ids are expected one per line at the start of the commit message, e.g.:
 * <p>
 * ABC-123
 * DEF-345
 * <p>
 * Additional comment lines can be added below
 */
public class CommitMessageUpdaterJira implements CommitMessageUpdater {
    static public class JiraSettings {
        JiraSettings(URI uri, String username, String password) {
            this.uri = uri;
            this.username = username;
            this.password = password;
        }

        public final URI uri;
        public final String username;
        public final String password;
    }

    private JiraRestClient restClient;
    private final JiraSettings jiraSettings;

    public CommitMessageUpdaterJira(JiraSettings jiraSettings) {
        this.jiraSettings = jiraSettings;
    }

    @Override
    public void update(CommitMessage commitMessage) {
        List<String> ticketIds = new ArrayList<>();
        while (commitMessage.getTextLines().size() > 0 && isTicketId(commitMessage.getTextLines().get(0))) {
            ticketIds.add(commitMessage.getTextLines().remove(0));
        }

        if (ticketIds.size() == 0)
            return;

        Collections.sort(ticketIds);

        int longestTicketLength = ticketIds.stream().max(Comparator.comparingInt(String::length)).get().length();

        int insertAt = 0;
        if (ticketIds.size() > 1) {
            commitMessage.getTextLines().add(0, "Issues " + String.join(" ", ticketIds));
            commitMessage.getTextLines().add(1, "");
            insertAt = 2;
        }
        for (String ticketId : ticketIds) {
            commitMessage.getTextLines().add(insertAt++, getIssueLine(ticketId, longestTicketLength));
            String footerLine = "Jira-Id: " + ticketId;
            if (!commitMessage.getFooterLines().contains(footerLine))
                commitMessage.appendFooterLine(footerLine);
        }
    }

    protected String getIssueLine(String ticketId, int longestTicketLength) {
        String format = "%-" + longestTicketLength + "s %s";
        return String.format(format, ticketId, getHeadline(ticketId));
    }

    protected String getHeadline(String ticketId) {
        try {
            return getHeadlineUnsafe(ticketId);
        } catch (Throwable e) {
            e.printStackTrace(); // print full error on the console for the user
            return "ERROR: " + e.toString();
        }
    }

    protected String getHeadlineUnsafe(String ticketId) {
        Promise<Issue> issue = getRestClient().getIssueClient().getIssue(ticketId);
        return issue.claim().getSummary();
    }

    static boolean isTicketId(String line) {
        // Regexp from https://confluence.atlassian.com/stashkb/integrating-with-custom-jira-issue-key-313460921.html
        return line.matches("((?<!([A-Z0-9]{1,10})-?)[A-Z0-9]+-\\d+)");
    }

    protected JiraRestClient getRestClient() {
        if (restClient == null) {
            restClient = new AsynchronousJiraRestClientFactory().createWithBasicHttpAuthentication(
                    jiraSettings.uri, jiraSettings.username, jiraSettings.password);

            restClient = new AsynchronousJiraRestClientFactory().create(jiraSettings.uri, new BasicHttpAuthenticationHandler(jiraSettings.username, jiraSettings.password));
        }
        return restClient;
    }
}
