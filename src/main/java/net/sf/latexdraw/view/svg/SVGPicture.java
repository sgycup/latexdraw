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

import net.sf.latexdraw.model.ShapeFactory;
import net.sf.latexdraw.model.api.shape.Picture;
import net.sf.latexdraw.parser.svg.SVGAttributes;
import net.sf.latexdraw.parser.svg.SVGDocument;
import net.sf.latexdraw.parser.svg.SVGElement;
import net.sf.latexdraw.parser.svg.SVGGElement;
import net.sf.latexdraw.parser.svg.SVGImageElement;
import net.sf.latexdraw.util.LNamespace;
import org.jetbrains.annotations.NotNull;

/**
 * An SVG generator for an picture.
 * @author Arnaud BLOUIN
 */
class SVGPicture extends SVGShape<Picture> {
	SVGPicture(final Picture shape) {
		super(shape);
	}


	/**
	 * Creates a picture from a SVGImage element.
	 * @param elt The source element.
	 */
	SVGPicture(final SVGImageElement elt) {
		this(ShapeFactory.INST.createPicture(ShapeFactory.INST.createPoint()));

		shape.setPathSource(elt.getURI());
		shape.getPosition().setPoint(elt.getX(), elt.getY());
		applyTransformations(elt);
	}


	/**
	 * Creates a picture from a latexdraw-SVG element.
	 * @param elt The source element.
	 */
	SVGPicture(final SVGGElement elt, final boolean withTransformation) {
		this(ShapeFactory.INST.createPicture(ShapeFactory.INST.createPoint()));

		final SVGElement elt2 = getLaTeXDrawElement(elt, null);

		if(elt == null || !(elt2 instanceof SVGImageElement)) {
			throw new IllegalArgumentException();
		}

		final SVGImageElement main = (SVGImageElement) elt2;

		shape.setPathSource(main.getURI());
		shape.setPosition(main.getX(), main.getY());

		if(withTransformation) {
			applyTransformations(elt);
		}
	}


	@Override
	SVGElement toSVG(final @NotNull SVGDocument doc) {
		final SVGElement root = new SVGGElement(doc);
		final SVGElement img;

		root.setAttribute(LNamespace.LATEXDRAW_NAMESPACE + ':' + LNamespace.XML_TYPE, LNamespace.XML_TYPE_PICTURE);
		root.setAttribute(SVGAttributes.SVG_ID, getSVGID());

		img = new SVGImageElement(doc, shape.getPathSource());
		img.setAttribute(SVGAttributes.SVG_X, String.valueOf(shape.getPosition().getX()));
		img.setAttribute(SVGAttributes.SVG_Y, String.valueOf(shape.getPosition().getY()));
		img.setAttribute(SVGAttributes.SVG_HEIGHT, String.valueOf(shape.getImage().getHeight()));
		img.setAttribute(SVGAttributes.SVG_WIDTH, String.valueOf(shape.getImage().getWidth()));
		setSVGRotationAttribute(root);
		root.appendChild(img);

		return root;
	}
}
