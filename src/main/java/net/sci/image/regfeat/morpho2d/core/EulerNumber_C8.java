/**
 * 
 */
package net.sci.image.regfeat.morpho2d.core;

import java.util.Arrays;
import java.util.Collection;

import net.sci.image.analyze.region2d.IntrinsicVolumes2DUtils;
import net.sci.image.regfeat.Feature;
import net.sci.image.regfeat.RegionFeatures;
import net.sci.image.regfeat.SingleValueFeature;

/**
 * Euler number using the C8 connectivity.
 * 
 * @see EulerNumber_C4
 */
public class EulerNumber_C8 extends SingleValueFeature
{
    /**
     * Default empty constructor.
     */
    public EulerNumber_C8()
    {
        super("Euler_Number_C2");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        int[][] histos = (int[][]) data.results.get(BinaryConfigurationHistogram.class);
        
        // compute LUT
        double[] lut = IntrinsicVolumes2DUtils.eulerNumberLut(8);
        
        return BinaryConfigurationHistogram.applyLut(histos, lut);
    }
    
    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(BinaryConfigurationHistogram.class);
    }    
}
