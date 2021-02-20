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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import net.sf.latexdraw.model.MathUtils;
import net.sf.latexdraw.model.ShapeFactory;
import net.sf.latexdraw.model.api.shape.FreeHandStyle;
import net.sf.latexdraw.model.api.shape.Freehand;
import net.sf.latexdraw.model.api.shape.Point;
import net.sf.latexdraw.parser.svg.SVGAttributes;
import net.sf.latexdraw.parser.svg.SVGDocument;
import net.sf.latexdraw.parser.svg.SVGElement;
import net.sf.latexdraw.parser.svg.SVGGElement;
import net.sf.latexdraw.parser.svg.SVGParserUtils;
import net.sf.latexdraw.parser.svg.SVGPathElement;
import net.sf.latexdraw.parser.svg.path.SVGPathSegClosePath;
import net.sf.latexdraw.parser.svg.path.SVGPathSegCurvetoCubic;
import net.sf.latexdraw.parser.svg.path.SVGPathSegLineto;
import net.sf.latexdraw.parser.svg.path.SVGPathSegList;
import net.sf.latexdraw.parser.svg.path.SVGPathSegMoveto;
import net.sf.latexdraw.util.LNamespace;
import org.jetbrains.annotations.NotNull;

/**
 * An SVG generator for a free hand drawing.
 * @author Arnaud BLOUIN
 */
class SVGFreeHand extends SVGShape<Freehand> {
	SVGFreeHand(final Freehand fh) {
		super(fh);
	}

	SVGFreeHand(final SVGGElement elt, final boolean withTransformation) {
		this(ShapeFactory.INST.createFreeHand(
			SVGParserUtils.INSTANCE.parsePoints(elt.getAttribute(LNamespace.LATEXDRAW_NAMESPACE + ':' + LNamespace.XML_POINTS)).stream().
			map(pt -> ShapeFactory.INST.createPoint(pt)).collect(Collectors.toList())));

		final SVGElement main = getLaTeXDrawElement(elt, null);

		MathUtils.INST.parserDouble(elt.getAttribute(LNamespace.LATEXDRAW_NAMESPACE + ':' + LNamespace.XML_INTERVAL)).
			ifPresent(val -> shape.setInterval((int) val));

		setSVGLatexdrawParameters(elt);
		setSVGParameters(main);
		setSVGShadowParameters(getLaTeXDrawElement(elt, LNamespace.XML_TYPE_SHADOW));
		setOpened(main);
		shape.setType(FreeHandStyle.getType(elt.getAttribute(LNamespace.LATEXDRAW_NAMESPACE + ':' + LNamespace.XML_PATH_TYPE)));

		if(withTransformation) {
			applyTransformations(elt);
		}
	}

	private final void setOpened(final SVGElement svgElt) {
		// The latest path segment of the path may be a closing path element
		final AtomicBoolean hasClosedSeg = new AtomicBoolean(false);
		SVGParserUtils.INSTANCE.parseSVGPath(svgElt.getAttribute(SVGAttributes.SVG_D), pathSeg -> {
			if(pathSeg instanceof SVGPathSegClosePath) {
				hasClosedSeg.set(true);
			}
		});
		shape.setOpened(!hasClosedSeg.get());
	}


