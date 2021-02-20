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
import javafx.beans.value.ChangeListener;
import javafx.scene.shape.Rectangle;
import net.sf.latexdraw.model.api.shape.SingleShape;

/**
 * A JFX abstract view to factorise code of views based on a JFX rectangle.
 * @author Arnaud Blouin
 */
public abstract class ViewRectangularBased<T extends SingleShape> extends ViewSingleShape<T, Rectangle> {
	final ChangeListener<? super Number> lineArcCall = (observable, oldValue, newValue) -> {
		final double lineArc = newValue.doubleValue();
		final double size = Math.min(model.getWidth(), model.getHeight());

		border.setArcHeight(lineArc * size);
		border.setArcWidth(lineArc * size);

		if(shadow != null) {
			shadow.setArcHeight(lineArc * size);
			shadow.setArcWidth(lineArc * size);
		}
		if(dblBorder != null) {
			dblBorder.setArcHeight(lineArc * size);
			dblBorder.setArcWidth(lineArc * size);
		}
	};

	/**
	 * Creates the rectangle view.
	 * @param sh The model.
	 */
	ViewRectangularBased(final T sh) {
		super(sh);

		if(dblBorder != null) {
			dblBorder.xProperty().bind(Bindings.createDoubleBinding(() -> getDbleBorderGap() + border.getX(),
				border.xProperty(), border.strokeWidthProperty(), border.strokeTypeProperty()));
			dblBorder.yProperty().bind(Bindings.createDoubleBinding(() -> getDbleBorderGap() + border.getY(),
				border.yProperty(), border.strokeWidthProperty(), border.strokeTypeProperty()));
			dblBorder.heightProperty().bind(Bindings.createDoubleBinding(() -> border.getHeight() - getDbleBorderGap() * 2d,
				border.heightProperty(), border.strokeWidthProperty(), border.strokeTypeProperty()));
			dblBorder.widthProperty().bind(Bindings.createDoubleBinding(() -> border.getWidth() - getDbleBorderGap() * 2d,
				border.widthProperty(), border.strokeWidthProperty(), border.strokeTypeProperty()));
		}

		if(shadow != null) {
			shadow.xProperty().bind(border.xProperty());
			shadow.yProperty().bind(border.yProperty());
			shadow.widthProperty().bind(border.widthProperty());
			shadow.heightProperty().bind(border.heightProperty());
		}
	}


	private static void unbindRect(final Rectangle rec) {
		if(rec != null) {
			rec.xProperty().unbind();
			rec.yProperty().unbind();
			rec.widthProperty().unbind();
			rec.heightProperty().unbind();
		}
	}

	@Override
	protected Rectangle createJFXShape() {
		return new Rectangle();
	}

	@Override
	public void flush() {
		super.flush();
		unbindRect(border);
		unbindRect(dblBorder);
		unbindRect(shadow);
	}
}
