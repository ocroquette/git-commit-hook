package com.github.ocroquette.commithook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CommitMessage {
    private final List<String> textLines = new ArrayList<>();
    private final List<String> footerLines = new ArrayList<>();
    private static final String EOL = System.getProperty("line.separator");
    private static final Pattern FOOTER_LINE_PATTERN = Pattern.compile("([A-Z][A-Za-z0-9_\\-]+)\\s*:\\s+(.*)");
    private static final Pattern COMMENT_LINE_PATTERN = Pattern.compile("#.*");
    private static final Pattern EMPTY_LINE_PATTERN = Pattern.compile("^\\s*$");

    public CommitMessage(String commitMessage) {
        String[] lines = commitMessage.split("\\r?\\n");

        // Generate a string, in which each char represents the type of the line:
        // empty (potential separator), potential footer line or normal text
        // This string is used below to identify the real start and end of the
        // footer
        String linesCategories = "";
        for ( int lineIndex = 0 ; lineIndex < lines.length ; lineIndex++) {
            if ( EMPTY_LINE_PATTERN.matcher(lines[lineIndex]).matches() )
                linesCategories += "E"; // EMPTY
            else if ( COMMENT_LINE_PATTERN.matcher(lines[lineIndex]).matches() ) {
                lines[lineIndex] = ""; // Remove comments
                linesCategories += "E"; // Comment -> EMPTY
            }
            else if ( FOOTER_LINE_PATTERN.matcher(lines[lineIndex]).matches() )
                linesCategories += "F"; // FOOTER
            else
                linesCategories += "T"; // TEXT
        }

        int startOfText = -1;
        int endOfText = -1;
        int startOfFooter = -1;
        int endOfFooter = -1;

        // The footer is the last block of "F" preceeded by a separator,
        // but only if there are some non empty lines before:
        if ( linesCategories.matches(".*[TF]E+F+E*")) {

            endOfFooter = linesCategories.lastIndexOf("F");
            startOfFooter = linesCategories.lastIndexOf("E", endOfFooter) + 1;

            // Footer found
            startOfText = minIndex(linesCategories.indexOf("T"),linesCategories.indexOf("F"));
            endOfText = linesCategories.lastIndexOf("E", endOfFooter) - 1;
        }
        else {
            startOfText = Math.max(0, Math.min(linesCategories.indexOf("T"),linesCategories.indexOf("F")));
            endOfText = Math.max(linesCategories.lastIndexOf("T"),linesCategories.lastIndexOf("F"));
        }

        try {
            this.textLines.addAll(new ArrayList<>(Arrays.asList(Arrays.copyOfRange(lines, startOfText, endOfText + 1))));
        }
        catch(ArrayIndexOutOfBoundsException e) {
            // This has been seen in the field, but the cause is not clear.
            System.err.println("Internal error: ArrayIndexOutOfBoundsException. Please report this error.");
            System.err.println("                linesCategories = "+linesCategories);
            System.err.println("                startOfText     = "+startOfText);
            System.err.println("                endOfText       = "+endOfText);
            System.err.println("                startOfFooter   = "+startOfFooter);
            System.err.println("                endOfFooter     = "+endOfFooter);
            System.err.println("                commitMessage   = \n\""+commitMessage + "\"");
            System.exit(1);
        }
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

    private int minIndex(int index1, int index2) {
        if (index1 < 0) {
            if (index2 < 0) {
                throw new RuntimeException("Both indices are negative");
            }
            return index2;
        }
        else if (index2 < -1) {
            return index1;
        }
        return Math.min(index1, index2);
    }

    public List<String> getFooterLines() {
        List<String> copy = new ArrayList<>();
        copy.addAll(footerLines);
        return copy;
    }

    public void setFooterLines(List<String> lines) {
        this.footerLines.clear();
        this.footerLines.addAll(lines);
    }

}
