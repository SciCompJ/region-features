/**
 * 
 */
package net.sci.image.regfeat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.sci.algo.Algo;
import net.sci.algo.AlgoEvent;
import net.sci.algo.AlgoListener;
import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.color.ColorMap;
import net.sci.array.color.ColorMaps;
import net.sci.array.numeric.Int;
import net.sci.array.numeric.IntArray;
import net.sci.axis.CategoricalAxis;
import net.sci.image.Image;
import net.sci.image.label.LabelImages;
import net.sci.table.CategoricalColumn;
import net.sci.table.Column;
import net.sci.table.NumericColumn;
import net.sci.table.Table;
import net.sci.table.impl.ColumnsTable;

/**
 * The main class of the plugin, that gathers all the information necessary to
 * analyze image as well as the results. The class contains:
 * <ul>
 * <li>a reference to the label map representing the regions to analyze</li>
 * <li>the list of features to analyze</li>
 * <li>for each feature, the result of computation</li>
 * <li>general options for computing features and presenting the results</li>
 * </ul>
 */
public class RegionFeatures extends AlgoStub
{
    // ==================================================
    // Enumerations
    
    /**
     * Specifies how to manage the display of unit names.
     */
    public enum UnitDisplay
    {
        /** Do not display unit names */
        NONE,
        /** Append unit names to column names */
        COLUMN_NAMES,
        /** Create new columns containing unit names */
        NEW_COLUMNS,
        /** Create a new table with column names as rows, ands unit names in a column */
        NEW_TABLE
    }
    
    
    // ==================================================
    // Static methods
    
    public static final RegionFeatures initialize(Image labelMapImage)
    {
        Array<?> array = labelMapImage.getData();
        
        if (!Int.class.isAssignableFrom(array.elementClass()))
        {
            throw new RuntimeException("Requires an image containing an instance of IntArray");
        }
        
        // cast to Int Array
        @SuppressWarnings({ "unchecked", "rawtypes" })
        IntArray<?> intArray = IntArray.wrap((Array<? extends Int>) array);
        return new RegionFeatures(labelMapImage, LabelImages.findAllLabels(intArray));
    }
    
    public static final RegionFeatures initialize(Image image, int[] labels)
    {
        return new RegionFeatures(image, labels);
    }
    
    
    // ==================================================
    // Class members
    
    /**
     * The image containing the map of region label for each pixel / voxel.
     */
    public Image labelMap;
    
    /**
     * The labels of the regions to be analyzed.
     */
    public int[] labels;
    
    /**
     * The classes of the features that will be used to populate the data table.
     */
    Collection<Class<? extends Feature>> featureClasses = new ArrayList<>();
    
    /**
     * The map of features indexed by their class. When feature is created for
     * the first time, it is indexed within the results class to retrieve it in
     * case it is requested later.
     */
    public Map<Class<? extends Feature>, Feature> features;
    
    /**
     * A map for storing optional data that can be used to compute additional
     * features, for example region intensities.
     */
    public Map<String, Image> imageData;
    
    /**
     * The results computed for each feature. 
     */
    public Map<Class<? extends Feature>, Object> results;
    
    public Color[] labelColors;
    
    public UnitDisplay unitDisplay = UnitDisplay.NONE;
    
    
    // ==================================================
    // Constructors
    
    public RegionFeatures(Image labelMapImage, int[] labels)
    {
        // store locally label map data
        this.labelMap = labelMapImage;
        this.labels = labels;
        
        // initialize data structures
        this.features = new HashMap<Class<? extends Feature>, Feature>();
        this.imageData = new HashMap<String, Image>();
        this.results = new HashMap<Class<? extends Feature>, Object>();
        
        // additional setup
        createLabelColors(this.labels.length);
    }
    
    private void createLabelColors(int nLabels)
    {
        ColorMap lut = ColorMaps.GLASBEY.createColorMap(nLabels);
        this.labelColors = new Color[nLabels];
        for (int i = 0; i < nLabels; i++)
        {
            this.labelColors[i] = awtColor(lut.getColor(i));
        }
    }
    
    private static final java.awt.Color awtColor(net.sci.array.color.Color color)
    {
        return new java.awt.Color((float) color.red(), (float) color.green(), (float) color.blue());
    }
    
    
    // ==================================================
    // Processing methods
    
    /**
     * Updates the informations stored within this result class with the feature
     * identified by the specified class, if it is not already computed.
     * 
     * @param featureClass
     *            the class to compute
     */
    public void process(Class<? extends Feature> featureClass)
    {
        if (isComputed(featureClass)) return;
        
        Feature feature = getFeature(featureClass);
        ensureRequiredFeaturesAreComputed(feature);
        
        // compute feature, and index into results
        this.fireStatusChanged(this, "Compute feature: " + featureClass.getSimpleName());
        
        // propagate algorithm event of feature to the RegionFeature listeners
        if (feature instanceof Algo)
        {
            ((Algo) feature).addAlgoListener(new AlgoListener() {

                @Override
                public void algoProgressChanged(AlgoEvent evt)
                {
                    fireProgressChanged(evt);
                }

                @Override
                public void algoStatusChanged(AlgoEvent evt)
                {
                    fireStatusChanged(evt);
                }
            });
        }
        
        // store within the results class
        this.results.put(featureClass, feature.compute(this));
    }
    
    public boolean isComputed(Class<? extends Feature> featureClass)
    {
        return results.containsKey(featureClass);
    }
    
    public Feature getFeature(Class<? extends Feature> featureClass)
    {
        Feature feature = this.features.get(featureClass);
        if (feature == null)
        {
            feature = Feature.create(featureClass);
            this.features.put(featureClass, feature);
        }
        return feature;
    }
    
