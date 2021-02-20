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
package net.sf.latexdraw.instrument;

import io.github.interacto.jfx.interaction.library.Click;
import io.github.interacto.jfx.interaction.library.DnD;
import io.github.interacto.jfx.interaction.library.MultiClick;
import io.github.interacto.jfx.interaction.library.PointsData;
import io.github.interacto.jfx.interaction.library.Press;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseButton;
import javafx.stage.FileChooser;
import net.sf.latexdraw.command.shape.AddShape;
import net.sf.latexdraw.command.shape.InitTextSetter;
import net.sf.latexdraw.command.shape.InsertPicture;
import net.sf.latexdraw.model.MathUtils;
import net.sf.latexdraw.model.ShapeFactory;
import net.sf.latexdraw.model.api.shape.BezierCurve;
import net.sf.latexdraw.model.api.shape.BorderPos;
import net.sf.latexdraw.model.api.shape.ControlPointShape;
import net.sf.latexdraw.model.api.shape.Freehand;
import net.sf.latexdraw.model.api.shape.ModifiablePointsShape;
import net.sf.latexdraw.model.api.shape.Point;
import net.sf.latexdraw.model.api.shape.Polygon;
import net.sf.latexdraw.model.api.shape.Polyline;
import net.sf.latexdraw.model.api.shape.PositionShape;
import net.sf.latexdraw.model.api.shape.RectangularShape;
import net.sf.latexdraw.model.api.shape.Shape;
import net.sf.latexdraw.model.api.shape.SquaredShape;
import net.sf.latexdraw.service.EditingService;
import net.sf.latexdraw.util.Inject;
import net.sf.latexdraw.view.jfx.Canvas;
import net.sf.latexdraw.view.jfx.MagneticGrid;
import net.sf.latexdraw.view.jfx.ViewFactory;
import org.jetbrains.annotations.NotNull;

/**
 * This instrument allows to draw shapes.
 * @author Arnaud Blouin
 */
public class Pencil extends CanvasInstrument {
	/** The text setter used to create text shapes. */
	private final @NotNull TextSetter textSetter;
	private final @NotNull ViewFactory viewFactory;
	private final @NotNull EditingService editing;

	/** The file chooser used to select the picture to insert. Use its getter instead as it is lazy instantiated. */
	private FileChooser pictureFileChooser;

	@Inject
	public Pencil(final Canvas canvas, final MagneticGrid grid, final TextSetter textSetter, final ViewFactory viewFactory, final EditingService editing) {
		super(canvas, grid);
		this.editing = Objects.requireNonNull(editing);
		this.textSetter = Objects.requireNonNull(textSetter);
		this.viewFactory = Objects.requireNonNull(viewFactory);
	}

	@Override
	public void uninstallBindings() {
		pictureFileChooser = null;
		super.uninstallBindings();
	}

	/**
	 * Entry point for a testing purpose only.
	 */
	protected void setPictureFileChooser(final FileChooser chooser) {
		pictureFileChooser = chooser;
	}

