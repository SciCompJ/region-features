/**
 * 
 */
package net.sci.image.regfeat;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;

import net.sci.image.Image;

/**
 * Abstract class for a feature that can compute "something" from a label map.
 * 
 * The result of the computation is provided by the <code>compute</code> method.
 */
public interface Feature
{
    /**
     * Creates a new instance of the feature determined by its class. The
     * Feature must provide an empty constructor. Returns <code>null</code> if
     * the creation fails.
     * 
     * @param featureClass
     *            the class of the feature to create.
     * @return an instance of feature with the specified class
     */
    public static Feature create(Class<? extends Feature> featureClass)
    {
        try
        {
            Constructor<? extends Feature> cons = featureClass.getConstructor();
            return cons.newInstance();
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
        catch (SecurityException e)
        {
            e.printStackTrace();
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Computes the feature from a label map image, based on the data stored in
     * the specified instance of {@code RegionFeatures}. All required features
     * must have been computed.
     * 
     * The type of the result varies depending on the feature. In the case of a
     * {@code RegionFeature}, the result is an array with as many elements as
     * the number of regions. In the case of a {@code SingleValueFeature}, the
     * result is an array of double values.
     * 
     * @see net.sci.image.regionfeatures.RegionFeatures#process(Class)
     * @see #requiredFeatures()
     * 
     * @param data
     *            a data structure containing data for computing this feature
     * @return the result of the computation
     */
    public abstract Object compute(RegionFeatures data);
    
    /**
     * Draws the result of feature computation as overlay on the specified image
     * (optional operation). This method must be called after the features have
     * been computed.
     * 
     * By default, this method does nothing. It is expected that mostly features
     * computed on 2D regions can be displayed.
     * 
     * @param image
     *            the Image instance to display results on.
     * @param data
     *            the data structure containing results of features computed on
     *            regions
     */
    public default void overlayResult(Image image, RegionFeatures data)
    {
    }
    
    /**
     * Returns the list of features this feature depends on. The result is given
     * as a list of classes.
     * 
     * Default behavior is to return an empty list (no dependency).
     * Implementations of the {@code Feature} class are expected to override
     * this method to specify which features they depend on.
     * 
     * @return the list of features this feature depends on
     */
    public default Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Collections.emptyList();
    }
}
