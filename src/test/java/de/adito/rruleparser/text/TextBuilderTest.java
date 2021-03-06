package de.adito.rruleparser.text;

import de.adito.rruleparser.tokenizer.*;
import de.adito.rruleparser.tokenizer.exception.RRuleTokenizeException;
import de.adito.rruleparser.tokenizer.validation.RRuleValidator;
import de.adito.rruleparser.tokenizer.value.*;
import de.adito.rruleparser.translation.LanguagePackageFragmentTranslator;
import de.adito.rruleparser.translation.language.EnglishTranslation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TextBuilderTest
{
  private static IValueParser valueParser = new RRuleValueParser();
  private static IRRuleTokenizer tokenizer = new RRuleTokenizer(valueParser, new RRuleValidator());
  private static TextBuilder textBuilder = new TextBuilder(new LanguagePackageFragmentTranslator(new EnglishTranslation()));

  private static Stream<Arguments> createTestCases()
  {
    return Stream.of(
        Arguments.of("FREQ=DAILY;INTERVAL=5", "Every 5 days"),
        Arguments.of("FREQ=WEEKLY;INTERVAL=5", "Every 5 weeks"),
        Arguments.of("FREQ=MONTHLY;INTERVAL=5", "Every 5 months"),
        Arguments.of("FREQ=YEARLY;INTERVAL=5", "Every 5 years"),

        Arguments.of("FREQ=DAILY;INTERVAL=1", "Daily"),
        Arguments.of("FREQ=WEEKLY;INTERVAL=1", "Weekly"),
        Arguments.of("FREQ=MONTHLY;INTERVAL=1", "Monthly"),
        Arguments.of("FREQ=YEARLY;INTERVAL=1", "Annually"),

        Arguments.of("FREQ=WEEKLY;INTERVAL=1;BYDAY=MO,TU", "Weekly on Monday, Tuesday"),
        Arguments.of("FREQ=WEEKLY;INTERVAL=2;BYDAY=MO,TU", "Every 2 weeks on Monday, Tuesday"),
        Arguments.of("FREQ=WEEKLY;INTERVAL=2;BYDAY=MO,TU,WE,SA", "Every 2 weeks on Mon, Tue, Wed, Sat"),

        Arguments.of("FREQ=MONTHLY;INTERVAL=1;BYMONTHDAY=5", "Monthly on day 5"),
        Arguments.of("FREQ=MONTHLY;INTERVAL=1;BYMONTHDAY=15", "Monthly on day 15"),
        Arguments.of("FREQ=MONTHLY;INTERVAL=2;BYMONTHDAY=15", "Every 2 months on day 15"),

        Arguments.of("FREQ=MONTHLY;INTERVAL=2;BYDAY=MO;BYSETPOS=-1", "Every 2 months on last Monday"),
        Arguments.of("FREQ=MONTHLY;INTERVAL=2;BYDAY=MO;BYSETPOS=1", "Every 2 months on first Monday"),
        Arguments.of("FREQ=MONTHLY;INTERVAL=2;BYDAY=SA;BYSETPOS=3", "Every 2 months on third Saturday"),

        Arguments.of("FREQ=YEARLY;BYMONTH=1;BYMONTHDAY=1", "Annually on January 01"),
        Arguments.of("FREQ=YEARLY;BYMONTH=4;BYMONTHDAY=5", "Annually on April 05"),

        Arguments.of("FREQ=YEARLY;BYDAY=SU;BYSETPOS=1;BYMONTH=1", "Annually on first Sunday of January"),
        Arguments.of("FREQ=YEARLY;BYDAY=WE;BYSETPOS=-1;BYMONTH=4", "Annually on last Wednesday of April"),


        Arguments.of("FREQ=DAILY;INTERVAL=1;COUNT=2", "Daily, 2 times"),
        Arguments.of("FREQ=WEEKLY;INTERVAL=1;COUNT=1", "Weekly"),
        Arguments.of("FREQ=MONTHLY;INTERVAL=1;UNTIL=20181023T220000Z", "Monthly, until 23 Oct 2018")
    );
  }

  @ParameterizedTest
  @MethodSource("createTestCases")
  @DisplayName("Test some cases for the translation process")
  void testCases(String rrule, String expectedResult) throws RRuleTokenizeException
  {
    IRRuleTokenContainer tokenContainer = tokenizer.tokenize(rrule);
    assertEquals(expectedResult, textBuilder.buildText(tokenContainer));
  }
}