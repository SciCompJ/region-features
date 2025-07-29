/**
 * 
 */
package net.sci.image.regfeat.morpho2d;

import java.util.Arrays;
import java.util.Collection;

import net.sci.image.regfeat.Feature;
import net.sci.image.regfeat.RegionFeatures;
import net.sci.image.regfeat.SingleValueFeature;
import net.sci.image.regfeat.morpho2d.core.EulerNumber_C4;

/**
 * Computes the Euler number of 2D regions. Uses the default C4 connectivity. In
 * practice, this feature is an alias for the EulerNumber_C4 feature.
 * 
 * @see net.sci.image.regionfeatures.morpho2d.core.EulerNumber_C4
 */
public class EulerNumber extends SingleValueFeature
{
    /**
     * Default empty constructor.
     */
    public EulerNumber()
    {
        super("Euler_Number");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        return (double[]) data.results.get(EulerNumber_C4.class);
    }
    
    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(EulerNumber_C4.class);
    }
}
