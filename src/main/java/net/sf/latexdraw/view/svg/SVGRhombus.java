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
import java.util.List;
import net.sf.latexdraw.model.MathUtils;
import net.sf.latexdraw.model.ShapeFactory;
import net.sf.latexdraw.model.api.shape.Point;
import net.sf.latexdraw.model.api.shape.Rhombus;
import net.sf.latexdraw.parser.svg.SVGAttributes;
import net.sf.latexdraw.parser.svg.SVGDocument;
import net.sf.latexdraw.parser.svg.SVGElement;
import net.sf.latexdraw.parser.svg.SVGGElement;
import net.sf.latexdraw.parser.svg.SVGParserUtils;
import net.sf.latexdraw.parser.svg.SVGPolygonElement;
import net.sf.latexdraw.util.LNamespace;
import org.jetbrains.annotations.NotNull;

/**
 * SVG/latexdraw rhombus import export.
 * @author Arnaud BLOUIN
 */
class SVGRhombus extends SVGPolygonBased<Rhombus> {
	/**
	 * Creates a latexdraw->svg generator.
	 * @param rhombus The rhombus used for the generation.
	 * @throws IllegalArgumentException If the given rhombus is null.
	 */
	SVGRhombus(final Rhombus rhombus) {
		super(rhombus);
	}

	/**
	 * Creates an SVG-latexdraw->latexdraw generator.
	 * @param elt The source element.
	 */
	SVGRhombus(final SVGGElement elt, final boolean withTransformation) {
		this(ShapeFactory.INST.createRhombus());

		final SVGElement elt2 = getLaTeXDrawElement(elt, null);

		if(elt == null || !(elt2 instanceof SVGPolygonElement)) {
			throw new IllegalArgumentException();
		}

		final SVGPolygonElement main = (SVGPolygonElement) elt2;
		setSVGLatexdrawParameters(elt);
		setSVGParameters(main);

		final List<Point2D> ptsPol = SVGParserUtils.INSTANCE.parsePoints(
								elt.getAttribute(elt.getUsablePrefix(LNamespace.LATEXDRAW_NAMESPACE_URI) + LNamespace.XML_POINTS));

		if(ptsPol.size() != 4) {
			throw new IllegalArgumentException();
		}

		shape.getPtAt(0).setPoint2D(ptsPol.get(0));
		shape.getPtAt(1).setPoint2D(ptsPol.get(1));
		shape.getPtAt(2).setPoint2D(ptsPol.get(3));
		shape.getPtAt(3).setPoint2D(ptsPol.get(2));

		setSVGShadowParameters(getLaTeXDrawElement(elt, LNamespace.XML_TYPE_SHADOW));
		setSVGDbleBordersParameters(getLaTeXDrawElement(elt, LNamespace.XML_TYPE_DBLE_BORDERS));

		if(withTransformation) {
			applyTransformations(elt);
		}
	}


	@Override
	SVGElement toSVG(final @NotNull SVGDocument doc) {
		final Point tl = shape.getTopLeftPoint();
		final Point br = shape.getBottomRightPoint();
		final Point gc = shape.getGravityCentre();
		final Point p1 = ShapeFactory.INST.createPoint((tl.getX() + br.getX()) / 2d, tl.getY());
		final Point p2 = ShapeFactory.INST.createPoint(br.getX(), (tl.getY() + br.getY()) / 2d);
		final Point p3 = ShapeFactory.INST.createPoint((tl.getX() + br.getX()) / 2d, br.getY());
		final SVGElement root = new SVGGElement(doc);
		final double gap = getPositionGap() / 2d;
		final double cornerGap1 = MathUtils.INST.getCornerGap(gc, p1, p2, gap);
		double cornerGap2 = MathUtils.INST.getCornerGap(gc, p2, p3, gap);

		if(p2.getX() < p3.getX()) {
			cornerGap2 *= -1d;
		}

		final String points = String.valueOf(p1.getX()) + ',' + (p1.getY() - cornerGap1) + ' ' + (p2.getX() + cornerGap2) + ',' + p2.getY() + ' ' +
								p3.getX() + ',' + (p3.getY() + cornerGap1) + ' ' + (tl.getX() - cornerGap2) + ',' + p2.getY();

		root.setAttribute(LNamespace.LATEXDRAW_NAMESPACE + ':' + LNamespace.XML_TYPE, LNamespace.XML_TYPE_RHOMBUS);
		root.setAttribute(SVGAttributes.SVG_ID, getSVGID());

		setShadowPolygon(doc, root, points);

		final SVGElement elt = new SVGPolygonElement(doc);
		elt.setAttribute(SVGAttributes.SVG_POINTS, points);
		root.appendChild(elt);
		root.setAttribute(LNamespace.LATEXDRAW_NAMESPACE + ':' + LNamespace.XML_POINTS, String.valueOf(tl.getX()) + ' ' + tl.getY() + ' ' +
			br.getX() + ' ' + tl.getY() + ' ' + tl.getX() + ' ' + br.getY() + ' ' + br.getX() + ' ' + br.getY());

		setDbleBorderPolygon(doc, root, points);

		setSVGAttributes(doc, elt, true);
		setSVGRotationAttribute(root);

		return root;
	}
}

