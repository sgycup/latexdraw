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

import net.sf.latexdraw.model.api.property.LineArcProp;
import net.sf.latexdraw.model.api.shape.Shape;
import net.sf.latexdraw.parser.svg.SVGAttributes;
import net.sf.latexdraw.parser.svg.SVGDocument;
import net.sf.latexdraw.parser.svg.SVGElement;
import net.sf.latexdraw.parser.svg.SVGGElement;
import net.sf.latexdraw.parser.svg.SVGRectElement;
import net.sf.latexdraw.util.LNamespace;

import static java.lang.Math.min;

/**
 * A class to factorise code between Rectangle and Square SVG generators.
 * @param <T> The type of the shape.
 */
abstract class SVGRectangular<T extends Shape & LineArcProp> extends SVGShape<T> {
	/**
	 * Creates the SVG generator.
	 * @param sh The shape used for the generation.
	 * @throws IllegalArgumentException If the given shape is null.
	 */
	SVGRectangular(final T sh) {
		super(sh);
	}

	/**
	 * Initialises the rectangle using an SVGGElement provided by a latexdraw SVG document.
	 * @param elt The source element.
	 * @throws IllegalArgumentException If the given element is null or not valid.
	 */
	void initRectangle(final SVGGElement elt, final boolean withTransformation) {
		final SVGElement elt2 = getLaTeXDrawElement(elt, null);

		if(elt == null || !(elt2 instanceof SVGRectElement)) {
			throw new IllegalArgumentException();
		}

		setSVGLatexdrawParameters(elt);
		setSVGRectParameters((SVGRectElement) elt2);
		setSVGShadowParameters(getLaTeXDrawElement(elt, LNamespace.XML_TYPE_SHADOW));
		setSVGDbleBordersParameters(getLaTeXDrawElement(elt, LNamespace.XML_TYPE_DBLE_BORDERS));

		if(withTransformation) {
			applyTransformations(elt);
		}
	}

	/**
	 * Sets the parameters of the latexdraw rectangle using the given SVG rectangle.
	 * @param elt The SVG rectangle used to set the latexdraw rectangle.
	 */
	abstract void setSVGRectParameters(final SVGRectElement elt);

	final void setShadowSVGRect(final SVGElement root, final double x, final double y, final double width, final double height, final SVGDocument doc) {
		if(shape.hasShadow()) {
			final SVGRectElement elt = new SVGRectElement(x, y, width, height, doc);
			setSVGShadowAttributes(elt, true);
			root.appendChild(elt);
			setSVGRoundCorner(elt);
		}
	}

	final void setDbleBordSVGRect(final SVGElement root, final double x, final double y, final double width, final double height, final SVGDocument doc) {
		if(shape.hasDbleBord()) {
			final SVGRectElement elt = new SVGRectElement(x, y, width, height, doc);
			setSVGDoubleBordersAttributes(elt);
			setSVGRoundCorner(elt);
			root.appendChild(elt);
		}
	}

	/**
	 * Sets the roundness of the SVG shape.
	 * @param elt The SVG element into which the roundness must be set.
	 */
	final void setSVGRoundCorner(final SVGElement elt) {
		if(elt != null && shape.isRoundCorner()) {
			final double value = 0.5 * (Math.min(shape.getWidth(), shape.getHeight()) - getRoundCornerGap()) * shape.getLineArc();
			elt.setAttribute(SVGAttributes.SVG_RX, String.valueOf(value));
		}
	}

	double getRoundCornerGap() {
		return shape.isDbleBorderable() ? shape.getDbleBordSep() + shape.getThickness() : 0d;
	}

	void setLineArc(final double rx) {
		shape.setLineArc(rx / (0.5 * (min(shape.getHeight(), shape.getWidth()) - getRoundCornerGap())));
	}
}
