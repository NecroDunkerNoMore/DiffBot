package org.ndnm.diffbot.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.ndnm.diffbot.model.CaptureType;
import org.ndnm.diffbot.model.HtmlSnapshot;
import org.ndnm.diffbot.model.diff.DiffResult;
import org.ndnm.diffbot.model.diff.DiffUrl;

import difflib.DiffUtils;
import difflib.Patch;

public class DiffGenerator {
    private static final String MULTI_NEWLINE_REGEX = "\\R+";

    public static DiffResult getDiffResult(DiffUrl diffUrl, String originalPageAsString, String revisedPageAsString) {
        List<String> originalFileLines = Arrays.asList(originalPageAsString.split(MULTI_NEWLINE_REGEX));
        List<String> revisedFileLines = Arrays.asList(revisedPageAsString.split(MULTI_NEWLINE_REGEX));

        Patch patch = DiffUtils.diff(originalFileLines, revisedFileLines);
        List<HtmlSnapshot> htmlSnapshots = createHtmlCaptures(diffUrl, originalPageAsString, revisedPageAsString);

        return new DiffResult(diffUrl, patch, htmlSnapshots, Calendar.getInstance().getTime());
    }


    private static List<HtmlSnapshot> createHtmlCaptures(DiffUrl diffUrl, String originalPageAsString, String revisedPageAsString) {
        List<HtmlSnapshot> htmlSnapshots = new ArrayList<>();

        HtmlSnapshot preEventCapture = new HtmlSnapshot(diffUrl, originalPageAsString, CaptureType.PRE_EVENT);
        htmlSnapshots.add(preEventCapture);

        HtmlSnapshot postEventCapture = new HtmlSnapshot(diffUrl, revisedPageAsString, CaptureType.POST_EVENT);
        htmlSnapshots.add(postEventCapture);

        return htmlSnapshots;
    }

}
