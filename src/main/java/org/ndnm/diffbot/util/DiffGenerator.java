package org.ndnm.diffbot.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.ndnm.diffbot.model.diff.CaptureType;
import org.ndnm.diffbot.model.diff.HtmlSnapshot;
import org.ndnm.diffbot.model.diff.DiffResult;
import org.ndnm.diffbot.model.diff.DiffUrl;

import difflib.DiffUtils;
import difflib.Patch;

public class DiffGenerator {
    //This regex will eat multiple empty lines, leaving only text-containing ones
    public static final String MULTI_NEWLINE_REGEX = "\\R+";

    public static DiffResult getDiffResult(Date dateCaptured, DiffUrl diffUrl, String originalPageAsString, String revisedPageAsString) {
        List<String> originalFileLines = Arrays.asList(originalPageAsString.split(MULTI_NEWLINE_REGEX));
        List<String> revisedFileLines = Arrays.asList(revisedPageAsString.split(MULTI_NEWLINE_REGEX));

        Patch patch = DiffUtils.diff(originalFileLines, revisedFileLines);
        List<HtmlSnapshot> htmlSnapshots = createHtmlCaptures(diffUrl, originalPageAsString, revisedPageAsString, dateCaptured);

        return new DiffResult(diffUrl, patch, htmlSnapshots, dateCaptured);
    }


    private static List<HtmlSnapshot> createHtmlCaptures(DiffUrl diffUrl, String originalPageAsString, String revisedPageAsString, Date dateCaptured) {
        List<HtmlSnapshot> htmlSnapshots = new ArrayList<>();

        HtmlSnapshot preEventCapture = new HtmlSnapshot(diffUrl, originalPageAsString, CaptureType.PRE_EVENT, dateCaptured);
        htmlSnapshots.add(preEventCapture);

        HtmlSnapshot postEventCapture = new HtmlSnapshot(diffUrl, revisedPageAsString, CaptureType.POST_EVENT, dateCaptured);
        htmlSnapshots.add(postEventCapture);

        return htmlSnapshots;
    }

}
