package com.github.ocroquette.commithook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CommitMessage {
    private final List<String> textLines = new ArrayList<>();
    private final List<String> footerLines = new ArrayList<>();
    private static final String EOL = System.getProperty("line.separator");
    private static final Pattern FOOTER_LINE_PATTERN = Pattern.compile("([^:]+):\\s+(.*)");

    public CommitMessage(String commitMessage) {
        Pattern emptyLine = Pattern.compile("^\\s*$");
        String[] lines = commitMessage.split("\\r?\\n");

        String linesCategories = "";
        for ( int lineIndex = 0 ; lineIndex < lines.length ; lineIndex++) {
            if ( emptyLine.matcher(lines[lineIndex]).matches() )
                linesCategories += "E"; // EMPTY
            else if ( FOOTER_LINE_PATTERN.matcher(lines[lineIndex]).matches() )
                linesCategories += "F"; // FOOTER
            else
                linesCategories += "T"; // TEXT
        }

        int startOfText = -1;
        int endOfText = -1;
        int startOfFooter = -1;
        int endOfFooter = -1;
        if ( linesCategories.matches(".*[TF]EF+E*")) {

            endOfFooter = linesCategories.lastIndexOf("F");
            startOfFooter = linesCategories.lastIndexOf("E", endOfFooter) + 1;

            // Footer found
            startOfText = Math.min(linesCategories.indexOf("T"),linesCategories.indexOf("F"));
            endOfText = linesCategories.lastIndexOf("E", endOfFooter) - 1;
        }
        else {
            startOfText = Math.max(0, Math.min(linesCategories.indexOf("T"),linesCategories.indexOf("F")));
            endOfText = Math.max(linesCategories.lastIndexOf("T"),linesCategories.lastIndexOf("F"));
        }

        this.textLines.addAll(new ArrayList<>(Arrays.asList(Arrays.copyOfRange(lines, startOfText, endOfText+1))));
        if ( startOfFooter > 0 )
            this.footerLines.addAll(new ArrayList<>(Arrays.asList(Arrays.copyOfRange(lines, startOfFooter, endOfFooter+1))));
    }

    public  List<String> getTextLines() {
        return textLines;
    }

    public String generate() {
        if (footerLines.isEmpty())
            return String.join(EOL, textLines);
        else {
            return String.join(EOL, textLines) + EOL + EOL + String.join(EOL, footerLines);
        }
    }

    public List<String> getFooterLines() {
        return footerLines;
    }
}
