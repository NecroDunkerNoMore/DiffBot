package org.ndnm.diffbot;

import static org.ndnm.diffbot.util.DiffGenerator.MULTI_NEWLINE_REGEX;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;

public class GeneratorTestBase {
    protected static String originalFileAsString;
    protected static String revisedFileAsString;
    protected static List<String> originalFileAsLines;
    protected static List<String> revisedFileAsLines;

    @BeforeClass
    public static void setup() {
        try {
            originalFileAsString = new String(Files.readAllBytes(Paths.get("src/test/resources/raw_data/original.html")));
            revisedFileAsString = new String(Files.readAllBytes(Paths.get("src/test/resources/raw_data/revised.html")));
        } catch (Exception e) {
            Assert.fail("Could not load test files!: " + e.getMessage());
            return;
        }

        originalFileAsLines = Arrays.asList(originalFileAsString.split(MULTI_NEWLINE_REGEX));
        revisedFileAsLines = Arrays.asList(revisedFileAsString.split(MULTI_NEWLINE_REGEX));
    }
}
