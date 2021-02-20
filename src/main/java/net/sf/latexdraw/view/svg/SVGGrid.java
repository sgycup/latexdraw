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
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import net.sf.latexdraw.model.MathUtils;
import net.sf.latexdraw.model.ShapeFactory;
import net.sf.latexdraw.model.api.shape.Color;
import net.sf.latexdraw.model.api.shape.Grid;
import net.sf.latexdraw.model.api.shape.Point;
import net.sf.latexdraw.model.api.shape.Shape;
import net.sf.latexdraw.parser.svg.CSSColors;
import net.sf.latexdraw.parser.svg.SVGAttributes;
import net.sf.latexdraw.parser.svg.SVGCircleElement;
import net.sf.latexdraw.parser.svg.SVGDocument;
import net.sf.latexdraw.parser.svg.SVGElement;
import net.sf.latexdraw.parser.svg.SVGGElement;
import net.sf.latexdraw.parser.svg.SVGLineElement;
import net.sf.latexdraw.parser.svg.SVGParserUtils;
import net.sf.latexdraw.parser.svg.SVGTextElement;
import net.sf.latexdraw.parser.svg.SVGTransform;
import net.sf.latexdraw.util.BadaboomCollector;
import net.sf.latexdraw.util.LNamespace;
import org.jetbrains.annotations.NotNull;

/**
 * An SVG generator for a grid.
 * @author Arnaud BLOUIN
 */
class SVGGrid extends SVGShape<Grid> {
	/**
	 * Creates a generator of SVG grids.
	 * @param grid The grid used for the generation.
	 * @throws IllegalArgumentException If grid is null.
	 */
	SVGGrid(final Grid grid) {
		super(grid);
	}


	/**
	 * Creates a grid from a latexdraw-SVG element.
	 * @param elt The source element.
	 */
	SVGGrid(final SVGGElement elt, final boolean withTransformation) {
		this(ShapeFactory.INST.createGrid(ShapeFactory.INST.createPoint()));

		if(elt == null) {
			throw new IllegalArgumentException();
		}

		final String prefix = LNamespace.LATEXDRAW_NAMESPACE + ':';

		setDimensionGridElement(elt, prefix);

		SVGElement gridElt = getLaTeXDrawElement(elt, LNamespace.XML_TYPE_GRID_SUB);

		if(gridElt != null) {
			setSubGridElement(gridElt, prefix);
		}

		gridElt = getLaTeXDrawElement(elt, LNamespace.XML_TYPE_GRID);

		if(gridElt != null) {
			setMainGridElement(gridElt, prefix);
		}

		try {
			shape.setUnit(Double.parseDouble(elt.getAttribute(prefix + LNamespace.XML_GRID_UNIT)));
		}catch(final NumberFormatException ignore) {
		}

		setLabelGridElement(getLaTeXDrawElement(elt, LNamespace.XML_TYPE_TEXT));

		if(withTransformation) {
			applyTransformations(elt);
		}
	}


	/**
	 * Sets the dimensions of a grid from an SVGGElement.
	 */
	private final void setDimensionGridElement(final SVGGElement elt, final String prefix) {
		List<Point2D> values;

		elt.getStroke().ifPresent(col -> shape.setLineColour(col));
		values = SVGParserUtils.INSTANCE.parsePoints(elt.getAttribute(prefix + LNamespace.XML_GRID_END));

		if(!values.isEmpty()) {
			shape.setGridEndX(values.get(0).getX());
			shape.setGridEndY(values.get(0).getY());
		}

		values = SVGParserUtils.INSTANCE.parsePoints(elt.getAttribute(prefix + LNamespace.XML_GRID_START));

		if(!values.isEmpty()) {
			shape.setGridStartX(values.get(0).getX());
			shape.setGridStartY(values.get(0).getY());
		}

		values = SVGParserUtils.INSTANCE.parsePoints(elt.getAttribute(prefix + LNamespace.XML_GRID_ORIGIN));

		if(!values.isEmpty()) {
			shape.setOriginX(values.get(0).getX());
			shape.setOriginY(values.get(0).getY());
		}

		shape.setXLabelSouth(Boolean.parseBoolean(elt.getAttribute(prefix + LNamespace.XML_GRID_X_SOUTH)));
		shape.setYLabelWest(Boolean.parseBoolean(elt.getAttribute(prefix + LNamespace.XML_GRID_Y_WEST)));
	}


