package com.github.ocroquette.commithook

import spock.lang.Specification

class CommitMessageUpdaterGerritChangeIdTest extends Specification {
    static final String EOL = System.getProperty("line.separator");

    def "No change is change ID is already provided"() {
        given:
        String input = this.getClass().getResource( '/commit-message-1.txt' ).text
        CommitMessage cm = new CommitMessage(input)
        when:
        new CommitMessageUpdaterGerritChangeId().update(cm)
        then:
        cm.getTextLines() == ["Bla: Introduced syntax error"]
        cm.getFooterLines().size() == 1
        cm.getFooterLines().get(0).matches('Change-Id: Iee5559fbee98decbf54b03509e72a6fc1a0d24e6')
    }

    def "Add generated change ID if none available"() {
        given:
        String input = this.getClass().getResource( '/commit-message-5.txt' ).text
        CommitMessage cm = new CommitMessage(input)
        when:
        new CommitMessageUpdaterGerritChangeId().update(cm)
        then:
        cm.getTextLines() == ["Introduced syntax error","", "I am a bad person, therefore I introduce bugs in the projects I have access to. In this case,","I have introduced a syntax error."]
        cm.getFooterLines().size() == 1
        cm.getFooterLines().get(0).matches('Change-Id: I[0-9a-f]{40}')
    }

    def "Add generated change ID if none available, single line"() {
        given:
        String input = this.getClass().getResource( '/commit-message-9.txt' ).text
        CommitMessage cm = new CommitMessage(input)
        when:
        new CommitMessageUpdaterGerritChangeId().update(cm)
        then:
        cm.getTextLines() == ["SANDBOX-123"]
        cm.getFooterLines().size() == 1
        cm.getFooterLines().get(0).matches('Change-Id: I[0-9a-f]{40}')
    }
}
