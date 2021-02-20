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
import io.github.interacto.jfx.interaction.library.WindowClosed;
import io.github.interacto.jfx.ui.JfxUI;
import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.sf.latexdraw.command.LoadDrawing;
import net.sf.latexdraw.command.NewDrawing;
import net.sf.latexdraw.command.SaveDrawing;
import net.sf.latexdraw.service.PreferencesService;
import net.sf.latexdraw.util.Bindings;
import net.sf.latexdraw.util.Inject;
import net.sf.latexdraw.util.Injector;
import net.sf.latexdraw.view.svg.SVGDocumentGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Saves and loads documents.
 * @author Arnaud Blouin
 */
public class FileLoaderSaver extends JfxInstrument implements Initializable {
	/** The menu used to save documents. */
	@FXML private MenuItem saveMenu;
	/** The menu used to load documents. */
	@FXML private MenuItem loadMenu;
	/** The menu used to create a new document. */
	@FXML private MenuItem newMenu;
	/** The menu that contains the menu item corresponding to the recent documents. */
	@FXML private Menu recentFilesMenu;
	/** The menu item used to save as a document. */
	@FXML private MenuItem saveAsMenu;
	@FXML private MenuButton fileMenu;
	/** The fileChooser used to save drawings. */
	private @Nullable FileChooser fileChooser;
	private @Nullable Alert modifiedAlert;
	private final @NotNull StatusBarController statusBar;
	private final @NotNull SVGDocumentGenerator svgGen;
	private final @NotNull JfxUI app;
	private final @NotNull Stage mainstage;
	private final @NotNull PreferencesService prefService;
	private final @NotNull Injector injector;

	@Inject
	public FileLoaderSaver(final StatusBarController statusBar, final SVGDocumentGenerator svgGen, final JfxUI app, final Stage mainstage,
		final PreferencesService prefService, final Injector injector) {
		super();
		this.statusBar = Objects.requireNonNull(statusBar);
		this.svgGen = Objects.requireNonNull(svgGen);
		this.app = Objects.requireNonNull(app);
		this.prefService = Objects.requireNonNull(prefService);
		this.mainstage = Objects.requireNonNull(mainstage);
		this.injector = Objects.requireNonNull(injector);
	}

	@Override
	public void reinit() {
		prefService.setCurrentFile(null);
		fileChooser = null;
	}


	private void updateOnIOCommand(final File file) {
		fileMenu.hide();

		prefService.setCurrentFile(file);

		prefService.getCurrentFile().ifPresent(cFile -> {
			prefService.setCurrentFolder(cFile.getParentFile());
			if(!cFile.getPath().endsWith(".svg")) { //NON-NLS
				prefService.setCurrentFile(new File(cFile.getPath() + ".svg")); //NON-NLS
			}
		});

		prefService.addRecentFile(file.getPath());
	}

	@Override
	public void setActivated(final boolean activated, final boolean hide) {
		super.setActivated(activated);

		final boolean showButtons = activated || !hide;
		newMenu.setVisible(showButtons);
		saveAsMenu.setVisible(showButtons);
		saveMenu.setVisible(showButtons);
		loadMenu.setVisible(showButtons);

		if(showButtons) {
			newMenu.setDisable(!activated);
			saveAsMenu.setDisable(!activated);
			saveMenu.setDisable(!activated);
			loadMenu.setDisable(!activated);
		}
	}

	@Override
	public void setActivated(final boolean activated) {
		setActivated(activated, true);
	}

	public void updateOnLoad(final File file) {
		prefService.setCurrentFile(file);
		prefService.getCurrentFile().ifPresent(f -> prefService.setCurrentFolder(f.getParentFile()));
	}

