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
package net.sf.latexdraw.view.svg;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.sf.latexdraw.model.MathUtils;
import net.sf.latexdraw.model.ShapeFactory;
import net.sf.latexdraw.model.api.shape.ModifiablePointsShape;
import net.sf.latexdraw.model.api.shape.Point;
import net.sf.latexdraw.parser.svg.AbstractPointsElement;
import net.sf.latexdraw.parser.svg.SVGElement;
import net.sf.latexdraw.parser.svg.SVGLineElement;
import net.sf.latexdraw.parser.svg.SVGPathElement;
import net.sf.latexdraw.parser.svg.path.SVGPathSeg;
import net.sf.latexdraw.parser.svg.path.SVGPathSegClosePath;
import net.sf.latexdraw.parser.svg.path.SVGPathSegLineto;
import net.sf.latexdraw.parser.svg.path.SVGPathSegList;
import net.sf.latexdraw.util.LNamespace;
import org.jetbrains.annotations.NotNull;

abstract class SVGModifiablePointsShape<S extends ModifiablePointsShape> extends SVGShape<S> {
	/**
	 * Creates the SVG generator.
	 * @param sh The shape used for the generation.
	 * @throws IllegalArgumentException If the given shape is null.
	 */
	SVGModifiablePointsShape(final S sh) {
		super(sh);
	}

	final void setRotationAngle(final SVGElement shapeElt) {
		try {
			final double angle = Double.parseDouble(shapeElt.getAttribute(shapeElt.getUsablePrefix(LNamespace.LATEXDRAW_NAMESPACE_URI) + LNamespace.XML_ROTATION));
			if(MathUtils.INST.isValidCoord(angle) && !MathUtils.INST.equalsDouble(angle, 0d, 0.000001)) {
				shape.setRotationAngleOnly(angle);
			}
		}catch(final NumberFormatException ignored) {
		}
	}

	/**
	 * Returns a set of points from an SVG element.
	 */
	static @NotNull List<Point> getPointsFromSVGElement(final SVGElement elt) {
		if(elt instanceof SVGLineElement) {
			final SVGLineElement lineElt = (SVGLineElement) elt;
			return Arrays.asList(ShapeFactory.INST.createPoint(lineElt.getX1(), lineElt.getY1()), ShapeFactory.INST.createPoint(lineElt.getX2(), lineElt.getY2()));
		}

		if(elt instanceof AbstractPointsElement) {
			final List<Point2D> ptsPol = ((AbstractPointsElement) elt).getPoints2D();
			if(ptsPol == null) {
				return Collections.emptyList();
			}
			return ptsPol.stream().map(pt -> ShapeFactory.INST.createPoint(pt.getX(), pt.getY())).collect(Collectors.toList());
		}

		return Collections.emptyList();
	}

	/**
	 * Gets the line points from the given SVGPathElement
	 */
	static List<Point> getLinePointsFromSVGPathElement(final SVGPathElement elt) {
		if(elt == null) {
			return Collections.emptyList();
		}

		final SVGPathSegList segs = elt.getSegList();
		final int size = segs.get(segs.size() - 1) instanceof SVGPathSegClosePath ? segs.size() - 1 : segs.size();
		Point2D pt = new Point2D.Double(); // Creating a point to support when the first path element is relative.
		final List<Point> pts = new ArrayList<>();

		for(int i = 0; i < size; i++) {
			final SVGPathSeg seg = segs.get(i);

			if(!(seg instanceof SVGPathSegLineto)) {
				throw new IllegalArgumentException("The given SVG path element is not a set of lines."); //NON-NLS
			}

			pt = ((SVGPathSegLineto) seg).getPoint(pt);
			pts.add(ShapeFactory.INST.createPoint(pt.getX(), pt.getY()));
		}

		return pts;
	}
}
