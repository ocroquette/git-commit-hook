package com.github.ocroquette.commithook

import spock.lang.Specification

class CommitMessageUpdaterWordWrapTest extends Specification {
    static final String EOL = System.getProperty("line.separator");

    def "Word wrap"() {
        given:
        String input = this.getClass().getResource('/commit-message-11.txt').text
        CommitMessage cm = new CommitMessage(input)
        when:
        createInstance().update(cm)
        def lines = cm.getTextLines()
        def i = 0
        then:
        TestHelper.compareLines(lines,
        """This is a headline that is too long (length > 50 chars) and should issue a warning

dummy001 dummy001 dummy001 dummy001 dummy001 dummy001 dummy001 dummy001
dummy002 dummy002 dummy002 dummy002 dummy002 dummy002 dummy002 dummy002 
dummy002

dummy003 dummy003 dummy003 dummy003 dummy003 dummy003 dummy003 dummy003,
dummy003

dummy004 dummy004 dummy004 dummy004 dummy004 dummy004 dummy004 
dummy004-dummy004

dummy005 dummy005 dummy005 dummy005 dummy005 dummy005 dummy005 dummy005 
dummy005 dummy005 dummy005 dummy005 dummy005 dummy005 dummy005 dummy005 
dummy005 dummy005 dummy005 dummy005 dummy005 dummy005 dummy005 dummy005 
dummy005 dummy005 dummy005

dummy006dummy006dummy006dummy006dummy006dummy006dummy006dummy006dummy006dummy006dummy006dummy006dummy006dummy006dummy006dummy006dummy006dummy006dummy006dummy006dummy006dummy006dummy006
dummy007dummy007dummy007dummy007dummy007dummy007dummy007dummy007dummy007dummy007dummy007dummy007dummy007dummy007dummy007dummy007dummy007dummy007dummy007dummy007dummy007dummy007dummy007dummy007dummy007dummy007dummy007:
dummy007dummy007dummy007dummy007dummy007dummy007dummy007dummy007dummy007dummy007dummy007dummy007dummy007dummy007dummy007dummy007dummy007

dummy008                                                                
           dummy008
""")
        cm.getFooterLines() == [
                "Some-Footer-Tag: a looooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooog value",
        ]
    }

    def createInstance() {
        return new CommitMessageUpdaterWordWrap();
    }
}
