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

import java.util.stream.IntStream;
import javafx.collections.ObservableList;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import net.sf.latexdraw.model.api.shape.ModifiablePointsShape;

/**
 * The JFX shape view for multipoints shapes.
 * @author Arnaud Blouin
 */
public abstract class ViewPolyPoint<T extends ModifiablePointsShape> extends ViewPathShape<T> {
	/**
	 * Creates the view.
	 * @param sh The model.
	 */
	ViewPolyPoint(final T sh, final PathElementProducer pathProducer) {
		super(sh, pathProducer);
		initPath(border);
		initPath(shadow);
		initPath(dblBorder);
	}

	private void initPath(final Path path) {
		final ObservableList<PathElement> elts = path.getElements();
		final MoveTo moveTo = pathProducer.createMoveTo(0d, 0d);
		moveTo.xProperty().bind(model.getPtAt(0).xProperty());
		moveTo.yProperty().bind(model.getPtAt(0).yProperty());
		elts.add(moveTo);

		IntStream.range(1, model.getNbPoints()).forEach(i -> {
			final LineTo lineto = pathProducer.createLineTo(0d, 0d);
			lineto.xProperty().bind(model.getPtAt(i).xProperty());
			lineto.yProperty().bind(model.getPtAt(i).yProperty());
			elts.add(lineto);
		});
	}

	@Override
	public void flush() {
		border.getElements().forEach(elt -> pathProducer.flushPathElement(elt));
		shadow.getElements().forEach(elt -> pathProducer.flushPathElement(elt));
		dblBorder.getElements().forEach(elt -> pathProducer.flushPathElement(elt));
		super.flush();
	}
}
