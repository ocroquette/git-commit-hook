package com.github.ocroquette.commithook;

import java.util.*;

/**
 * Wraps the text automatically
 */
public class CommitMessageUpdaterWordWrap implements CommitMessageUpdater {
    static final int MAX_LENGTH = 72;

    private static final Set<Character> SEPARATORS = new HashSet<Character>(Arrays.asList(
            new Character[] {
                    ',',
                    '.',
                    ':',
                    ';',
                    ' ',
                    '\t',
            }
    ));

    public void update(CommitMessage commitMessage) {
        List<String> textLines = commitMessage.getTextLines();
        checkFirstLines(commitMessage);
        for(int lineIndex = 1 ; lineIndex < textLines.size() ; lineIndex++) {
            String line = textLines.get(lineIndex);
            if (line.length()> MAX_LENGTH) {
                int splitIndex = getPotentialSplitIndex(line);
                if (splitIndex != -1) {
                    textLines.remove(lineIndex);
                    textLines.add(lineIndex, line.substring(0, splitIndex + 1 ));
                    textLines.add(lineIndex + 1, line.substring(splitIndex + 1, line.length()));
                }
            }
        }
    }

    private void checkFirstLines(CommitMessage commitMessage) {
        List<String> textLines = commitMessage.getTextLines();
        if (textLines.size() == 0)
            throw new RuntimeException("Empty text in commit message");
        if (textLines.get(0).isEmpty())
            throw new RuntimeException("First line of commit message is empty");
        if (textLines.get(0).length() > MAX_LENGTH)
            System.err.println("WARNING: first line of commit message is longer than the recommended " + MAX_LENGTH + " characters");
        if (textLines.size() > 1 && ! textLines.get(1).matches("\\s*"))
            System.err.println("WARNING: second line of commit message is not empty as it should be: " + textLines.get(1));
    }

    private int getPotentialSplitIndex(String s) {
        // Try to split so that the first line is short enough
        for(int i = MAX_LENGTH - 1; i > 0 ; i--) {
            if( SEPARATORS.contains(s.charAt(i)) ) {
                return i;
            }
        }
        // Plan B: try to have first line as short as possible
        for(int i = MAX_LENGTH + 1; i < s.length() ; i++) {
            if( SEPARATORS.contains(s.charAt(i)) ) {
                return i;
            }
        }
        // Line too long but no separator found
        return -1;
    }
}