	/**
	 * Sets the label properties of a grid from an SVGElement.
	 */
	private final void setLabelGridElement(final SVGElement labelElt) {
		if(labelElt == null) {
			shape.setLabelsSize(0);
		}else {
			try {
				shape.setLabelsSize((int) Double.parseDouble(labelElt.getAttribute(labelElt.getUsablePrefix() + SVGAttributes.SVG_FONT_SIZE)));
			}catch(final NumberFormatException ignore) {
			}

			labelElt.getStroke().ifPresent(col -> shape.setGridLabelsColour(col));
		}
	}


	/**
	 * Sets the main grid properties of a grid from an SVGElement.
	 */
	private final void setMainGridElement(final SVGElement mainGridElt, final String prefix) {
		boolean isGridDotted = false;

		try {
			shape.setGridDots((int) Double.parseDouble(mainGridElt.getAttribute(prefix + LNamespace.XML_GRID_DOTS)));
			isGridDotted = shape.getGridDots() > 0;
		}catch(final NumberFormatException ignore) {
		}

		if(isGridDotted) {
			shape.setLineColour(CSSColors.INSTANCE.getRGBColour(mainGridElt.getFill()));
		}else {
			mainGridElt.getStroke().ifPresent(col -> shape.setLineColour(col));
		}

		final String val = mainGridElt.getAttribute(prefix + LNamespace.XML_GRID_WIDTH);

		if(val.isEmpty()) {
			final double st = mainGridElt.getStrokeWidth();

			if(!Double.isNaN(st)) {
				shape.setGridWidth(st);
			}
		}else {
			try {
				shape.setGridWidth(Double.parseDouble(val));
			}catch(final NumberFormatException ex) {
				BadaboomCollector.INSTANCE.add(ex);
			}
		}
	}


	/**
	 * Sets the sub-grid properties of a grid from an SVGElement.
	 */
	private final void setSubGridElement(final SVGElement subGridElt, final String prefix) {
		boolean isGridDotted = false;

		try {
			shape.setSubGridDots((int) Double.parseDouble(subGridElt.getAttribute(prefix + LNamespace.XML_GRID_DOTS)));
			isGridDotted = shape.getSubGridDots() > 0;
		}catch(final NumberFormatException ignore) {
		}

		try {
			shape.setSubGridDiv((int) Double.parseDouble(subGridElt.getAttribute(prefix + LNamespace.XML_GRID_SUB_DIV)));
		}catch(final NumberFormatException ignore) {
		}

		if(isGridDotted) {
			shape.setSubGridColour(CSSColors.INSTANCE.getRGBColour(subGridElt.getFill()));
		}else {
			subGridElt.getStroke().ifPresent(col -> shape.setSubGridColour(col));
		}

		final String val = subGridElt.getAttribute(prefix + LNamespace.XML_GRID_WIDTH);

		if(val.isEmpty()) {
			final double st = subGridElt.getStrokeWidth();

			if(!Double.isNaN(st)) {
				shape.setSubGridWidth(st);
			}
		}else {
			try {
				shape.setSubGridWidth(Double.parseDouble(val));
			}catch(final NumberFormatException ex) {
				BadaboomCollector.INSTANCE.add(ex);
			}
		}
	}


