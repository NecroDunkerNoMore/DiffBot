package org.ndnm.diffbot.util;

import javax.annotation.Resource;

import org.ndnm.diffbot.model.diff.DiffDelta;
import org.ndnm.diffbot.model.diff.DiffLine;
import org.ndnm.diffbot.model.diff.DiffResult;
import org.springframework.stereotype.Component;


@Component
public class RedditPostFormatter {
    private static final String FOOTER = "^[FAQ](https://np.reddit.com/r/TheEssaysChanged/wiki/index)&nbsp;| ^[Source&nbsp;Code](https://github.com/NecroDunkerNoMore/DiffBot)&nbsp;| ^[PM&nbsp;Developer](https://www.reddit.com/message/compose?to=NecroDunkerNoMore&subject=NecroDunkerNoMore)&nbsp;| ^v%s";
    private static final String DELTA_SECTION_HEADER = "**(Delta starting at line %d, ending at line %d)**";
    private static final String CHANGED_LINE_BEFORE_FORMAT = "\\[Before]: `%s`";
    private static final String CHANGED_LINE_AFTER_FORMAT = "&nbsp;&nbsp;&nbsp;\\[After]: `%s`";
    private static final String INSERT_LINE_FORMAT = "\\[Inserted]: `%s`";
    private static final String DELETE_LINE_FORMAT = "\\[Deleted]: `%s`";
    private static final String REDDIT_LINE = "-----";

    @Resource(name = "diffBotVersion")
    private String diffBotVersion;
    private static StringBuilder stringBuilder = new StringBuilder();


    public String formatPostTitle(DiffResult diffResult) {
        int deltaCount = diffResult.getNumDeltas();
        String websiteUrl = diffResult.getDiffUrl().getSourceUrl();

        return String.format("Detected %d Deltas for: %s", deltaCount, websiteUrl);
    }


    public String formatCommentBody(DiffResult diffResult) {
        generateHeader(diffResult);
        generateStatsTable(diffResult);
        generateChangeDeltaSection(diffResult);
        generateInsertDeltaSection(diffResult);
        generateDeleteDeltaSection(diffResult);
        generateFooter();

        String body = stringBuilder.toString();
        stringBuilder = new StringBuilder();

        return body;
    }


    private void generateHeader(DiffResult diffResult) {
        String dateString = TimeUtils.formatGmt(diffResult.getDateCaptured());
        addLineWithTwoNewlines(REDDIT_LINE);
        addLineWithTwoNewlines(String.format("#%s: %d Deltas(s) from: %s", dateString, diffResult.getNumDeltas(), diffResult.getDiffUrl().getSourceUrl()));
        addLineWithTwoNewlines(REDDIT_LINE);
    }


    private void generateStatsTable(DiffResult diffResult) {
        addLineWithOneNewline("Count|Metric");
        addLineWithOneNewline("---|---");
        addLineWithOneNewline(String.format("%d | Modification delta(s)", diffResult.getChangeDeltas().size()));
        addLineWithOneNewline(String.format("%d | Insertion delta(s)", diffResult.getInsertDeltas().size()));
        addLineWithOneNewline(String.format("%d | Deletion delta(s)", diffResult.getDeleteDeltas().size()));
        addLineWithOneNewline(String.format("%d | Total lines affected", diffResult.getTotalLinesAffected()));
        addLineWithOneNewline(REDDIT_LINE);
    }


    private void generateChangeDeltaSection(DiffResult diffResult) {
        if (diffResult.getChangeDeltas().size() > 0) {
            addLineWithTwoNewlines("#Change Delta(s)");

            boolean first = true;
            for (DiffDelta diffDelta : diffResult.getChangeDeltas()) {
                if (!first) {
                    addLineWithTwoNewlines("&nbsp;");
                }
                first = false;

                addLineWithTwoNewlines(String.format(DELTA_SECTION_HEADER, diffDelta.getStartPosition(), diffDelta.getEndPosition()));
                for (int i = 0; i < diffDelta.getOriginalLines().size(); i++) {
                    String originalLine = diffDelta.getOriginalLines().get(i).getLine();
                    String revisedLine = diffDelta.getRevisedLines().get(i).getLine();
                    addLineWithTwoNewlines(String.format(CHANGED_LINE_BEFORE_FORMAT, originalLine));
                    addLineWithTwoNewlines(String.format(CHANGED_LINE_AFTER_FORMAT, revisedLine));
                }

            }//for
        }//if
    }


    private void generateInsertDeltaSection(DiffResult diffResult) {
        if (diffResult.getInsertDeltas().size() > 0) {
            addLineWithTwoNewlines(REDDIT_LINE);
            addLineWithTwoNewlines("#Insert Delta(s)");

            boolean first = true;
            for (DiffDelta diffDelta : diffResult.getInsertDeltas()) {
                if (!first) {
                    addLineWithTwoNewlines("&nbsp;");
                }
                first = false;

                addLineWithTwoNewlines(String.format(DELTA_SECTION_HEADER, diffDelta.getStartPosition(), diffDelta.getEndPosition()));
                for (DiffLine diffLine : diffDelta.getRevisedLines()) {
                    addLineWithTwoNewlines(String.format(INSERT_LINE_FORMAT, diffLine.getLine()));
                }

            }//for
        }//if
    }


    private void generateDeleteDeltaSection(DiffResult diffResult) {
        if (diffResult.getDeleteDeltas().size() > 0) {
            addLineWithTwoNewlines(REDDIT_LINE);
            addLineWithTwoNewlines("#Delete Delta(s)");

            boolean first = true;
            for (DiffDelta diffDelta : diffResult.getDeleteDeltas()) {
                if (!first) {
                    addLineWithTwoNewlines("&nbsp;");
                }
                first = false;

                addLineWithTwoNewlines(String.format(DELTA_SECTION_HEADER, diffDelta.getStartPosition(), diffDelta.getEndPosition()));
                for (DiffLine diffLine : diffDelta.getOriginalLines()) {
                    addLineWithTwoNewlines(String.format(DELETE_LINE_FORMAT, diffLine.getLine()));
                }

            }//for
        }//if
    }


    private void generateFooter() {
        addLineWithTwoNewlines(REDDIT_LINE);
        addLineWithTwoNewlines(String.format(FOOTER, getDiffBotVersion()));
    }


    private void addLineWithTwoNewlines(String line) {
        stringBuilder.append(line);
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append(System.lineSeparator());
    }


    private void addLineWithOneNewline(String line) {
        stringBuilder.append(line);
        stringBuilder.append(System.lineSeparator());
    }


    private String getDiffBotVersion() {
        return diffBotVersion;
    }
}
