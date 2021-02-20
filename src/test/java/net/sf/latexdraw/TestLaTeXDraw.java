package net.sf.latexdraw;

import io.github.interacto.command.CommandsRegistry;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import net.sf.latexdraw.command.SaveDrawing;
import net.sf.latexdraw.instrument.ExceptionsManager;
import net.sf.latexdraw.service.PreferencesService;
import net.sf.latexdraw.util.Page;
import net.sf.latexdraw.view.jfx.Canvas;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.util.WaitForAsyncUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(LatexdrawExtension.class)
@ExtendWith(ApplicationExtension.class)
public class TestLaTeXDraw {
	static LaTeXDraw app;

	@BeforeAll
	static void beforeAll() throws TimeoutException {
		Canvas.setMargins(20);
		FxToolkit.registerPrimaryStage();
		final AtomicReference<LaTeXDraw> ld = new AtomicReference<>();
		final Supplier<Application> supplier = () -> {
			final LaTeXDraw laTeXDraw = new LaTeXDraw();
			ld.set(laTeXDraw);
			return laTeXDraw;
		};
		FxToolkit.setupApplication(supplier);
		WaitForAsyncUtils.waitForFxEvents();
		app = ld.get();
		WaitForAsyncUtils.waitFor(10L, TimeUnit.SECONDS, app.getMainStage().showingProperty());
	}

	@AfterAll
	static void afterAll() throws TimeoutException {
		FxToolkit.cleanupStages();
		FxToolkit.cleanupApplication(app);
		app = null;
	}

	@BeforeEach
	public void setUp() {
		app.getInjector().getInstance(PreferencesService.class).setPage(Page.HORIZONTAL);
	}

	@Test
	public void testNotModified() {
		assertFalse(app.isModified());
	}

	@Test
	public void testGetInstrument() {
		assertNotNull(app.getInstruments());
		assertFalse(app.getInstruments().isEmpty());
	}

	@Test
	public void testGetAdditionalComponents() {
		assertNotNull(app.getAdditionalComponents());
		assertFalse(app.getAdditionalComponents().isEmpty());
	}

	@Test
	public void testGetMainStage() {
		assertNotNull(app.getMainStage());
	}

	@Test
	public void testReinit() {
		Platform.runLater(() -> app.reinit());
		WaitForAsyncUtils.waitForFxEvents();
		assertFalse(app.isModified());
	}

	@Test
	public void testMoveViewPort(final FxRobot robot) {
		Platform.runLater(() -> {
			try {
				app.getMainStage().showAndWait();
			}catch(final IllegalStateException ignored) {
				// Already visible
			}
		});
		WaitForAsyncUtils.waitForFxEvents();
		final ScrollPane pane = robot.lookup("#scrollPane").query();
		final double hvalue = pane.getHvalue();
		final double vvalue = pane.getVvalue();
		robot.drag("#canvas", MouseButton.MIDDLE).dropBy(100d, 200d);
		WaitForAsyncUtils.waitForFxEvents();
		assertThat(hvalue).isGreaterThan(pane.getHvalue());
		assertThat(vvalue).isGreaterThan(pane.getVvalue());
	}

	@Test
	void testIntegrationSaveNoCrash(final FxRobot robot, @TempDir final Path dir) {
		final File file = Paths.get(dir.toString(), "foo.svg").toFile();
		app.getInjector().getInstance(PreferencesService.class).setCurrentFile(file);
		Platform.runLater(() -> app.getInjector().getInstance(Canvas.class).requestFocus());
		WaitForAsyncUtils.waitForFxEvents();
		robot.clickOn(app.getMainStage()).sleep(800L).clickOn("#dotB").clickOn("#canvas");
		WaitForAsyncUtils.waitForFxEvents();
		robot.clickOn("#fileMenu").clickOn("#saveMenu").sleep(1000L);
		WaitForAsyncUtils.waitForFxEvents();
		assertTrue(CommandsRegistry.getInstance().getCommands().get(CommandsRegistry.getInstance().getCommands().size() - 1) instanceof SaveDrawing);
		assertFalse(app.getInjector().getInstance(ExceptionsManager.class).isActivated());
	}
}