	/**
	 * Creates the SVG element corresponding to the sub dotted part of the grid.
	 */
	private void createSVGSubGridDots(final SVGDocument document, final SVGElement elt, final String prefix, final double subGridDiv, final double unit, final
	double xSubStep, final double ySubStep, final double minX, final double maxX, final double minY, final double maxY, final int subGridDots, final double
		subGridWidth, final double tlx, final double tly, final double brx, final double bry, final Color subGridColour) {
		final double dotStep = unit * Shape.PPC / (subGridDots * subGridDiv);
		final double nbX = (maxX - minX) * subGridDiv;
		final double nbY = (maxY - minY) * subGridDiv;
		final SVGElement subgridDots = new SVGGElement(document);
		SVGElement dot;

		subgridDots.setAttribute(SVGAttributes.SVG_FILL, CSSColors.INSTANCE.getColorName(subGridColour, true));
		subgridDots.setAttribute(prefix + LNamespace.XML_TYPE, LNamespace.XML_TYPE_GRID_SUB);
		subgridDots.setAttribute(prefix + LNamespace.XML_GRID_DOTS, String.valueOf(subGridDots));
		subgridDots.setAttribute(prefix + LNamespace.XML_GRID_SUB_DIV, String.valueOf(subGridDiv));
		subgridDots.setAttribute(prefix + LNamespace.XML_GRID_WIDTH, String.valueOf(subGridWidth));

		if(subGridColour.getO() < 1d) {
			subgridDots.setAttribute(SVGAttributes.SVG_FILL_OPACITY, MathUtils.INST.format.format(subGridColour.getO()));
		}

		for(double i = 0, n = tlx; i < nbX; i++, n += xSubStep) {
			for(double j = 0, m = tly; j <= nbY; j++, m += ySubStep) {
				for(double k = 0; k < subGridDots; k++) {
					dot = new SVGCircleElement(document);
					dot.setAttribute(SVGAttributes.SVG_CX, String.valueOf(n + k * dotStep));
					dot.setAttribute(SVGAttributes.SVG_CY, String.valueOf(m));
					dot.setAttribute(SVGAttributes.SVG_R, String.valueOf(subGridWidth / 2.));
					subgridDots.appendChild(dot);
				}
			}
		}

		for(double j = 0, n = tly; j < nbY; j++, n += ySubStep) {
			for(double i = 0, m = tlx; i <= nbX; i++, m += xSubStep) {
				for(double k = 0; k < subGridDots; k++) {
					dot = new SVGCircleElement(document);
					dot.setAttribute(SVGAttributes.SVG_CX, String.valueOf(m));
					dot.setAttribute(SVGAttributes.SVG_CY, String.valueOf(n + k * dotStep));
					dot.setAttribute(SVGAttributes.SVG_R, String.valueOf(subGridWidth / 2.));
					subgridDots.appendChild(dot);
				}
			}
		}

		dot = new SVGCircleElement(document);
		dot.setAttribute(SVGAttributes.SVG_CX, String.valueOf(brx));
		dot.setAttribute(SVGAttributes.SVG_CY, String.valueOf(bry));
		dot.setAttribute(SVGAttributes.SVG_R, String.valueOf(subGridWidth / 2.));

		elt.appendChild(subgridDots);
	}


	/**
	 * Creates the SVG element corresponding to the sub not-dotted part of the grid.
	 */
	private void createSVGSubGridDiv(final SVGDocument document, final SVGElement elt, final String prefix, final double subGridDiv, final double xSubStep,
								final double ySubStep, final double minX, final double maxX, final double minY, final double maxY, final int subGridDots,
								final double subGridWidth, final double tlx, final double tly, final double brx, final double bry,
								final Color subGridColour, final double posX, final double posY, final double xStep, final double yStep) {
		double i;
		double j;
		double k;
		final SVGElement subgrids = new SVGGElement(document);
		SVGElement line;

		subgrids.setAttribute(SVGAttributes.SVG_STROKE_WIDTH, String.valueOf(subGridWidth));
		subgrids.setAttribute(SVGAttributes.SVG_STROKE, CSSColors.INSTANCE.getColorName(subGridColour, true));
		subgrids.setAttribute(SVGAttributes.SVG_STROKE_LINECAP, SVGAttributes.SVG_LINECAP_VALUE_ROUND);
		subgrids.setAttribute(prefix + LNamespace.XML_TYPE, LNamespace.XML_TYPE_GRID_SUB);
		subgrids.setAttribute(prefix + LNamespace.XML_GRID_DOTS, String.valueOf(subGridDots));
		subgrids.setAttribute(prefix + LNamespace.XML_GRID_SUB_DIV, String.valueOf(subGridDiv));

		if(subGridColour.getO() < 1d) {
			subgrids.setAttribute(SVGAttributes.SVG_STROKE_OPACITY, MathUtils.INST.format.format(subGridColour.getO()));
		}

		for(k = minX, i = posX; k < maxX; i += xStep, k++) {
			for(j = 0; j <= subGridDiv; j++) {
				final String value = String.valueOf(i + xSubStep * j);
				line = new SVGLineElement(document);
				line.setAttribute(SVGAttributes.SVG_X1, value);
				line.setAttribute(SVGAttributes.SVG_X2, value);
				line.setAttribute(SVGAttributes.SVG_Y1, String.valueOf(bry));
				line.setAttribute(SVGAttributes.SVG_Y2, String.valueOf(tly));
				subgrids.appendChild(line);
			}
		}

		for(k = minY, i = posY; k < maxY; i -= yStep, k++) {
			for(j = 0; j <= subGridDiv; j++) {
				final String value = String.valueOf(i - ySubStep * j);
				line = new SVGLineElement(document);
				line.setAttribute(SVGAttributes.SVG_X1, String.valueOf(tlx));
				line.setAttribute(SVGAttributes.SVG_X2, String.valueOf(brx));
				line.setAttribute(SVGAttributes.SVG_Y1, value);
				line.setAttribute(SVGAttributes.SVG_Y2, value);
				subgrids.appendChild(line);
			}
		}

		elt.appendChild(subgrids);
	}