	@Override
	protected void configureBindings() {
		final var shortcutBaseBinder = shortcutWindowBinder().on(mainstage);

		// Close window
		windowBinder()
			.usingInteraction(WindowClosed::new)
			.toProduce(() -> new SaveDrawing(true, true, prefService.getCurrentFolder(), getDialog(true),
				injector, prefService.getCurrentFile().orElse(null), svgGen, statusBar.getProgressBar(), app,
				statusBar.getLabel(), mainstage, getAskModificationsDialog()))
			.on(mainstage)
			.ifHadEffects((i, c) -> updateOnIOCommand(c.getFile()))
			.bind();

		// Quit shortcut
		shortcutBaseBinder
			.toProduce(() -> new SaveDrawing(true, true, prefService.getCurrentFolder(), getDialog(true),
				injector, prefService.getCurrentFile().orElse(null), svgGen, statusBar.getProgressBar(), app,
				statusBar.getLabel(), mainstage, getAskModificationsDialog()))
			.with(KeyCode.W, KeyCode.SHORTCUT)
			.ifHadEffects((i, c) -> updateOnIOCommand(c.getFile()))
			.bind();

		// Save menu
		menuItemBinder()
			.toProduce(() -> new SaveDrawing(false, false, prefService.getCurrentFolder(), getDialog(true),
				injector, prefService.getCurrentFile().orElse(null), svgGen, statusBar.getProgressBar(), app,
				statusBar.getLabel(), mainstage, getAskModificationsDialog()))
			.on(saveMenu)
			.ifHadEffects((i, c) -> updateOnIOCommand(c.getFile()))
			.bind();

		// Save shortcut
		shortcutBaseBinder
			.toProduce(() -> new SaveDrawing(false, false, prefService.getCurrentFolder(), getDialog(true),
				injector, prefService.getCurrentFile().orElse(null), svgGen, statusBar.getProgressBar(), app,
				statusBar.getLabel(), mainstage, getAskModificationsDialog()))
			.with(KeyCode.S, KeyCode.SHORTCUT)
			.ifHadEffects((i, c) -> updateOnIOCommand(c.getFile()))
			.bind();

		// Save as menu
		menuItemBinder()
			.toProduce(() -> new SaveDrawing(true, false, prefService.getCurrentFolder(), getDialog(true),
				injector, null, svgGen, statusBar.getProgressBar(), app, statusBar.getLabel(), mainstage, getAskModificationsDialog()))
			.on(saveAsMenu)
			.ifHadEffects((i, c) -> updateOnIOCommand(c.getFile()))
			.bind();

		// Load menu
		menuItemBinder()
			.toProduce(() -> new LoadDrawing(null, svgGen, statusBar.getProgressBar(), statusBar.getLabel(), app,
				getDialog(false), prefService.getCurrentFolder(), mainstage, getAskModificationsDialog()))
			.on(loadMenu)
			.ifHadEffects((i, c) -> {
				updateOnIOCommand(c.getFile());
				updateOnLoad(c.getFile());
			})
			.bind();

		// Load shortcut
		shortcutBaseBinder
			.toProduce(() -> new LoadDrawing(null, svgGen, statusBar.getProgressBar(), statusBar.getLabel(), app,
				getDialog(false), prefService.getCurrentFolder(), mainstage, getAskModificationsDialog()))
			.with(KeyCode.O, KeyCode.SHORTCUT)
			.ifHadEffects((i, c) -> {
				updateOnIOCommand(c.getFile());
				updateOnLoad(c.getFile());
			})
			.bind();

		// New menu
		menuItemBinder()
			.toProduce(() -> new NewDrawing(prefService.getCurrentFile().orElse(null), svgGen, statusBar.getProgressBar(),
				statusBar.getLabel(), app, getDialog(false), prefService.getCurrentFolder(), mainstage, getAskModificationsDialog()))
			.on(newMenu)
			.bind();

		// New shortcut
		shortcutBaseBinder
			.toProduce(() -> new NewDrawing(prefService.getCurrentFile().orElse(null), svgGen, statusBar.getProgressBar(),
				statusBar.getLabel(), app, getDialog(false), prefService.getCurrentFolder(), mainstage, getAskModificationsDialog()))
			.with(KeyCode.N, KeyCode.SHORTCUT)
			.bind();

		// Recent files menus
		menuItemBinder()
			.toProduce(i -> new LoadDrawing(new File((String) i.getWidget().getUserData()), svgGen, statusBar.getProgressBar(),
				statusBar.getLabel(), app, getDialog(false), prefService.getCurrentFolder(), mainstage, getAskModificationsDialog()))
			.on(recentFilesMenu.getItems())
			.ifHadEffects((i, c) -> {
				updateOnIOCommand(c.getFile());
				updateOnLoad(c.getFile());
			})
			.bind();
	}

	/**
	 * @param save True: the dialogue box will be configured in a saving purpose. Otherwise, in an loading purpose.
	 * @return The dialogue box to open or save drawings.
	 */
	public  @NotNull FileChooser getDialog(final boolean save) {
		if(fileChooser == null) {
			fileChooser = new FileChooser();
			fileChooser.getExtensionFilters().clear();
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SVG", "*.svg")); //NON-NLS
		}

		fileChooser.setTitle(save ? prefService.getBundle().getString("saveDrawing") : //NON-NLS
			prefService.getBundle().getString("openDrawing")); //NON-NLS

		return fileChooser;
	}

	public @NotNull Alert getAskModificationsDialog() {
		if(modifiedAlert == null) {
			modifiedAlert = new Alert(Alert.AlertType.CONFIRMATION);
			modifiedAlert.setTitle(prefService.getBundle().getString("wantSaveDrawing"));
			modifiedAlert.setHeaderText(prefService.getBundle().getString("saveDrawing"));
			modifiedAlert.getButtonTypes().setAll(ButtonType.NO, ButtonType.YES, ButtonType.CANCEL);
		}
		return modifiedAlert;
	}


	@Override
	public void initialize(final URL location, final ResourceBundle resources) {
		recentFilesMenu.disableProperty().bind(prefService.getRecentFiles().emptyProperty());

		Bindings.INSTANCE.bindListContent(recentFilesMenu.getItems(), prefService.getRecentFiles(),
			fileName -> {
				final MenuItem item = new MenuItem(new File(fileName).getName());
				item.setUserData(fileName);
				item.setId("recent" + prefService.getRecentFiles().indexOf(fileName)); //NON-NLS
				return item;
		});

		setActivated(true);
	}

	/**
	 * For testing purpose only.
	 * @param fc The file chooser to use.
	 */
	void setFileChooser(final @Nullable FileChooser fc) {
		fileChooser = fc;
	}
}
