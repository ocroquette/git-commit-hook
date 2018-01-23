package com.github.ocroquette.commithook;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommitMessageUpdaterJira implements CommitMessageUpdater {
    private final URI jiraUri;
    private final String username;
    private final String password;

    private JiraRestClient restClient;

    public CommitMessageUpdaterJira(URI jiraUri, String username, String password) {
        this.jiraUri = jiraUri;
        this.username = username;
        this.password = password;
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


        int insertAt = 0;
        if (ticketIds.size() > 1) {
            commitMessage.getTextLines().add(0, "Issues " + String.join(" ", ticketIds));
            commitMessage.getTextLines().add(1, "");
            insertAt = 2;
        }
        for (String ticketId : ticketIds) {
            commitMessage.getTextLines().add(insertAt++, getIssueLine(ticketId));
            String footerLine = "Jira-Id: " + ticketId;
            if (!commitMessage.getFooterLines().contains(footerLine))
                commitMessage.getFooterLines().add(footerLine);
        }
    }

    protected String getIssueLine(String ticketId) {
        return String.format("%-16s%s", ticketId, getHeadline(ticketId));
    }

    protected String getHeadline(String ticketId) {
        Promise<Issue> issue = getRestClient().getIssueClient().getIssue(ticketId);
        return issue.claim().getSummary();
    }

    protected boolean isTicketId(String line) {
        // Regexp from https://confluence.atlassian.com/stashkb/integrating-with-custom-jira-issue-key-313460921.html
        return line.matches("((?<!([A-Z]{1,10})-?)[A-Z]+-\\d+)");
    }

    protected JiraRestClient getRestClient() {
        if (restClient == null)
            restClient = new AsynchronousJiraRestClientFactory().createWithBasicHttpAuthentication(jiraUri, username, password);
        return restClient;
    }
}
