/**
 * 
 */
package net.sci.image.regfeat;

import java.util.Map;

import net.sci.array.Array;
import net.sci.array.numeric.IntArray;
import net.sci.image.label.LabelImages;
import net.sci.table.IntegerColumn;
import net.sci.table.Table;

/**
 * Counts the number of elements (pixels or voxels) that compose each region.
 * 
 * Note: this class does not implement SingleValueFeature, as the result is
 * given as an array of int, rather than an array of double.
 */
public class ElementCount implements RegionTabularFeature
{
    /**
     * Default empty constructor.
     */
    public ElementCount()
    {
    }
    
    @Override
    public int[] compute(RegionFeatures data)
    {
        // create map of labels to indices
        Map<Integer, Integer> labelIndices = LabelImages.mapLabelIndices(data.labels);
        
        // retrieve image size
        Array<?> array = data.labelMap.getData();
        IntArray<?> labelMap = (IntArray<?>) array;
        
        // allocate memory
        int[] counts = new int[data.labels.length];
        
        // iterate over integer elements
        IntArray.Iterator<?> iter = labelMap.iterator();
        while(iter.hasNext())
        {
            int label = iter.nextInt();
            // process only labels specified in data
            if (label == 0) continue;
            if (!labelIndices.containsKey(label)) continue;
            
            // update result
            counts[labelIndices.get(label)]++;
        }
        
        return counts;
    }

    @Override
    public void updateTable(Table table, RegionFeatures data)
    {
        Object obj = data.results.get(this.getClass());
        if (obj instanceof int[] counts)
        {
            table.addColumn(IntegerColumn.create("Count", counts));
//            int[] array = (int[]) obj;
//            for (int r = 0; r < array.length; r++)
//            {
//                table.setValue(r, "Count", array[r]);
//            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of integer values");
        }
    }

}
