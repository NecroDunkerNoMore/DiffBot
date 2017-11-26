package org.ndnm.diffbot.util;

import javax.annotation.Resource;

import org.ndnm.diffbot.model.diff.DiffDelta;
import org.ndnm.diffbot.model.diff.DiffLine;
import org.ndnm.diffbot.model.diff.DiffResult;
import org.springframework.stereotype.Component;


@Component
public class RedditPostFormatter {
    private static final String FOOTER = "^[FAQ](https://np.reddit.com/r/TheEssaysChanged/wiki/index)&nbsp;| ^[Source&nbsp;Code](https://github.com/NecroDunkerNoMore/DiffBot)&nbsp;| ^[PM&nbsp;Developer](https://www.reddit.com/message/compose?to=NecroDunkerNoMore&subject=NecroDunkerNoMore)&nbsp;| ^v%s";
    private static final String DELTA_SECTION_HEADER = "(Delta starting at line %d, ending at line %d)";
    private static final String CHANGED_LINE_BEFORE_FORMAT = "[Before]: %s";
    private static final String CHANGED_LINE_AFTER_FORMAT =  " [After]: %s";
    private static final String INSERT_LINE_FORMAT = " [Inserted]: %s";
    private static final String DELETE_LINE_FORMAT = " [Deleted]: %s";
    private static final String LONG_LINE = "\\--------------------------------------------------------------------------------";
    private static final String MEDIUM_LINE = "\\----------------------------------------";
    private static final String SHORT_LINE = "\\--------------------";
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
        addLine(LONG_LINE);
        addLine(String.format("%d Delta(s) from: %s", diffResult.getNumDeltas(), diffResult.getDiffUrl().getSourceUrl()));
        addLine(LONG_LINE);

        if (diffResult.getChangeDeltas().size() > 0) {
            addLine(MEDIUM_LINE);
            addLine("Change Delta(s)");

            for (DiffDelta diffDelta : diffResult.getChangeDeltas()) {
                addLine(SHORT_LINE);
                addLine(String.format(DELTA_SECTION_HEADER, diffDelta.getStartPosition(), diffDelta.getEndPosition()));
                for (int i = 0; i < diffDelta.getOriginalLines().size(); i++) {
                    String originalLine = diffDelta.getOriginalLines().get(i).getLine();
                    String revisedLine = diffDelta.getRevisedLines().get(i).getLine();
                    addLine(String.format(CHANGED_LINE_BEFORE_FORMAT, originalLine));
                    addLine(String.format(CHANGED_LINE_AFTER_FORMAT, revisedLine));
                }

            }
        }

        if (diffResult.getInsertDeltas().size() > 0) {
            addLine(MEDIUM_LINE);
            addLine("Insert Delta(s)");

            for (DiffDelta diffDelta : diffResult.getInsertDeltas()) {
                addLine(SHORT_LINE);
                addLine(String.format(DELTA_SECTION_HEADER, diffDelta.getStartPosition(), diffDelta.getEndPosition()));
                for (DiffLine diffLine : diffDelta.getRevisedLines()) {
                    addLine(String.format(INSERT_LINE_FORMAT, diffLine.getLine()));
                }

            }
        }

        if (diffResult.getDeleteDeltas().size() > 0) {
            addLine(MEDIUM_LINE);
            addLine("Delete Delta(s)");

            for (DiffDelta diffDelta : diffResult.getDeleteDeltas()) {
                addLine(SHORT_LINE);
                addLine(String.format(DELTA_SECTION_HEADER, diffDelta.getStartPosition(), diffDelta.getEndPosition()));
                for (DiffLine diffLine : diffDelta.getRevisedLines()) {
                    addLine(String.format(DELETE_LINE_FORMAT, diffLine.getLine()));
                }

            }
        }

        addLine(REDDIT_LINE);
        addLine(String.format(FOOTER, getDiffBotVersion()));

        String body = stringBuilder.toString();
        stringBuilder = new StringBuilder();

        return body;
    }


    private void addLine(String line) {
        stringBuilder.append(line);
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append(System.lineSeparator());
    }


    private String getDiffBotVersion() {
        return diffBotVersion;
    }
}
