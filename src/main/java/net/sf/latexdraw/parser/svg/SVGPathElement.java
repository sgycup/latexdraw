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
package net.sf.latexdraw.parser.svg;

import net.sf.latexdraw.parser.svg.path.SVGPathSegClosePath;
import net.sf.latexdraw.parser.svg.path.SVGPathSegCurvetoCubic;
import net.sf.latexdraw.parser.svg.path.SVGPathSegCurvetoCubicSmooth;
import net.sf.latexdraw.parser.svg.path.SVGPathSegLineto;
import net.sf.latexdraw.parser.svg.path.SVGPathSegList;
import net.sf.latexdraw.parser.svg.path.SVGPathSegMoveto;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Node;

/**
 * Defines the SVG tag <code>path</code>.
 * @author Arnaud BLOUIN
 */
public class SVGPathElement extends SVGElement {
	public SVGPathElement(final Node n, final SVGElement p) {
		super(n, p);
	}


	/**
	 * Creates an empty SVG path element.
	 * @param owner The owner document.
	 */
	public SVGPathElement(final SVGDocument owner) {
		super(owner);

		setAttribute(SVGAttributes.SVG_D, "");
		setNodeName(SVGElements.SVG_PATH);
	}


	/**
	 * @return True if the path is a unique line.
	 */
	public boolean isLine() {
		final SVGPathSegList segList = getSegList();

		return segList.size() == 2 && segList.get(0) instanceof SVGPathSegMoveto && segList.get(1) instanceof SVGPathSegLineto;
	}


	/**
	 * @return True if the path is composed of lines and has a 'close path' segment at the end.
	 */
	public boolean isLines() {
		final SVGPathSegList segList = getSegList();

		if(segList.size() < 3 || !(segList.get(0) instanceof SVGPathSegMoveto)) {
			return false;
		}

		boolean ok = true;
		int i;
		final int size;

		for(i = 1, size = segList.size() - 1; i < size && ok; i++) {
			if(!(segList.get(i) instanceof SVGPathSegLineto)) {
				ok = false;
			}
		}

		return ok;
	}


	public boolean isBezierCurve() {
		final SVGPathSegList segList = getSegList();

		if(segList.isEmpty() || !(segList.get(0) instanceof SVGPathSegMoveto)) {
			return false;
		}

		final int size = segList.size() - 1;
		boolean ok = true;
		int i;

		for(i = 1; i < size && ok; i++) {
			if(!(segList.get(i) instanceof SVGPathSegCurvetoCubic) && !(segList.get(i) instanceof SVGPathSegCurvetoCubicSmooth)) {
				ok = false;
			}
		}

		return ok && (segList.get(size) instanceof SVGPathSegClosePath || segList.get(size) instanceof SVGPathSegCurvetoCubic ||
			segList.get(size) instanceof SVGPathSegCurvetoCubicSmooth);
	}

	/**
	 * @return True if the path is composed of lines and has a 'close path' segment at the end.
	 */
	public boolean isPolygon() {
		final SVGPathSegList segList = getSegList();

		if(segList.isEmpty() || !(segList.get(0) instanceof SVGPathSegMoveto)) {
			return false;
		}

		boolean ok = true;
		int i;
		final int size;

		for(i = 1, size = segList.size() - 1; i < size && ok; i++) {
			if(!(segList.get(i) instanceof SVGPathSegLineto)) {
				ok = false;
			}
		}

		if(!(segList.get(segList.size() - 1) instanceof SVGPathSegClosePath)) {
			ok = false;
		}

		return ok;
	}


	/**
	 * The definition of the outline of a shape.
	 * @return The path data (in postscript) from the segList attribute.
	 */
	public @NotNull String getPathData() {
		return getAttribute(getUsablePrefix() + SVGAttributes.SVG_D);
	}

	/**
	 * Sets the path data.
	 * @param path The path to set.
	 */
	public void setPathData(final SVGPathSegList path) {
		if(path != null) {
			setAttribute(SVGAttributes.SVG_D, path.toString());
		}
	}

	@Override
	public boolean checkAttributes() {
		return !getPathData().isEmpty();
	}

	@Override
	public boolean enableRendering() {
		return checkAttributes();
	}

	/**
	 * @return the segList.
	 */
	public SVGPathSegList getSegList() {
		final SVGPathSegList segList = new SVGPathSegList();
		SVGParserUtils.INSTANCE.parseSVGPath(getPathData(), segList);
		return segList;
	}
}
