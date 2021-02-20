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
package net.sf.latexdraw.view.jfx;

import java.awt.geom.Point2D;
import java.util.Optional;
import java.util.function.Supplier;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.transform.Rotate;
import net.sf.latexdraw.model.MathUtils;
import net.sf.latexdraw.model.ShapeFactory;
import net.sf.latexdraw.model.api.shape.Color;
import net.sf.latexdraw.model.api.shape.FillingStyle;
import net.sf.latexdraw.model.api.shape.Line;
import net.sf.latexdraw.model.api.shape.LineStyle;
import net.sf.latexdraw.model.api.shape.Point;
import net.sf.latexdraw.model.api.shape.SingleShape;

/**
 * The base class of a JFX single shape view.
 * @param <S> The type of the model.
 * @param <T> The type of the JFX shape used to draw the view.
 * @author Arnaud Blouin
 */
public abstract class ViewSingleShape<S extends SingleShape, T extends Shape> extends ViewShape<S> {
	protected final T border;
	protected final T dblBorder;
	protected final T shadow;
	protected Rotate shapeRotation;

	private final ChangeListener<?> strokesUpdateCall = (obj, oldVal, newVal) -> updateStrokes();
	private final ChangeListener<?> fillUpdateCall;
	private final ChangeListener<Boolean> shadowSetCall;
	private final ChangeListener<Number> shadowUpdateCall = (obs, oldVal, newVal) -> updateShadowPosition();


	/**
	 * Creates the view.
	 * @param sh The model.
	 */
	ViewSingleShape(final S sh) {
		super(sh);

		border = createJFXShape();
		border.setStrokeLineJoin(StrokeLineJoin.MITER);
		border.setStrokeLineCap(StrokeLineCap.BUTT);

		if(model.isShadowable()) {
			shadow = createJFXShape();
			shadow.setStrokeLineCap(StrokeLineCap.BUTT);
			getChildren().add(shadow);
			shadowSetCall = (obs, oldVal, newVal) -> {
				shadow.setDisable(!newVal);
				if(newVal && model.isFillable() && model.shadowFillsShape()) {
					border.setFill(getFillingPaint(model.getFillingStyle()));
				}
			};
			model.shadowProperty().addListener(shadowSetCall);
			shadow.strokeProperty().bind(Bindings.createObjectBinding(() -> model.getShadowCol().toJFX(), model.shadowColProperty()));
			shadow.fillProperty().bind(Bindings.createObjectBinding(
				() -> model.isFillable() && (model.isFilled() || model.shadowFillsShape()) ? model.getShadowCol().toJFX() : null, model.shadowColProperty()));
			model.shadowAngleProperty().addListener(shadowUpdateCall);
			model.shadowSizeProperty().addListener(shadowUpdateCall);
			shadow.strokeTypeProperty().bind(border.strokeTypeProperty());
			shadow.visibleProperty().bind(Bindings.createBooleanBinding(() -> !shadow.isDisable(), shadow.disableProperty()));
			shadow.setDisable(!model.hasShadow());
		}else {
			shadow = null;
			shadowSetCall = null;
		}

		getChildren().add(border);

		if(model.isDbleBorderable()) {
			dblBorder = createJFXShape();
			getChildren().add(dblBorder);
			dblBorder.setFill(null);
			dblBorder.setStrokeLineCap(StrokeLineCap.BUTT);
			dblBorder.layoutXProperty().bind(border.layoutXProperty());
			dblBorder.layoutYProperty().bind(border.layoutYProperty());
			model.dbleBordProperty().addListener((ChangeListener<? super Boolean>) strokesUpdateCall);
			model.dbleBordSepProperty().addListener((ChangeListener<? super Number>) strokesUpdateCall);
			model.dbleBordColProperty().addListener((ChangeListener<? super Color>) strokesUpdateCall);
			dblBorder.visibleProperty().bind(Bindings.createBooleanBinding(() -> !dblBorder.isDisable(), dblBorder.disableProperty()));
		} else {
			dblBorder = null;
		}

		if(model.isFillable()) {
			fillUpdateCall = (obs, oldVal, newVal) -> border.setFill(getFillingPaint(model.getFillingStyle()));
			model.fillingProperty().addListener((ChangeListener<? super FillingStyle>) fillUpdateCall);
			model.gradColStartProperty().addListener((ChangeListener<? super Color>) fillUpdateCall);
			model.gradColEndProperty().addListener((ChangeListener<? super Color>) fillUpdateCall);
			model.gradMidPtProperty().addListener((ChangeListener<? super Number>) fillUpdateCall);
			model.gradAngleProperty().addListener((ChangeListener<? super Number>) fillUpdateCall);
			model.fillingColProperty().addListener((ChangeListener<? super Color>) fillUpdateCall);
			model.hatchingsAngleProperty().addListener((ChangeListener<? super Number>) fillUpdateCall);
			model.hatchingsSepProperty().addListener((ChangeListener<? super Number>) fillUpdateCall);
			model.hatchingsWidthProperty().addListener((ChangeListener<? super Number>) fillUpdateCall);
			model.hatchingsColProperty().addListener((ChangeListener<? super Color>) fillUpdateCall);
			border.setFill(getFillingPaint(model.getFillingStyle()));
			// The filling must be updated on resize and co.
			border.boundsInLocalProperty().addListener((ChangeListener<? super Bounds>) fillUpdateCall);

		}else {
			fillUpdateCall = null;
		}

		border.strokeProperty().bind(Bindings.createObjectBinding(() -> model.getLineColour().toJFX(), model.lineColourProperty()));

		setUpLine();
		bindBorderMovable();
		updateStrokes();
		updateShadowPosition();
		bindRotationAngle();
	}