	/**
	 * Creates the SVG element corresponding to the main dotted part of the grid.
	 */
	private void createSVGGridDots(final SVGDocument document, final SVGElement elt, final String prefix, final double absStep, final double minX, final
					double maxX, final double minY, final double maxY, final double tlx, final double tly, final double brx, final double bry,
					final double unit, final double posX, final double posY, final double xStep, final double yStep, final double gridWidth,
					final Color linesColour) {
		double k;
		double i;
		double m;
		double n;
		double l;
		double j;
		final int gridDots = shape.getGridDots();
		final double dotStep = unit * Shape.PPC / gridDots;
		final SVGElement gridDotsElt = new SVGGElement(document);
		SVGElement dot;

		gridDotsElt.setAttribute(SVGAttributes.SVG_FILL, CSSColors.INSTANCE.getColorName(linesColour, true));
		gridDotsElt.setAttribute(prefix + LNamespace.XML_TYPE, LNamespace.XML_TYPE_GRID);
		gridDotsElt.setAttribute(prefix + LNamespace.XML_GRID_DOTS, String.valueOf(gridDots));
		gridDotsElt.setAttribute(prefix + LNamespace.XML_GRID_WIDTH, String.valueOf(gridWidth));

		if(linesColour.getO() < 1d) {
			gridDotsElt.setAttribute(SVGAttributes.SVG_FILL_OPACITY, MathUtils.INST.format.format(linesColour.getO()));
		}

		for(k = minX, i = posX; k <= maxX; i += xStep, k++) {
			for(m = tly, n = minY; n < maxY; n++, m += absStep) {
				for(l = 0, j = m; l < gridDots; l++, j += dotStep) {
					dot = new SVGCircleElement(document);
					dot.setAttribute(SVGAttributes.SVG_CX, String.valueOf(i));
					dot.setAttribute(SVGAttributes.SVG_CY, String.valueOf(j));
					dot.setAttribute(SVGAttributes.SVG_R, String.valueOf(gridWidth / 2.));
					gridDotsElt.appendChild(dot);
				}
			}
		}

		for(k = minY, i = posY; k <= maxY; i -= yStep, k++) {
			for(m = tlx, n = minX; n < maxX; n++, m += absStep) {
				for(l = 0, j = m; l < gridDots; l++, j += dotStep) {
					dot = new SVGCircleElement(document);
					dot.setAttribute(SVGAttributes.SVG_CX, String.valueOf(j));
					dot.setAttribute(SVGAttributes.SVG_CY, String.valueOf(i));
					dot.setAttribute(SVGAttributes.SVG_R, String.valueOf(gridWidth / 2.));
					gridDotsElt.appendChild(dot);
				}
			}
		}

		dot = new SVGCircleElement(document);
		dot.setAttribute(SVGAttributes.SVG_CX, String.valueOf(brx));
		dot.setAttribute(SVGAttributes.SVG_CY, String.valueOf(bry));
		dot.setAttribute(SVGAttributes.SVG_R, String.valueOf(gridWidth / 2.));
		gridDotsElt.appendChild(dot);

		elt.appendChild(gridDotsElt);
	}


