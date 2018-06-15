package com.github.ocroquette.commithook;

import java.net.URI;

public class CommitMessageUpdaterBundle implements CommitMessageUpdater {
    private final URI jiraUri;
    private final String jiraUsername;
    private final String jiraPassword;

    public CommitMessageUpdaterBundle(URI jiraUri, String jiraUsername, String jiraPassword) {
        this.jiraUri = jiraUri;
        this.jiraUsername = jiraUsername;
        this.jiraPassword = jiraPassword;
    }

    public void update(CommitMessage commitMessage) {
        new CommitMessageUpdaterJira(jiraUri, jiraUsername, jiraPassword).update(commitMessage);

        new CommitMessageUpdaterGerritChangeId().update(commitMessage);

        new CommitMessageUpdaterWordWrap().update(commitMessage);

    }
}
