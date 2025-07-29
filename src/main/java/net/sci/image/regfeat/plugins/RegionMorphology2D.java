/**
 * 
 */
package net.sci.image.regfeat.plugins;

import java.util.ArrayList;
import java.util.Collection;

import imago.app.ImageHandle;
import imago.gui.FramePlugin;
import imago.gui.GenericDialog;
import imago.gui.ImagoFrame;
import imago.gui.image.ImageFrame;
import imago.gui.table.TableFrame;
import net.sci.algo.ConsoleAlgoListener;
import net.sci.image.Image;
import net.sci.image.regionfeatures.Feature;
import net.sci.image.regionfeatures.RegionFeatures;
import net.sci.image.regionfeatures.RegionFeatures.UnitDisplay;
import net.sci.image.regionfeatures.morpho2d.Area;
import net.sci.image.regionfeatures.morpho2d.Circularity;
import net.sci.image.regionfeatures.morpho2d.EulerNumber;
import net.sci.image.regionfeatures.morpho2d.Perimeter;
import net.sci.table.Table;


/**
 * 
 */
public class RegionMorphology2D implements FramePlugin
{
    public static final String[] unitDisplayLabels = new String[] { "None", "Column Names", "New Columns", "New Table" };
    public static final UnitDisplay[] unitDisplayValues = new UnitDisplay[] { UnitDisplay.NONE, UnitDisplay.COLUMN_NAMES, UnitDisplay.NEW_COLUMNS, UnitDisplay.NEW_TABLE };
    
    Options initialOptions = null;
    
    /**
     * Default empty constructor.
     */
    public RegionMorphology2D()
    {
    }
    
    @Override
    public void run(ImagoFrame frame, String optionsString)
    {
        // Check type is image frame
        if (!(frame instanceof ImageFrame))
        {
            return;
        }
        
        // retrieve image data
        ImageHandle handle = ((ImageFrame) frame).getImageHandle();
        Image image = handle.getImage();
        if (!image.isLabelImage())
        {
            throw new IllegalArgumentException("Requires label image as input");
        }
        
        // initialize options if necessary
        if (initialOptions == null)
        {
            initialOptions = new Options();
            initialOptions.features.add(Area.class);
            initialOptions.features.add(Perimeter.class);
            initialOptions.features.add(EulerNumber.class);
            initialOptions.features.add(Circularity.class);
        }
        
        // Choose analysis options from interactive dialog
        Options options = chooseOptions(frame, image, initialOptions);
        
        // If cancel was clicked, features is null
        if (options == null) return;
        
        // keep choices for next plugin call
        initialOptions = options;
        
        Table[] tables = analyze(image, options);
        Table featuresTable = tables[0];
//        if (options.includeImageName)
//        {
//            featuresTable = insertImageNameColumn(featuresTable, image.getName());
//        }
        
        // show result
        featuresTable.setName(image.getName() + "-Morphometry");
        TableFrame tableFrame = TableFrame.create(featuresTable, frame);
        
        if (options.unitDisplay == UnitDisplay.NEW_TABLE)
        {
            TableFrame.create(tables[1], tableFrame);
        }
    }
    
    private static final Options chooseOptions(ImagoFrame frame, Image labelMap, Options initialChoice)
    {
        GenericDialog gd = new GenericDialog(frame, "Region Morphology");
        
        // a collection of check boxes to choose features
        Collection<Class<? extends Feature>> features = initialChoice.features;
        String[] featureNames = new String[] {
                "Area", "Perimeter", 
                "Circularity", "Euler_Number",
//                "Bounding_Box", "Centroid",
//                "Equivalent_Ellipse", "Ellipse_Elongation",
//                "Convexity", "Max_Feret_Diameter",
//                "Oriented_Box", "Oriented_Box_Elongation",
//                "Geodesic_Diameter", "Tortuosity",
//                "Max_Inscribed_Disk", "Average_Thickness",
//                "Geodesic_Elongation",
        };
        boolean[] states = new boolean[] {
                features.contains(Area.class), features.contains(Perimeter.class),
                features.contains(Circularity.class), features.contains(EulerNumber.class),
//                features.contains(Bounds.class), features.contains(Centroid.class),
//                features.contains(EquivalentEllipse.class), features.contains(EllipseElongation.class),
//                features.contains(Convexity.class), features.contains(MaxFeretDiameter.class),
//                features.contains(OrientedBoundingBox.class), features.contains(OrientedBoxElongation.class),
//                features.contains(GeodesicDiameter.class), features.contains(Tortuosity.class),
//                features.contains(LargestInscribedDisk.class), features.contains(AverageThickness.class),
//                features.contains(GeodesicElongation.class),
        };
        gd.addCheckboxGroup(featureNames.length / 2 + 1, 2, featureNames, states, new String[] {"Features:", ""});
        
        gd.addMessage("");
        gd.addChoice("Unit_Display", unitDisplayLabels, unitDisplayLabels[1]);
        gd.addCheckBox("Include_Image_Name", initialChoice.includeImageName);
        
        // Display dialog and wait for user validation
        gd.showDialog();
        if (gd.wasCanceled())
        {
            return null;
        }
        
        // Extract features to quantify from image
        Options options = new Options();
        features = options.features;
        // if (gd.getNextBoolean()) features.add(Feature.PIXEL_COUNT);
        if (gd.getNextBoolean()) features.add(Area.class);
        if (gd.getNextBoolean()) features.add(Perimeter.class);
        if (gd.getNextBoolean()) features.add(Circularity.class);
        if (gd.getNextBoolean()) features.add(EulerNumber.class);
//        if (gd.getNextBoolean()) features.add(Bounds.class);
//        if (gd.getNextBoolean()) features.add(Centroid.class);
//        if (gd.getNextBoolean()) features.add(EquivalentEllipse.class);
//        if (gd.getNextBoolean()) features.add(EllipseElongation.class);
//        if (gd.getNextBoolean()) features.add(Convexity.class);
//        if (gd.getNextBoolean()) features.add(MaxFeretDiameter.class);
//        if (gd.getNextBoolean()) features.add(OrientedBoundingBox.class);
//        if (gd.getNextBoolean()) features.add(OrientedBoxElongation.class);
//        if (gd.getNextBoolean()) features.add(GeodesicDiameter.class);
//        if (gd.getNextBoolean()) features.add(Tortuosity.class);
//        if (gd.getNextBoolean()) features.add(LargestInscribedDisk.class);
//        if (gd.getNextBoolean()) features.add(AverageThickness.class);
//        if (gd.getNextBoolean()) features.add(GeodesicElongation.class);
        
        options.unitDisplay = unitDisplayValues[gd.getNextChoiceIndex()];
        options.includeImageName = gd.getNextBoolean();

        return options;
    }
    