	/**
	 * Creates the SVG element corresponding to the main not-dotted part of the grid.
	 */
	private void createSVGGridDiv(final SVGDocument document, final SVGElement elt, final String prefix, final double minX, final double maxX, final double
		minY, final double maxY, final double tlx, final double tly, final double brx, final double bry, final double posX, final double posY, final double
		xStep, final double yStep, final double gridWidth, final Color linesColour) {
		double k;
		double i;
		final SVGElement grids = new SVGGElement(document);
		SVGElement line;

		grids.setAttribute(SVGAttributes.SVG_STROKE_WIDTH, String.valueOf(gridWidth));
		grids.setAttribute(SVGAttributes.SVG_STROKE, CSSColors.INSTANCE.getColorName(linesColour, true));
		grids.setAttribute(SVGAttributes.SVG_STROKE_LINECAP, SVGAttributes.SVG_LINECAP_VALUE_SQUARE);
		grids.setAttribute(prefix + LNamespace.XML_TYPE, LNamespace.XML_TYPE_GRID);

		if(linesColour.getO() < 1d) {
			grids.setAttribute(SVGAttributes.SVG_STROKE_OPACITY, MathUtils.INST.format.format(linesColour.getO()));
		}

		for(k = minX, i = posX; k <= maxX; i += xStep, k++) {
			line = new SVGLineElement(document);
			line.setAttribute(SVGAttributes.SVG_X1, String.valueOf(i));
			line.setAttribute(SVGAttributes.SVG_X2, String.valueOf(i));
			line.setAttribute(SVGAttributes.SVG_Y1, String.valueOf(bry));
			line.setAttribute(SVGAttributes.SVG_Y2, String.valueOf(tly));
			grids.appendChild(line);
		}

		for(k = minY, i = posY; k <= maxY; i -= yStep, k++) {
			line = new SVGLineElement(document);
			line.setAttribute(SVGAttributes.SVG_X1, String.valueOf(tlx));
			line.setAttribute(SVGAttributes.SVG_X2, String.valueOf(brx));
			line.setAttribute(SVGAttributes.SVG_Y1, String.valueOf(i));
			line.setAttribute(SVGAttributes.SVG_Y2, String.valueOf(i));
			grids.appendChild(line);
		}

		elt.appendChild(grids);
	}


	/**
	 * Companion method of createSVGGridLabels
	 */
	private final void produceSVGGridLabelsTexts(final SVGDocument document, final SVGElement texts, final double gridWidth, final double xorigin,
		final double yorigin, final Text fooText, final double minX, final double maxX, final double minY, final double maxY, final double tlx,
		final double tly, final double labelWidth, final double labelHeight, final double absStep) {
		final boolean isYLabelWest = shape.isYLabelWest();

		produceSVGGridXLabelsTexts(document, texts, yorigin, minX, maxX, tlx, labelWidth, gridWidth, absStep);

		if(isYLabelWest) {
			produceSVGGridYWestLabelsTexts(document, texts, xorigin, tly, gridWidth, fooText, minY,
				maxY, labelHeight, absStep);
		}else {
			produceSVGGridYEastLabelsTexts(document, texts, xorigin, tly, gridWidth, minY, maxY, labelHeight, absStep);
		}
	}

	private final void produceSVGGridXLabelsTexts(final SVGDocument document, final SVGElement texts, final double yorigin, final double minX, final double maxX,
												final double tlx, final double labelWidth, final double gridWidth, final double absStep) {
		final int gridLabelsSize = shape.getLabelsSize();
		final double width = gridWidth / 2d;
		final double tmp =  shape.isXLabelSouth() ? width : -width;

		for(double i = tlx + (shape.isYLabelWest() ? width + gridLabelsSize / 4d : -width - labelWidth - gridLabelsSize / 4d), j = minX; j <= maxX; i += absStep, j++) {
			final SVGElement text = new SVGTextElement(document);
			text.setAttribute(SVGAttributes.SVG_X, String.valueOf((int) i));
			text.setAttribute(SVGAttributes.SVG_Y, String.valueOf((int) (yorigin + tmp)));
			text.setTextContent(String.valueOf((int) j));
			texts.appendChild(text);
		}
	}

