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
package net.sf.latexdraw.handler;

import java.util.List;
import javafx.beans.binding.Bindings;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Rectangle;
import net.sf.latexdraw.model.api.shape.Position;
import org.jetbrains.annotations.NotNull;

/**
 * A handler to scale shapes.
 * @author Arnaud BLOUIN
 */
public class ScaleHandler extends Path implements Handler {
	/** The position of the handler. */
	private final @NotNull Position position;

	/**
	 * Creates the handler.
	 * @param handlerPosition The position of the handler.
	 */
	public ScaleHandler(final @NotNull Position handlerPosition, final @NotNull Rectangle border) {
		super();
		position = handlerPosition;
		setStroke(null);
		setFill(DEFAULT_COLOR);

		final List<PathElement> elements = getElements();
		final double x = 0d;
		final double y = 0d;
		final double quart = DEFAULT_SIZE / 4d;
		final double half = DEFAULT_SIZE / 2d;
		final double oct = DEFAULT_SIZE / 8d;

		switch(position) {
			case EAST -> {
				elements.add(new MoveTo(x, y - quart));
				elements.add(new LineTo(x, y + quart));
				elements.add(new LineTo(x + half, y + quart));
				elements.add(new LineTo(x + half, y + half));
				elements.add(new LineTo(x + DEFAULT_SIZE, y));
				elements.add(new LineTo(x + half, y - half));
				elements.add(new LineTo(x + half, y - quart));
				translateXProperty().bind(Bindings.createDoubleBinding(() -> border.getLayoutX() + border.getWidth(), border.xProperty(),
					border.widthProperty(), border.layoutXProperty()));
				translateYProperty().bind(Bindings.createDoubleBinding(() -> border.getLayoutY() + border.getHeight() / 2d, border.yProperty(),
					border.heightProperty(), border.layoutYProperty()));
			}
			case NORTH -> {
				elements.add(new MoveTo(x - quart, y));
				elements.add(new LineTo(x + quart, y));
				elements.add(new LineTo(x + quart, y - half));
				elements.add(new LineTo(x + half, y - half));
				elements.add(new LineTo(x, y - DEFAULT_SIZE));
				elements.add(new LineTo(x - half, y - half));
				elements.add(new LineTo(x - quart, y - half));
				translateXProperty().bind(Bindings.createDoubleBinding(() -> border.getLayoutX() + border.getWidth() / 2d, border.xProperty(),
					border.widthProperty(), border.layoutXProperty()));
				translateYProperty().bind(Bindings.createDoubleBinding(() -> border.getLayoutY(), border.yProperty(), border.heightProperty(),
					border.layoutYProperty()));
			}
			case SOUTH -> {
				elements.add(new MoveTo(x - quart, y));
				elements.add(new LineTo(x + quart, y));
				elements.add(new LineTo(x + quart, y + half));
				elements.add(new LineTo(x + half, y + half));
				elements.add(new LineTo(x, y + DEFAULT_SIZE));
				elements.add(new LineTo(x - half, y + half));
				elements.add(new LineTo(x - quart, y + half));
				translateXProperty().bind(Bindings.createDoubleBinding(() -> border.getLayoutX() + border.getWidth() / 2d, border.xProperty(),
					border.widthProperty(), border.layoutXProperty()));
				translateYProperty().bind(Bindings.createDoubleBinding(() -> border.getLayoutY() + border.getHeight(), border.yProperty(),
					border.heightProperty(), border.layoutYProperty()));
			}
			case WEST -> {
				elements.add(new MoveTo(x, y - quart));
				elements.add(new LineTo(x, y + quart));
				elements.add(new LineTo(x - half, y + quart));
				elements.add(new LineTo(x - half, y + half));
				elements.add(new LineTo(x - DEFAULT_SIZE, y));
				elements.add(new LineTo(x - half, y - half));
				elements.add(new LineTo(x - half, y - quart));
				translateXProperty().bind(Bindings.createDoubleBinding(() -> border.getLayoutX(), border.xProperty(), border.widthProperty(),
					border.layoutXProperty()));
				translateYProperty().bind(Bindings.createDoubleBinding(() -> border.getLayoutY() + border.getHeight() / 2d, border.yProperty(),
					border.heightProperty(), border.layoutYProperty()));
			}
			case NE -> {
				elements.add(new MoveTo(x + DEFAULT_SIZE - oct, y - DEFAULT_SIZE + oct));
				elements.add(new LineTo(x + DEFAULT_SIZE - oct, y - quart + oct));
				elements.add(new LineTo(x + half + quart - oct, y - half + oct));
				elements.add(new LineTo(x + quart - oct, y + oct));
				elements.add(new LineTo(x - oct, y - quart + oct));
				elements.add(new LineTo(x + half - oct, y - half - quart + oct));
				elements.add(new LineTo(x + quart - oct, y - DEFAULT_SIZE + oct));
				translateXProperty().bind(Bindings.createDoubleBinding(() -> border.getLayoutX() + border.getWidth(), border.xProperty(),
					border.widthProperty(), border.layoutXProperty()));
				translateYProperty().bind(Bindings.createDoubleBinding(() -> border.getLayoutY(), border.yProperty(), border.heightProperty(),
					border.layoutYProperty()));
			}
			case NW -> {
				elements.add(new MoveTo(x - DEFAULT_SIZE + oct, y - DEFAULT_SIZE + oct));
				elements.add(new LineTo(x - DEFAULT_SIZE + oct, y - quart + oct));
				elements.add(new LineTo(x - half - quart + oct, y - half + oct));
				elements.add(new LineTo(x - quart + oct, y + oct));
				elements.add(new LineTo(x + oct, y - quart + oct));
				elements.add(new LineTo(x - half + oct, y - half - quart + oct));
				elements.add(new LineTo(x - quart + oct, y - DEFAULT_SIZE + oct));
				translateXProperty().bind(Bindings.createDoubleBinding(() -> border.getLayoutX(), border.xProperty(), border.widthProperty(),
					border.layoutXProperty()));
				translateYProperty().bind(Bindings.createDoubleBinding(() -> border.getLayoutY(), border.yProperty(), border.heightProperty(),
					border.layoutYProperty()));
			}
			case SW -> {
				elements.add(new MoveTo(x - DEFAULT_SIZE + oct, y + DEFAULT_SIZE - oct));
				elements.add(new LineTo(x - DEFAULT_SIZE + oct, y + quart - oct));
				elements.add(new LineTo(x - half - quart + oct, y + half - oct));
				elements.add(new LineTo(x - quart + oct, y - oct));
				elements.add(new LineTo(x + oct, y + quart - oct));
				elements.add(new LineTo(x - half + oct, y + half + quart - oct));
				elements.add(new LineTo(x - quart + oct, y + DEFAULT_SIZE - oct));
				translateXProperty().bind(Bindings.createDoubleBinding(() -> border.getLayoutX(), border.xProperty(), border.widthProperty(),
					border.layoutXProperty()));
				translateYProperty().bind(Bindings.createDoubleBinding(() -> border.getLayoutY() + border.getHeight(), border.yProperty(),
					border.heightProperty(), border.layoutYProperty()));
			}
			case SE -> {
				elements.add(new MoveTo(x + DEFAULT_SIZE - oct, y + DEFAULT_SIZE - oct));
				elements.add(new LineTo(x + DEFAULT_SIZE - oct, y + quart - oct));
				elements.add(new LineTo(x + half + quart - oct, y + half - oct));
				elements.add(new LineTo(x + quart - oct, y - oct));
				elements.add(new LineTo(x - oct, y + quart - oct));
				elements.add(new LineTo(x + half - oct, y + half + quart - oct));
				elements.add(new LineTo(x + quart - oct, y + DEFAULT_SIZE - oct));
				translateXProperty().bind(Bindings.createDoubleBinding(() -> border.getLayoutX() + border.getWidth(), border.xProperty(), border.widthProperty(), border.layoutXProperty()));
				translateYProperty().bind(Bindings.createDoubleBinding(() -> border.getLayoutY() + border.getHeight(), border.yProperty(), border.heightProperty(), border.layoutYProperty()));
			}
		}

		elements.add(new ClosePath());
	}


	/**
	 * @return The position of the handler.
	 */
	public @NotNull Position getPosition() {
		return position;
	}
}
