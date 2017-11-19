package org.ndnm.diffbot.util;

import java.util.Calendar;
import java.util.List;

import org.ndnm.diffbot.model.diff.DiffResult;
import org.ndnm.diffbot.model.diff.DiffUrl;

import difflib.DiffUtils;
import difflib.Patch;

public class DiffGenerator {

    public static DiffResult getDiffResult(DiffUrl diffUrl, List<String> originalFileLines, List<String> revisedFileLines) {
        Patch patch = DiffUtils.diff(originalFileLines, revisedFileLines);
        return new DiffResult(diffUrl, patch, Calendar.getInstance().getTime());

    }

}
