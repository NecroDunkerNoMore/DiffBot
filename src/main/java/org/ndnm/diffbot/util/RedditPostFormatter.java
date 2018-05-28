package org.ndnm.diffbot.util;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.ndnm.diffbot.model.ArchivedUrl;
import org.ndnm.diffbot.model.diff.DiffDelta;
import org.ndnm.diffbot.model.diff.DiffLine;
import org.ndnm.diffbot.model.diff.DiffResult;
import org.ndnm.diffbot.service.ArchivedUrlService;
import org.springframework.stereotype.Component;

import com.github.difflib.algorithm.DiffException;
import com.github.difflib.text.DiffRow;
import com.github.difflib.text.DiffRowGenerator;

/*
 * Class uses a StringBuilder to generate each section of a post,
 * and will reset the builder when the full comment is done being
 * built.
 *
 * New logic will add word-by-word formatting that highlights
 * changes by applying:
 * - Change: bold
 * - Insert: bold
 * - Delete: strikethough
 *
 *
 */
@Component
public class RedditPostFormatter {
    private static final String CHANGE_SECTION_TITLE = "#Change Delta(s)";
    private static final String INSERT_SECTION_TITLE = "#Insert Delta(s)";
    private static final String DELETE_SECTION_TITLE = "#Delete Delta(s)";
    private static final String DELTA_SECTION_HEADER = "**(Delta starting at line %d, ending at line %d)**";
    private static final String FOOTER = "^[FAQ](https://np.reddit.com/r/TheEssaysChanged/wiki/index)&nbsp;| ^[Source&nbsp;Code](https://github.com/NecroDunkerNoMore/DiffBot)&nbsp;| ^[PM&nbsp;Developer](https://www.reddit.com/message/compose?to=NecroDunkerNoMore&subject=NecroDunkerNoMore)&nbsp;| ^v%s";
    private static final String REDDIT_LINE = "-----";

    @Resource(name = "diffBotVersion")
    private String diffBotVersion;
    private ArchivedUrlService archivedUrlService;
    private static StringBuilder stringBuilder = new StringBuilder();


    public RedditPostFormatter(ArchivedUrlService archivedUrlService) {
        this.archivedUrlService = archivedUrlService;
    }


    public String formatPostTitle(DiffResult diffResult) {
        int deltaCount = diffResult.getNumDeltas();
        String websiteUrl = diffResult.getDiffUrl().getSourceUrl();

        return String.format("Detected %d Deltas for: %s", deltaCount, websiteUrl);
    }


    public String formatCommentBody(DiffResult diffResult) {
        generateHeader(diffResult);
        generateStatsTable(diffResult);
        generateFormattingLegend();
        generateChangeDeltaSection(diffResult);
        generateInsertDeltaSection(diffResult);
        generateDeleteDeltaSection(diffResult);
        generateFooter();

        String body = stringBuilder.toString();

        // Can only post 10k chars, so avoid posting and not being able to comment
        if (body.length() > 10*1000) {
            // TODO: Implement multi-comment to chunk when over 10k
            stringBuilder = new StringBuilder();
            throw new RuntimeException("Post exceeds api limit: post.length(): " + body.length());
        }

        stringBuilder = new StringBuilder();

        return body;
    }


    private void generateHeader(DiffResult diffResult) {
        String dateString = TimeUtils.formatGmt(diffResult.getDateCaptured());
        addLineWithTwoNewlines(REDDIT_LINE);
        addLineWithTwoNewlines(String.format("#%s: %d Deltas(s) from: %s", dateString, diffResult.getNumDeltas(), diffResult.getDiffUrl().getSourceUrl()));
        addLineWithOneNewline(getArchiveLinkLine(diffResult));
        addLineWithOneNewline("");
        addLineWithTwoNewlines(REDDIT_LINE);
    }


    private String getArchiveLinkLine(DiffResult diffResult) {
        List<ArchivedUrl> archivedUrls = getArchivedUrlService().findAllByDiffUrlId(diffResult.getDiffUrl().getId());
        int numBotObservedPageChanges = archivedUrls.size() - 1;//First archive was from very first bot initialization
        String archiveHistoryLink = String.format("http://archive.is/%s", diffResult.getDiffUrl().getSourceUrl());

        return String.format("*%d occasion(s) bot has detected this page changing; see [full archive history here](%s)*",
                numBotObservedPageChanges, archiveHistoryLink);
    }


