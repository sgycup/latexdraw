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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.sf.latexdraw.model.ShapeFactory;
import net.sf.latexdraw.model.api.shape.BorderPos;
import net.sf.latexdraw.model.api.shape.Color;
import net.sf.latexdraw.model.api.shape.Dot;
import net.sf.latexdraw.model.api.shape.FillingStyle;
import net.sf.latexdraw.model.api.shape.Group;
import net.sf.latexdraw.model.api.shape.LineStyle;
import net.sf.latexdraw.model.api.shape.Point;
import net.sf.latexdraw.model.api.shape.Position;
import net.sf.latexdraw.model.api.shape.Shape;
import net.sf.latexdraw.model.api.shape.SquaredShape;
import net.sf.latexdraw.model.api.shape.StandardGrid;
import net.sf.latexdraw.view.pst.PSTricksConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This trait encapsulates the code of the group related to the support of the general shape's properties.
 * @author Arnaud Blouin
 */
interface GroupShapeBase extends Group {
	@Override
	default void copy(final Shape sh) {
	}

	@Override
	default double getBorderGap() {
		return isEmpty() ? 0d : getShapes().get(0).getBorderGap();
	}

	@Override
	default double getDashSepBlack() {
		return getShapes().stream().filter(sh -> sh.isLineStylable()).findFirst().map(sh -> sh.getDashSepBlack()).orElse(Double.NaN);
	}

	@Override
	default double getDashSepWhite() {
		return getShapes().stream().filter(sh -> sh.isLineStylable()).findFirst().map(sh -> sh.getDashSepWhite()).orElse(Double.NaN);
	}

	@Override
	default double getDotSep() {
		return getShapes().stream().filter(sh -> sh.isLineStylable()).findFirst().map(sh -> sh.getDotSep()).orElse(Double.NaN);
	}

	@Override
	default @NotNull Point getFullBottomRightPoint() {
		final double gap = getBorderGap();
		final Point br = getBottomRightPoint();
		br.translate(gap, gap);
		return br;
	}

	@Override
	default @NotNull Point getFullTopLeftPoint() {
		final double gap = getBorderGap();
		final Point tl = getTopLeftPoint();
		tl.translate(-gap, -gap);
		return tl;
	}

	@Override
	default double getHeight() {
		return isEmpty() ? 0d : getShapes().get(0).getHeight();
	}

	@Override
	default int getNbPoints() {
		return 0;
	}

	@Override
	default @NotNull ObservableList<Point> getPoints() {
		return FXCollections.emptyObservableList();
	}

	@Override
	default Point getPtAt(final int index) {
		return null;
	}

	@Override
	default double getWidth() {
		return isEmpty() ? 0d : getShapes().get(0).getWidth();
	}

	@Override
	default void scaleWithRatio(final double x, final double x2, final @NotNull Position x3, final @NotNull Rectangle2D x4) {
	}

	@Override
	default void setDashSepBlack(final double dash) {
		getShapes().stream().filter(sh -> sh.isLineStylable()).forEach(sh -> sh.setDashSepBlack(dash));
	}

	@Override
	default void setDashSepWhite(final double dash) {
		getShapes().stream().filter(sh -> sh.isLineStylable()).forEach(sh -> sh.setDashSepWhite(dash));
	}

	@Override
	default void setDotSep(final double dot) {
		getShapes().stream().filter(sh -> sh.isLineStylable()).forEach(sh -> sh.setDotSep(dot));
	}

	@Override
	default boolean shadowFillsShape() {
		return getShapes().stream().anyMatch(sh -> sh.shadowFillsShape());
	}

	@Override
	default void mirrorHorizontal(final double x) {
		getShapes().forEach(sh -> sh.mirrorHorizontal(x));
	}

	@Override
	default void mirrorVertical(final double y) {
		getShapes().forEach(sh -> sh.mirrorVertical(y));
	}

	@Override
	default void setThickness(final double thickness) {
		getShapes().forEach(sh -> sh.setThickness(thickness));
	}

	@Override
	default void scale(final double prevWidth, final double prevHeight, final @NotNull Position pos, final @NotNull Rectangle2D bound) {
		if(getShapes().stream().anyMatch(sh -> sh instanceof SquaredShape || sh instanceof StandardGrid || sh instanceof Dot)) {
			getShapes().forEach(sh -> sh.scaleWithRatio(prevWidth, prevHeight, pos, bound));
		}else {
			getShapes().forEach(sh -> sh.scale(prevWidth, prevHeight, pos, bound));
		}
	}

	@Override
	default double getThickness() {
		return getShapes().stream().filter(sh -> sh.isThicknessable()).map(sh -> sh.getThickness()).findFirst().orElse(Double.NaN);
	}