	private final void setUpLine() {
		if(model.isThicknessable()) {
			model.thicknessProperty().addListener((ChangeListener<? super Number>) strokesUpdateCall);
		}

		if(model.isLineStylable()) {
			model.linestyleProperty().addListener((ChangeListener<? super LineStyle>) strokesUpdateCall);
			model.dashSepBlackProperty().addListener((ChangeListener<? super Number>) strokesUpdateCall);
			model.dashSepWhiteProperty().addListener((ChangeListener<? super Number>) strokesUpdateCall);
			model.dotSepProperty().addListener((ChangeListener<? super Number>) strokesUpdateCall);
		}
	}


	private void bindRotationTransformation(final Supplier<Double> pivotX, final Supplier<Double> pivotY) {
		shapeRotation = new Rotate();
		border.getTransforms().add(shapeRotation);
		shapeRotation.angleProperty().bind(Bindings.createDoubleBinding(() -> Math.toDegrees(model.getRotationAngle()), model.rotationAngleProperty()));
		shapeRotation.pivotXProperty().bind(Bindings.createDoubleBinding(() -> pivotX.get(), model.rotationAngleProperty(),
			border.boundsInLocalProperty()));
		shapeRotation.pivotYProperty().bind(Bindings.createDoubleBinding(() -> pivotY.get(), model.rotationAngleProperty(),
			border.boundsInLocalProperty()));
	}

	/**
	 * Unbinds the current rotation binding if one is already installed.
	 */
	void unbindRotationAngle() {
		if(shapeRotation != null) {
			border.getTransforms().remove(shapeRotation);
			shapeRotation.angleProperty().unbind();
			shapeRotation.pivotXProperty().unbind();
			shapeRotation.pivotYProperty().unbind();
		}
	}

	/**
	 * Installs the default rotation binding.
	 */
	private void bindRotationAngle() {
		bindRotationTransformation(() -> model.getGravityCentre().getX(), () -> model.getGravityCentre().getY());
	}


	private final void updateShadowPosition() {
		if(shadow != null) {
			final Point gc = model.getGravityCentre();
			final Point shadowgc = ShapeFactory.INST.createPoint(gc.getX() + model.getShadowSize(), gc.getY());
			shadowgc.setPoint(shadowgc.rotatePoint(gc, model.getShadowAngle()));
			shadow.setTranslateX(shadowgc.getX() - gc.getX());
			shadow.setTranslateY(gc.getY() - shadowgc.getY());
		}
	}


	protected abstract T createJFXShape();

	private Paint getFillingPaint(final FillingStyle style) {
		return switch(style) {
			case NONE -> model.hasShadow() && model.shadowFillsShape() ? model.getFillingCol().toJFX() : null;
			case GRAD -> computeGradient();
			case PLAIN -> model.getFillingCol().toJFX();
			case CLINES_PLAIN, HLINES_PLAIN, VLINES_PLAIN, CLINES, VLINES, HLINES -> getHatchingsFillingPaint(style);
		};
	}


