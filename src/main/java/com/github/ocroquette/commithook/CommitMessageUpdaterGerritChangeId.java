package com.github.ocroquette.commithook;

import java.util.Random;

/**
 * Add a Change-Id footer line if not present
 *
 * The Change-Id is just a random string, it seems good enough in practice.
 */
public class CommitMessageUpdaterGerritChangeId implements CommitMessageUpdater {
    static private final String PREFIX = "Change-Id:";

    public CommitMessageUpdaterGerritChangeId() {

    }

    public void update(CommitMessage commitMessage) {
        if ( ! hasChangeId(commitMessage) ) {
            commitMessage.appendFooterLine(PREFIX + " I" + generateChangeId());
        }
    }

    private boolean hasChangeId(CommitMessage commitMessage) {
        return commitMessage.getFooterLines().stream().anyMatch(this::isChangeId);
    }

    private boolean isChangeId(String line) {
        return line.startsWith(PREFIX);
    }

    private String generateChangeId() {
        String SALTCHARS = "0123456789abcdef";
        StringBuilder sb = new StringBuilder();
        Random rnd = new Random();
        rnd.setSeed(System.currentTimeMillis());
        while (sb.length() < 40) {
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            sb.append(SALTCHARS.charAt(index));
        }
        return sb.toString();

    }
}
