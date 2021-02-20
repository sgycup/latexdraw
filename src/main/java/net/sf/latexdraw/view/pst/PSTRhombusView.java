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
package net.sf.latexdraw.view.pst;

import net.sf.latexdraw.model.MathUtils;
import net.sf.latexdraw.model.api.shape.Point;
import net.sf.latexdraw.model.api.shape.Rhombus;
import org.jetbrains.annotations.NotNull;

/**
 * Defines a PSTricks view of the LRhombus model.
 * @author Arnaud Blouin
 */
public class PSTRhombusView extends PSTClassicalView<Rhombus> {
	/**
	 * Creates and initialises a LRhombus PSTricks view.
	 * @param model The model to view.
	 */
	protected PSTRhombusView(final @NotNull Rhombus model) {
		super(model);
	}


	@Override
	public @NotNull String getCode(final @NotNull Point origin, final float ppc) {
		final Point tl = shape.getTopLeftPoint();
		final Point br = shape.getBottomRightPoint();
		final double tlx = tl.getX();
		final double tly = tl.getY();
		final double brx = br.getX();
		final double bry = br.getY();
		final double xCenter = (tlx + brx) / 2f - origin.getX();
		final double yCenter = origin.getY() - (tly + bry) / 2f;
		final StringBuilder params = getPropertiesCode(ppc);
		final double rotationAngle = Math.toDegrees(shape.getRotationAngle()) % 360;
		final StringBuilder code = new StringBuilder();

		if(!MathUtils.INST.equalsDouble(rotationAngle, 0.0)) {
			params.append(", gangle=").append(MathUtils.INST.getCutNumberFloat(-rotationAngle)); //NON-NLS
		}

		code.append("\\psdiamond["); //NON-NLS
		code.append(params);
		code.append(']').append('(');
		code.append(MathUtils.INST.getCutNumberFloat(xCenter / ppc)).append(',');
		code.append(MathUtils.INST.getCutNumberFloat(yCenter / ppc)).append(')').append('(');
		code.append(MathUtils.INST.getCutNumberFloat((brx - tlx) / 2f) / ppc).append(',');
		code.append(MathUtils.INST.getCutNumberFloat((bry - tly) / 2f) / ppc).append(')');

		return code.toString();
	}
}