	private Paint getHatchingsFillingPaint(final FillingStyle style) {
		final Bounds bounds = border.getBoundsInParent();

		if(bounds.getWidth() <= 0d || bounds.getHeight() <= 0d) {
			return null;
		}

		final Group hatchings = new Group();
		final double hAngle = model.getHatchingsAngle();

		hatchings.getChildren().add(new Rectangle(bounds.getWidth(), bounds.getHeight(), style.isFilled() ? model.getFillingCol().toJFX() : null));

		final double angle = hAngle > 0d ? hAngle - Math.PI / 2d : hAngle + Math.PI / 2d;

		switch(style) {
			case VLINES, VLINES_PLAIN -> computeHatchings(hatchings, hAngle, bounds.getWidth(), bounds.getHeight());
			case HLINES, HLINES_PLAIN -> computeHatchings(hatchings, angle, bounds.getWidth(), bounds.getHeight());
			case CLINES, CLINES_PLAIN -> {
				computeHatchings(hatchings, hAngle, bounds.getWidth(), bounds.getHeight());
				computeHatchings(hatchings, angle, bounds.getWidth(), bounds.getHeight());
			}
		}

		final WritableImage image = new WritableImage((int) bounds.getWidth(), (int) bounds.getHeight());
		hatchings.snapshot(new SnapshotParameters(), image);
		return new ImagePattern(image, 0, 0, 1, 1, true);
	}

	/**
	 * Companion method of computeHatchings
	 */
	private double computeHatchingsAngle(final double angle) {
		if(angle > 0d) {
			if(angle > 3f * Math.PI / 2d) {
				return angle - Math.PI * 2d;
			}
			if(angle > Math.PI / 2d) {
				return angle - Math.PI;
			}
		}else {
			if(angle < -3f * Math.PI / 2d) {
				return angle + Math.PI * 2d;
			}
			if(angle < -Math.PI / 2d) {
				return angle + Math.PI;
			}
		}
		return angle;
	}

	/**
	 * Companion method of computeHatchings
	 */
	private void computeRotatedHatchings(final Group hatchings, final double angle2, final double width, final double height, final double val) {
		if(MathUtils.INST.equalsDouble(angle2, Math.PI / 2d)) {
			for(double y = 0d; y < height; y += val) {
				createHatchingLine(hatchings, 0d, y, width, y, width, height);
			}
		}else {
			final double incX = val / Math.cos(angle2);
			final double incY = val / Math.sin(angle2);
			final double limitX;
			double startY;
			double endX = 0d;

			if(angle2 > 0) {
				startY = 0d;
				limitX = width + height * Math.tan(angle2);
			}else {
				startY = height;
				limitX = width - height * Math.tan(angle2);
			}

			final double endY = startY;

			while(endX < limitX) {
				endX += incX;
				startY += incY;
				createHatchingLine(hatchings, 0d, startY, endX, endY, width, height);
			}
		}
	}

	private void computeHatchings(final Group hatchings, final double angle, final double width, final double height) {
		final double adaptedAngle = computeHatchingsAngle(angle % (Math.PI * 2d));
		final double val = model.getHatchingsWidth() + model.getHatchingsSep();

		if(MathUtils.INST.equalsDouble(adaptedAngle, 0d)) {

			for(double x = 0d; x < width; x += val) {
				createHatchingLine(hatchings, x, 0d, x, height, width, height);
			}
		}else {
			computeRotatedHatchings(hatchings, adaptedAngle, width, height, val);
		}
	}

	private void createHatchingLine(final Group group, final double x1, final double y1, final double x2, final double y2,
									final double clipWidth, final double clipHeight) {
		final javafx.scene.shape.Line line = new javafx.scene.shape.Line();
		line.setStrokeWidth(model.getHatchingsWidth());
		line.setStrokeLineJoin(StrokeLineJoin.MITER);
		line.setStrokeLineCap(StrokeLineCap.SQUARE);
		line.setStroke(model.getHatchingsCol().toJFX());
		line.setStartX(x1);
		line.setStartY(y1);
		line.setEndX(x2);
		line.setEndY(y2);
		// Required, otherwise the line may not be drawn.
		line.setClip(new Rectangle(0, 0, clipWidth, clipHeight));
		group.getChildren().add(line);
	}


