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

    def "Word wrap commit-message-with-long-link.txt"() {
        given:
        String input = this.getClass().getResource( '/commit-message-with-long-link.txt' ).text
        CommitMessage cm = new CommitMessage(input)
        when:
        createInstance().update(cm)
        def lines = cm.getTextLines()
        then:
        TestHelper.compareLines(lines,
                """Introduced syntax error

This is the link: 
http://www.loooooooooooooooooooooooo.com/loooooooooooooooooooogpath/%E0%B9%82%E0.php?arg1=loooooooooooooooooooooooooooooooarg#looooooooooooooooooganchor 
Check it out!

Change-Id: Iee5559fbee98decbf54b03509e72a6fc1a0d24e6
""")
    }

    def "Word wrap commit-message-with-indent.txt"() {
        given:
        String input = this.getClass().getResource( '/commit-message-with-indent.txt' ).text
        CommitMessage cm = new CommitMessage(input)
        when:
        createInstance().update(cm)
        def lines = cm.getTextLines()
        then:
        TestHelper.compareLines(lines,
                """Introduced syntax error

Blabla
  Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean 
  commodo ligula eget dolor. Aenean massa. Cum sociis natoque 
  penatibus et magnis dis parturient montes, nascetur ridiculus mus. 
  Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem.
Nulla consequat massa quis enim. Donec pede justo, fringilla vel, 
aliquet nec.
    Vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, 
    venenatis vitae, justo. Nullam dictum felis eu pede mollis 
    pretium. Integer tincidunt. Cras dapibus. Vivamus elementum 
    semper nisi.

Some more text

    A even more

Change-Id: Iee5559fbee98decbf54b03509e72a6fc1a0d24e6
""")
    }

    def createInstance() {
        return new CommitMessageUpdaterWordWrap(new CommitMessageUpdaterWordWrap.WordWrapSettings(72));
    }
}