	private final void produceSVGGridYWestLabelsTexts(final SVGDocument document, final SVGElement texts, final double xorigin, final double tly,
									final double gridWidth, final Text fooText, final double minY, final double maxY, final double labelHeight, final double absStep) {
		final double width = gridWidth / 2d;
		final int gridLabelsSize = shape.getLabelsSize();

		for(double i = tly + (shape.isXLabelSouth() ? -width - gridLabelsSize / 4d : width + labelHeight), j = maxY; j >= minY; i += absStep, j--) {
			final String label = String.valueOf((int) j);
			final SVGElement text = new SVGTextElement(document);
			fooText.setText(label);
			text.setAttribute(SVGAttributes.SVG_X, String.valueOf((int) (xorigin - fooText.getLayoutBounds().getWidth() - gridLabelsSize / 4d - width)));
			text.setAttribute(SVGAttributes.SVG_Y, String.valueOf((int) i));
			text.setTextContent(label);
			texts.appendChild(text);
		}
	}

	private final void produceSVGGridYEastLabelsTexts(final SVGDocument document, final SVGElement texts, final double xorigin, final double tly,
								final double gridWidth, final double minY, final double maxY, final double labelHeight, final double absStep) {
		final double width = gridWidth / 2d;
		final int gridLabelsSize = shape.getLabelsSize();

		for(double i = tly + (shape.isXLabelSouth() ? -width - gridLabelsSize / 4d : width + labelHeight), j = maxY; j >= minY; i += absStep, j--) {
			final String label = String.valueOf((int) j);
			final SVGElement text = new SVGTextElement(document);
			text.setAttribute(SVGAttributes.SVG_X, String.valueOf((int) (xorigin + gridLabelsSize / 4d + width)));
			text.setAttribute(SVGAttributes.SVG_Y, String.valueOf((int) i));
			text.setTextContent(label);
			texts.appendChild(text);
		}
	}


	/**
	 * Creates the SVG element corresponding to the labels of the grid.
	 */
	private void createSVGGridLabels(final SVGDocument document, final SVGElement elt, final String prefix, final double minX, final double maxX, final double
		minY, final double maxY, final double tlx, final double tly, final double xStep, final double yStep, final double gridWidth, final double absStep) {
		final Color gridLabelsColor = shape.getGridLabelsColour();
		final SVGElement texts = new SVGGElement(document);
		final double originX = shape.getOriginX();
		final double originY = shape.getOriginY();
		final double xorigin = xStep * originX;
		final Text fooText = new Text(String.valueOf((int) maxX));
		fooText.setFont(new Font(null, shape.getLabelsSize()));
		final double labelHeight = fooText.getBaselineOffset();
		final double labelWidth = fooText.getBoundsInLocal().getWidth();
		final double yorigin = shape.isXLabelSouth() ? yStep * originY + labelHeight : yStep * originY - 2d;

		texts.setAttribute(SVGAttributes.SVG_FONT_SIZE, String.valueOf(shape.getLabelsSize()));
		texts.setAttribute(SVGAttributes.SVG_STROKE, CSSColors.INSTANCE.getColorName(gridLabelsColor, true));
		texts.setAttribute(prefix + LNamespace.XML_TYPE, LNamespace.XML_TYPE_TEXT);

		if(gridLabelsColor.getO() < 1d) {
			texts.setAttribute(SVGAttributes.SVG_OPACITY, MathUtils.INST.format.format(gridLabelsColor.getO()));
		}

		produceSVGGridLabelsTexts(document, texts, gridWidth, xorigin, yorigin, fooText, minX, maxX, minY, maxY, tlx, tly, labelWidth, labelHeight, absStep);

		elt.appendChild(texts);
	}


