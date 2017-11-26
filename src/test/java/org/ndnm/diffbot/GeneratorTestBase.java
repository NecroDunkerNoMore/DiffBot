package org.ndnm.diffbot;

import static org.ndnm.diffbot.util.DiffGenerator.MULTI_NEWLINE_REGEX;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.text.CharacterPredicate;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.ndnm.diffbot.util.TimeUtils;

public class GeneratorTestBase {
    protected static String originalFileAsString;
    protected static String revisedFileAsString;
    protected static List<String> originalFileAsLines;
    protected static List<String> revisedFileAsLines;
    private static RandomStringGenerator stringGenerator;

    @BeforeClass
    public static void setup() {
        try {
            originalFileAsString = new String(Files.readAllBytes(Paths.get("src/test/resources/raw_data/original.html")));
            revisedFileAsString = new String(Files.readAllBytes(Paths.get("src/test/resources/raw_data/revised.html")));
        } catch (Exception e) {
            Assert.fail("Could not load test files!: " + e.getMessage());
            return;
        }

        originalFileAsLines = Arrays.asList(originalFileAsString.split(MULTI_NEWLINE_REGEX));
        revisedFileAsLines = Arrays.asList(revisedFileAsString.split(MULTI_NEWLINE_REGEX));

        SecureRandom rand = new SecureRandom();
        stringGenerator = new RandomStringGenerator
                .Builder()
                .usingRandom(rand::nextInt).withinRange(0, 'z')
                .filteredBy(new AlphaNumericPredicate())
                .build();
    }


    public static Date getTruncatedDate() {
        Date date = TimeUtils.getTimeGmt();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND, 0);
        date.setTime(calendar.getTimeInMillis());

        return date;
    }


    protected String generateRandomString(int length) {
        return stringGenerator.generate(length);
    }


    static class AlphaNumericPredicate implements CharacterPredicate {
        @Override
        public boolean test(int codePoint) {
            return CharacterPredicates.DIGITS.test(codePoint) ||
                    CharacterPredicates.LETTERS.test(codePoint);

        }
    }

}
