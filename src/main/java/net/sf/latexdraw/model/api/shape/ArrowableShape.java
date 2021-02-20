/*
 * This file is part of LaTeXDraw.
 * Copyright (c) 2005-2020 Arnaud BLOUIN
 * LaTeXDraw is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later version.
 * LaTeXDraw is distributed without any warranty; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 */
package net.sf.latexdraw.model.api.shape;

import java.util.List;
import net.sf.latexdraw.model.api.property.Arrowable;
import org.jetbrains.annotations.NotNull;

/**
 * The API for shapes that can have arrows.
 * @author Arnaud Blouin
 */
public interface ArrowableShape extends Shape, Arrowable {
	/**
	 * @param arrow The arrow to look for.
	 * @return The index of the given arrow in the set of arrows of the shape. -1 is not in the set.
	 */
	int getArrowIndex(final Arrow arrow);

	/**
	 * @return The number of arrows.
	 */
	int getNbArrows();

	/**
	 * Sets the style of the arrow at the given position.
	 * @param style The style to set.
	 * @param position The position of the arrow to modify.
	 */
	void setArrowStyle(final @NotNull ArrowStyle style, final int position);

	/**
	 * @param position The position of the arrow to use.
	 * @return The style of the arrow at the given position.
	 */
	ArrowStyle getArrowStyle(final int position);

	/**
	 * @param position The position of the wanted arrow (-1 for the last arrow).
	 * @return The arrow at the given position or null if the position is not valid.
	 */
	Arrow getArrowAt(final int position);

	@NotNull List<Arrow> getArrows();
}
