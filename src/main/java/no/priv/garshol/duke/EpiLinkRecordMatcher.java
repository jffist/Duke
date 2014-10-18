package no.priv.garshol.duke;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EpiLinkRecordMatcher implements RecordsMatcher {
  private List<Property> properties;
  private double totalWeight;
  private boolean doStrictChecks = false;

  public EpiLinkRecordMatcher(Configuration config) {
    this.properties = new ArrayList<Property>();
    this.totalWeight = 0;
    for (Property p : config.getProperties()) {
      if (!p.isIdProperty() && !p.isIgnoreProperty()) {
        properties.add(p);
        if (Double.isInfinite(p.getWeight())) {
          throw new IllegalArgumentException("property "+p.getName()+" hasn't got weight for EpiLink records matcher");
        }
        totalWeight += p.getWeight();
      }
    }
    if(properties.isEmpty()){
      throw new IllegalArgumentException("no properties configured for EpiLink records matcher");
    }
  }

  public void setDoStrictChecks(boolean doStrictChecks) {
    this.doStrictChecks = doStrictChecks;
  }

  public boolean isDoStrictChecks() {
    return doStrictChecks;
  }

  @Override
  public double estimateMatchProbability(Record r1, Record r2) {
    double prob = 0;
    for(Property p : properties){
      Collection<String> values1 = r1.getValues(p.getName());
      Collection<String> values2 = r2.getValues(p.getName());

      double propertySimilarity = 0D;
      for(String v1 : values1){
        for(String v2 : values2){
          try {
            propertySimilarity = Math.max(propertySimilarity, p.calculateSimilarity(v1, v2));
          } catch (Exception e) {
            throw new DukeException("Comparison of values '" + v1 + "' and " +
                "'" + v2 + "' with " + p.getComparator() + " failed", e);
          }
        }
      }
      if(doStrictChecks && (propertySimilarity < 0 || propertySimilarity > 1))
        throw new DukeException("property "+p.getName()+" similarity lay out of [0, 1] range for comparator "+p.getComparator());

      prob += p.getWeight() * propertySimilarity;
    }
    return prob / totalWeight;
  }
}

