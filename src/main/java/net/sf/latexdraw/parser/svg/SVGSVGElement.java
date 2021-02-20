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

import java.util.List;
import org.w3c.dom.Node;

/**
 * Defines the SVG tag <code>SVG</code>.
 * @author Arnaud BLOUIN
 */
public class SVGSVGElement extends SVGElement implements SVGRectParseTrait {
	/**
	 * @param n The node.
	 * @param owner The owner document.
	 * @throws IllegalArgumentException parameters not valid.
	 */
	public SVGSVGElement(final SVGDocument owner, final Node n) {
		super(n);
		if(n == null || !n.getNodeName().endsWith(SVGElements.SVG_SVG) || owner == null) {
			throw new IllegalArgumentException();
		}
		if(!checkAttributes()) {
			throw new IllegalArgumentException("Tags not correct");
		}

		ownerDocument = owner;
	}


	/**
	 * @param n The source node.
	 * @param e Will not be used.
	 * @throws IllegalArgumentException parameters not valid.
	 */
	public SVGSVGElement(final Node n, final SVGElement e) {
		super(n, e);
		if(n == null || !n.getNodeName().endsWith(SVGElements.SVG_SVG)) {
			throw new IllegalArgumentException();
		}
		if(!checkAttributes()) {
			throw new IllegalArgumentException("Tags not correct");
		}
	}


	/**
	 * Creates an empty SVG element.
	 * @param owner The owner document.
	 * @throws IllegalArgumentException If owner is null.
	 */
	public SVGSVGElement(final SVGDocument owner) {
		super();

		if(owner == null) {
			throw new IllegalArgumentException();
		}

		setAttribute("xmlns", SVGDocument.SVG_NAMESPACE); //NON-NLS
		ownerDocument = owner;

		setNodeName(SVGElements.SVG_SVG);
	}


	/**
	 * @return the meta element or null.
	 */
	public SVGMetadataElement getMeta() {
		return children.getNodes().stream().filter(ch -> ch instanceof SVGMetadataElement).map(ch -> (SVGMetadataElement) ch).findAny().orElse(null);
	}


	/**
	 * @return the defs element or null.
	 */
	public SVGDefsElement getDefs() {
		return children.getNodes().stream().filter(ch -> ch instanceof SVGDefsElement).map(ch -> (SVGDefsElement) ch).findAny().orElse(null);
	}


	@Override
	public String toString() {
		final SVGMetadataElement meta = getMeta();
		final SVGDefsElement defs = getDefs();
		final StringBuilder str = new StringBuilder().append('[').append("attributes="); //NON-NLS
		final String eol = System.getProperty("line.separator");

		str.append(attributes).append(eol);

		if(meta != null) {
			str.append(", meta=").append(meta).append(eol); //NON-NLS
		}

		if(defs != null) {
			str.append(", defs=").append(defs).append(eol); //NON-NLS
		}

		str.append(", children={"); //NON-NLS

		final List<SVGElement> chiNodes = children.getNodes();
		final int size = chiNodes.size();

		for(int i = 0; i < size - 1; i++) {
			str.append(chiNodes.get(i)).append(',');
		}

		if(size > 0) {
			str.append(chiNodes.get(chiNodes.size() - 1));
		}

		return str.append('}').append(']').toString();
	}


	@Override
	public boolean checkAttributes() {
		return true;
	}


	@Override
	public boolean enableRendering() {
		return getWidth() >= 0d && getHeight() >= 0d;
	}


	/**
	 * @return The version of the SVG document or an empty string if it is not specified.
	 */
	public String getVersion() {
		return getAttribute(getUsablePrefix() + SVGAttributes.SVG_VERSION);
	}

	@Override
	public boolean isDimensionsRequired() {
		return false;
	}
}
