package com.github.ocroquette.commithook

class TestHelper {
    static final String EOL = System.getProperty("line.separator");

    static void compareLines(List<String> realLines, String expectedLinesRaw) {
        List<String> expectedLines = expectedLinesRaw.split(EOL)
        int max = Math.max(realLines.size(), expectedLines.size());
        for ( int i = 0 ; i < realLines.size() ; i++ ) {
            String realLine = realLines.getAt(i)
            String expectedLine = expectedLines.getAt(i)
            assert realLine == expectedLine : "Mismatch at line $i"
        }
    }
}