	@Override
	default double getFullThickness() {
		return getShapes().stream().filter(sh -> sh.isThicknessable()).map(sh -> sh.getFullThickness()).findFirst().orElse(Double.NaN);
	}

	@Override
	default boolean isColourable() {
		return getShapes().stream().anyMatch(sh -> sh.isColourable());
	}

	@Override
	default boolean isThicknessable() {
		return getShapes().stream().anyMatch(sh -> sh.isThicknessable());
	}

	@Override
	default boolean isShowPtsable() {
		return getShapes().stream().anyMatch(sh -> sh.isShowPtsable());
	}

	@Override
	default boolean isShowPts() {
		return getShapes().stream().anyMatch(sh -> sh.isShowPtsable() && sh.isShowPts());
	}

	@Override
	default void setShowPts(final boolean show) {
		getShapes().stream().filter(sh -> sh.isShowPtsable()).forEach(sh -> sh.setShowPts(show));
	}

	@Override
	default @NotNull Color getLineColour() {
		return isEmpty() ? PSTricksConstants.DEFAULT_LINE_COLOR : getShapes().get(0).getLineColour();
	}

	@Override
	default boolean isLineStylable() {
		return getShapes().stream().anyMatch(sh -> sh.isLineStylable());
	}

	@Override
	default @NotNull LineStyle getLineStyle() {
		return getShapes().stream().filter(sh -> sh.isLineStylable()).map(sh -> sh.getLineStyle()).findFirst().orElse(LineStyle.SOLID);
	}

	@Override
	default void setLineStyle(final @NotNull LineStyle style) {
		getShapes().stream().filter(sh -> sh.isLineStylable()).forEach(sh -> sh.setLineStyle(style));
	}

	@Override
	default boolean isBordersMovable() {
		return getShapes().stream().anyMatch(sh -> sh.isBordersMovable());
	}

	@Override
	default @NotNull BorderPos getBordersPosition() {
		return getShapes().stream().filter(sh -> sh.isBordersMovable()).map(sh -> sh.getBordersPosition()).findFirst().orElse(BorderPos.INTO);
	}

	@Override
	default void setBordersPosition(final @NotNull BorderPos position) {
		getShapes().stream().filter(sh -> sh.isLineStylable()).forEach(sh -> sh.setBordersPosition(position));
	}

	@Override
	default void setLineColour(final @NotNull Color lineColour) {
		getShapes().forEach(sh -> sh.setLineColour(lineColour));
	}

	@Override
	default void setDbleBordCol(final @NotNull Color colour) {
		getShapes().stream().filter(sh -> sh.isDbleBorderable()).forEach(sh -> sh.setDbleBordCol(colour));
	}

	@Override
	default @NotNull Color getDbleBordCol() {
		return getShapes().stream().filter(sh -> sh.hasDbleBord()).map(sh -> sh.getDbleBordCol()).findFirst().orElse(PSTricksConstants.DEFAULT_DOUBLE_COLOR);
	}

	@Override
	default boolean hasDbleBord() {
		return getShapes().stream().anyMatch(sh -> sh.isDbleBorderable() && sh.hasDbleBord());
	}

	@Override
	default void setHasDbleBord(final boolean dbleBorders) {
		getShapes().stream().filter(sh -> sh.isLineStylable()).forEach(sh -> sh.setHasDbleBord(dbleBorders));
	}

	@Override
	default boolean isDbleBorderable() {
		return getShapes().stream().anyMatch(sh -> sh.isDbleBorderable());
	}

	@Override
	default void setDbleBordSep(final double dbleBorderSep) {
		getShapes().stream().filter(sh -> sh.isLineStylable()).forEach(sh -> sh.setDbleBordSep(dbleBorderSep));
	}

	@Override
	default double getDbleBordSep() {
		return getShapes().stream().filter(sh -> sh.isDbleBorderable() && sh.hasDbleBord()).map(sh -> sh.getDbleBordSep()).findFirst().orElse(Double.NaN);
	}

	@Override
	default boolean isShadowable() {
		return getShapes().stream().anyMatch(sh -> sh.isShadowable());
	}

	@Override
	default boolean hasShadow() {
		return getShapes().stream().anyMatch(sh -> sh.isShadowable() && sh.hasShadow());
	}

	@Override
	default void setHasShadow(final boolean shadow) {
		getShapes().stream().filter(sh -> sh.isShadowable()).forEach(sh -> sh.setHasShadow(shadow));
	}

	@Override
	default void setShadowSize(final double shadSize) {
		getShapes().stream().filter(sh -> sh.isShadowable()).forEach(sh -> sh.setShadowSize(shadSize));
	}

	@Override
	default double getShadowSize() {
		return getShapes().stream().filter(sh -> sh.isShadowable() && sh.hasShadow()).map(sh -> sh.getShadowSize()).findFirst().orElse(Double.NaN);
	}

