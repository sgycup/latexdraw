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

import javafx.scene.Group;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import net.sf.latexdraw.model.api.shape.StandardGrid;

/**
 * The JFX view of an abstract grid.
 * @author Arnaud Blouin
 */
public abstract class ViewStdGrid<T extends StandardGrid> extends ViewPositionShape<T> {
	protected final Group labels;
	protected final PathElementProducer pathProducer;

	/**
	 * Creates the view.
	 * @param sh The model.
	 */
	ViewStdGrid(final T sh, final PathElementProducer pathProducer) {
		super(sh);
		this.pathProducer = pathProducer;
		labels = new Group();
		getChildren().add(labels);
	}


	public Text createTextLabel(final String text, final double x, final double y, final Font font) {
		final Text label = new Text(x, y, text);
		label.setFont(font);
		labels.getChildren().add(label);
		return label;
	}


	final void cleanLabels() {
		labels.getChildren().parallelStream().forEach(node -> {
			final Text txt = (Text) node;
			txt.strokeProperty().unbind();
			txt.fontProperty().unbind();
		});
		labels.getChildren().clear();
	}


	@Override
	public void flush() {
		cleanLabels();
		super.flush();
	}

	/**
	 * @return The JFX group of labels of the grid shape.
	 */
	public Group getLabels() {
		return labels;
	}
}
