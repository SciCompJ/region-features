/**
 * 
 */
package net.sci.image.regfeat.morpho2d;


import java.util.Arrays;
import java.util.Collection;

import net.sci.image.Calibration;
import net.sci.image.regfeat.ElementCount;
import net.sci.image.regfeat.Feature;
import net.sci.image.regfeat.RegionFeatures;
import net.sci.image.regfeat.SingleValueFeature;

/**
 * A feature that computes the area of 2D regions.
 * 
 * @see ElementCount
 */
public class Area extends SingleValueFeature
{
    /**
     * Default empty constructor.
     */
    public Area()
    {
        super("Area");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        int[] counts = (int[]) data.results.get(ElementCount.class);
        
        // area of unit voxel
        Calibration calib = data.labelMap.getCalibration();
        double pixelArea = calib.getXAxis().getSpacing() * calib.getYAxis().getSpacing(); 
        
        // compute area from pixel count
        return Arrays.stream(counts)
                .mapToDouble(count -> count * pixelArea)
                .toArray();
    }
    
    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(ElementCount.class);
    }
    
    @Override
    public String[] columnUnitNames(RegionFeatures data)
    {
        return new String[] {data.labelMap.getCalibration().getXAxis().getUnitName() + "^2"};
    }
}
