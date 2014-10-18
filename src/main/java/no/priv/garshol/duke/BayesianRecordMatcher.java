package no.priv.garshol.duke;


import no.priv.garshol.duke.utils.Utils;

import java.util.Collection;

public class BayesianRecordMatcher implements RecordsMatcher {
  private Configuration config;

  public BayesianRecordMatcher(Configuration config) {
    this.config = config;
  }

  @Override
  public double estimateMatchProbability(Record r1, Record r2) {
    double prob = 0.5;
    for (String propname : r1.getProperties()) {
      Property prop = config.getPropertyByName(propname);
      if (prop == null)
        continue; // means the property is unknown
      if (prop.isIdProperty() || prop.isIgnoreProperty())
        continue;

      Collection<String> vs1 = r1.getValues(propname);
      Collection<String> vs2 = r2.getValues(propname);
      if (vs1 == null || vs1.isEmpty() || vs2 == null || vs2.isEmpty())
        continue; // no values to compare, so skip

      double high = 0.0;
      for (String v1 : vs1) {
        if (v1.equals("")) // FIXME: these values shouldn't be here at all
          continue;

        for (String v2 : vs2) {
          if (v2.equals("")) // FIXME: these values shouldn't be here at all
            continue;

          try {
            double p = prop.compare(v1, v2);
            high = Math.max(high, p);
          } catch (Exception e) {
            throw new DukeException("Comparison of values '" + v1 + "' and "+
                "'" + v2 + "' with " +
                prop.getComparator() + " failed", e);
          }
        }
      }

      prob = Utils.computeBayes(prob, high);
    }
    return prob;
  }
}
