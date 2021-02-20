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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.TitledPane;
import net.sf.latexdraw.command.shape.ShapeProperties;
import net.sf.latexdraw.model.api.shape.Drawing;
import net.sf.latexdraw.model.api.shape.Group;
import net.sf.latexdraw.service.EditingService;
import net.sf.latexdraw.util.Inject;
import net.sf.latexdraw.view.jfx.Canvas;

/**
 * This instrument modifies shadow properties of shapes or the pencil.
 * @author Arnaud BLOUIN
 */
public class ShapeShadowCustomiser extends ShapePropertyCustomiser {
	/** Sets if the a shape has a shadow or not. */
	@FXML private CheckBox shadowCB;
	/** Sets the colour of the shadow of a figure. */
	@FXML private ColorPicker shadowColB;
	/** Changes the size of the shadow. */
	@FXML private Spinner<Double> shadowSizeField;
	/** Changes the angle of the shadow. */
	@FXML private Spinner<Double> shadowAngleField;
	@FXML private TitledPane mainPane;

	@Inject
	public ShapeShadowCustomiser(final Hand hand, final Pencil pencil, final Canvas canvas, final Drawing drawing, final EditingService editing) {
		super(hand, pencil, canvas, drawing, editing);
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
		if(shape.isShadowable()) {
			final boolean hasShadow = shape.hasShadow();

			shadowCB.setSelected(hasShadow);
			shadowColB.setDisable(!hasShadow);
			shadowAngleField.setDisable(!hasShadow);
			shadowSizeField.setDisable(!hasShadow);

			if(hasShadow) {
				shadowColB.setValue(shape.getShadowCol().toJFX());
				shadowAngleField.getValueFactory().setValue(Math.toDegrees(shape.getShadowAngle()));
				shadowSizeField.getValueFactory().setValue(shape.getShadowSize());
			}
			setActivated(true);
		}else {
			setActivated(false);
		}
	}

	@Override
	protected void configureBindings() {
		addCheckboxPropBinding(shadowCB, ShapeProperties.SHADOW);

		addColorPropBinding(shadowColB, ShapeProperties.SHADOW_COLOUR);

		addSpinnerPropBinding(shadowSizeField, ShapeProperties.SHADOW_SIZE);
		addSpinnerAnglePropBinding(shadowAngleField, ShapeProperties.SHADOW_ANGLE);
	}
}