    public void ensureRequiredFeaturesAreComputed(Feature feature)
    {
        feature.requiredFeatures().stream()
            .filter(fc -> !isComputed(fc))
            .forEach(fc -> process(fc));
    }

    public RegionFeatures add(Class<? extends Feature> featureClass)
    {
        this.featureClasses.add(featureClass);
        return this;
    }
    
    public boolean contains(Class<? extends Feature> featureClass)
    {
        return this.featureClasses.contains(featureClass);
    }
    
    public RegionFeatures addImageData(String dataName, Image image)
    {
        this.imageData.put(dataName, image);
        return this;
    }
    
    public Image getImageData(String dataName)
    {
        return this.imageData.get(dataName);
    }
    
    public RegionFeatures computeAll()
    {
        this.featureClasses.stream().forEach(this::process);
        return this;
    }
    
    /**
     * Creates a new results table from the different features contained within
     * the class.
     * 
     * @return a new Table containing a summary of the computed features.
     */
    public Table createTable()
    {
        return createTables()[0];
    }
    
    /**
     * Returns an array containing two Tables: one with the feature
     * results, another one containing the unit associated to each column in the
     * first table.
     * 
     * @return an array of two Table.
     */
    public Table[] createTables()
    {
        // ensure everything is computed
        this.fireStatusChanged(this, "RegionFeatures: compute all features");
        computeAll();
        
        this.fireStatusChanged(this, "RegionFeatures: create result tables");
        Table fullTable = initializeRegionTable();
        ColumnsTable columnUnitsTable = new ColumnsTable();
        
        ArrayList<String> allColNames = new ArrayList<>();
        ArrayList<String> allUnitNames = new ArrayList<>();
        
        // update the global table with each feature
        for (Class<? extends Feature> featureClass : this.featureClasses)
        {
            if (!isComputed(featureClass))
            {
                throw new RuntimeException("Feature has not been computed: " + featureClass);
            }
            
            Feature feature = getFeature(featureClass);
            if (feature instanceof RegionTabularFeature tabularFeature)
            {
                // create table associated to feature
                Table table = tabularFeature.createTable(this);
                
                // also retrieve information about columns 
                String[] colNames = table.getColumnNames();
                String[] unitNames = tabularFeature.columnUnitNames(this);
                
                // switch processing depending on the strategy for managing unit names
                switch(unitDisplay)
                {
                    case NONE:
                        // simply append columns to the full table
                        for (Column col : table.columns())
                        {
                            fullTable.addColumn(col);
                        }
                        break;
                    case COLUMN_NAMES:
                        // update columns names before appending to the full tables
                        for (Column col : table.columns())
                        {
                            Column col2 = col.duplicate();
                            System.out.println("col name: " + col.getName() + " / " + col2.getName());
                            
                            // append unit name to column name if necessary
                            if (col2 instanceof NumericColumn numCol)
                            {
                                String unitName = numCol.getUnitName();
                                if (unitName != null)
                                {
                                    col2.setName(String.format("%s_(%s)", col.getName(), unitName));
                                }
                            }
                            fullTable.addColumn(col2);
                        }
                        break;
                        
                    case NEW_COLUMNS:
                        // append columns and new columns containing unit names
                        for (Column col : table.columns())
                        {
                            fullTable.addColumn(col);
                            
                            // add a new colmumn containing unit name 
                            if (col instanceof NumericColumn numCol)
                            {
                                String unitName = numCol.getUnitName();
                                if (unitName != null)
                                {
                                    // add a new column containing unit name
                                    String unitColName = col.getName() + "_unit";
                                    int[] indices = new int[col.length()];
                                    CategoricalColumn unitCol = CategoricalColumn.create(unitColName, indices, new String[] {unitName});
                                    fullTable.addColumn(unitCol);
                                }
                            }
                        }
                        break;
                        
                    case NEW_TABLE:
                        // append full table, and update the columnUnits table
                        for (Column col : table.columns())
                        {
                            fullTable.addColumn(col);
                        }
                        // TODO: could be updated
                        for (int c = 0; c < colNames.length; c++)
                        {
                            allColNames.add(colNames[c]);
                            String unitName = unitNames != null && c < unitNames.length ? unitNames[c] : ""; 
                            allUnitNames.add(unitName);
                        }
                        break;

                    default:
                        throw new RuntimeException("Unknown strategy for managing units");
                }
            }
        }
        
        if (unitDisplay == UnitDisplay.NEW_TABLE)
        {
            CategoricalColumn unitColumn = CategoricalColumn.create("Unit", allUnitNames.toArray(String[]::new));
            CategoricalAxis rowAxis = CategoricalAxis.create("Feature", allColNames.toArray(String[]::new));
            columnUnitsTable = new ColumnsTable(unitColumn);
            columnUnitsTable.setRowAxis(rowAxis);
        }
        
        return new Table[] {fullTable, columnUnitsTable};
    }
    
    public RegionFeatures unitDisplay(UnitDisplay unitDisplay)
    {
        this.unitDisplay = unitDisplay;
        return this;
    }
    
    public RegionFeatures displayUnitsInTable(boolean flag)
    {
        this.unitDisplay = flag ? UnitDisplay.COLUMN_NAMES : UnitDisplay.NONE;
        return this;
    }
    
    public Table initializeRegionTable()
    {
        // Initialize label column in table
        int nLabels = this.labels.length;
        Table table = Table.create(nLabels, 0);
        String[] rowNames = new String[nLabels];
        for (int i = 0; i < this.labels.length; i++)
        {
            rowNames[i] = "" + this.labels[i];
        }
        table.setRowAxis(new CategoricalAxis("Label", rowNames));
        return table;
    }

    public void printComputedFeatures()
    {
        results.keySet().stream().forEach(c -> System.out.println(c.getSimpleName()));
    }
}
