package net.sf.latexdraw.command;

import java.io.File;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.malai.command.Command;
import org.malai.command.CommandsRegistry;
import org.malai.javafx.ui.JfxUI;
import org.malai.javafx.ui.OpenSaver;
import org.malai.undo.UndoCollector;
import org.malai.undo.Undoable;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.util.WaitForAsyncUtils;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(ApplicationExtension.class)
@ExtendWith(MockitoExtension.class)
public class TestNewDrawing {
	@Mock
	File file;
	@Mock
	OpenSaver<Label> openSaver;
	@Mock
	ProgressBar progressBar;
	@Mock
	Label statusWidget;
	@Mock
	JfxUI ui;
	@Mock
	FileChooser fileChooser;
	@Mock
	File currentFolder;
	@Mock
	ResourceBundle lang;
	@Mock
	Stage mainstage;
	NewDrawing cmd;


	@BeforeEach
	protected void configCorrectCmd() {
		cmd = new NewDrawing(file, openSaver, progressBar, statusWidget, ui, fileChooser, Optional.of(currentFolder), lang, mainstage);
	}

	@Test
	void testCanDoOK() {
		assertThat(cmd.canDo()).isTrue();
		assertThat(cmd.hadEffect()).isFalse();
	}

	@Test
	void testCanDoKO1() {
		cmd.setOpenSaveManager(null);
		assertThat(cmd.canDo()).isFalse();
	}

	@Test
	void testCanDoKO2() {
		cmd.setUi(null);
		assertThat(cmd.canDo()).isFalse();
	}

	@Test
	void testDoNotModified() {
		UndoCollector.INSTANCE.add(Mockito.mock(Undoable.class), null);
		CommandsRegistry.INSTANCE.addCommand(Mockito.mock(Command.class), null);
		Mockito.when(ui.isModified()).thenReturn(Boolean.FALSE);
		cmd.doIt();
		cmd.done();
		assertThat(cmd.hadEffect()).isTrue();
		Mockito.verify(ui, Mockito.times(1)).reinit();
		assertThat(UndoCollector.INSTANCE.getUndo()).isEmpty();
		assertThat(CommandsRegistry.INSTANCE.getCommands()).isEmpty();
	}

	@Test
	void testModifiedSave(final FxRobot robot) {
		UndoCollector.INSTANCE.add(Mockito.mock(Undoable.class), null);
		CommandsRegistry.INSTANCE.addCommand(Mockito.mock(Command.class), null);
		final Task<Boolean> task = Mockito.mock(Task.class);
		final File file = new File("foo.svg");
		Mockito.when(ui.isModified()).thenReturn(Boolean.TRUE);
		Mockito.when(fileChooser.showSaveDialog(mainstage)).thenReturn(file);
		Mockito.when(openSaver.save(file.getPath(), progressBar, statusWidget)).thenReturn(task);
		Platform.runLater(() -> cmd.doIt());
		WaitForAsyncUtils.waitForFxEvents();
		robot.type(KeyCode.ENTER);
		WaitForAsyncUtils.waitForFxEvents();
		cmd.done();
		Mockito.verify(openSaver, Mockito.times(1)).save(file.getPath(), progressBar, statusWidget);
		assertThat(cmd.hadEffect()).isTrue();
		Mockito.verify(ui, Mockito.times(1)).reinit();
		assertThat(UndoCollector.INSTANCE.getUndo()).isEmpty();
		assertThat(CommandsRegistry.INSTANCE.getCommands()).isEmpty();
	}

	@Test
	void testModifiedDoNotSave(final FxRobot robot) {
		UndoCollector.INSTANCE.add(Mockito.mock(Undoable.class), null);
		CommandsRegistry.INSTANCE.addCommand(Mockito.mock(Command.class), null);
		Mockito.when(ui.isModified()).thenReturn(Boolean.TRUE);
		Platform.runLater(() -> cmd.doIt());
		WaitForAsyncUtils.waitForFxEvents();
		robot.type(KeyCode.LEFT, KeyCode.ENTER);
		WaitForAsyncUtils.waitForFxEvents();
		assertThat(cmd.hadEffect()).isFalse();
		Mockito.verify(ui, Mockito.times(1)).reinit();
		assertThat(UndoCollector.INSTANCE.getUndo()).isEmpty();
		assertThat(CommandsRegistry.INSTANCE.getCommands()).isEmpty();
	}

	@Test
	void testModifiedCancel(final FxRobot robot) {
		Mockito.when(ui.isModified()).thenReturn(Boolean.TRUE);
		Platform.runLater(() -> cmd.doIt());
		WaitForAsyncUtils.waitForFxEvents();
		robot.type(KeyCode.ESCAPE);
		WaitForAsyncUtils.waitForFxEvents();
		assertThat(cmd.hadEffect()).isFalse();
		Mockito.verify(ui, Mockito.never()).reinit();
	}
}
