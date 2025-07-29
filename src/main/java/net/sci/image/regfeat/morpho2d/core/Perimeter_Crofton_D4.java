/**
 * 
 */
package net.sci.image.regfeat.morpho2d.core;

import java.util.Arrays;
import java.util.Collection;

import net.sci.image.Calibration;
import net.sci.image.analyze.region2d.IntrinsicVolumes2DUtils;
import net.sci.image.regfeat.Feature;
import net.sci.image.regfeat.RegionFeatures;
import net.sci.image.regfeat.SingleValueFeature;

/**
 * Computation of perimeter using discretization of Crofton formula with four
 * directions.
 */
public class Perimeter_Crofton_D4 extends SingleValueFeature
{
    /**
     * Default empty constructor.
     */
    public Perimeter_Crofton_D4()
    {
        super("Perimeter_Crofton_D4");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        int[][] histos = (int[][]) data.results.get(BinaryConfigurationHistogram.class);
        
        // compute LUT
        Calibration calib = data.labelMap.getCalibration();
        double[] lut = IntrinsicVolumes2DUtils.perimeterLut(calib, 4);
        
        return BinaryConfigurationHistogram.applyLut(histos, lut);
    }
    
    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(BinaryConfigurationHistogram.class);
    }
    
    public String[] columnUnitNames(RegionFeatures data)
    {
        return new String[] {data.labelMap.getCalibration().getXAxis().getUnitName()};
    }
}