	@Override
	default void setShadowAngle(final double shadAngle) {
		getShapes().stream().filter(sh -> sh.isShadowable()).forEach(sh -> sh.setShadowAngle(shadAngle));
	}

	@Override
	default double getShadowAngle() {
		return getShapes().stream().filter(sh -> sh.isShadowable() && sh.hasShadow()).map(sh -> sh.getShadowAngle()).findFirst().orElse(Double.NaN);
	}

	@Override
	default void setShadowCol(final @NotNull Color colour) {
		getShapes().stream().filter(sh -> sh.isShadowable()).forEach(sh -> sh.setShadowCol(colour));
	}

	@Override
	default @NotNull Color getShadowCol() {
		return getShapes().stream().filter(sh -> sh.isShadowable() && sh.hasShadow()).
			map(sh -> sh.getShadowCol()).findFirst().orElse(PSTricksConstants.DEFAULT_SHADOW_COLOR);
	}

	@Override
	default @NotNull FillingStyle getFillingStyle() {
		return getShapes().stream().filter(sh -> sh.isInteriorStylable()).map(sh -> sh.getFillingStyle()).findFirst().orElse(FillingStyle.NONE);
	}

	@Override
	default void setFillingStyle(final @NotNull FillingStyle style) {
		getShapes().stream().filter(sh -> sh.isInteriorStylable()).forEach(sh -> sh.setFillingStyle(style));
	}

	@Override
	default void setFilled(final boolean filled) {
		getShapes().stream().filter(sh -> sh.isFillable()).forEach(sh -> sh.setFilled(filled));
	}

	@Override
	default boolean isFilled() {
		return getShapes().stream().anyMatch(sh -> sh.isFillable() && sh.isFilled());
	}

	@Override
	default boolean isFillable() {
		return getShapes().stream().anyMatch(sh -> sh.isFillable());
	}

	@Override
	default boolean isInteriorStylable() {
		return getShapes().stream().anyMatch(sh -> sh.isInteriorStylable());
	}

	@Override
	default void setFillingCol(final @NotNull Color colour) {
		getShapes().stream().filter(sh -> sh.isFillable()).forEach(sh -> sh.setFillingCol(colour));
	}

	@Override
	default @NotNull Color getFillingCol() {
		return getShapes().stream().filter(sh -> sh.isFillable() && sh.isFilled()).
			map(sh -> sh.getFillingCol()).findFirst().orElse(PSTricksConstants.DEFAULT_FILL_COLOR);
	}

	@Override
	default void setHatchingsCol(final @NotNull Color colour) {
		getShapes().stream().filter(sh -> sh.isInteriorStylable()).forEach(sh -> sh.setHatchingsCol(colour));
	}

	@Override
	default @NotNull Color getHatchingsCol() {
		return getShapes().stream().filter(sh -> sh.isInteriorStylable() && sh.getFillingStyle().isHatchings()).
			map(sh -> sh.getHatchingsCol()).findFirst().orElse(PSTricksConstants.DEFAULT_HATCHING_COLOR);
	}

	@Override
	default void setGradColStart(final @NotNull Color colour) {
		getShapes().stream().filter(sh -> sh.isInteriorStylable()).forEach(sh -> sh.setGradColStart(colour));
	}

	@Override
	default @NotNull Color getGradColStart() {
		return getShapes().stream().filter(sh -> sh.isInteriorStylable() && sh.getFillingStyle().isGradient()).
			map(sh -> sh.getGradColStart()).findFirst().orElse(PSTricksConstants.DEFAULT_GRADIENT_START_COLOR);
	}

	@Override
	default void setGradColEnd(final @NotNull Color colour) {
		getShapes().stream().filter(sh -> sh.isInteriorStylable()).forEach(sh -> sh.setGradColEnd(colour));
	}

	@Override
	default @NotNull Color getGradColEnd() {
		return getShapes().stream().filter(sh -> sh.isInteriorStylable() && sh.getFillingStyle().isGradient()).
			map(sh -> sh.getGradColEnd()).findFirst().orElse(PSTricksConstants.DEFAULT_GRADIENT_END_COLOR);
	}

	@Override
	default void setGradAngle(final double gradAngle) {
		getShapes().stream().filter(sh -> sh.isInteriorStylable()).forEach(sh -> sh.setGradAngle(gradAngle));
	}

	@Override
	default double getGradAngle() {
		return getShapes().stream().filter(sh -> sh.isInteriorStylable() && sh.getFillingStyle().isGradient()).
			map(sh -> sh.getGradAngle()).findFirst().orElse(Double.NaN);
	}

	@Override
	default void setGradMidPt(final double gradMidPoint) {
		getShapes().stream().filter(sh -> sh.isInteriorStylable()).forEach(sh -> sh.setGradMidPt(gradMidPoint));
	}

