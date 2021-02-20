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

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import net.sf.latexdraw.model.MathUtils;
import net.sf.latexdraw.model.api.property.LineArcProp;
import org.jetbrains.annotations.NotNull;

/**
 * Line arc properties.
 * To turn as a trait as soon as Java will support private attributes in interfaces.
 * @author Arnaud Blouin
 */
class LineArcPropImpl implements LineArcProp {
	/** The radius of arcs drawn at the corners of lines. */
	final @NotNull DoubleProperty frameArc;

	LineArcPropImpl() {
		super();
		frameArc = new SimpleDoubleProperty(0d);
	}

	@Override
	public double getLineArc() {
		return frameArc.doubleValue();
	}

	@Override
	public boolean isRoundCorner() {
		return getLineArc() > 0d;
	}

	@Override
	public void setLineArc(final double arc) {
		if(arc >= 0d && arc <= 1d && MathUtils.INST.isValidCoord(arc)) {
			frameArc.set(arc);
		}
	}
}
