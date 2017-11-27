package org.ndnm.diffbot;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.ndnm.diffbot.model.diff.DiffDelta;
import org.ndnm.diffbot.model.diff.DiffLine;
import org.ndnm.diffbot.model.diff.DiffResult;
import org.ndnm.diffbot.model.diff.DiffUrl;
import org.ndnm.diffbot.util.DiffGenerator;
import org.ndnm.diffbot.util.TimeUtils;


public class DiffingTest extends GeneratorTestBase {
    private static DiffResult diffResult;


    @BeforeClass
    public static void initDiffResult() {
        diffResult = DiffGenerator.getDiffResult(TimeUtils.getTimeGmt(),
                                                 new DiffUrl(), originalFileAsString, revisedFileAsString);
    }

    /*
     * A change will produce lines from both the original and revised versions.
     */
    @Test
    public void testChangeDeltas() {
        List<DiffDelta> changeDeltas = diffResult.getChangeDeltas();
        System.out.printf("**** %d CHANGES **************************************************************************************\n", changeDeltas.size());
        Assert.assertTrue("Did not get the 2 change deltas expected!: " + changeDeltas.size(), changeDeltas.size() == 2);

        for (DiffDelta delta : changeDeltas) {

            int linePosition = delta.getStartPosition();
            List<DiffLine> originalLines = delta.getOriginalLines();
            List<DiffLine> revisedLines = delta.getRevisedLines();

            Assert.assertTrue(originalLines.size() == 1 && revisedLines.size() == 1);

            // Report to stdout to see what this looks like
            System.out.printf("--- Starting at line %d ---------------------------------------\n", linePosition);
            for (int i = 0; i < originalLines.size(); i++) {
                System.out.println("Old: " + originalLines.get(i).toString().trim());
                System.out.println("New: " + revisedLines.get(i).toString().trim());
            }//for
        }//for
        System.out.println();
    }


    /*
     * Insertions will only produce lines from the revised version.
     */
    @Test
    public void testInsertDeltas() {
        List<DiffDelta> insertDeltas = diffResult.getInsertDeltas();
        System.out.printf("**** %d INSERTIONS **************************************************************************************\n", insertDeltas.size());
        Assert.assertTrue("Did not get the 2 insert deltas expected!" + insertDeltas.size(), insertDeltas.size() == 2);

        // Expect there will only be new to print out
        for (DiffDelta delta : insertDeltas) {
            List<DiffLine> originalLines = delta.getOriginalLines();
            List<DiffLine> revisedLines = delta.getRevisedLines();
            int linePosition = delta.getStartPosition();

            Assert.assertTrue(originalLines.size() == 0 && revisedLines.size() > 0);

            // Report to stdout to see what this looks like
            System.out.printf("--- Starting at Line %d ---------------------------------------\n", linePosition);
            for (DiffLine line : revisedLines) {
                if (StringUtils.isBlank(line.toString())) {
                    continue;
                }
                System.out.printf("%s\n", line.toString().trim());
            }//for
        }//for
        System.out.printf("\n");
    }


    /*
     * Deletions will produce the missing lines from the original version.
     */
    @Test
    public void testDeleteDeltas() {
        List<DiffDelta> deleteDeltas = diffResult.getDeleteDeltas();
        System.out.printf("**** %d DELETIONS **************************************************************************************\n", deleteDeltas.size());
        Assert.assertTrue("Did not get the 2 delete deltas expected!" + deleteDeltas.size(), deleteDeltas.size() == 2);

        // Expect that we show old to show what was deleted in new
        for (DiffDelta delta : deleteDeltas) {
            List<DiffLine> originalLines = delta.getOriginalLines();
            List<DiffLine> revisedLines = delta.getRevisedLines();
            int linePosition = delta.getStartPosition();

            Assert.assertTrue(originalLines.size() > 0 && revisedLines.size() == 0);

            // Report to stdout to see what this looks like
            System.out.printf("--- Starting at line %d ---------------------------------------\n", linePosition);
            for (DiffLine line : originalLines) {
                if (StringUtils.isBlank(line.toString())) {
                    continue;
                }
                System.out.printf("%s\n", line.toString().trim());
            }//for
        }//for
        System.out.printf("\n");
    }

}
