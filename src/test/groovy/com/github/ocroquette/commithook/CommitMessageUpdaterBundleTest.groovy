package com.github.ocroquette.commithook

import spock.lang.Specification

class CommitMessageUpdaterBundleTest extends Specification {
    def "Complicated case"() {
        given:
        String input = this.getClass().getResource('/commit-message-12.txt').text
        CommitMessage cm = new CommitMessage(input)
        when:
        createInstance().update(cm)
        def lines = cm.getTextLines()
        def i = 0
        then:
        TestHelper.compareLines(cm.getTextLines(),
                """blabla blabla blabla blabla blabla blabla blabla blabla blabla blabla blabla blabla blabla blabla blabla blabla blabla blabla

Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean
commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus
et magnis dis parturient montes, nascetur ridiculus mus. Donec quam
felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla
consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec,
 vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a,
venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium.
Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean
vulputate eleifend tellus. Aenean leo ligula, porttitor eu, consequat
vitae, eleifend ac, enim. Aliquam lorem ante, dapibus in, viverra quis,
feugiat a, tellus. Phasellus viverra nulla ut metus varius laoreet.
Quisque rutrum. Aenean imperdiet. Etiam ultricies nisi vel augue.
Curabitur ullamcorper ultricies nisi. Nam eget dui. Etiam rhoncus.
Maecenas tempus, tellus eget condimentum rhoncus, sem quam semper
libero, sit amet adipiscing sem neque sed ipsum. Nam quam nunc, blandit
vel, luctus pulvinar, hendrerit id, lorem. Maecenas nec odio et ante
tincidunt tempus. Donec vitae sapien ut libero venenatis faucibus.
Nullam quis ante. Etiam sit amet orci eget eros faucibus tincidunt.
Duis leo. Sed fringilla mauris sit amet nibh. Donec sodales sagittis
magna. Sed consequat, leo eget bibendum sodales, augue velit cursus
nunc,

Change-Id: Id88c2af9e04dad15cafc830717a8b0e8042c3d87""")
        cm.getFooterLines() == [
                "Change-Id: Id88c2af9e04dad15cafc830717a8b0e8042c3d87",
        ]
    }

    def createInstance() {
        return new CommitMessageUpdaterBundle(null, null, null)
    }
}
