/**
 * Main package of the RegionFeatures plugin.
 * 
 * The two main files are:
 * <ul>
 * <li>The <code>Feature</code> interface, that defines the behavior of region
 * features provided by the plugin as well as user-defined region features,</li>
 * <li>The <code>RegionFeatures</code> class, that aggregates results computed
 * from images as well as additional data</li>
 * </ul>
 * 
 * The sub-pacakges correspond to different families of features:
 * <ul>
 * <li>the <code>morpho2d</code> package gathers features describing morphology
 * of regions,
 * <li>
 * <li>the <code>spatial</code> package gathers mostly provides computation of
 * region adjacency graph.
 * <li>
 * </ul>
 */
package net.sci.image.regfeat;