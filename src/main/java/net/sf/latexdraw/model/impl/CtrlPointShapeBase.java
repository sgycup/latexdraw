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

import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.sf.latexdraw.model.MathUtils;
import net.sf.latexdraw.model.ShapeFactory;
import net.sf.latexdraw.model.api.shape.ControlPointShape;
import net.sf.latexdraw.model.api.shape.Line;
import net.sf.latexdraw.model.api.shape.Point;
import net.sf.latexdraw.model.api.shape.Position;
import org.jetbrains.annotations.NotNull;

/**
 * An implementation of a abstract shape that contains control points.
 * @author Arnaud Blouin
 */
abstract class CtrlPointShapeBase extends ModifiablePointsShapeBase implements ControlPointShape {
	static List<Point> computeDefaultFirstCtrlPoints(final List<Point> pts) {
		return pts == null ? Collections.emptyList() : pts.stream().
			map(pt -> ShapeFactory.INST.createPoint(pt.getX(), pt.getY() + DEFAULT_POSITION_CTRL)).collect(Collectors.toList());
	}

	/** The default balance gap used to balance all the points of the bézier curve. */
	protected int defaultBalanceGap = 50;
	/** This vector contains the points which allows to change the angles of the curves */
	protected final @NotNull List<Point> firstCtrlPts;
	/** Contains the second control points of each points; useful for closed curve. */
	protected final @NotNull List<Point> secondCtrlPts;

	CtrlPointShapeBase(final @NotNull List<Point> pts, final @NotNull List<Point> ctrlPts) {
		super(pts);
		firstCtrlPts = ctrlPts.stream().map(ShapeFactory.INST::createPoint).collect(Collectors.toUnmodifiableList());
		secondCtrlPts = pts.stream().map(pt -> ShapeFactory.INST.createPoint()).collect(Collectors.toUnmodifiableList());
		updateSecondControlPoints();
	}

	@Override
	public void scale(final double prevWidth, final double prevHeight, final @NotNull Position pos, final @NotNull Rectangle2D bound) {
		super.scale(prevWidth, prevHeight, pos, bound);
		scaleSetPoints(firstCtrlPts, prevWidth, prevHeight, pos, bound);
		scaleSetPoints(secondCtrlPts, prevWidth, prevHeight, pos, bound);
	}

	@Override
	public void scaleWithRatio(final double prevWidth, final double prevHeight, final @NotNull Position pos, final @NotNull Rectangle2D bound) {
		super.scaleWithRatio(prevWidth, prevHeight, pos, bound);
		scaleSetPointsWithRatio(firstCtrlPts, prevWidth, prevHeight, pos, bound);
		scaleSetPointsWithRatio(secondCtrlPts, prevWidth, prevHeight, pos, bound);
	}

	/**
	 * Method used by the balance method. Just returns the balanced control points of the given points.
	 */
	private Point[] getBalancedPoints(final Point pt, final Point prevPt, final Point nextPt) {
		final Line line = ShapeFactory.INST.createLine(prevPt, nextPt);

		if(line.isHorizontalLine()) {
			line.setLine(pt.getX(), pt.getY(), pt.getX() + 10d, pt.getY());
		}else {
			final double b = pt.getY() - line.getA() * pt.getX();
			line.setLine(pt.getX(), pt.getY(), pt.getX() + 10d, line.getA() * (pt.getX() + 10d) + b);
		}

		return line.findPoints(pt, defaultBalanceGap);
	}


	/**
	 * Method used by the balance method. Just sets the given control points at the given position.
	 */
	private void setControlPoints(final int position, final Point[] ctrlPts) {
		if(ctrlPts == null || ctrlPts.length != 2) {
			return;
		}

		// If there exists an intersection point between the two lines created using control points and points,
		// where is a loop that must be removed by inverting the control points.
		// For the first point, the lines are created differently.
		final int posPrev;
		final int posNext;

		if(position == 0) {
			posNext = points.size() - 1;
			posPrev = 1;
		}else {
			posNext = position == points.size() - 1 ? 0 : position + 1;
			posPrev = position - 1;
		}

		final Line line1 = ShapeFactory.INST.createLine(getPtAt(posPrev), ctrlPts[0]);
		final Line line2 = ShapeFactory.INST.createLine(getPtAt(posNext), ctrlPts[1]);

		if(line1.getIntersectionSegment(line2) == null) {
			firstCtrlPts.get(position).setPoint(ctrlPts[0]);
			secondCtrlPts.get(position).setPoint(ctrlPts[1]);
		}else {
			firstCtrlPts.get(position).setPoint(ctrlPts[1]);
			secondCtrlPts.get(position).setPoint(ctrlPts[0]);
		}
	}


