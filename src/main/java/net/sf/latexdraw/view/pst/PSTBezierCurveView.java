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

import java.util.List;
import net.sf.latexdraw.model.MathUtils;
import net.sf.latexdraw.model.api.shape.BezierCurve;
import net.sf.latexdraw.model.api.shape.Point;
import org.jetbrains.annotations.NotNull;

/**
 * Defines a PSTricks view of the LBezierCurve model.
 * @author Arnaud Blouin
 */
public class PSTBezierCurveView extends PSTClassicalView<BezierCurve> {
	/**
	 * Creates and initialises a LBezierCurve PSTricks view.
	 * @param model The model to view.
	 * @throws IllegalArgumentException If the given model is not valid.
	 */
	protected PSTBezierCurveView(final @NotNull BezierCurve model) {
		super(model);
	}


	@Override
	public @NotNull String getCode(final @NotNull Point origin, final float ppc) {
		if(shape.getNbPoints() < 2) {
			return "";
		}

		final int size = shape.getNbPoints();
		final StringBuilder coord = new StringBuilder();
		final List<Point> pts = shape.getPoints();
		final List<Point> fCtrlPts = shape.getFirstCtrlPts();
		final List<Point> sCtrlPts = shape.getSecondCtrlPts();
		final double originx = origin.getX();
		final double originy = origin.getY();

		coord.append('(').append(MathUtils.INST.getCutNumberFloat((pts.get(0).getX() - originx) / ppc));
		coord.append(',').append(MathUtils.INST.getCutNumberFloat((originy - pts.get(0).getY()) / ppc));
		coord.append(')').append('(').append(MathUtils.INST.getCutNumberFloat((fCtrlPts.get(0).getX() - originx) / ppc));
		coord.append(',').append(MathUtils.INST.getCutNumberFloat((originy - fCtrlPts.get(0).getY()) / ppc));
		coord.append(')').append('(').append(MathUtils.INST.getCutNumberFloat((fCtrlPts.get(1).getX() - originx) / ppc));
		coord.append(',').append(MathUtils.INST.getCutNumberFloat((originy - fCtrlPts.get(1).getY()) / ppc));
		coord.append(')').append('(').append(MathUtils.INST.getCutNumberFloat((pts.get(1).getX() - originx) / ppc));
		coord.append(',').append(MathUtils.INST.getCutNumber((originy - pts.get(1).getY()) / ppc)).append(')');

		for(int i = 2; i < size; i++) {
			final Point ctrlPt1 = fCtrlPts.get(i);
			final Point ctrlPt2 = sCtrlPts.get(i - 1);

			coord.append('(').append(MathUtils.INST.getCutNumberFloat((ctrlPt2.getX() - originx) / ppc));
			coord.append(',').append(MathUtils.INST.getCutNumberFloat((originy - ctrlPt2.getY()) / ppc));
			coord.append(')').append('(').append(MathUtils.INST.getCutNumberFloat((ctrlPt1.getX() - originx) / ppc));
			coord.append(',').append(MathUtils.INST.getCutNumberFloat((originy - ctrlPt1.getY()) / ppc));
			coord.append(')').append('(');

			final Point pt = pts.get(i);
			coord.append(MathUtils.INST.getCutNumberFloat((pt.getX() - originx) / ppc)).append(',');
			coord.append(MathUtils.INST.getCutNumberFloat((originy - pt.getY()) / ppc)).append(')');
		}

		if(!shape.isOpened()) {
			final Point ctrlPt1 = sCtrlPts.get(0);
			final Point ctrlPt2 = sCtrlPts.get(sCtrlPts.size() - 1);

			coord.append('(').append(MathUtils.INST.getCutNumberFloat((ctrlPt2.getX() - originx) / ppc));
			coord.append(',').append(MathUtils.INST.getCutNumberFloat((originy - ctrlPt2.getY()) / ppc));
			coord.append(')').append('(').append(MathUtils.INST.getCutNumberFloat((ctrlPt1.getX() - originx) / ppc));
			coord.append(',').append(MathUtils.INST.getCutNumberFloat((originy - ctrlPt1.getY()) / ppc));
			coord.append(')').append('(');

			final Point pt = pts.get(0);
			coord.append(MathUtils.INST.getCutNumberFloat((pt.getX() - originx) / ppc)).append(',');
			coord.append(MathUtils.INST.getCutNumberFloat((originy - pt.getY()) / ppc)).append(')');
		}

		final StringBuilder code = new StringBuilder();

		code.append("\\psbezier[").append(getPropertiesCode(ppc)).append(']'); //NON-NLS

		final StringBuilder arrowsStyle = getArrowsStyleCode();
		if(arrowsStyle != null) {
			code.append(arrowsStyle);
		}

		code.append(coord);

		return code.toString();
	}
}
