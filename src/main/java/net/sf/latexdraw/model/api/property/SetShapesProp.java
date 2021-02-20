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
package net.sf.latexdraw.model.api.property;

import java.util.Optional;
import javafx.beans.property.ListProperty;
import net.sf.latexdraw.model.api.shape.Shape;
import org.jetbrains.annotations.NotNull;

/**
 * Properties of shapes that are composed of shapes.
 * @author Arnaud BLOUIN
 */
public interface SetShapesProp {
	/**
	 * Adds a shape to the drawing.
	 * @param s The shape to add. Does nothing if the given shape is null.
	 */
	void addShape(final @NotNull Shape s);

	/**
	 * Adds a shape to the drawing at at given position.
	 * @param s The shape to add. Does nothing if the given shape is null.
	 * @param index The position where the figure must be inserted. Does nothing if the given position is not valid.
	 */
	void addShape(final @NotNull Shape s, final int index);

	/**
	 * Removes a shape of the drawing.
	 * @param s The shape to remove.
	 * @return true if the given shape is removed. False if the given shape is null.
	 */
	boolean removeShape(final @NotNull Shape s);

	/**
	 * Removes a shape of the drawing a the given position.
	 * @param i the position of the shape in the vector (-1: the last shape of the vector).
	 * @return The deleted shape if it exists.
	 */
	@NotNull Optional<Shape> removeShape(final int i);

	/**
	 * Allows to get the shape located at the given position.
	 * @param i The position of the figure (-1: the last shape of the drawing).
	 * @return The searched shape if it exists.
	 */
	@NotNull Optional<Shape> getShapeAt(final int i);

	/**
	 * Allows to get the number of shapes that contains the drawing.
	 * @return The number of shapes in the drawing.
	 */
	int size();

	/**
	 * Allows to know if a shape is in the drawing.
	 * @param s The shape to check.
	 * @return True if the shape is in the drawing. False otherwise.
	 */
	boolean contains(final @NotNull Shape s);

	/**
	 * Allows to know if the drawing is empty or not.
	 * @return True if there is at least one shape in the drawing.
	 */
	boolean isEmpty();

	/**
	 * Empties the drawing.
	 */
	void clear();

	/**
	 * @return The shapes of the drawing.
	 */
	@NotNull ListProperty<Shape> getShapes();
}
