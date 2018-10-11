package com.github.ocroquette.commithook;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Sort the footer lines.
 *
 */
public class CommitMessageUpdaterSortFooterLines implements CommitMessageUpdater {
    @Override
    public void update(CommitMessage commitMessage) {
        List<String> lines = commitMessage.getFooterLines();
        lines.sort(null);
        commitMessage.setFooterLines(lines);
    }
}
