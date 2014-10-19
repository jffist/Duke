package no.priv.garshol.duke;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Tool that is used to estimate EpiLink weights
 */
public class DukeTrain {
  public static void main(String[] args) throws IOException {
    if(args.length != 2){
      System.out.println("Usage: DukeTrain <config-file> <model-file>");
      System.exit(1);
    }
    String configFile = args[0];
    String modelFile = args[1];

    // load the configuration
    Configuration config;
    try {
      config = ConfigLoader.load(configFile);
    } catch (FileNotFoundException e) {
      System.err.println("ERROR: Config file '" + configFile + "' not found!");
      return;
    } catch (SAXParseException e) {
      System.err.println("ERROR: Couldn't parse config file: " + e.getMessage());
      System.err.println("Error in " + e.getSystemId() + ":" +
          e.getLineNumber() + ":" + e.getColumnNumber());
      return;
    } catch (SAXException e) {
      System.err.println("ERROR: Couldn't parse config file: " + e.getMessage());
      return;
    }

    EpilinkWeightsEstimator estimator = new EpilinkWeightsEstimator(config);
    List<DataSource> dataSources = new ArrayList<DataSource>();
    dataSources.addAll(config.getDataSources());
    dataSources.addAll(config.getDataSources(1));
    dataSources.addAll(config.getDataSources(2));
    Map<String, Double> model = estimator.estimateWeights(dataSources);

    saveModel(model, modelFile);
  }

  private static void saveModel(Map<String, Double> model, String modelFile) throws IOException {
    List<String> names = new ArrayList<String>(model.keySet());
    Collections.sort(names);
    FileWriter writer = new FileWriter(modelFile);
    for (String name : names) {
      writer.write(name);
      writer.write(",");
      writer.write(String.format("%g",model.get(name)));
      writer.write("\n");
    }
    writer.close();
  }
}
