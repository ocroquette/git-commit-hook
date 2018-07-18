package com.github.ocroquette.commithook

import com.github.ocroquette.commithook.CommitMessage
import spock.lang.Specification

class CommitMessageTest extends Specification {
    static final String EOL = System.getProperty("line.separator");

    def "Parse commit-message-1.txt"() {
        given:
        String input = this.getClass().getResource( '/commit-message-1.txt' ).text
        when:
        CommitMessage cm = new CommitMessage(input)
        then:
        cm.getTextLines() == ["Introduced syntax error"]
        cm.getFooterLines() == ["Change-Id: Iee5559fbee98decbf54b03509e72a6fc1a0d24e6"]
    }

    def "Parse commit-message-2.txt"() {
        given:
        String input = this.getClass().getResource( '/commit-message-2.txt' ).text
        when:
        CommitMessage cm = new CommitMessage(input)
        then:
        cm.getTextLines() == ["Introduced syntax error", "", "Change-Id: Not part of the footer because of the separator!"]
        cm.getFooterLines() == ["Change-Id: Iee5559fbee98decbf54b03509e72a6fc1a0d24e6"]
    }

    def "Parse commit-message-3.txt"() {
        given:
        String input = this.getClass().getResource( '/commit-message-3.txt' ).text
        when:
        CommitMessage cm = new CommitMessage(input)
        then:
        cm.getTextLines() == ["Headline", "", "Some text"]
        cm.getFooterLines() == ["Jira-Id: ABCD-1234", "Change-Id: Iee5559fbee98decbf54b03509e72a6fc1a0d24e6"]
    }

    def "Parse commit-message-4.txt"() {
        given:
        String input = this.getClass().getResource( '/commit-message-4.txt' ).text
        when:
        CommitMessage cm = new CommitMessage(input)
        then:
        cm.getTextLines() == ["Jira-Id: ABCD-1234", "Change-Id: Iee5559fbee98decbf54b03509e72a6fc1a0d24e6"]
        cm.getFooterLines() == []
    }

    def "Parse commit-message-9.txt"() {
        given:
        String input = this.getClass().getResource( '/commit-message-9.txt' ).text
        when:
        CommitMessage cm = new CommitMessage(input)
        then:
        cm.getTextLines() == ["SANDBOX-123"]
        cm.getFooterLines() == []
    }

    def "Parse commit-message-13.txt"() {
        given:
        String input = this.getClass().getResource( '/commit-message-13.txt' ).text
        when:
        CommitMessage cm = new CommitMessage(input)
        then:
        cm.getTextLines() == ["blabla blabla blabla blabla blabla blabla blabla", "Change-Id: Id88c2af9e04dad15cafc830717a8b0e8042c3d87"]
        cm.getFooterLines() == []
    }

    def "Parse commit-message-14.txt"() {
        given:
        String input = this.getClass().getResource( '/commit-message-14.txt' ).text
        when:
        CommitMessage cm = new CommitMessage(input)
        then:
        cm.getTextLines() == ["headline", "", "blabla blabla blabla blabla blabla blabla blabla", "Change-Id: Id88c2af9e04dad15cafc830717a8b0e8042c3d87"]
        cm.getFooterLines() == []
    }
    def "Parse commit-message-15.txt"() {
        given:
        String input = this.getClass().getResource( '/commit-message-15.txt' ).text
        when:
        CommitMessage cm = new CommitMessage(input)
        then:
        cm.getTextLines() == ["Introduced syntax error", ""]
        cm.getFooterLines() == ["Change-Id: Iee5559fbee98decbf54b03509e72a6fc1a0d24e6"]
    }
}
