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
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory;
import javafx.scene.control.TitledPane;
import net.sf.latexdraw.command.shape.ShapeProperties;
import net.sf.latexdraw.model.api.property.IStdGridProp;
import net.sf.latexdraw.model.api.shape.Drawing;
import net.sf.latexdraw.model.api.shape.Group;
import net.sf.latexdraw.service.EditingService;
import net.sf.latexdraw.util.Inject;
import net.sf.latexdraw.view.jfx.Canvas;

/**
 * This instrument modifies the parameters of grids and axes.
 * @author Arnaud BLOUIN
 */
public class ShapeStdGridCustomiser extends ShapePropertyCustomiser {
	/** The field that sets the X-coordinate of the starting point of the grid. */
	@FXML private Spinner<Double> xStartS;
	/** The field that sets the Y-coordinate of the starting point of the grid. */
	@FXML private Spinner<Double> yStartS;
	/** The field that sets the X-coordinate of the ending point of the grid. */
	@FXML private Spinner<Double> xEndS;
	/** The field that sets the Y-coordinate of the ending point of the grid. */
	@FXML private Spinner<Double> yEndS;
	/** The field that sets the size of the labels of the grid. */
	@FXML private Spinner<Integer> labelsSizeS;
	/** The field that sets the X-coordinate of the origin point of the grid. */
	@FXML private Spinner<Double> xOriginS;
	/** The field that sets the Y-coordinate of the origin point of the grid. */
	@FXML private Spinner<Double> yOriginS;
	@FXML private TitledPane mainPane;

	@Inject
	public ShapeStdGridCustomiser(final Hand hand, final Pencil pencil, final Canvas canvas, final Drawing drawing, final EditingService editing) {
		super(hand, pencil, canvas, drawing, editing);
	}

	@Override
	public void initialize(final URL location, final ResourceBundle resources) {
		super.initialize(location, resources);
		mainPane.managedProperty().bind(mainPane.visibleProperty());
		((DoubleSpinnerValueFactory) xStartS.getValueFactory()).maxProperty().bind(xEndS.valueProperty());
		((DoubleSpinnerValueFactory) yStartS.getValueFactory()).maxProperty().bind(yEndS.valueProperty());
		((DoubleSpinnerValueFactory) xEndS.getValueFactory()).minProperty().bind(xStartS.valueProperty());
		((DoubleSpinnerValueFactory) yEndS.getValueFactory()).minProperty().bind(yStartS.valueProperty());
	}

	@Override
	protected void update(final Group gp) {
		if(gp.isTypeOf(IStdGridProp.class)) {
			xStartS.getValueFactory().setValue(gp.getGridStartX());
			yStartS.getValueFactory().setValue(gp.getGridStartY());
			xEndS.getValueFactory().setValue(gp.getGridEndX());
			yEndS.getValueFactory().setValue(gp.getGridEndY());
			xOriginS.getValueFactory().setValue(gp.getOriginX());
			yOriginS.getValueFactory().setValue(gp.getOriginY());
			labelsSizeS.getValueFactory().setValue(gp.getLabelsSize());
			setActivated(true);
		}else {
			setActivated(false);
		}
	}

	@Override
	protected void setWidgetsVisible(final boolean visible) {
		mainPane.setVisible(visible);
	}

	@Override
	protected void configureBindings() {
		addSpinnerPropBinding(labelsSizeS, ShapeProperties.GRID_SIZE_LABEL);

		addSpinnerXYPropBinding(xEndS, yEndS, ShapeProperties.GRID_END);
		addSpinnerXYPropBinding(xStartS, yStartS, ShapeProperties.GRID_START);
		addSpinnerXYPropBinding(xOriginS, yOriginS, ShapeProperties.GRID_ORIGIN);
	}
}
