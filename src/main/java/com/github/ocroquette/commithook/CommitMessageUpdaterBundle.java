package com.github.ocroquette.commithook;

import com.github.ocroquette.commithook.CommitMessageUpdaterJira.JiraSettings;
import com.github.ocroquette.commithook.CommitMessageUpdaterWordWrap.WordWrapSettings;

public class CommitMessageUpdaterBundle implements CommitMessageUpdater {
    private final JiraSettings jiraSettings;
    private final WordWrapSettings wordWrapSettings;


    public CommitMessageUpdaterBundle(JiraSettings jiraSettings, WordWrapSettings wordWrapSettings) {
        this.jiraSettings = jiraSettings;
        this.wordWrapSettings = wordWrapSettings;
    }

    public void update(CommitMessage commitMessage) {
        if( jiraSettings.uri.toString().isEmpty()
                || jiraSettings.username.isEmpty()
                || jiraSettings.password.isEmpty()) {
            System.err.println("WARNING: Jira configuration is incomplete, skipping processing of Jira tickets in commit message");
        }
        else {
            new CommitMessageUpdaterJira(jiraSettings).update(commitMessage);
        }

        new CommitMessageUpdaterGerritChangeId().update(commitMessage);

        new CommitMessageUpdaterWordWrap(wordWrapSettings).update(commitMessage);

        new CommitMessageUpdaterSortFooterLines().update(commitMessage);

    }
}
