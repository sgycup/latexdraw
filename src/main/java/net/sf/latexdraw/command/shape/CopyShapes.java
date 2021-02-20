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
package net.sf.latexdraw.command.shape;

import io.github.interacto.command.Command;
import io.github.interacto.command.CommandImpl;
import java.util.List;
import java.util.stream.Collectors;
import net.sf.latexdraw.model.ShapeFactory;
import net.sf.latexdraw.model.api.shape.Shape;
import org.jetbrains.annotations.NotNull;

/**
 * This command copies the selected shapes.
 * @author Arnaud BLOUIN
 */
public class CopyShapes extends CommandImpl {
	/** The selection command. */
	protected final @NotNull SelectShapes selection;
	/** The copied shapes from the selection. */
	protected List<Shape> copiedShapes;
	/** The number of times that the shapes have been copied. Use to compute the gap while pasting. */
	protected int nbTimeCopied;

	public CopyShapes(final @NotNull SelectShapes selection) {
		super();
		this.selection = selection;
		nbTimeCopied = 0;
	}

	@Override
	protected void doCmdBody() {
		copiedShapes = selection
			.getShapes()
			.stream()
			.map(ShapeFactory.INST::duplicate)
			.filter(s -> s.isPresent())
			.map(s -> s.get())
			.collect(Collectors.toList());
	}

	@Override
	public boolean unregisteredBy(final Command cmd) {
		return cmd instanceof CopyShapes;
	}

	@Override
	public boolean canDo() {
		return !selection.getShapes().isEmpty();
	}

	@Override
	public void flush() {
		super.flush();
		if(copiedShapes != null) {
			copiedShapes.clear();
		}
	}
}
