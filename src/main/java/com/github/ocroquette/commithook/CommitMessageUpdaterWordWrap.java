package com.github.ocroquette.commithook;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Wraps the text automatically
 */
public class CommitMessageUpdaterWordWrap implements CommitMessageUpdater {

    static public class WordWrapSettings {
        WordWrapSettings(int wrapLength) {
            this.wrapLength = wrapLength;
        }
        public final int wrapLength;
    }

    private final WordWrapSettings wordWrapSettings;

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

    private String indentString = "";

    public CommitMessageUpdaterWordWrap(WordWrapSettings wordWrapSettings) {
        this.wordWrapSettings = wordWrapSettings;
    }

    public void update(CommitMessage commitMessage) {

        if(getWrapLength() <= 0) {
            // Disabled
            return;
        }

        List<String> textLines = commitMessage.getTextLines();
        checkFirstLines(commitMessage);
        final Pattern spaces = Pattern.compile("(\\s*).*");
        boolean isOriginalLine = true;
        for(int lineIndex = 1 ; lineIndex < textLines.size() ; lineIndex++) {
            String line = textLines.get(lineIndex);
            if (isOriginalLine) {
                Matcher matcher = spaces.matcher(line);
                matcher.matches();
                if(indentString.length() != line.length())
                    indentString = matcher.group(1);
            }
            isOriginalLine = true;
            if (line.length()> getWrapLength()) {
                int splitIndex = getPotentialSplitIndex(line, indentString.length());
                if (splitIndex != -1) {
                    textLines.remove(lineIndex);
                    textLines.add(lineIndex, line.substring(0, splitIndex + 1 ));

                    String nextLine = line.substring(splitIndex + 1, line.length());
                    nextLine.replaceAll("^\\s+", "");
                    nextLine = indentString + nextLine;
                    textLines.add(lineIndex + 1, nextLine);

                    isOriginalLine = false;
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
        if (textLines.get(0).length() > getWrapLength())
            System.err.println("WARNING: first line of commit message is longer than the recommended " + getWrapLength() + " characters.");
        if (textLines.size() > 1 && ! textLines.get(1).isEmpty())
            System.err.println("WARNING: second line of commit message is not empty as it should be.");
    }

    private int getPotentialSplitIndex(String s, int indentLength) {
        List<Integer> breakPoints = new ArrayList<>();

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
            if(breakPoints.get(index)  < getWrapLength() - indentLength)
                return breakPoints.get(index);

        // Plan B: try to have first line as short as possible
        return breakPoints.get(0);
    }

    private int getWrapLength() {
        return wordWrapSettings.wrapLength;
    }
}
