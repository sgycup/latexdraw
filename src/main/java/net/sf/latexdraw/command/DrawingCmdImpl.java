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
package net.sf.latexdraw.command;

import io.github.interacto.command.CommandImpl;
import net.sf.latexdraw.model.api.shape.Drawing;
import org.jetbrains.annotations.NotNull;

/**
 * @author Arnaud Blouin
 */
public abstract class DrawingCmdImpl extends CommandImpl {
	/** The drawing that will be handled by the command. */
	protected final @NotNull Drawing drawing;

	protected DrawingCmdImpl(final @NotNull Drawing theDrawing) {
		super();
		drawing = theDrawing;
	}

	public @NotNull Drawing getDrawing() {
		return drawing;
	}
}