	/**
	 * Creates the SVG element corresponding to the grid.
	 */
	void createSVGGrid(final SVGElement elt, final SVGDocument document) {
		if(elt == null || document == null) {
			return;
		}
		// Initialisation of the parameters.
		final String prefix = LNamespace.LATEXDRAW_NAMESPACE + ':';
		final double unit = shape.getUnit();
		final int subGridDiv = shape.getSubGridDiv();
		double xStep = Shape.PPC * unit;
		final double xSubStep;
		double yStep = Shape.PPC * unit;
		final double ySubStep;
		xStep *= shape.getGridEndX() < shape.getGridStartX() ? -1 : 1;
		yStep *= shape.getGridEndY() < shape.getGridStartY() ? -1 : 1;
		xSubStep = xStep / subGridDiv;
		ySubStep = yStep / subGridDiv;
		final int subGridDots = shape.getSubGridDots();
		final Point tl = shape.getTopLeftPoint();
		final Point br = shape.getBottomRightPoint();
		double tlx = tl.getX();
		double tly = tl.getY();
		double brx = br.getX();
		double bry = br.getY();
		final double minX = shape.getGridMinX();
		final double maxX = shape.getGridMaxX();
		final double minY = shape.getGridMinY();
		final double maxY = shape.getGridMaxY();
		final double absStep = Math.abs(xStep);
		final Color subGridColor = shape.getSubGridColour();
		final Color linesColor = shape.getLineColour();
		final double gridWidth = shape.getGridWidth();
		final double posX = Math.min(shape.getGridStartX(), shape.getGridEndX()) * Shape.PPC * unit;
		final double posY = -Math.min(shape.getGridStartY(), shape.getGridEndY()) * Shape.PPC * unit;
		final Point position = shape.getPosition();

		tlx -= position.getX();
		brx -= position.getX();
		tly -= position.getY();
		bry -= position.getY();
		elt.setAttribute(SVGAttributes.SVG_TRANSFORM, new SVGTransform.SVGTranslateTransformation(position.getX(), position.getY()).toString());

		// Creation of the sub-grid
		if(subGridDots > 0) {
			createSVGSubGridDots(document, elt, prefix, subGridDiv, unit, xSubStep, ySubStep, minX, maxX, minY, maxY, subGridDots, shape.getSubGridWidth(), tlx, tly, brx, bry, subGridColor);
		}else {
			if(subGridDiv > 1) {
				createSVGSubGridDiv(document, elt, prefix, subGridDiv, xSubStep, ySubStep, minX, maxX, minY, maxY, subGridDots, shape.getSubGridWidth(), tlx,
					tly, brx, bry, subGridColor, posX, posY, xStep, yStep);
			}
		}

		if(shape.getGridDots() > 0) {
			createSVGGridDots(document, elt, prefix, absStep, minX, maxX, minY, maxY, tlx, tly, brx, bry, unit, posX, posY, xStep, yStep, gridWidth,
				linesColor);
		}else {
			createSVGGridDiv(document, elt, prefix, minX, maxX, minY, maxY, tlx, tly, brx, bry, posX, posY, xStep, yStep, gridWidth, linesColor);
		}

		if(shape.getLabelsSize() > 0) {
			createSVGGridLabels(document, elt, prefix, minX, maxX, minY, maxY, tlx, tly, xStep, yStep, gridWidth, absStep);
		}

		if(MathUtils.INST.equalsDouble(shape.getRotationAngle() % (Math.PI * 2), 0.)) {
			setSVGRotationAttribute(elt);
		}
	}


	@Override
	SVGElement toSVG(final @NotNull SVGDocument doc) {
		final String prefix = LNamespace.LATEXDRAW_NAMESPACE + ':';
		final SVGElement root = new SVGGElement(doc);

		root.setAttribute(SVGAttributes.SVG_ID, getSVGID());
		root.setAttribute(prefix + LNamespace.XML_TYPE, LNamespace.XML_TYPE_GRID);
		root.setAttribute(prefix + LNamespace.XML_GRID_X_SOUTH, String.valueOf(shape.isXLabelSouth()));
		root.setAttribute(prefix + LNamespace.XML_GRID_Y_WEST, String.valueOf(shape.isYLabelWest()));
		root.setAttribute(prefix + LNamespace.XML_GRID_UNIT, String.valueOf(shape.getUnit()));
		root.setAttribute(prefix + LNamespace.XML_GRID_END, shape.getGridEndX() + " " + shape.getGridEndY()); //NON-NLS
		root.setAttribute(prefix + LNamespace.XML_GRID_START, shape.getGridStartX() + " " + shape.getGridStartY()); //NON-NLS
		root.setAttribute(prefix + LNamespace.XML_GRID_ORIGIN, shape.getOriginX() + " " + shape.getOriginY()); //NON-NLS
		createSVGGrid(root, doc);
		setSVGRotationAttribute(root);

		return root;
	}
}
