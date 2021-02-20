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

import io.github.interacto.jfx.instrument.JfxInstrument;
import io.github.interacto.jfx.interaction.library.HyperlinkClicked;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import net.sf.latexdraw.util.Inject;
import org.jetbrains.annotations.NotNull;

/**
 * The controller for the status bar.
 * @author Arnaud Blouin
 */
public class StatusBarController extends JfxInstrument implements Initializable {
	@FXML private Label label;
	@FXML private ProgressBar progressBar;
	@FXML private Hyperlink link;
	private final @NotNull HostServices services;

	@Inject
	public StatusBarController(final HostServices services) {
		super();
		this.services = Objects.requireNonNull(services);
	}

	/**
	 * @return The label of status bar.
	 */
	public Label getLabel() {
		return label;
	}

	/**
	 * @return The progress bar of the status bar.
	 */
	public ProgressBar getProgressBar() {
		return progressBar;
	}

	/**
	 * @return The hyperlink of the status bar.
	 */
	public Hyperlink getLink() {
		return link;
	}

	@Override
	public void initialize(final URL location, final ResourceBundle resources) {
		progressBar.managedProperty().bind(progressBar.visibleProperty());
		link.setVisible(false);
		setActivated(true);
	}

	@Override
	protected void configureBindings() {
		anonCmdBinder(() -> services.showDocument(link.getText()))
			.usingInteraction(HyperlinkClicked::new)
			.on(link)
			.bind();
	}
}
