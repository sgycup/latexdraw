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
package net.sf.latexdraw.parser.svg.path;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * A list of SVGPath segments.
 * @author Arnaud BLOUIN
 */
public class SVGPathSegList extends ArrayList<SVGPathSeg> implements Consumer<SVGPathSeg> {
	@Override
	public String toString() {
		return stream()
			.filter(seg -> seg != null)
			.map(seg -> seg.toString())
			.collect(Collectors.joining(" "));
	}

	@Override
	public void accept(final SVGPathSeg pathSeg) {
		add(pathSeg);
	}
}
