/**
 * 
 */
package net.sci.image.regfeat.morpho2d;


import java.util.Arrays;
import java.util.Collection;

import net.sci.image.regfeat.Feature;
import net.sci.image.regfeat.RegionFeatures;
import net.sci.image.regfeat.SingleValueFeature;

/**
 * The circularity of a region, defined from normalized ratio of area and
 * squared perimeter.
 * 
 * @see Area
 * @see Perimeter
 */
public class Circularity extends SingleValueFeature
{
    /**
     * Default empty constructor.
     */
    public Circularity()
    {
        super("Circularity");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        double[] areas = (double[]) data.results.get(Area.class);
        double[] perims = (double[]) data.results.get(Perimeter.class);
        
        // iterate over labels to compute new feature
        int[] labels = data.labels;
        double[] res = new double[labels.length];
        for (int i = 0; i < labels.length; i++)
        {
            double a = areas[i];
            double p = perims[i];
            double circ = 4 * Math.PI * a / (p * p);
            res[i] = circ;
        }
        return res;
    }
    
    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(Area.class, Perimeter.class);
    }
}