	/**
	 * Fills the given SVG path with elements corresponding to the Freehand curved path.
	 */
	final void getPathCurves(final SVGPathSegList path) {
		double prevx = shape.getPtAt(-1).getX();
		double prevy = shape.getPtAt(-1).getY();
		double curx = shape.getPtAt(0).getX();
		double cury = shape.getPtAt(0).getY();
		double midx = (curx + prevx) / 2d;
		double midy = (cury + prevy) / 2d;
		int i;
		final int size = shape.getNbPoints();
		final int interval = shape.getInterval();

		path.add(new SVGPathSegMoveto(curx, cury, false));

		if(size > interval) {
			prevx = curx;
			prevy = cury;
			curx = shape.getPtAt(interval).getX();
			cury = shape.getPtAt(interval).getY();
			midx = (curx + prevx) / 2d;
			midy = (cury + prevy) / 2d;
			path.add(new SVGPathSegLineto(midx, midy, false));
		}

		for(i = interval * 2; i < size; i += interval) {
			final double x1 = (midx + curx) / 2d;
			final double y1 = (midy + cury) / 2d;
			prevx = curx;
			prevy = cury;
			curx = shape.getPtAt(i).getX();
			cury = shape.getPtAt(i).getY();
			midx = (curx + prevx) / 2d;
			midy = (cury + prevy) / 2d;
			final double x2 = (prevx + midx) / 2d;
			final double y2 = (prevy + midy) / 2d;

			path.add(new SVGPathSegCurvetoCubic(midx, midy, x1, y1, x2, y2, false));
		}

		if(i - interval + 1 < size) {
			final double x1 = (midx + curx) / 2d;
			final double y1 = (midy + cury) / 2d;
			prevx = curx;
			prevy = cury;
			curx = shape.getPtAt(-1).getX();
			cury = shape.getPtAt(-1).getY();
			midx = (curx + prevx) / 2d;
			midy = (cury + prevy) / 2d;
			final double x2 = (prevx + midx) / 2d;
			final double y2 = (prevy + midy) / 2d;

			path.add(new SVGPathSegCurvetoCubic(shape.getPtAt(-1).getX(), shape.getPtAt(-1).getY(), x1, y1, x2, y2, false));
		}
	}


	/**
	 * Fills the given SVG path with elements corresponding to the Freehand lined path.
	 */
	final void getPathLines(final SVGPathSegList path) {
		final Point p = shape.getPtAt(0);
		int i;
		final int size = shape.getNbPoints();
		final int interval = shape.getInterval();

		path.add(new SVGPathSegMoveto(p.getX(), p.getY(), false));

		for(i = interval; i < size; i += interval) {
			path.add(new SVGPathSegLineto(shape.getPtAt(i).getX(), shape.getPtAt(i).getY(), false));
		}

		if(i - interval < size) {
			path.add(new SVGPathSegLineto(shape.getPtAt(-1).getX(), shape.getPtAt(-1).getY(), false));
		}
	}


	/**
	 * @return The path of the shape.
	 */
	final SVGPathSegList getPath() {
		final SVGPathSegList path = new SVGPathSegList();

		switch(shape.getType()) {
			case CURVES -> getPathCurves(path);
			case LINES -> getPathLines(path);
		}

		if(!shape.isOpened()) {
			path.add(new SVGPathSegClosePath());
		}

		return path;
	}


	@Override
	SVGElement toSVG(final @NotNull SVGDocument doc) {
		final SVGElement root = new SVGGElement(doc);
		SVGElement elt;

		root.setAttribute(LNamespace.LATEXDRAW_NAMESPACE + ':' + LNamespace.XML_TYPE, LNamespace.XML_TYPE_FREEHAND);
		root.setAttribute(SVGAttributes.SVG_ID, getSVGID());
		root.setAttribute(LNamespace.LATEXDRAW_NAMESPACE + ':' + LNamespace.XML_PATH_TYPE, String.valueOf(shape.getType()));
		root.setAttribute(LNamespace.LATEXDRAW_NAMESPACE + ':' + LNamespace.XML_INTERVAL, String.valueOf(shape.getInterval()));
		final String path = getPath().toString();
		final StringBuilder pts = new StringBuilder();

		for(final Point pt : shape.getPoints()) {
			pts.append(pt.getX()).append(' ').append(pt.getY()).append(' ');
		}

		root.setAttribute(LNamespace.LATEXDRAW_NAMESPACE + ':' + LNamespace.XML_POINTS, pts.toString());

		if(shape.hasShadow()) {
			final SVGElement shad = new SVGPathElement(doc);
			shad.setAttribute(SVGAttributes.SVG_D, path);
			setSVGShadowAttributes(shad, false);
			root.appendChild(shad);
		}

		// The background of the borders must be filled is there is a shadow.
		if(shape.hasShadow() && shape.isFilled()) {
			elt = new SVGPathElement(doc);
			elt.setAttribute(SVGAttributes.SVG_D, path);
			setSVGBorderBackground(elt, root);
		}

		elt = new SVGPathElement(doc);
		elt.setAttribute(SVGAttributes.SVG_D, path);
		root.appendChild(elt);

		setSVGAttributes(doc, elt, false);
		setSVGRotationAttribute(root);

		return root;
	}
}
