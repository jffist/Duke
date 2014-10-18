
package no.priv.garshol.duke;

/**
 * Represents a property.
 */
public interface Property {

  /**
   * Returns the name of the property.
   */
  public String getName();

  /**
   * Returns true iff the property is an identifying property. These
   * properties are never used for comparisons.
   */
  public boolean isIdProperty();

  // FIXME: should we remove this?
  public boolean isAnalyzedProperty();

  public Comparator getComparator();

  public double getHighProbability();

  public double getLowProbability();

  public double getWeight();

  public Lookup getLookupBehaviour();
  
  /**
   * Sets the comparator used for this property. Note that changing
   * this while Duke is processing may have unpredictable
   * consequences.
   */
  public void setComparator(Comparator comparator);

  /**
   * Sets the high probability used for this property. Note that
   * changing this while Duke is processing may have unpredictable
   * consequences.
   */
  public void setHighProbability(double high);

  /**
   * Sets the low probability used for this property. Note that
   * changing this while Duke is processing may have unpredictable
   * consequences.
   */
  public void setLowProbability(double low);

  /**
   *  set weight of this property for EpiLink matcher
   *  (weight could be treated as importance of a feature, or estimated automatically)
   */
  public void setWeight(double weight);

  /**
   * Iff true the property should not be used for comparing records.
   */
  public boolean isIgnoreProperty();
  
  /**
   * Makes Duke skip this property when comparing records.
   */
  public void setIgnoreProperty(boolean ignore);

  /**
   * Sets the lookup behaviour of this property.
   */
  public void setLookupBehaviour(Lookup lookup);
  
  /**
   * Returns the probability that the records v1 and v2 represent the
   * same entity, based on high and low probability settings etc.
   */
  public double compare(String v1, String v2);

  /**
   * Returns similarity of records v1 and v2
   */
  public double calculateSimilarity(String v1, String v2);

  /**
   * Returns a copy of the property.
   */
  public Property copy();

  /**
   * The lookup behaviour for this property.
   */
  public enum Lookup {
    // means: always look up this property, and require values to match
    REQUIRED,

    // always look up this property
    TRUE,

    // never look up this property
    FALSE,

    // default behaviour (look up if analysis says we should)
    DEFAULT
  }
}
