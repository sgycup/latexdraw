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
package net.sf.latexdraw.model.impl;

import java.util.List;
import java.util.stream.Collectors;
import net.sf.latexdraw.model.MathUtils;
import net.sf.latexdraw.model.ShapeFactory;
import net.sf.latexdraw.model.api.shape.ModifiablePointsShape;
import net.sf.latexdraw.model.api.shape.Point;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A model of a shape that contains points that can be modified.
 * @author Arnaud Blouin
 */
abstract class ModifiablePointsShapeBase extends ShapeBase implements ModifiablePointsShape {
	protected ModifiablePointsShapeBase(final @NotNull List<Point> pts) {
		super();
		if(pts.stream().anyMatch(pt -> !MathUtils.INST.isValidPt(pt))) {
			throw new IllegalArgumentException();
		}
		points.addAll(pts.stream().map(pt -> ShapeFactory.INST.createPoint(pt)).collect(Collectors.toList()));
	}

	@Override
	public abstract @NotNull ModifiablePointsShape duplicate();

	@Override
	public void setRotationAngleOnly(final double rotationAngle) {
		super.setRotationAngle(rotationAngle);
	}

	@Override
	public void rotate(final @Nullable Point gc, final double angle) {
		if(MathUtils.INST.isValidCoord(angle)) {
			final double diff = angle - getRotationAngle();
			final Point gc2 = gc == null ? getGravityCentre() : gc;

			super.setRotationAngle(angle);
			points.forEach(pt -> pt.setPoint(pt.rotatePoint(gc2, diff)));
		}
	}

	@Override
	public void setRotationAngle(final double angle) {
		rotate(null, angle);
	}


	@Override
	public boolean setPoint(final double x, final double y, final int position) {
		if(position < -1 || !MathUtils.INST.isValidPt(x, y) || position > points.size() || points.isEmpty()) {
			return false;
		}

		final Point p = position == -1 ? points.get(points.size() - 1) : points.get(position);
		p.setPoint(x, y);

		return true;
	}
}
