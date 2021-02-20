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
import net.sf.latexdraw.model.api.shape.Color;
import net.sf.latexdraw.model.api.shape.Point;
import net.sf.latexdraw.model.api.shape.Text;
import org.jetbrains.annotations.NotNull;

/**
 * Defines a PSTricks view of the LText model.
 * @author Arnaud Blouin
 */
public class PSTTextView extends PSTShapeView<Text> {
	/**
	 * Creates and initialises a LText PSTricks view.
	 * @param model The model to view.
	 */
	protected PSTTextView(final @NotNull Text model) {
		super(model);
	}


	@Override
	public @NotNull String getCode(final @NotNull Point origin, final float ppc) {
		final StringBuilder rot = getRotationHeaderCode(ppc, origin);
		final StringBuilder code = new StringBuilder();

		if(rot != null) {
			code.append(rot);
		}

		final String colorName;
		final Color lineCol = shape.getLineColour();

		if(PSTricksConstants.DEFAULT_LINE_COLOR.equals(lineCol)) {
			colorName = null;
		}else {
			colorName = getColourName(shape.getLineColour());
			addColour(colorName);
		}

		final String tokenPosition = shape.getTextPosition().getLatexToken();

		if(tokenPosition.isEmpty()) {
			code.append("\\rput("); //NON-NLS
		}else {
			code.append("\\rput[").append(shape.getTextPosition().getLatexToken()).append(']').append('('); //NON-NLS
		}

		code.append(MathUtils.INST.getCutNumberFloat((shape.getX() - origin.getX()) / ppc)).append(',');
		code.append(MathUtils.INST.getCutNumberFloat((origin.getY() - shape.getY()) / ppc)).append(')').append('{');

		if(colorName != null) {
			code.append("\\textcolor{").append(colorName).append('}').append('{'); //NON-NLS
		}

		code.append(shape.getText()).append('}');

		if(colorName != null) {
			code.append('}');
		}

		if(rot != null) {
			code.append('}');
		}

		return code.toString();
	}
}