    private static final Table[] analyze(Image imagePlus, Options options)
    {
//        // retrieve dimensions
//        int nChannels = imagePlus.getNChannels();
//        int nFrames = imagePlus.getNFrames();
//        
//        // process simple case
//        if (nChannels * nFrames == 1)
//        {
            return analyzeSingleSlice(imagePlus, options);
//        }
//        
//        ImageStack stack = imagePlus.getStack();
//        ArrayList<ResultsTable> allTables = new ArrayList<ResultsTable>(nChannels * nFrames);
//        ResultsTable unitsTable = null;
//
//        // iterate over slices 
//        for (int iFrame = 0; iFrame < nFrames; iFrame++)
//        {
//            for (int iChannel = 0; iChannel < nChannels; iChannel++)
//            {
//                int index = imagePlus.getStackIndex(iChannel, 0, iFrame);
//                ImageProcessor array = stack.getProcessor(index);
//                ImagePlus sliceImage = new ImagePlus(imagePlus.getTitle(), array);
//                sliceImage.copyScale(imagePlus);
//
//                ResultsTable[] tables = analyzeSingleSlice(sliceImage, options);
//                allTables.add(tables[0]);
//                unitsTable = tables[1];
//            }
//        }
//
//        Table res = new Table();
//        Iterator<Table> iter = allTables.iterator();
//        
//        // create string patterns
//        // TODO: should also add iteration on slices within a 3D image
//        String pattC = "_c%0" + Math.max((int) Math.ceil(Math.log10(nChannels-1)), 1) + "d";
//        String pattT = "_t%0" + Math.max((int) Math.ceil(Math.log10(nFrames-1)), 1) + "d";
//        StringBuilder sb = new StringBuilder();
//        
//        // iterate over individual tables
//        for (int iFrame = 0; iFrame < nFrames; iFrame++)
//        {
//            String tStr = String.format(pattT, iFrame);
//            
//            for (int iChannel = 0; iChannel < nChannels; iChannel++)
//            {
//                String cStr = String.format(pattC, iChannel);
//                
//                Table tbl = iter.next();
//                for (int iRow = 0; iRow < tbl.getCounter(); iRow++)
//                {
//                    // start new row
//                    res.incrementCounter();
//                    
//                    String labelString = tbl.getLabel(iRow);
//                    
//                    // create label for the new row
//                    sb.setLength(0);
//                    sb.append("L" + labelString);
//                    if (nFrames > 1) sb.append(tStr);
//                    if (nChannels > 1) sb.append(cStr);
//                    res.addLabel(sb.toString());
//                    
//                    // add columns for meta-data
//                    res.addValue("Region", Integer.parseInt(labelString));
//                    if (nChannels > 1) res.addValue("Channel", iChannel);
//                    if (nFrames > 1) res.addValue("Frame", iFrame);
//                    
//                    // copy all column values
//                    for (String colName : tbl.getHeadings())
//                    {
//                        if ("Label".equalsIgnoreCase(colName)) continue;
//                        res.addValue(colName, tbl.getValue(colName, iRow));
//                    }
//                }
//            }
//        }
//        
//        return new Table[] {res, unitsTable};
    }
    
    private static final Table[] analyzeSingleSlice(Image imagePlus, Options options)
    {
        // create a Region feature analyzer from options
        RegionFeatures analyzer = options.createAnalyzer(imagePlus);
        
        // Call the main processing method
        // TODO: add frame listener
        ConsoleAlgoListener.monitor(analyzer);
        return analyzer.createTables();
    }

    static class Options
    {
        /**
         * The list of features to compute.
         */
        ArrayList<Class<? extends Feature>> features = new ArrayList<>();
        
        /**
         * Display calibration unit within table column names, when appropriate.
         */
        boolean displayUnits = false;
        
        UnitDisplay unitDisplay = UnitDisplay.COLUMN_NAMES; 
        
        /**
         * Can be useful when concatenating results obtained on different images
         * into a single table.
         */
        boolean includeImageName = false;
        
        /**
         * Creates a new Region Feature Analyzer for the specified image.
         * 
         * @param image
         *            the image containing the label map.
         * @return a new RegionFeatures instance.
         */
        public RegionFeatures createAnalyzer(Image image)
        {
            RegionFeatures analyzer = RegionFeatures.initialize(image);
            features.stream().forEachOrdered(feature -> analyzer.add(feature));
            analyzer.unitDisplay = this.unitDisplay;
            return analyzer;
        }
    }
}
