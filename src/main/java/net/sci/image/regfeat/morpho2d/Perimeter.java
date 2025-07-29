/**
 * 
 */
package net.sci.image.regfeat.morpho2d;

import java.util.Arrays;
import java.util.Collection;

import net.sci.image.regfeat.Feature;
import net.sci.image.regfeat.RegionFeatures;
import net.sci.image.regfeat.SingleValueFeature;
import net.sci.image.regfeat.morpho2d.core.Perimeter_Crofton_D4;

/**
 * Computes the perimeter of a 2D region. In practice, this feature is an alias
 * for the Perimeter_Crofton_D4 feature.
 * 
 */
public class Perimeter extends SingleValueFeature
{
    /**
     * Default empty constructor.
     */
    public Perimeter()
    {
        super("Perimeter");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        return (double[]) data.results.get(Perimeter_Crofton_D4.class);
    }
    
    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(Perimeter_Crofton_D4.class);
    }
    
    public String[] columnUnitNames(RegionFeatures data)
    {
        return new String[] {data.labelMap.getCalibration().getXAxis().getUnitName()};
    }
}
