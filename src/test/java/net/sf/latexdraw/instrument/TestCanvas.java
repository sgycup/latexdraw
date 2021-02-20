package net.sf.latexdraw.instrument;

import io.github.interacto.command.CommandsRegistry;
import io.github.interacto.command.library.Redo;
import io.github.interacto.command.library.Undo;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.stream.Collectors;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.stage.Stage;
import net.sf.latexdraw.command.shape.RotateShapes;
import net.sf.latexdraw.data.ShapeSupplier;
import net.sf.latexdraw.util.Injector;
import net.sf.latexdraw.view.jfx.Canvas;
import net.sf.latexdraw.view.jfx.MagneticGrid;
import net.sf.latexdraw.view.jfx.PageView;
import net.sf.latexdraw.view.jfx.ViewRectangle;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.testfx.util.WaitForAsyncUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class TestCanvas extends BaseTestCanvas {
	@Override
	protected Injector createInjector() {
		return new ShapePropInjector() {
			@Override
			protected void configure() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
				super.configure();
				bindToSupplier(Stage.class, () -> stage);
				bindToInstance(Border.class, Mockito.mock(Border.class));
				bindToInstance(CanvasController.class, Mockito.mock(CanvasController.class));
				bindAsEagerSingleton(FacadeCanvasController.class);
				bindToInstance(Pencil.class, Mockito.mock(Pencil.class));
				bindToInstance(Hand.class, Mockito.mock(Hand.class));
				bindToInstance(MetaShapeCustomiser.class, Mockito.mock(MetaShapeCustomiser.class));
				bindToInstance(TextSetter.class, Mockito.mock(TextSetter.class));
			}
		};
	}

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		hand.setActivated(true);
		when(pencil.isActivated()).thenReturn(false);
	}

	@Test
	public void testSetSelectionMustNotCrash() throws IOException {
		canvas.getDrawing().setSelection(ShapeSupplier.getDiversifiedShapes().collect(Collectors.toList()));
	}

	@Test
	public void testPageViewExits() {
		assertTrue(canvas.getChildren().get(0) instanceof PageView);
	}

	@Test
	public void testMagneticGridExists() {
		assertTrue(canvas.getChildren().get(1) instanceof MagneticGrid);
	}

	@Test
	public void testViewsPaneExists() {
		assertTrue(canvas.getChildren().get(2) instanceof Group);
	}

	@Test
	public void testViewsPanePositionORIGIN() {
		final Group group = getPane();
		assertEquals(Canvas.ORIGIN.getX(), group.getLayoutX(), 0.000001);
		assertEquals(Canvas.ORIGIN.getY(), group.getLayoutY(), 0.000001);
	}

	@Test
	public void testShapeAddedViewCreated() {
		Cmds.of(addRec).execute();
		assertEquals(1, getPane().getChildren().size());
	}

	@Test
	public void testShapeAddedViewRecCreated() {
		Cmds.of(addRec).execute();
		assertTrue(getPane().getChildren().get(0) instanceof ViewRectangle);
	}

	@Test
	public void testSelectionBorderUpdatedOnModifyingCmd() {
		Cmds.of(addRec, selectAllShapes).execute();
		final Bounds border = canvas.getSelectionBorder().getBoundsInLocal();
		final RotateShapes cmd = new RotateShapes(addedRec.getGravityCentre(), addedRec, Math.PI / 2d);
		cmd.doIt();
		cmd.done();
		CommandsRegistry.getInstance().addCommand(cmd);
		WaitForAsyncUtils.waitForFxEvents();
		final Bounds border2 = canvas.getSelectionBorder().getBoundsInLocal();
		assertThat(border2).isNotEqualTo(border);
	}

	@Test
	public void testSelectionBorderUpdatedOnUndoModifyingCmd() {
		Cmds.of(addRec, selectAllShapes).execute();
		final Bounds borderInitial = canvas.getSelectionBorder().getBoundsInLocal();
		final RotateShapes cmd = new RotateShapes(addedRec.getGravityCentre(), addedRec, Math.PI / 2d);
		cmd.doIt();
		cmd.done();
		CommandsRegistry.getInstance().addCommand(cmd);
		WaitForAsyncUtils.waitForFxEvents();
		final Bounds border = canvas.getSelectionBorder().getBoundsInLocal();
		final Undo undo = new Undo();
		undo.doIt();
		undo.done();
		CommandsRegistry.getInstance().addCommand(undo);
		WaitForAsyncUtils.waitForFxEvents();
		final Bounds border2 = canvas.getSelectionBorder().getBoundsInLocal();
		assertThat(border2).isNotEqualTo(border);
		assertThat(border2).isEqualTo(borderInitial);
	}

	@Test
	public void testSelectionBorderUpdatedOnRedoModifyingCmd() {
		Cmds.of(addRec, selectAllShapes).execute();
		final RotateShapes cmd = new RotateShapes(addedRec.getGravityCentre(), addedRec, Math.PI / 2d);
		cmd.doCmdBody();
		cmd.done();
		CommandsRegistry.getInstance().addCommand(cmd);
		WaitForAsyncUtils.waitForFxEvents();
		final Bounds borderInitial = canvas.getSelectionBorder().getBoundsInLocal();
		final Undo undo = new Undo();
		undo.doIt();
		undo.done();
		CommandsRegistry.getInstance().addCommand(undo);
		WaitForAsyncUtils.waitForFxEvents();
		final Bounds border = canvas.getSelectionBorder().getBoundsInLocal();
		final Redo redo = new Redo();
		redo.doIt();
		redo.done();
		CommandsRegistry.getInstance().addCommand(redo);
		WaitForAsyncUtils.waitForFxEvents();
		final Bounds border2 = canvas.getSelectionBorder().getBoundsInLocal();
		assertThat(border2).isNotEqualTo(border);
		assertThat(border2).isEqualTo(borderInitial);
	}
}
