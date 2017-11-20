package org.ndnm.diffbot.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.ndnm.diffbot.model.CaptureType;
import org.ndnm.diffbot.model.HtmlCapture;
import org.ndnm.diffbot.model.diff.DiffResult;
import org.ndnm.diffbot.model.diff.DiffUrl;

import difflib.DiffUtils;
import difflib.Patch;

public class DiffGenerator {

    public static DiffResult getDiffResult(DiffUrl diffUrl, String originalPageAsString, String revisedPageAsString) {
        List<String> originalFileLines = Arrays.asList(originalPageAsString.split("\\R+"));
        List<String> revisedFileLines = Arrays.asList(revisedPageAsString.split("\\R+"));

        Patch patch = DiffUtils.diff(originalFileLines, revisedFileLines);
        List<HtmlCapture> htmlCaptures = createHtmlCaptures(originalPageAsString, revisedPageAsString);

        return new DiffResult(diffUrl, patch, htmlCaptures, Calendar.getInstance().getTime());

    }


    private static List<HtmlCapture> createHtmlCaptures(String originalPageAsString, String revisedPageAsString) {
        List<HtmlCapture> htmlCaptures = new ArrayList<>();

        HtmlCapture preEventCapture = new HtmlCapture(originalPageAsString, CaptureType.PRE_EVENT);
        htmlCaptures.add(preEventCapture);

        HtmlCapture postEventCapture = new HtmlCapture(revisedPageAsString, CaptureType.POST_EVENT);
        htmlCaptures.add(postEventCapture);

        return htmlCaptures;
    }

}
