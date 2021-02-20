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

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import net.sf.latexdraw.command.shape.MoveBackForegroundShapes;
import net.sf.latexdraw.model.api.shape.Drawing;
import net.sf.latexdraw.model.api.shape.Group;
import net.sf.latexdraw.service.EditingService;
import net.sf.latexdraw.util.Inject;
import net.sf.latexdraw.view.jfx.Canvas;

/**
 * Puts shapes in background / foreground.
 * @author Arnaud BLOUIN
 */
public class ShapePositioner extends ShapePropertyCustomiser {
	/** The foreground button. */
	@FXML private Button foregroundB;
	/** The background button. */
	@FXML private Button backgroundB;
	@FXML private Pane mainPane;

	@Inject
	public ShapePositioner(final Hand hand, final Pencil pencil, final Canvas canvas, final Drawing drawing, final EditingService editing) {
		super(hand, pencil, canvas, drawing, editing);
	}

	@Override
	protected void configureBindings() {
		buttonBinder()
			.toProduce(() -> new MoveBackForegroundShapes(canvas.getDrawing().getSelection().duplicateDeep(false), true, canvas.getDrawing()))
			.on(foregroundB)
			.bind();

		buttonBinder()
			.toProduce(() -> new MoveBackForegroundShapes(canvas.getDrawing().getSelection().duplicateDeep(false), false, canvas.getDrawing()))
			.on(backgroundB)
			.bind();
	}

	@Override
	public void initialize(final URL location, final ResourceBundle resources) {
		super.initialize(location, resources);
		mainPane.managedProperty().bind(mainPane.visibleProperty());
	}

	@Override
	protected void setWidgetsVisible(final boolean visible) {
		mainPane.setVisible(visible);
	}

	@Override
	protected void update(final Group shape) {
		setActivated(hand.isActivated() && !shape.isEmpty());
	}
}