	private LinearGradient computeRotatedGradient(final double angle, final double gradMidPt, final Point tl, final Point br) {
		Point pt1;
		Point pt2;

		if(MathUtils.INST.equalsDouble(angle % (Math.PI / 2d), 0d)) {
			pt1 = ShapeFactory.INST.createPoint(tl.getX(), (tl.getY() + br.getY()) / 2d);
			pt2 = ShapeFactory.INST.createPoint(br.getX(), (tl.getY() + br.getY()) / 2d);

			if(gradMidPt < 0.5) {
				pt1.setX(pt2.getX() - Point2D.distance(pt2.getX(), pt2.getY(), br.getX(), (tl.getY() + br.getY()) / 2d));
			}

			pt2.setX(tl.getX() + (br.getX() - tl.getX()) * gradMidPt);
		}else {
			final Line l2;
			final Point cg = model.getGravityCentre();
			pt1 = ShapeFactory.INST.createPoint((tl.getX() + br.getX()) / 2d, tl.getY()).rotatePoint(cg, -angle);
			pt2 = ShapeFactory.INST.createPoint((tl.getX() + br.getX()) / 2d, br.getY()).rotatePoint(cg, -angle);
			final Line l = ShapeFactory.INST.createLine(pt1, pt2);

			if(angle >= 0d && angle < Math.PI / 2d) {
				l2 = l.getPerpendicularLine(tl);
			}else {
				l2 = l.getPerpendicularLine(ShapeFactory.INST.createPoint(tl.getX(), br.getY()));
			}

			pt1 = l.getIntersection(l2);
			l.setX1(pt1.getX());
			l.setY1(pt1.getY());
			pt2 = l.findPoints(pt1, 2d * Point2D.distance(cg.getX(), cg.getY(), pt1.getX(), pt1.getY()) * gradMidPt)[0];

			if(gradMidPt < 0.5) {
				pt1 = pt1.rotatePoint(model.getGravityCentre(), Math.PI);
			}
		}

		return new LinearGradient(pt1.getX(), pt1.getY(), pt2.getX(), pt2.getY(), false, CycleMethod.NO_CYCLE,
			new Stop(0d, model.getGradColStart().toJFX()), new Stop(1d, model.getGradColEnd().toJFX()));
	}

	private LinearGradient computeGradient() {
		final Point tl = model.getTopLeftPoint();
		final Point br = model.getBottomRightPoint();

		if(tl.equals(br)) {
			return null;
		}

		double angle = model.getGradAngle() % (2d * Math.PI);
		double gradMidPt = model.getGradMidPt();

		if(angle < 0d) {
			angle = 2d * Math.PI + angle;
		}

		if(angle >= Math.PI) {
			gradMidPt = 1d - gradMidPt;
			angle -= Math.PI;
		}

		if(!MathUtils.INST.equalsDouble(angle, 0d)) {
			return computeRotatedGradient(angle, gradMidPt, tl, br);
		}

		final Point pt1 = ShapeFactory.INST.createPoint((tl.getX() + br.getX()) / 2d, tl.getY());
		final Point pt2 = ShapeFactory.INST.createPoint((tl.getX() + br.getX()) / 2d, br.getY());

		if(gradMidPt < 0.5) {
			pt1.setY(pt2.getY() - Point2D.distance(pt2.getX(), pt2.getY(), (tl.getX() + br.getX()) / 2d, br.getY()));
		}

		pt2.setY(tl.getY() + (br.getY() - tl.getY()) * gradMidPt);

		return new LinearGradient(pt1.getX(), pt1.getY(), pt2.getX(), pt2.getY(), false, CycleMethod.NO_CYCLE,
			new Stop(0d, model.getGradColStart().toJFX()), new Stop(1d, model.getGradColEnd().toJFX()));
	}

	private final void bindBorderMovable() {
		if(model.isBordersMovable()) {
			border.strokeTypeProperty().bind(Bindings.createObjectBinding(() -> switch(model.getBordersPosition()) {
				case INTO -> StrokeType.INSIDE;
				case MID -> StrokeType.CENTERED;
				case OUT -> StrokeType.OUTSIDE;
			}, model.borderPosProperty()));

			if(dblBorder != null) {
				dblBorder.strokeTypeProperty().bind(border.strokeTypeProperty());
			}
		}
	}

	protected double getDbleBorderGap() {
		if(!model.isDbleBorderable()) {
			return 0d;
		}

		return switch(model.getBordersPosition()) {
			case MID -> 0d;
			case INTO -> model.getThickness();
			case OUT -> -model.getThickness();
		};
	}

