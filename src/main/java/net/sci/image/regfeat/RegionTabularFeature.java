/**
 * 
 */
package net.sci.image.regfeat;

import net.sci.table.Table;

/**
 * Abstract class for a feature that can generate a data table with as many rows
 * as the number of regions to analyze.
 */
public interface RegionTabularFeature extends Feature
{
    /**
     * Creates a new table containing the numerical data representing this
     * feature.
     * 
     * @param data
     *            the class containing all the computed features.
     * @return a new table containing concatenation of the measures for all
     *         computed features
     */
    public default Table createTable(RegionFeatures data)
    {
        Table table = data.initializeRegionTable();
        updateTable(table, data);
        return table;
    }
    
    /**
     * Updates the specified result table with the result of this feature.
     * Depending on features, this method may populate one or several columns.
     * 
     * @param table
     *            the table to populate
     * @param data
     *            the class containing all the computed features.
     */
    public abstract void updateTable(Table table, RegionFeatures data);

    /**
     * Returns either {@code null}, or an array of String containing the unit
     * name of each column of the table created by the method
     * {@code createTable()}.
     * 
     * Default behavior is to return {@code null}, meaning unit names are
     * unspecified. If overridden, the number of unit name strings must be equal
     * to the number of columns of the table (without the row label heading).
     * 
     * @param data
     *            the class containing all the computed features.
     * @return an array of String containing the unit name of each column
     */
    public default String[] columnUnitNames(RegionFeatures data)
    {
        return null;
    }

}
