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
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import net.sf.latexdraw.model.api.shape.Triangle;

/**
 * @author Arnaud Blouin
 */
public class ViewTriangle extends ViewPathShape<Triangle> {
	/**
	 * Creates the view.
	 * @param sh The model.
	 */
	ViewTriangle(final Triangle sh, final PathElementProducer pathProducer) {
		super(sh, pathProducer);
		setupPath(border);
		setupPath(shadow);
		setupPath(dblBorder);
		rotateProperty().bind(Bindings.createDoubleBinding(() -> Math.toDegrees(model.getRotationAngle()), model.rotationAngleProperty()));
	}

	private final void setupPath(final Path path) {
		final MoveTo moveTo = pathProducer.createMoveTo(0d, 0d);

		moveTo.xProperty().bind(Bindings.createDoubleBinding(() -> model.getPtAt(0).getX() + model.getWidth() / 2d,
			model.getPtAt(0).xProperty(), model.getPtAt(1).xProperty()));
		moveTo.yProperty().bind(model.getPtAt(0).yProperty());
		path.getElements().add(moveTo);

		LineTo lineTo = pathProducer.createLineTo(0d, 0d);
		lineTo.xProperty().bind(model.getPtAt(2).xProperty());
		lineTo.yProperty().bind(model.getPtAt(2).yProperty());
		path.getElements().add(lineTo);

		lineTo = pathProducer.createLineTo(0d, 0d);
		lineTo.xProperty().bind(model.getPtAt(3).xProperty());
		lineTo.yProperty().bind(model.getPtAt(3).yProperty());
		path.getElements().add(lineTo);

		path.getElements().add(pathProducer.createClosePath());
	}
}