    private void generateStatsTable(DiffResult diffResult) {
        addLineWithTwoNewlines("## Current Changes Detected:");
        addLineWithOneNewline("Count|Type");
        addLineWithOneNewline("---|---");
        addLineWithOneNewline(String.format("%d | Modification delta(s)", diffResult.getChangeDeltas().size()));
        addLineWithOneNewline(String.format("%d | Insertion delta(s)", diffResult.getInsertDeltas().size()));
        addLineWithOneNewline(String.format("%d | Deletion delta(s)", diffResult.getDeleteDeltas().size()));
        addLineWithOneNewline(String.format("%d | Total lines affected", diffResult.getTotalLinesAffected()));
        addLineWithOneNewline(REDDIT_LINE);
    }


    private void generateFormattingLegend() {
        addLineWithTwoNewlines("## Diff Formatting");
        addLineWithTwoNewlines("Reddit's markdown is extremely limited, so this bot uses the following formats:");
        addLineWithTwoNewlines("Normal Text: No change/insert/delete");
        addLineWithTwoNewlines("Bold Text: **Change/Insert**");
        addLineWithTwoNewlines("Strikethrough: ~~Delete~~");
    }


    private void generateChangeDeltaSection(DiffResult diffResult) {
        generateDeltaSection(diffResult.getChangeDeltas(), CHANGE_SECTION_TITLE);
    }


    private void generateInsertDeltaSection(DiffResult diffResult) {
        generateDeltaSection(diffResult.getInsertDeltas(), INSERT_SECTION_TITLE);
    }


    private void generateDeleteDeltaSection(DiffResult diffResult) {
        generateDeltaSection(diffResult.getDeleteDeltas(), DELETE_SECTION_TITLE);
    }


    private void generateDeltaSection(List<DiffDelta> diffDeltas, String title) {
        if (diffDeltas != null && !diffDeltas.isEmpty()) {
            addLineWithTwoNewlines(REDDIT_LINE);
            addLineWithTwoNewlines(title);

            boolean first = true;
            for (DiffDelta diffDelta : diffDeltas) {
                if (!first) {
                    addLineWithTwoNewlines("&nbsp;");
                }
                first = false;

                addLineWithTwoNewlines(String.format(DELTA_SECTION_HEADER, diffDelta.getStartPosition(), diffDelta.getEndPosition()));
                generateDiffTableFromLines(diffDelta);
            }//for
        }//if
    }


    /*
     * A couple of hacks here to get formatting to work across reddit old, reddit new,
     * and RES:
     * - For bolding, I had to wrap the tokens in '&shy;' which is a 'soft hyphen'
     *   that isn't visible, and doesn't change spacing
     * - I escape all periods, since those broke rendering too
     */
    private void generateDiffTableFromLines(DiffDelta diffDelta) {
        /*
         * Settings will produce a table with two columns -- first col
         * is the original line, the second col is the revised row. The
         * text will be formatted to show deletions as strikethough, and
         * additions/changes as bold. In the cases of full line
         * deletion/insertion, one of the rows columns will be empty
         * to indicate this.
         */
        DiffRowGenerator generator = DiffRowGenerator.create()
                .showInlineDiffs(true)
                .inlineDiffByWord(true)
                .ignoreWhiteSpaces(true)
                .oldTag(f -> "~~")//Strikethrough for deletes
                .newTag(f -> "&shy;**&shy;")//Bold for inserts
                .build();

        // Generator only works with lines as strings
        List<String> originalLines = getStringListFromDiffLines(diffDelta.getOriginalLines());
        List<String> revisedLines = getStringListFromDiffLines(diffDelta.getRevisedLines());

        // All prepped, now execute
        List<DiffRow> rows;
        try {
            rows = generator.generateDiffRows(
                    originalLines,
                    revisedLines);
        } catch (DiffException e) {
            throw new RuntimeException(e);
        }

        // Add a table header, then each of the formatted lines found
        // in the delta
        addLineWithOneNewline("|Original|Revised|");
        addLineWithOneNewline("|---|---|");
        for (DiffRow row : rows) {
            String formattedRow = "|" + row.getOldLine() + "|" + row.getNewLine() + "|";
            formattedRow = formattedRow.replaceAll("\\.", "\\\\.");//snoodown hack escaping periods
            addLineWithOneNewline(formattedRow);
        }

        addLineWithTwoNewlines("&nbsp;");

    }


    private List<String> getStringListFromDiffLines(List<DiffLine> diffLines) {
        List<String> stringList = new ArrayList<>();
        for (DiffLine diffLine : diffLines) {
            stringList.add(diffLine.getLine());
        }

        return stringList;
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


    private ArchivedUrlService getArchivedUrlService() {
        return archivedUrlService;
    }

}
