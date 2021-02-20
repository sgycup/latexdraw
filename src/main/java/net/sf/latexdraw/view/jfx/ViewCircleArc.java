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

import javafx.beans.binding.Bindings;
import javafx.scene.shape.Arc;
import net.sf.latexdraw.model.api.shape.CircleArc;

/**
 * A JFX view to render arcs.
 * @author Arnaud Blouin
 */
public class ViewCircleArc extends ViewSingleShape<CircleArc, Arc> {
	protected final ViewArrowableTraitArc viewArrows = new ViewArrowableTraitArc(this);

	/**
	 * Creates the view.
	 * @param sh The model.
	 */
	ViewCircleArc(final CircleArc sh) {
		super(sh);

		// Code duplicated from ViewCircle and ViewEllipseBased, but Arc and Ellipse share no common interface and no structural typing in Java.
		if(dblBorder != null) {
			dblBorder.centerXProperty().bind(border.centerXProperty());
			dblBorder.centerYProperty().bind(border.centerYProperty());
			dblBorder.radiusXProperty().bind(Bindings.createDoubleBinding(() -> border.getRadiusX() - getDbleBorderGap(),
				border.radiusXProperty(), border.strokeWidthProperty(), border.strokeTypeProperty()));
			dblBorder.radiusYProperty().bind(Bindings.createDoubleBinding(() -> border.getRadiusY() - getDbleBorderGap(),
				border.radiusYProperty(), border.strokeWidthProperty(), border.strokeTypeProperty()));
			bindArcProperties(dblBorder);
		}

		if(shadow != null) {
			shadow.centerXProperty().bind(border.centerXProperty());
			shadow.centerYProperty().bind(border.centerYProperty());
			shadow.radiusXProperty().bind(border.radiusXProperty());
			shadow.radiusYProperty().bind(border.radiusYProperty());
			bindArcProperties(shadow);
		}

		border.centerXProperty().bind(Bindings.createDoubleBinding(() -> model.getCenter().getX(), model.getPtAt(2).xProperty(), model.getPtAt(3).xProperty()));
		border.centerYProperty().bind(Bindings.createDoubleBinding(() -> model.getCenter().getY(), model.getPtAt(0).yProperty(), model.getPtAt(3).yProperty()));
		border.radiusXProperty().bind(Bindings.createDoubleBinding(() -> model.getRadius() + model.getFullThickness() / 2d,
			model.getPtAt(0).xProperty(), model.getPtAt(1).xProperty(), model.thicknessProperty(), model.dbleBordSepProperty(),
			model.dbleBordProperty()));
		border.radiusYProperty().bind(Bindings.createDoubleBinding(() -> model.getRadius() + model.getFullThickness() / 2d,
			model.getPtAt(1).yProperty(), model.getPtAt(2).yProperty(), model.thicknessProperty(), model.dbleBordSepProperty(),
			model.dbleBordProperty()));
		bindArcProperties(border);

		getChildren().add(viewArrows);
		viewArrows.updateAllArrows();
	}

	private final void bindArcProperties(final Arc arc) {
		arc.startAngleProperty().bind(Bindings.createDoubleBinding(() -> Math.toDegrees(model.getAngleStart()), model.angleStartProperty()));
		arc.lengthProperty().bind(Bindings.createDoubleBinding(
			() -> model.getAngleEnd() > model.getAngleStart() ? Math.toDegrees(model.getAngleEnd() - model.getAngleStart()) :
				Math.toDegrees(Math.PI * 2d - model.getAngleStart() + model.getAngleEnd()), model.angleStartProperty(), model.angleEndProperty()));
		model.angleStartProperty().addListener(viewArrows.updateArrow);
		model.angleEndProperty().addListener(viewArrows.updateArrow);
		arc.typeProperty().bind(Bindings.createObjectBinding(() -> model.getArcStyle().getJFXStyle(), model.arcStyleProperty()));
	}

	@Override
	protected Arc createJFXShape() {
		return new Arc();
	}

	@Override
	public void flush() {
		unbindArc(border);
		unbindArc(shadow);
		unbindArc(dblBorder);
		super.flush();
	}

	private static void unbindArc(final Arc arc) {
		if(arc != null) {
			arc.centerXProperty().unbind();
			arc.centerYProperty().unbind();
			arc.radiusXProperty().unbind();
			arc.radiusYProperty().unbind();
			arc.startAngleProperty().unbind();
			arc.lengthProperty().unbind();
			arc.typeProperty().unbind();
		}
	}
}
