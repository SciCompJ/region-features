# Region Features plug in for Imago

Plugin for the Imago software that allows to compute several features for image regions stored as label map (or label images).

## How to use

Installation:
* download the latgest release
* copy the jar file of the plugin int othe "polugins" direcgtoryu of the Imago software
* restart Imago. There should be a plugin in the `Plugins > Region Features` menu entry.

Usage:
* open an image, and generate a label map, either by applying a "connected component analysis"  operation on a binary image, or by applying a segmentation algorithm such as a watershed.
* run the Plugin `Plugins > Region Features > Region Morphology (2D)`
* choose the features you want to compute, and the other options such as the way you want to display units
* when clicking "OK", this opens a new Table containing the numerical representation of the various features that have been requested.
