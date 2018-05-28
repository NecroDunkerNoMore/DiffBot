package org.ndnm.diffbot;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.ndnm.diffbot.model.diff.DiffDelta;
import org.ndnm.diffbot.util.TimeUtils;

import com.github.difflib.DiffUtils;
import com.github.difflib.algorithm.DiffException;
import com.github.difflib.patch.Delta;
import com.github.difflib.patch.DeltaType;
import com.github.difflib.patch.Patch;


public class ObjectConstructionTest extends GeneratorTestBase {



    @Test
    public void testDiffDeltaConstruction() {
        Date dateCreated = TimeUtils.getTimeGmt();
        Patch patch = null;
        try {
            patch = DiffUtils.diff(originalFileAsLines, revisedFileAsLines);
        } catch (DiffException e) {
            throw new RuntimeException(e);
        }

        @SuppressWarnings("unchecked")
        List<Delta> deltas = patch.getDeltas();
        for (Delta delta : deltas) {
            DiffDelta diffDelta = new DiffDelta(delta, dateCreated);

            Assert.assertTrue("DeltaLines not initialized correctly!", diffDelta.getDiffLines().size() > 0);
            Assert.assertTrue("Original/Revised lines not set correctly!",
                    diffDelta.getOriginalLines().size() > 0 ||  diffDelta.getRevisedLines().size() > 0);
            Assert.assertTrue("Delta type is missing!", diffDelta.getDeltaType() != null);

            if (delta.getType() == DeltaType.INSERT) {
                Assert.assertTrue("Enum type should be INSERT but was not!", diffDelta.getDeltaType() == DeltaType.INSERT);
            } else if (delta.getType() == DeltaType.CHANGE) {
                Assert.assertTrue("Enum type should be CHANGE but was not!", diffDelta.getDeltaType() == DeltaType.CHANGE);
            } else if (delta.getType() == DeltaType.DELETE) {
                Assert.assertTrue("Enum type should be DELETE but was not!", diffDelta.getDeltaType() == DeltaType.DELETE);
            } else {
                Assert.fail("Enum type is wack!");
            }

            if (diffDelta.getDeltaType() == DeltaType.INSERT) {
                int internalStart = delta.getRevised().getPosition();
                int internalEnd = delta.getRevised().last();
                Assert.assertTrue("Start position is wrong!", diffDelta.getStartPosition() == internalStart);
                Assert.assertTrue("End position is wrong!", diffDelta.getEndPosition() == internalEnd);
            } else if (diffDelta.getDeltaType() == DeltaType.CHANGE || diffDelta.getDeltaType() == DeltaType.DELETE) {
                int internalStart = delta.getOriginal().getPosition();
                int internalEnd = delta.getOriginal().last();
                Assert.assertTrue("Start position is wrong!", diffDelta.getStartPosition() == internalStart);
                Assert.assertTrue("End position is wrong!", diffDelta.getEndPosition() == internalEnd);
            }

            Assert.assertTrue("Date created is wrong!", diffDelta.getDateCreated().equals(dateCreated));
        }
    }

}
