/*
 *  This file is part of the Artenus 2D Framework.
 *  Copyright (C) 2015  Hessan Feghhi
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Foobar is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * <p>Contains classes and interfaces that handle post-processing of rendered frames. A rendered
 * frame in Artenus pipeline goes down a series of, possibly multi-pass, filters that can add
 * effects to or process the image. When using filters you should keep in mind that each pass in a
 * filter is a separate drawing of the image, which adds overhead to every frame. Adding too many
 * filters can slow down the rendering process and impair user experience.</p>
 * <p>You can either use the filters provided in this package, or design your own post-processing
 * filters. When you design a new filter, you should optimize the number of passes needed to
 * complete the filter.</p>
 *
 * @author Hessan Feghhi
 */
package com.annahid.libs.artenus.graphics.filters;