	private final void updateStrokes() {
		if(model.isThicknessable()) {
			border.setStrokeWidth(model.getFullThickness());
			if(shadow != null) {
				shadow.setStrokeWidth(model.getFullThickness());
			}
		}

		if(dblBorder != null) {
			dblBorder.setDisable(!model.hasDbleBord());
			if(model.hasDbleBord()) {
				dblBorder.setStroke(model.getDbleBordCol().toJFX());
				dblBorder.setStrokeWidth(model.getDbleBordSep());
			}
		}

		if(model.isLineStylable()) {
			switch(model.getLineStyle()) {
				case DASHED -> {
					border.setStrokeLineCap(StrokeLineCap.BUTT);
					border.getStrokeDashArray().clear();
					border.getStrokeDashArray().addAll(model.getDashSepBlack(), model.getDashSepWhite());
				}
				case DOTTED -> {
					border.setStrokeLineCap(StrokeLineCap.ROUND);
					border.getStrokeDashArray().clear();
					border.getStrokeDashArray().addAll(0d, model.getDotSep() + model.getFullThickness());
				}
				case SOLID -> {
					border.setStrokeLineCap(StrokeLineCap.BUTT);
					border.getStrokeDashArray().clear();
				}
			}
		}
	}

	public T getBorder() {
		return border;
	}

	public Optional<T> getDbleBorder() {
		return Optional.ofNullable(dblBorder);
	}

	public Optional<T> getShadow() {
		return Optional.ofNullable(shadow);
	}

	@Override
	public void flush() {
		super.flush();

		unbindRotationAngle();

		if(model.isThicknessable()) {
			model.thicknessProperty().removeListener((ChangeListener<? super Number>) strokesUpdateCall);
		}

		if(model.isLineStylable()) {
			model.linestyleProperty().removeListener((ChangeListener<? super LineStyle>) strokesUpdateCall);
			model.dashSepBlackProperty().removeListener((ChangeListener<? super Number>) strokesUpdateCall);
			model.dashSepWhiteProperty().removeListener((ChangeListener<? super Number>) strokesUpdateCall);
			model.dotSepProperty().removeListener((ChangeListener<? super Number>) strokesUpdateCall);
		}

		if(dblBorder != null) {
			dblBorder.layoutXProperty().unbind();
			dblBorder.layoutYProperty().unbind();
			model.dbleBordProperty().removeListener((ChangeListener<? super Boolean>) strokesUpdateCall);
			model.dbleBordSepProperty().removeListener((ChangeListener<? super Number>) strokesUpdateCall);
			model.dbleBordColProperty().removeListener((ChangeListener<? super Color>) strokesUpdateCall);
			dblBorder.strokeTypeProperty().unbind();
			dblBorder.visibleProperty().unbind();
		}

		if(fillUpdateCall != null) {
			model.fillingProperty().removeListener((ChangeListener<? super FillingStyle>) fillUpdateCall);
			model.gradColStartProperty().removeListener((ChangeListener<? super Color>) fillUpdateCall);
			model.gradColEndProperty().removeListener((ChangeListener<? super Color>) fillUpdateCall);
			model.gradMidPtProperty().removeListener((ChangeListener<? super Number>) fillUpdateCall);
			model.gradAngleProperty().removeListener((ChangeListener<? super Number>) fillUpdateCall);
			model.gradColEndProperty().removeListener((ChangeListener<? super Color>) fillUpdateCall);
			model.fillingColProperty().removeListener((ChangeListener<? super Color>) fillUpdateCall);
			model.hatchingsAngleProperty().removeListener((ChangeListener<? super Number>) fillUpdateCall);
			model.hatchingsSepProperty().removeListener((ChangeListener<? super Number>) fillUpdateCall);
			model.hatchingsWidthProperty().removeListener((ChangeListener<? super Number>) fillUpdateCall);
			model.hatchingsColProperty().removeListener((ChangeListener<? super Color>) fillUpdateCall);
			border.boundsInLocalProperty().removeListener((ChangeListener<? super Bounds>) fillUpdateCall);
		}

		if(shadowSetCall != null) {
			model.shadowProperty().removeListener(shadowSetCall);
			shadow.strokeProperty().unbind();
			shadow.fillProperty().unbind();
			model.shadowAngleProperty().removeListener(shadowUpdateCall);
			model.shadowSizeProperty().removeListener(shadowUpdateCall);
			shadow.strokeTypeProperty().unbind();
			shadow.visibleProperty().unbind();
		}

		border.strokeProperty().unbind();

		if(model.isBordersMovable()) {
			border.strokeTypeProperty().unbind();
		}
	}
}
