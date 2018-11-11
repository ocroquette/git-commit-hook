package com.github.ocroquette.commithook

class TestHelper {
    static void compareLines(List<String> realLines, String expectedLinesRaw) {
        // Replace Windows CRLF by LF (if any), and split on LF
        List<String> expectedLines = expectedLinesRaw.replaceAll("\r\n", "\n").split("\n")
        int max = Math.max(realLines.size(), expectedLines.size());
        for ( int i = 0 ; i < realLines.size() ; i++ ) {
            String realLine = realLines.getAt(i)
            String expectedLine = expectedLines.getAt(i)
            assert realLine == expectedLine : "Mismatch at line ${i+1}"
        }
    }
}