	@Override
	public void balance() {
		final int size = getNbPoints();

		//Works only with more than 2 points.
		if(size < 3) {
			return;
		}

		Point ptPrev;
		Point ptNext;

		// Balancing all the points except the first and the last one.
		for(int i = 1; i < size - 1; i++) {
			ptPrev = points.get(i - 1);
			ptNext = points.get(i + 1);
			setControlPoints(i, getBalancedPoints(points.get(i), ptPrev, ptNext));
		}

		// Balancing the first and the last points.
		ptPrev = points.get(size - 1);
		ptNext = points.get(1);
		setControlPoints(0, getBalancedPoints(points.get(0), ptPrev, ptNext));
		ptPrev = points.get(size - 2);
		ptNext = points.get(0);
		setControlPoints(size - 1, getBalancedPoints(points.get(size - 1), ptPrev, ptNext));
	}


	@Override
	public Point getFirstCtrlPtAt(final int position) {
		if(position < -1 || firstCtrlPts.isEmpty() || position >= firstCtrlPts.size()) {
			return null;
		}
		return position == -1 ? firstCtrlPts.get(firstCtrlPts.size() - 1) : firstCtrlPts.get(position);
	}


	@Override
	public @NotNull List<Point> getFirstCtrlPts() {
		return firstCtrlPts;
	}

	@Override
	public void mirrorHorizontal(final double x) {
		super.mirrorHorizontal(x);
		if(MathUtils.INST.isValidCoord(x)) {
			firstCtrlPts.forEach(pt -> pt.setPoint(pt.horizontalSymmetry(x)));
			secondCtrlPts.forEach(pt -> pt.setPoint(pt.horizontalSymmetry(x)));
		}
	}

	@Override
	public void mirrorVertical(final double y) {
		super.mirrorVertical(y);
		if(MathUtils.INST.isValidCoord(y)) {
			firstCtrlPts.forEach(pt -> pt.setPoint(pt.verticalSymmetry(y)));
			secondCtrlPts.forEach(pt -> pt.setPoint(pt.verticalSymmetry(y)));
		}
	}

	@Override
	public Point getSecondCtrlPtAt(final int position) {
		if(position < -1 || secondCtrlPts.isEmpty() || position >= secondCtrlPts.size()) {
			return null;
		}
		return position == -1 ? secondCtrlPts.get(secondCtrlPts.size() - 1) : secondCtrlPts.get(position);
	}


	@Override
	public @NotNull List<Point> getSecondCtrlPts() {
		return secondCtrlPts;
	}


	@Override
	public void setXFirstCtrlPt(final double x, final int id) {
		if(id >= 0 && MathUtils.INST.isValidCoord(x) && id < firstCtrlPts.size()) {
			firstCtrlPts.get(id).setX(x);
		}
	}


	@Override
	public void setXSecondCtrlPt(final double x, final int id) {
		if(id >= 0 && MathUtils.INST.isValidCoord(x) && id < secondCtrlPts.size()) {
			secondCtrlPts.get(id).setX(x);
		}
	}


	@Override
	public void setYFirstCtrlPt(final double y, final int id) {
		if(id >= 0 && MathUtils.INST.isValidCoord(y) && id < firstCtrlPts.size()) {
			firstCtrlPts.get(id).setY(y);
		}
	}


	@Override
	public void setYSecondCtrlPt(final double y, final int id) {
		if(id >= 0 && MathUtils.INST.isValidCoord(y) && id < secondCtrlPts.size()) {
			secondCtrlPts.get(id).setY(y);
		}
	}


	@Override
	public void updateSecondControlPoints() {
		for(int i = 0, size = points.size(); i < size; i++) {
			secondCtrlPts.get(i).setPoint(firstCtrlPts.get(i).centralSymmetry(points.get(i)));
		}
	}


	@Override
	public boolean setPoint(final double x, final double y, final int position) {
		final Point pt = getPtAt(position);

		if(pt == null || !MathUtils.INST.isValidPt(x, y)) {
			return false;
		}

		final double tx = x - pt.getX();
		final double ty = y - pt.getY();
		getFirstCtrlPtAt(position).translate(tx, ty);
		getSecondCtrlPtAt(position).translate(tx, ty);
		super.setPoint(x, y, position);

		return true;
	}


	@Override
	public void setRotationAngle(final double angle) {
		if(MathUtils.INST.isValidCoord(angle)) {
			final double diff = angle - getRotationAngle();
			final Point gc = getGravityCentre();

			super.setRotationAngle(angle);
			firstCtrlPts.forEach(pt -> pt.setPoint(pt.rotatePoint(gc, diff)));
			updateSecondControlPoints();
		}
	}


	@Override
	public void translate(final double tx, final double ty) {
		// Translating control points.
		if(MathUtils.INST.isValidPt(tx, ty)) {
			firstCtrlPts.forEach(pt -> pt.translate(tx, ty));
			secondCtrlPts.forEach(pt -> pt.translate(tx, ty));
			super.translate(tx, ty);
		}
	}

	@Override
	public abstract @NotNull ControlPointShape duplicate();
}
