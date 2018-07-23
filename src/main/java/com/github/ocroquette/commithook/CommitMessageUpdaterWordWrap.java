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

    private static final String LINK_PREFIX = "[a-z]://.*";

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
            System.err.println("WARNING: first line of commit message is longer than the recommended " + MAX_LENGTH + " characters.");
        if (textLines.size() > 1 && ! textLines.get(1).isEmpty())
            System.err.println("WARNING: second line of commit message is not empty as it should be.");
    }

    private int getPotentialSplitIndex(String s) {
        List<Integer> breakPoints = new ArrayList<>();

        System.out.println("getPotentialSplitIndex " + s);

        boolean inLink = false;
        for(int i = 1; i < s.length() ; i++) {
            if(s.substring(i).matches(LINK_PREFIX)) {
                inLink = true;
                continue;
            }
            if (inLink && s.substring(i, i+1).matches("\\s"))
                inLink = false;
            if(inLink)
                continue;
            if(SEPARATORS.contains(s.charAt(i)))
                breakPoints.add(i);
        }

        if(breakPoints.size() == 0 )
            return -1;

        // Try to split so that the first line is short enough
        for (int index = breakPoints.size()-1 ; index > 0 ; index--)
            if(breakPoints.get(index)  < MAX_LENGTH)
                return breakPoints.get(index);

        // Plan B: try to have first line as short as possible
        return breakPoints.get(0);
    }
}
