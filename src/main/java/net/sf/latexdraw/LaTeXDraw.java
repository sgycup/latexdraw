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
package net.sf.latexdraw;

import io.github.interacto.command.CommandsRegistry;
import io.github.interacto.jfx.instrument.JfxInstrument;
import io.github.interacto.jfx.ui.JfxUI;
import io.github.interacto.properties.Modifiable;
import io.github.interacto.properties.Reinitialisable;
import io.github.interacto.undo.UndoCollector;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.BuilderFactory;
import javafx.util.Duration;
import net.sf.latexdraw.instrument.PreferencesSetter;
import net.sf.latexdraw.instrument.StatusBarController;
import net.sf.latexdraw.instrument.TabSelector;
import net.sf.latexdraw.model.api.shape.Drawing;
import net.sf.latexdraw.service.PreferencesService;
import net.sf.latexdraw.util.BadaboomCollector;
import net.sf.latexdraw.util.Injector;
import net.sf.latexdraw.util.VersionChecker;
import net.sf.latexdraw.view.jfx.Canvas;

/**
 * The main class of the project.
 * @author Arnaud Blouin
 */
public class LaTeXDraw extends JfxUI {
	public static final String LABEL_APP = "LaTeXDraw"; //NON-NLS

	static {
		UndoCollector.getInstance().setSizeMax(30);
		CommandsRegistry.getInstance().setSizeMax(30);
	}

	/**
	 * The entry point of the program.
	 * @param args The parameters.
	 */
	public static void main(final String[] args) {
		launch(args);
	}

	private Stage mainStage;
	private Injector injector;

	public LaTeXDraw() {
		super();
	}

	private void showSplash(final Stage initStage, final Task<Void> task) {
		final ProgressBar loadProgress = new ProgressBar();
		loadProgress.progressProperty().bind(task.progressProperty());

		final Pane splashLayout = new VBox();
		final Image img = new Image("res/LaTeXDrawSmall.png", 450, 250, true, true); //NON-NLS
		final ImageView splash = new ImageView(img);
		splashLayout.getChildren().addAll(splash, loadProgress);
		splashLayout.setEffect(new DropShadow());
		loadProgress.setPrefWidth(img.getWidth());

		task.setOnSucceeded(ignore -> {
			loadProgress.progressProperty().unbind();
			loadProgress.setProgress(1d);
			final FadeTransition fadeSplash = new FadeTransition(Duration.seconds(0.8), splashLayout);
			fadeSplash.setFromValue(1d);
			fadeSplash.setToValue(0d);
			fadeSplash.setOnFinished(evt -> {
				initStage.hide();
				mainStage.setIconified(false);
				mainStage.toFront();
			});
			fadeSplash.play();
		});

		final Scene splashScene = new Scene(splashLayout);
		initStage.setScene(splashScene);
		initStage.initStyle(StageStyle.UNDECORATED);
		initStage.getIcons().add(new Image("/res/LaTeXDrawIcon.png")); //NON-NLS
		initStage.centerOnScreen();
		initStage.toFront();
		initStage.show();
	}

	@Override
	public void start(final Stage stage) {
		final Task<Void> task = new Task<>() {
			@Override
			protected Void call() throws IOException {
				updateProgress(0.1, 1d);
				final CountDownLatch latch = new CountDownLatch(1);

				Platform.runLater(() -> {
					mainStage = new Stage(StageStyle.DECORATED);
					mainStage.setIconified(true);
					injector = new LatexdrawInjector(LaTeXDraw.this);
					latch.countDown();
				});

				// We need to wait for the javafx thread to perform its job before loading the UI (because of the injector).
				try {
					latch.await();
				}catch(final InterruptedException ex) {
					Thread.currentThread().interrupt();
					throw new RuntimeException(ex);
				}

				final PreferencesService prefs = injector.getInstance(PreferencesService.class);
				prefs.readPreferences();

				final Parent root = FXMLLoader.load(getClass().getResource("/fxml/UI.fxml"), prefs.getBundle(), //NON-NLS
					injector.getInstance(BuilderFactory.class), cl -> injector.getInstance(cl));
				updateProgress(0.6, 1d);
				final Scene scene = new Scene(root);
				updateProgress(0.7, 1d);
				scene.getStylesheets().add("css/style.css"); //NON-NLS
				// Binding the title of the app on the title of the drawing.
				mainStage.titleProperty().bind(injector.getInstance(Drawing.class).titleProperty().concat(" -- " + LABEL_APP));
				updateProgress(0.8, 1d);

				Platform.runLater(() -> {
					mainStage.setScene(scene);
					updateProgress(0.9, 1d);
					setModified(false);
					mainStage.show();
					registerScene(scene);
					// Preventing the stage to close automatically.
					mainStage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, WindowEvent::consume);
					mainStage.getIcons().add(new Image("/res/LaTeXDrawIcon.png")); //NON-NLS
					mainStage.centerOnScreen();
					injector.getInstance(TabSelector.class).centreViewport();
					injector.getInstance(Canvas.class).requestFocus();
					// Checking a new version if required.
					if(VersionChecker.WITH_UPDATE && injector.getInstance(PreferencesSetter.class).isVersionCheckEnable()) {
						new Thread(new VersionChecker(injector.getInstance(StatusBarController.class), prefs.getBundle())).start();
					}
					setModified(false);
				});
				return null;
			}
		};

		task.setOnFailed(BadaboomCollector.INSTANCE);
		showSplash(stage, task);
		new Thread(task).start();
	}

	@Override
	public Set<JfxInstrument> getInstruments() {
		return injector.getInstances().stream().filter(ins -> ins instanceof JfxInstrument).map(obj -> (JfxInstrument) obj).collect(Collectors.toSet());
	}

	@Override
	protected <T extends Modifiable & Reinitialisable> Set<T> getAdditionalComponents() {
		final Set<T> set = new HashSet<>();
		set.add((T) injector.getInstance(Canvas.class));
		set.add((T) injector.getInstance(Drawing.class));
		return set;
	}

	/**
	 * @return The main stage of the app.
	 */
	public Stage getMainStage() {
		return mainStage;
	}

	protected Injector getInjector() {
		return injector;
	}
}
