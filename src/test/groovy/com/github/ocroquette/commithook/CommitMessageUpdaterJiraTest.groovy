package com.github.ocroquette.commithook

import com.atlassian.jira.rest.client.api.domain.Issue
import com.atlassian.util.concurrent.Promise
import spock.lang.Specification

class CommitMessageUpdaterJiraTest extends Specification {
    static final String EOL = System.getProperty("line.separator");

    def "Single issue"() {
        given:
        String input = this.getClass().getResource( '/commit-message-9.txt' ).text
        CommitMessage cm = new CommitMessage(input)
        when:
        createInstance().update(cm)
        then:
        cm.getTextLines() == [
                "SANDBOX-123     Headline of SANDBOX-123",
        ]
        cm.getFooterLines() == [
                "Jira-Id: SANDBOX-123",
        ]
    }


    def "Multiple issues"() {
        given:
        String input = this.getClass().getResource( '/commit-message-6.txt' ).text
        CommitMessage cm = new CommitMessage(input)
        when:
        createInstance().update(cm)
        then:
        cm.getTextLines() == [
                "Issues ABC-999 JIRAPROJ-7890 SANDBOX-123 SANDBOX-456",
                "",
                "ABC-999         Headline of ABC-999",
                "JIRAPROJ-7890   Headline of JIRAPROJ-7890",
                "SANDBOX-123     Headline of SANDBOX-123",
                "SANDBOX-456     Headline of SANDBOX-456",
                "",
                "Some comments after the tickets"
        ]
        cm.getFooterLines() == [
                "Jira-Id: ABC-999",
                "Jira-Id: JIRAPROJ-7890",
                "Jira-Id: SANDBOX-123",
                "Jira-Id: SANDBOX-456"
        ]
    }

    def "Multiple issues with Change-Id"() {
        given:
        String input = this.getClass().getResource( '/commit-message-7.txt' ).text
        CommitMessage cm = new CommitMessage(input)
        when:
        createInstance().update(cm)
        then:
        cm.getTextLines() == [
                "Issues ABC-999 JIRAPROJ-7890 SANDBOX-123 SANDBOX-456",
                "",
                "ABC-999         Headline of ABC-999",
                "JIRAPROJ-7890   Headline of JIRAPROJ-7890",
                "SANDBOX-123     Headline of SANDBOX-123",
                "SANDBOX-456     Headline of SANDBOX-456",
                "",
                "Some comments after the tickets, but with a Change-Id"
        ]
        cm.getFooterLines() == [
                "Change-Id: Iee5559fbee98decbf54b03509e72a6fc1a0d24e6",
                "Jira-Id: ABC-999",
                "Jira-Id: JIRAPROJ-7890",
                "Jira-Id: SANDBOX-123",
                "Jira-Id: SANDBOX-456"
        ]
    }

    def createInstance() {
        return new CommitMessageUpdaterJira(null, null, null) {
            @Override
            protected String getHeadline(String ticketId) {
                return "Headline of " + ticketId;
            }
        }
    }
}
