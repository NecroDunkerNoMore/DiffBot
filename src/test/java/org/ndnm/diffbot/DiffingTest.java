package org.ndnm.diffbot;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.codec.Charsets;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.ndnm.diffbot.util.DiffGenerator;

import difflib.Delta;

@SuppressWarnings("unchecked")
public class DiffingTest {
    private static List<Delta> changeDeltas;
    private static List<Delta> insertedDeltas;
    private static List<Delta> deletedDeltas;


    @BeforeClass
    public static void setup() {
        List<String> originalFileLines;
        List<String> revisedFileLines;
        try {
            originalFileLines = Files.readAllLines(Paths.get("src/test/resources/raw_data/original.html"), Charsets.UTF_8);
            revisedFileLines = Files.readAllLines(Paths.get("src/test/resources/raw_data/revised.html"), Charsets.UTF_8);
        } catch (Exception e) {
            Assert.fail("Could not load test files!: " + e.getMessage());
            return;
        }

        DiffGenerator differ = new DiffGenerator(originalFileLines, revisedFileLines);
        changeDeltas = differ.getChangeDeltas();
        insertedDeltas = differ.getInsertDeltas();
        deletedDeltas = differ.getDeleteDeltas();
    }


    /*
     * A change will produce lines from both the original and revised versions.
     */
    @Test
    public void testChangeDeltas() {
        System.out.printf("**** %d CHANGES **************************************************************************************\n", changeDeltas.size());
        Assert.assertTrue("Did not get the 2 change deltas expected!" + changeDeltas.size(), changeDeltas.size() == 2);

        for (Delta delta : changeDeltas) {

            int linePosition = delta.getRevised().getPosition();
            List<String> originalLines = delta.getOriginal().getLines();
            List<String> revisedLines = delta.getRevised().getLines();

            Assert.assertTrue(originalLines.size() == 1 && revisedLines.size() == 1);

            // Report to stdout to see what this looks like
            System.out.printf("--- Starting at line %d ---------------------------------------\n", linePosition);
            for (int i = 0; i < originalLines.size(); i++) {
                System.out.println("Old: " + originalLines.get(i).trim());
                System.out.println("New: " + revisedLines.get(i).trim());
            }//for
        }//for
        System.out.println();
    }


    /*
     * Insertions will only produce lines from the revised version.
     */
    @Test
    public void testInsertDeltas() {
        System.out.printf("**** %d INSERTIONS **************************************************************************************\n", insertedDeltas.size());
        Assert.assertTrue("Did not get the 2 insert deltas expected!" + insertedDeltas.size(), insertedDeltas.size() == 2);

        // Expect there will only be new to print out
        for (Delta delta : insertedDeltas) {
            List<String> originalLines = delta.getOriginal().getLines();
            List<String> revisedLines = delta.getRevised().getLines();
            int linePosition = delta.getRevised().getPosition();

            Assert.assertTrue(originalLines.size() == 0 && revisedLines.size() > 0);

            // Report to stdout to see what this looks like
            System.out.printf("--- Starting at Line %d ---------------------------------------\n", linePosition);
            for (String line : revisedLines) {
                if (StringUtils.isBlank(line)) {
                    continue;
                }
                System.out.printf("%s\n", line.trim());
            }//for
        }//for
        System.out.println();
    }


    /*
     * Deletions will produce the missing lines from the original version.
     */
    @Test
    public void testDeleteDeltas() {
        System.out.printf("**** %d DELETIONS **************************************************************************************\n", deletedDeltas.size());
        Assert.assertTrue("Did not get the 2 delete deltas expected!" + deletedDeltas.size(), deletedDeltas.size() == 2);

        // Expect that we show old to show what was deleted in new
        for (Delta delta : deletedDeltas) {
            List<String> originalLines = delta.getOriginal().getLines();
            List<String> revisedLines = delta.getRevised().getLines();
            int linePosition = delta.getRevised().getPosition();

            Assert.assertTrue(originalLines.size() > 0 && revisedLines.size() == 0);

            // Report to stdout to see what this looks like
            System.out.printf("--- Starting at line %d ---------------------------------------\n", linePosition);
            for (String line : originalLines) {
                if (StringUtils.isBlank(line)) {
                    continue;
                }
                System.out.printf("%s\n", line.trim());
            }//for
        }//for
        System.out.printf("\n");
    }

}