	/**
	 * @return The file chooser used to select pictures to insert. Cannot be null.
	 */
	protected FileChooser getPictureFileChooser() {
		if(pictureFileChooser == null) {
			pictureFileChooser = new FileChooser();
			pictureFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
				"png/bmp/gif/jpg/eps/pdf", Arrays.asList("*.png", "*.bmp", "*.gif", "*.jpeg", "*.jpg", "*.eps", "*.pdf"))); //NON-NLS
		}
		return pictureFileChooser;
	}

	@Override
	public void setActivated(final boolean active) {
		if(activated != active) {
			super.setActivated(active);
		}
	}


	@Override
	protected void configureBindings() {
		bindPressToAddShape();

		// Binds a pressure to insert a picture
		nodeBinder()
			.usingInteraction(Press::new)
			.toProduce(i -> new InsertPicture(ShapeFactory.INST.createPicture(getAdaptedPoint(i.getSrcLocalPoint())), canvas.getDrawing(), getPictureFileChooser()))
			.on(canvas)
			.when(i -> editing.getCurrentChoice() == EditionChoice.PICTURE && i.getButton() == MouseButton.PRIMARY)
			.bind();

		bindDnDToDrawRectangularShape();
		bindDnDToDrawSquaredShape();
		bindDnDToDrawFreeHandShape();
		bindMultiClic2AddShape();

		// Binds a pressure to show the text setter
		nodeBinder()
			.usingInteraction(Click::new)
			.toProduce(i -> new InitTextSetter(textSetter, textSetter, "", getAdaptedPoint(i.getSrcLocalPoint()), null, null))
			.on(canvas)
			.when(i -> (editing.getCurrentChoice() == EditionChoice.TEXT || editing.getCurrentChoice() == EditionChoice.PLOT) && i.getButton() == MouseButton.PRIMARY)
			.bind();
	}

	/**
	 * Binds a DnD interaction to create shape.
	 */
	private void bindDnDToDrawFreeHandShape() {
		nodeBinder()
			.usingInteraction(() -> new DnD(false, true))
			.toProduce(i -> {
				final Shape sh = editing.createShapeInstance();
				final Point pt = getAdaptedPoint(i.getSrcLocalPoint());
				sh.getPoints().get(0).setPoint(pt.getX(), pt.getY());
				return new AddShape(sh, canvas.getDrawing());
			})
			.on(canvas)
			.first((i, c) -> canvas.requestFocus())
			.then((i, c) -> {
				final Point last = c.getShape().getPtAt(-1);
				final Point endPt = getAdaptedPoint(i.getTgtLocalPoint());
				if(!MathUtils.INST.equalsDouble(last.getX(), endPt.getX(), 0.0001) &&
					!MathUtils.INST.equalsDouble(last.getY(), endPt.getY(), 0.0001)) {
					c.setShape(ShapeFactory.INST.createFreeHandFrom((Freehand) c.getShape(), endPt));
				}
				canvas.setTempView(viewFactory.createView(c.getShape()).orElse(null));
			})
			.endOrCancel(i -> canvas.setTempView(null))
			.when(i -> i.getButton() == MouseButton.PRIMARY && editing.getCurrentChoice() == EditionChoice.FREE_HAND)
			.strictStart()
			.bind();
	}

	/**
	 * Binds a DnD interaction to draw squared shapes.
	 */
	private void bindDnDToDrawSquaredShape() {
		nodeBinder()
			.usingInteraction(() -> new DnD(false, true))
			.toProduce(i -> {
				final SquaredShape sq = (SquaredShape) editing.createShapeInstance();
				final Point pt = getAdaptedPoint(i.getSrcLocalPoint());
				sq.setPosition(pt.getX() - 1d, pt.getY() - 1d);
				sq.setWidth(2d);
				return new AddShape(sq, canvas.getDrawing());
			})
			.on(canvas)
			.first((i, c) -> {
				canvas.requestFocus();
				canvas.setTempView(viewFactory.createView(c.getShape()).orElse(null));
			})
			.then((i, c) -> updateShapeFromCentre((SquaredShape) c.getShape(), getAdaptedPoint(i.getSrcLocalPoint()), getAdaptedPoint(i.getTgtLocalPoint()).getX()))
			.endOrCancel(i -> canvas.setTempView(null))
			.when(i -> i.getButton() == MouseButton.PRIMARY && (editing.getCurrentChoice() == EditionChoice.SQUARE ||
				editing.getCurrentChoice() == EditionChoice.CIRCLE || editing.getCurrentChoice() == EditionChoice.CIRCLE_ARC))
			.strictStart()
			.bind();
	}

	/**
	 * Binds a DnD interaction to draw rectangular shapes.
	 */
	private void bindDnDToDrawRectangularShape() {
		nodeBinder()
			.usingInteraction(() -> new DnD(false, true))
			.toProduce(i -> new AddShape(editing.createShapeInstance(), canvas.getDrawing()))
			.on(canvas)
			.first((i, c) -> {
				canvas.requestFocus();
				canvas.setTempView(viewFactory.createView(c.getShape()).orElse(null));
			})
			.then((i, c) -> updateShapeFromDiag((RectangularShape) c.getShape(), getAdaptedPoint(i.getSrcLocalPoint()), getAdaptedPoint(i.getTgtLocalPoint())))
			.endOrCancel(i -> canvas.setTempView(null))
			.strictStart()
			.when(i -> i.getButton() == MouseButton.PRIMARY &&
				(editing.getCurrentChoice() == EditionChoice.RECT || editing.getCurrentChoice() == EditionChoice.ELLIPSE ||
				editing.getCurrentChoice() == EditionChoice.RHOMBUS || editing.getCurrentChoice() == EditionChoice.TRIANGLE))
			.bind();
	}

	/**
	 * Binds a multi-click interaction to creates multi-point shapes.
	 */
	private void bindMultiClic2AddShape() {
		final Function<PointsData, AddShape> creation = i -> new AddShape(setInitialPtsShape(editing.createShapeInstance(),
			i.getPointsData().get(0).getSrcLocalPoint()), canvas.getDrawing());

		// Binding for polygons
		nodeBinder()
			.usingInteraction(() -> new MultiClick(3))
			.toProduce(creation)
			.on(canvas)
			.then((i, c) -> {
				final Point currPoint = getAdaptedPoint(i.getCurrentPosition());
				if(c.getShape().getNbPoints() == i.getPointsData().size() && i.getLastButton().orElse(MouseButton.NONE) == MouseButton.PRIMARY) {
					c.setShape(ShapeFactory.INST.createPolygonFrom((Polygon) c.getShape(), ShapeFactory.INST.createPoint(currPoint.getX(), currPoint.getY())));
				}else {
					((ModifiablePointsShape) c.getShape()).setPoint(currPoint.getX(), currPoint.getY(), -1);
				}
				canvas.setTempView(viewFactory.createView(c.getShape()).orElse(null));
			})
			.strictStart()
			.endOrCancel(i -> canvas.setTempView(null))
			.when(() -> editing.getCurrentChoice() == EditionChoice.POLYGON)
			.bind();

		// Binding for polyline
		nodeBinder()
			.usingInteraction(MultiClick::new)
			.toProduce(creation)
			.on(canvas)
			.then((i, c) -> {
				final Point currPoint = getAdaptedPoint(i.getCurrentPosition());
				if(c.getShape().getNbPoints() == i.getPointsData().size() && i.getLastButton().orElse(MouseButton.NONE) == MouseButton.PRIMARY) {
					c.setShape(ShapeFactory.INST.createPolylineFrom((Polyline) c.getShape(), ShapeFactory.INST.createPoint(currPoint.getX(), currPoint.getY())));
				}else {
					((ModifiablePointsShape) c.getShape()).setPoint(currPoint.getX(), currPoint.getY(), -1);
				}
				canvas.setTempView(viewFactory.createView(c.getShape()).orElse(null));
			})
			.strictStart()
			.endOrCancel(i -> canvas.setTempView(null))
			.when(() -> editing.getCurrentChoice() == EditionChoice.LINES)
			.bind();

		// Binding for bézier curves
		nodeBinder()
			.usingInteraction(MultiClick::new)
			.toProduce(creation)
			.on(canvas)
			.then((i, c) -> {
				final Point currPoint = getAdaptedPoint(i.getCurrentPosition());
				if(c.getShape().getNbPoints() == i.getPointsData().size() && i.getLastButton().orElse(MouseButton.NONE) == MouseButton.PRIMARY) {
					c.setShape(ShapeFactory.INST.createBezierCurveFrom((BezierCurve) c.getShape(), ShapeFactory.INST.createPoint(currPoint.getX(), currPoint.getY())));
				}else {
					((ModifiablePointsShape) c.getShape()).setPoint(currPoint.getX(), currPoint.getY(), -1);
				}
				((ControlPointShape) c.getShape()).balance();
				canvas.setTempView(viewFactory.createView(c.getShape()).orElse(null));
			})
			.strictStart()
			.endOrCancel(i -> canvas.setTempView(null))
			.when(() -> editing.getCurrentChoice() == EditionChoice.BEZIER_CURVE)
			.bind();
	}

	/**
	 * Binds a press interaction to add a shape.
	 */
	private void bindPressToAddShape() {
		// Add axes, grids, or dots
		nodeBinder()
			.usingInteraction(Press::new)
			.toProduce(i -> {
				final PositionShape sh = (PositionShape) editing.createShapeInstance();
				sh.setPosition(getAdaptedPoint(i.getSrcLocalPoint()));
				return new AddShape(sh, canvas.getDrawing());
			})
			.on(canvas)
			.when(i -> i.getButton() == MouseButton.PRIMARY && (editing.getCurrentChoice() == EditionChoice.GRID ||
				editing.getCurrentChoice() == EditionChoice.DOT || editing.getCurrentChoice() == EditionChoice.AXES))
			.bind();

		// When a user starts to type a text using the text setter and then he clicks somewhere else in the canvas,
		// the text typed must be added (if possible to the canvas) before starting typing a new text.
		nodeBinder()
			.usingInteraction(Press::new)
			.toProduce(i -> new AddShape(ShapeFactory.INST.createText(ShapeFactory.INST.createPoint(textSetter.getPosition()), textSetter.getTextField().getText()),
				canvas.getDrawing()))
			.on(canvas)
			.when(i -> textSetter.isActivated() && !textSetter.getTextField().getText().isEmpty() && editing.getCurrentChoice() == EditionChoice.TEXT)
			.bind();
	}

	/**
	 * Companion function to the bindMultiClic2AddShape binding.
	 * It initialises the first two points of the given shape.
	 */
	private Shape setInitialPtsShape(final Shape sh, final Point3D firstPt) {
		if(sh instanceof ModifiablePointsShape) {
			final ModifiablePointsShape modShape = (ModifiablePointsShape) sh;
			final Point pt = getAdaptedPoint(firstPt);
			modShape.setPoint(pt.getX(), pt.getY(), 0);
			modShape.setPoint(pt.getX() + 1d, pt.getY() + 1d, 1);
		}
		return sh;
	}

	/**
	 * @param shape The shape to analyse.
	 * @return The gap that must respect the pencil to not allow shape to disappear when they are too small.
	 */
	private double getGap(final Shape shape) {
		// These lines are necessary to avoid shape to disappear. It appends when the borders position is INTO.
		// In this case,the minimum radius must be computed using the thickness and the double size.
		if(shape.isBordersMovable() && shape.getBordersPosition() == BorderPos.INTO) {
			return shape.getThickness() + (shape.isDbleBorderable() && shape.hasDbleBord() ? shape.getDbleBordSep() : 0d);
		}
		return 1d;
	}

	/**
	 * Companion method to a bind. It updates the given from its centre using the new given data points.
	 */
	private void updateShapeFromCentre(final SquaredShape shape, final Point startPt, final double endX) {
		final double sx = startPt.getX();
		final double radius = Math.max(sx < endX ? endX - sx : sx - endX, getGap(shape));
		shape.setPosition(sx - radius, startPt.getY() + radius);
		shape.setWidth(radius * 2d);
	}

	/**
	 * Companion method to a bind. It updates the given from its diagonal using the new given data points.
	 */
	private void updateShapeFromDiag(final RectangularShape shape, final Point startPt, final Point endPt) {
		final double gap = getGap(shape);
		double v1 = startPt.getX();
		double v2 = endPt.getX();
		double tlx = Double.NaN;
		double tly = Double.NaN;
		double brx = Double.NaN;
		double bry = Double.NaN;
		boolean ok = true;

		if(Math.abs(v1 - v2) > gap) {
			if(v1 < v2) {
				brx = v2;
				tlx = v1;
			}else {
				brx = v1;
				tlx = v2;
			}
		}else {
			ok = false;
		}

		v1 = startPt.getY();
		v2 = endPt.getY();

		if(Math.abs(v1 - v2) > gap) {
			if(v1 < v2) {
				bry = v2;
				tly = v1;
			}else {
				bry = v1;
				tly = v2;
			}
		}else {
			ok = false;
		}

		if(ok) {
			shape.setPosition(tlx, bry);
			shape.setWidth(brx - tlx);
			shape.setHeight(bry - tly);
		}
	}
}
