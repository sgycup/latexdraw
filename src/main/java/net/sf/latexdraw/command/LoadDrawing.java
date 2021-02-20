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

import io.github.interacto.jfx.command.Load;
import io.github.interacto.jfx.ui.JfxUI;
import io.github.interacto.jfx.ui.OpenSaver;
import java.io.File;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This command loads an SVG document into the app.
 * @author Arnaud Blouin
 */
public class LoadDrawing extends Load<Label> implements Modifying {
	/** The file chooser that will be used to select the location to save. */
	private final @NotNull FileChooser fileChooser;
	private final @NotNull Optional<File> currentFolder;
	private final @NotNull Stage mainstage;
	private final @NotNull Alert modifiedAlert;

	public LoadDrawing(final @Nullable File file, final @NotNull OpenSaver<Label> openSaveManager, final @NotNull ProgressBar progressBar,
			final @NotNull Label statusWidget, final @NotNull JfxUI ui, final @NotNull FileChooser fileChooser,
			final @NotNull Optional<File> currentFolder, final @NotNull Stage mainstage,
			final @NotNull Alert modifiedAlert) {
		super(file, openSaveManager, progressBar, statusWidget, ui);
		this.fileChooser = fileChooser;
		this.currentFolder = currentFolder;
		this.mainstage = mainstage;
		this.modifiedAlert = modifiedAlert;
	}

	@Override
	protected void doCmdBody() {
		if(ui.isModified()) {
			final ButtonType type = modifiedAlert.showAndWait().orElse(ButtonType.CANCEL);
			if(type == ButtonType.NO) {
				load();
			}else {
				if(type == ButtonType.YES) {
					saveAndLoad();
				}
			}
		}else {
			load();
		}
	}

	private void saveAndLoad() {
		SaveDrawing.showDialog(fileChooser, true, file, currentFolder, ui, mainstage).ifPresent(f -> {
			final Task<Boolean> saveTask = openSaveManager.save(f.getPath(), progressBar, statusWidget);
			try {
				if(saveTask.get()) {
					ui.setModified(false);
					load();
				}
			}catch(final InterruptedException ignored) {
				Thread.currentThread().interrupt();
			}catch(final ExecutionException ignored) {
				// ignored.
			}
		});
	}


	private void load() {
		if(file == null) {
			currentFolder.ifPresent(folder -> fileChooser.setInitialDirectory(folder));
			file = fileChooser.showOpenDialog(mainstage);
		}else {
			fileChooser.setInitialDirectory(file.getParentFile());
			fileChooser.setInitialFileName(file.getName());
		}

		if(file != null && file.canRead()) {
			super.doCmdBody();
		}else {
			ok = false;
		}
	}
}
