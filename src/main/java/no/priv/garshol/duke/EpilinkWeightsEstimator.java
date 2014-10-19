package no.priv.garshol.duke;


import java.util.*;

public class EpilinkWeightsEstimator {
  private final double correctionError = 0.01;    //todo: add justification & reasoning behind this number
  private Configuration configuration;

  public EpilinkWeightsEstimator(Configuration configuration) {
    this.configuration = configuration;
  }

  public Map<String, Double> estimateWeights(Collection<DataSource> dataSources) {
    Map<String, Set<String>> propertyUniqueValues = initUniqueValuesMap(configuration);
    List<String> propNames = new ArrayList<String>(propertyUniqueValues.keySet());

    for (DataSource dataSource : dataSources) {
      RecordIterator recordIterator = dataSource.getRecords();
      while (recordIterator.hasNext()){
        Record record = recordIterator.next();
        for(String propName : propNames) {
          propertyUniqueValues.get(propName).addAll(record.getValues(propName));
        }
      }
    }

    Collections.sort(propNames);
    Map<String, Double> weights = new LinkedHashMap<String, Double>(propNames.size());
    for (String propName : propNames) {
      Set<String> uniqVals = propertyUniqueValues.get(propName);
      double w = 0D;
      if(!uniqVals.isEmpty()){
        w = Math.log( (1 - correctionError) * uniqVals.size() ) / Math.log(2);
      }
      weights.put(propName, w);
    }
    return weights;
  }

  private Map<String, Set<String>> initUniqueValuesMap(Configuration configuration) {
    Map<String, Set<String>> map = new HashMap<String, Set<String>>();
    for (Property property : configuration.getProperties()) {
      if (!property.isIdProperty() && !property.isIgnoreProperty()) {
        map.put(property.getName(), new HashSet<String>());
      }
    }
    if(map.isEmpty())
      throw new DukeConfigException("no active properties found to estimate EpiLink for");
    return map;
  }
}
