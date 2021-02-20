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
package net.sf.latexdraw.command.shape;

import io.github.interacto.command.CommandImpl;
import net.sf.latexdraw.command.Modifying;
import net.sf.latexdraw.model.MathUtils;
import net.sf.latexdraw.model.ShapeFactory;
import net.sf.latexdraw.model.api.shape.Point;
import org.jetbrains.annotations.NotNull;

/**
 * This abstract command moves any kind of points.
 * @author Arnaud Blouin
 */
public abstract class MovePoint extends CommandImpl implements Modifying {
	/** The point to move. */
	protected final @NotNull Point point;

	/** The new coordinates of the point to move. */
	protected Point newCoord;

	protected final @NotNull Point mementoPoint;


	/**
	 * Creates the command.
	 */
	protected MovePoint(final @NotNull Point pt) {
		super();
		point = pt;
		mementoPoint = ShapeFactory.INST.createPoint(point);
	}

	@Override
	public boolean canDo() {
		return MathUtils.INST.isValidPt(point) && MathUtils.INST.isValidPt(newCoord);
	}

	@Override
	public boolean hadEffect() {
		return super.hadEffect() && !mementoPoint.equals(point, 0.001);
	}

	/**
	 * Sets the new coordinates of the point to move.
	 * @param coord The new coordinates of the point to move.
	 */
	public void setNewCoord(final Point coord) {
		newCoord = coord;
	}
}
