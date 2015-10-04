/**
 * <p>Contains classes and interfaces that handle post-processing of rendered frames. A rendered
 * frame in Artenus pipeline goes down a series of, possibly multi-pass, filters that can add
 * effects to or process the image. When using filters you should keep in mind that each pass in a
 * filter is a separate drawing of the image, which adds overhead to every frame. Adding too many
 * filters can slow down the rendering process and impair user experience.</p>
 * <p>You can either use the filters provided in this package, or design your own post-processing
 * filters. When you design a new filter, you should optimize the number of passes needed to
 * complete the filter.</p>
 */
package com.annahid.libs.artenus.graphics.filters;