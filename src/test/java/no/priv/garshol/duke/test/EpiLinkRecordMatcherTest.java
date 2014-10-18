package no.priv.garshol.duke.test;


import no.priv.garshol.duke.*;
import no.priv.garshol.duke.comparators.ExactComparator;
import no.priv.garshol.duke.comparators.JaroWinkler;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static no.priv.garshol.duke.test.TestUtils.makeRecord;
import static org.junit.Assert.assertEquals;

public class EpiLinkRecordMatcherTest {

  private EpiLinkRecordMatcher createMatcher(Comparator valueComparator) {
    List<Property> props = new ArrayList();
    props.add(new PropertyImpl("ID"));
    props.add(new PropertyImpl("NAME",  valueComparator, 5));
    props.add(new PropertyImpl("EMAIL", valueComparator, 15));

    ConfigurationImpl config = new ConfigurationImpl();
    config.setProperties(props);
    return new EpiLinkRecordMatcher(config);
  }

  @Test
  public void testZeroWhenNoCommonFields() throws Exception {
    EpiLinkRecordMatcher matcher = createMatcher(new ExactComparator());

    double actual = matcher.estimateMatchProbability(
        makeRecord("ID", "1", "NAME", "A", "EMAIL", "XX"),
        makeRecord("ID", "2", "NAME", "B", "EMAIL", "YY")
    );
    assertEquals(0, actual, 1e-08);
  }

  @Test
  public void testWeightedSimilarityForCommonFields_ExactComparator() throws Exception {
    EpiLinkRecordMatcher matcher = createMatcher(new ExactComparator());
    double actual = matcher.estimateMatchProbability(
        makeRecord("ID", "1", "NAME", "A", "EMAIL", "XX"),
        makeRecord("ID", "2", "NAME", "A", "EMAIL", "YY")
    );
    assertEquals("match name", 5.0 / ( 5 + 15), actual, 1e-08);

    actual = matcher.estimateMatchProbability(
        makeRecord("ID", "1", "NAME", "A", "EMAIL", "XX"),
        makeRecord("ID", "2", "NAME", "B", "EMAIL", "XX")
    );
    assertEquals("match email", 15.0 / ( 5 + 15), actual, 1e-08);

    actual = matcher.estimateMatchProbability(
        makeRecord("ID", "1", "NAME", "A", "EMAIL", "XX"),
        makeRecord("ID", "2", "NAME", "A", "EMAIL", "XX")
    );
    assertEquals("match name & email", 1, actual, 1e-08);
  }

  @Test
  public void testWeightedSimilarityForCommonFields_FuzzyComparator() throws Exception {
    EpiLinkRecordMatcher matcher = createMatcher(new JaroWinkler());
    double actual = matcher.estimateMatchProbability(
        makeRecord("ID", "1", "NAME", "AAA", "EMAIL", "E1"),
        makeRecord("ID", "2", "NAME", "ABC", "EMAIL", "E2")
    );
    assertEquals("match name", (0.8 * 5.0 + 0.7 * 15) / ( 5 + 15), actual, 1e-08);
  }

}
