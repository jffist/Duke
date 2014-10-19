package no.priv.garshol.duke.test;


import no.priv.garshol.duke.*;
import no.priv.garshol.duke.comparators.ExactComparator;
import no.priv.garshol.duke.datasources.CSVDataSource;
import no.priv.garshol.duke.datasources.InMemoryDataSource;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static no.priv.garshol.duke.test.TestUtils.makeRecord;
import static org.junit.Assert.assertEquals;

public class EpilinkWeightsEstimatorTest {
  @Test
  public void test() throws Exception {
    ExactComparator comparator = new ExactComparator();
    List<Property> props = new ArrayList();
    props.add(new PropertyImpl("ID"));
    props.add(new PropertyImpl("NAME",  comparator, 5));
    props.add(new PropertyImpl("EMAIL", comparator, 15));

    ConfigurationImpl config = new ConfigurationImpl();
    config.setProperties(props);
    //ds1
    InMemoryDataSource ds1 = new InMemoryDataSource();
    ds1.add(makeRecord("ID","1","NAME","N1","EMAIL","E1"));
    ds1.add(makeRecord("ID","2","NAME","N1","EMAIL","E2"));
    ds1.add(makeRecord("ID","3","NAME","N2","EMAIL","E3"));
    ds1.add(makeRecord("ID","4","EMAIL","E4","EMAIL","E5"));
    //ds2
    InMemoryDataSource ds2 = new InMemoryDataSource();
    ds2.add(makeRecord("ID","5","NAME","N1","NAME","N3"));
    ds2.add(makeRecord("ID","6","NAME","N3","EMAIL","E6"));
    //name  - 3 unique values
    //email - 6 unique values
    EpilinkWeightsEstimator estimator = new EpilinkWeightsEstimator(config);
    Map<String, Double> model = estimator.estimateWeights(Arrays.<DataSource>asList(ds1, ds2));

    assertEquals(2, model.size());
    double expectedNameWeight = Math.log((1D - 0.01) * 3D) / Math.log(2);
    assertEquals(expectedNameWeight, model.get("NAME"), 1e-08);

    double expectedEmailWeight = Math.log((1D - 0.01) * 6D) / Math.log(2);
    assertEquals(expectedEmailWeight, model.get("EMAIL"), 1e-08);
  }
}