	@Override
	default double getGradMidPt() {
		return getShapes().stream().filter(sh -> sh.isInteriorStylable() && sh.getFillingStyle().isGradient()).
			map(sh -> sh.getGradMidPt()).findFirst().orElse(Double.NaN);
	}

	@Override
	default double getHatchingsAngle() {
		return getShapes().stream().filter(sh -> sh.isInteriorStylable()).map(sh -> sh.getHatchingsAngle()).findFirst().orElse(Double.NaN);
	}

	@Override
	default double getHatchingsSep() {
		return getShapes().stream().filter(sh -> sh.isInteriorStylable()).map(sh -> sh.getHatchingsSep()).findFirst().orElse(Double.NaN);
	}

	@Override
	default double getHatchingsWidth() {
		return getShapes().stream().filter(sh -> sh.isInteriorStylable()).map(sh -> sh.getHatchingsWidth()).findFirst().orElse(Double.NaN);
	}

	@Override
	default void setHatchingsAngle(final double hatchingsAngle) {
		getShapes().stream().filter(sh -> sh.isInteriorStylable()).forEach(sh -> sh.setHatchingsAngle(hatchingsAngle));
	}

	@Override
	default void setHatchingsSep(final double hatchingsSep) {
		getShapes().stream().filter(sh -> sh.isInteriorStylable()).forEach(sh -> sh.setHatchingsSep(hatchingsSep));
	}

	@Override
	default void setHatchingsWidth(final double hatchingsWidth) {
		getShapes().stream().filter(sh -> sh.isInteriorStylable()).forEach(sh -> sh.setHatchingsWidth(hatchingsWidth));
	}

	@Override
	default void translate(final double tx, final double ty) {
		getShapes().forEach(sh -> sh.translate(tx, ty));
	}

	@Override
	default void addToRotationAngle(final Point gravCentre, final double angle) {
		rotationAngleProperty().setValue(rotationAngleProperty().get() + angle);
		final Point gc = getGravityCentre();
		getShapes().forEach(sh -> sh.addToRotationAngle(gc, angle));
	}

	@Override
	default void setRotationAngle(final double rotationAngle) {
		rotationAngleProperty().setValue(rotationAngle);
		getShapes().forEach(sh -> sh.setRotationAngle(rotationAngle));
	}

	@Override
	default void rotate(final @Nullable Point point, final double angle) {
		getShapes().forEach(sh -> sh.rotate(point, angle));
	}

	@Override
	default double getRotationAngle() {
		return size() == 1 ? getShapeAt(0).map(s -> s.getRotationAngle()).orElse(0d) : rotationAngleProperty().doubleValue();
	}

	@Override
	default @NotNull Point getGravityCentre() {
		return isEmpty() ? ShapeFactory.INST.createPoint() : getTopLeftPoint().getMiddlePoint(getBottomRightPoint());
	}

	@Override
	default @NotNull Point getBottomRightPoint() {
		return getShapes().parallelStream().map(sh -> sh.getBottomRightPoint()).
			reduce((p1, p2) -> ShapeFactory.INST.createPoint(Math.max(p1.getX(), p2.getX()), Math.max(p1.getY(), p2.getY()))).
			orElseGet(() -> ShapeFactory.INST.createPoint(Double.NaN, Double.NaN));
	}

	@Override
	default @NotNull Point getBottomLeftPoint() {
		return getShapes().parallelStream().map(sh -> sh.getBottomLeftPoint()).
			reduce((p1, p2) -> ShapeFactory.INST.createPoint(Math.min(p1.getX(), p2.getX()), Math.max(p1.getY(), p2.getY()))).
			orElseGet(() -> ShapeFactory.INST.createPoint(Double.NaN, Double.NaN));
	}

	@Override
	default @NotNull Point getTopLeftPoint() {
		return getShapes().parallelStream().map(sh -> sh.getTopLeftPoint()).
			reduce((p1, p2) -> ShapeFactory.INST.createPoint(Math.min(p1.getX(), p2.getX()), Math.min(p1.getY(), p2.getY()))).
			orElseGet(() -> ShapeFactory.INST.createPoint(Double.NaN, Double.NaN));
	}

	@Override
	default @NotNull Point getTopRightPoint() {
		return getShapes().parallelStream().map(sh -> sh.getTopRightPoint()).
			reduce((p1, p2) -> ShapeFactory.INST.createPoint(Math.max(p1.getX(), p2.getX()), Math.min(p1.getY(), p2.getY()))).
			orElseGet(() -> ShapeFactory.INST.createPoint(Double.NaN, Double.NaN));
	}

	@Override
	default boolean hasHatchings() {
		return getShapes().stream().anyMatch(sh -> sh.hasHatchings());
	}

	@Override
	default boolean hasGradient() {
		return getShapes().stream().anyMatch(sh -> sh.hasGradient());
	}
}
