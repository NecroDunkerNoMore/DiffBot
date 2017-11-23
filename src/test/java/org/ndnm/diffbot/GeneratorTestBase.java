package org.ndnm.diffbot;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.BeforeClass;

public class GeneratorTestBase {
    protected static String originalFileAsString;
    protected static String revisedFileAsString;

    @BeforeClass
    public static void setup() {
        try {
            originalFileAsString = new String(Files.readAllBytes(Paths.get("src/test/resources/raw_data/original.html")));
            revisedFileAsString = new String(Files.readAllBytes(Paths.get("src/test/resources/raw_data/revised.html")));
        } catch (Exception e) {
            Assert.fail("Could not load test files!: " + e.getMessage());
        }
    }
}
