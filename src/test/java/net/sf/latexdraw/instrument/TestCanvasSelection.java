package net.sf.latexdraw.instrument;

import java.awt.GraphicsEnvironment;
import java.lang.reflect.InvocationTargetException;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.control.Spinner;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import net.sf.latexdraw.model.ShapeFactory;
import net.sf.latexdraw.model.api.shape.FillingStyle;
import net.sf.latexdraw.service.EditingService;
import net.sf.latexdraw.util.Injector;
import net.sf.latexdraw.view.jfx.Canvas;
import net.sf.latexdraw.view.jfx.ViewArrow;
import net.sf.latexdraw.view.jfx.ViewGroup;
import net.sf.latexdraw.view.jfx.ViewPlot;
import net.sf.latexdraw.view.jfx.ViewPolyline;
import net.sf.latexdraw.view.jfx.ViewText;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;
import static org.mockito.Mockito.when;

public class TestCanvasSelection extends BaseTestCanvas {
	TextSetter setter;

	final CmdVoid clickOnAddedFirstShape = () -> rightClickOn(getPane().getChildren().get(0));

	final CmdVoid ctrlClickOnAddedRec2 = () -> press(KeyCode.CONTROL).rightClickOn(getPane().getChildren().get(1)).release(KeyCode.CONTROL);

	final CmdVoid shiftClickOnAddedRec = () -> press(KeyCode.SHIFT).rightClickOn(getPane().getChildren().get(0)).release(KeyCode.SHIFT);

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
				bindAsEagerSingleton(TextSetter.class);
				bindAsEagerSingleton(Hand.class);
				bindToInstance(EditingService.class, Mockito.mock(EditingService.class));
				bindToInstance(Pencil.class, Mockito.mock(Pencil.class));
				bindToInstance(MetaShapeCustomiser.class, Mockito.mock(MetaShapeCustomiser.class));
				bindToInstance(ShapeTextCustomiser.class, Mockito.mock(ShapeTextCustomiser.class));
				bindToInstance(ShapePlotCustomiser.class, Mockito.mock(ShapePlotCustomiser.class));
			}
		};
	}

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		setter = injector.getInstance(TextSetter.class);
		Platform.runLater(() -> {
			setter.initialize(null, null);
			hand.setActivated(true);
		});
		when(pencil.isActivated()).thenReturn(false);
	}

	@Test
	public void testOneClickOnShapeSelectsIt() {
		Cmds.of(addRec, clickOnAddedFirstShape).execute();
		assertEquals(1, canvas.getDrawing().getSelection().size());
		assertSame(addedRec, canvas.getDrawing().getSelection().getShapeAt(0).orElseThrow());
	}

	@Test
	public void testClickOnCanvasUnselects() {
		Cmds.of(addRec, clickOnAddedFirstShape, () -> clickOn(canvas)).execute();
		assertTrue(canvas.getDrawing().getSelection().isEmpty());
	}

	@Test
	public void testTwoClicksOnShapeSelectsItOneTime() {
		Cmds.of(addRec, clickOnAddedFirstShape, clickOnAddedFirstShape).execute();
		assertEquals(1, canvas.getDrawing().getSelection().size());
	}

//	@Ignore("Monocle does not capture key modifiers https://github.com/TestFX/Monocle/pull/48")
	@Test
	public void testShiftClickOnShapeDeselectsIt() {
		assumeFalse(GraphicsEnvironment.isHeadless());
		Cmds.of(addRec, clickOnAddedFirstShape, shiftClickOnAddedRec).execute();
		assertTrue(canvas.getDrawing().getSelection().isEmpty());
	}

//	@Ignore("Monocle does not capture key modifiers https://github.com/TestFX/Monocle/pull/48")
	@Test
	public void testCtrlClickOnShapeAddsSelection() {
		assumeFalse(GraphicsEnvironment.isHeadless());
		Cmds.of(addRec, addRec2, clickOnAddedFirstShape, ctrlClickOnAddedRec2).execute();
		assertEquals(2, canvas.getDrawing().getSelection().size());
		assertNotSame(canvas.getDrawing().getSelection().getShapeAt(0).orElseThrow(), canvas.getDrawing().getSelection().getShapeAt(1).orElseThrow());
	}

//	@Ignore("Monocle does not capture key modifiers https://github.com/TestFX/Monocle/pull/48")
	@Test
	public void testTwoAddsAndShiftClickSelectsOneShape() {
		assumeFalse(GraphicsEnvironment.isHeadless());
		Cmds.of(addRec, addRec2, clickOnAddedFirstShape, ctrlClickOnAddedRec2,
			shiftClickOnAddedRec).execute();
		assertEquals(1, canvas.getDrawing().getSelection().size());
		assertNotSame(addedRec, canvas.getDrawing().getSelection().getShapeAt(0).orElseThrow());
	}

//	@Ignore("Monocle does not capture key modifiers https://github.com/TestFX/Monocle/pull/48")
	@Test
	public void testCtrlASelectAll() {
		assumeFalse(GraphicsEnvironment.isHeadless());
		Cmds.of(addRec, addRec2,
			() -> clickOn(canvas).press(KeyCode.CONTROL).type(KeyCode.A).release(KeyCode.CONTROL)).execute();
		assertEquals(2, canvas.getDrawing().getSelection().size());
	}

	@Test
	public void testClickArrowSelectsShape() {
		Cmds.of(addLines).execute();
		final ViewPolyline vlines = (ViewPolyline) getPane().getChildren().get(0);
		Cmds.of(() -> clickOn(vlines.lookup("#" + ViewArrow.ID))).execute();
		assertEquals(1, canvas.getDrawing().getSelection().size());
		assertSame(addedPolyline, canvas.getDrawing().getSelection().getShapeAt(0).orElseThrow());
	}

	@Test
	public void testClickPlotSelectsShape() {
		Cmds.of(addPlot).execute();
		final ViewPlot vplot = (ViewPlot) getPane().getChildren().get(0);
		Cmds.of(() -> clickOn(vplot)).execute();
		assertEquals(1, canvas.getDrawing().getSelection().size());
		assertSame(addedPlot, canvas.getDrawing().getSelection().getShapeAt(0).orElseThrow());
	}

	@Test
	public void testClickInsideEmptyShapeDoesNotSelectIt() {
		Cmds.of(addLines).execute();
		final Bounds bounds = getPane().getChildren().get(0).getBoundsInParent();
		Cmds.of(() -> clickOn(canvas.getScene().getWindow().getX() + Canvas.ORIGIN.getX() + bounds.getMinX() + bounds.getWidth() / 2d,
			canvas.getScene().getWindow().getY() + Canvas.ORIGIN.getY() + bounds.getMinY() + bounds.getHeight() / 2d),
			waitFXEvents).execute();
		assertTrue(canvas.getDrawing().getSelection().isEmpty());
	}

	@Test
	public void testDnDInsideEmptyShapeDoesNotSelectIt() {
		Cmds.of(addLines).execute();
		final Bounds bounds = getPane().getChildren().get(0).getBoundsInParent();
		final double x = canvas.getScene().getWindow().getX() + Canvas.ORIGIN.getX() + bounds.getMinX() + bounds.getWidth() / 2d;
		final double y = canvas.getScene().getWindow().getY() + Canvas.ORIGIN.getY() + bounds.getMinY() + bounds.getHeight() / 2d;
		Cmds.of(() -> clickOn(x, y), () -> drag(x + 5, y + 5)).execute();
		assertTrue(canvas.getDrawing().getSelection().isEmpty());
	}

	@Test
	public void testClickInsideBOundsButOutsideShapeDoesNotSelectIt() {
		Cmds.of(addLines).execute();
		final Bounds bounds = getPane().getChildren().get(0).getBoundsInParent();
		final double x = canvas.getScene().getWindow().getX() + Canvas.ORIGIN.getX() + bounds.getMaxX() - 5;
		final double y = canvas.getScene().getWindow().getY() + Canvas.ORIGIN.getY() + bounds.getMinY() + 5;
		Cmds.of(() -> clickOn(x, y)).execute();
		assertTrue(canvas.getDrawing().getSelection().isEmpty());
	}

	@Test
	public void testDnDToSelectShapeSelectsIt() {
		Cmds.of(addLines).execute();
		final Bounds bounds = getPane().getChildren().get(0).getBoundsInParent();
		final double x = canvas.getScene().getWindow().getX() + Canvas.ORIGIN.getX() + bounds.getMinX() + bounds.getWidth() / 2d - 20d;
		final double y = canvas.getScene().getWindow().getY() + Canvas.ORIGIN.getY() + bounds.getMinY() + bounds.getHeight() / 2d;
		Cmds.of(() -> clickOn(x, y), () -> drag(x + 10, y + 10),
			() -> drag(x + 50, y), () -> drag(x + 100, y)).execute();
		assertEquals(1, canvas.getDrawing().getSelection().size());
		assertSame(addedPolyline, canvas.getDrawing().getSelection().getShapeAt(0).orElseThrow());
	}

	@Test
	public void testDnDToSelectShapeSelectsIt2() {
		Cmds.of(addLines).execute();
		final Bounds bounds = getPane().getChildren().get(0).getBoundsInParent();
		final double x = canvas.getScene().getWindow().getX() + Canvas.ORIGIN.getX() + bounds.getMinX() - 10d;
		final double y = canvas.getScene().getWindow().getY() + Canvas.ORIGIN.getY() + bounds.getMinY();
		Cmds.of(() -> clickOn(x, y), () -> drag(x + 10, y + 10),
			() -> drag(x + 60, y + 10), () -> drag(x + 70, y + 10)).execute();
		assertEquals(1, canvas.getDrawing().getSelection().size());
		assertSame(addedPolyline, canvas.getDrawing().getSelection().getShapeAt(0).orElseThrow());
	}

	@Test
	public void testDnDToSelectRecOutToIn() {
		Cmds.of(addRec).execute();
		final Bounds bounds = getPane().getChildren().get(0).getBoundsInParent();
		final double x = canvas.getScene().getWindow().getX() + Canvas.ORIGIN.getX() + bounds.getMinX() - 20d;
		final double y = canvas.getScene().getWindow().getY() + Canvas.ORIGIN.getY() + bounds.getMinY();
		Cmds.of(() -> clickOn(x, y), () -> drag(x + 10, y + 20),
			() -> drag(x + 50, y + 20), () -> drag(x + 100, y + 20)).execute();
		assertEquals(1, canvas.getDrawing().getSelection().size());
		assertSame(addedRec, canvas.getDrawing().getSelection().getShapeAt(0).orElseThrow());
	}

	@Test
	public void testDnDToSelectRecInsideFill() {
		Cmds.of(addRec).execute();
		final Bounds bounds = getPane().getChildren().get(0).getBoundsInParent();
		final double x = canvas.getScene().getWindow().getX() + Canvas.ORIGIN.getX() + bounds.getMinX() + bounds.getWidth() / 2d;
		final double y = canvas.getScene().getWindow().getY() + Canvas.ORIGIN.getY() + bounds.getMinY() + bounds.getHeight() / 2d;
		Cmds.of(() -> clickOn(x, y), () -> drag(x + 10, y + 20),
			() -> drag(x + 50, y + 20), () -> drag(x + 100, y + 20)).execute();
		assertEquals(1, canvas.getDrawing().getSelection().size());
		assertSame(addedRec, canvas.getDrawing().getSelection().getShapeAt(0).orElseThrow());
	}

	@Test
	public void testDnDToNotSelectRecInsideEmpty() {
		Cmds.of(addRec, CmdFXVoid.of(() -> addedRec.setFillingStyle(FillingStyle.NONE))).execute();
		final Bounds bounds = getPane().getChildren().get(0).getBoundsInParent();
		final double x = canvas.getScene().getWindow().getX() + Canvas.ORIGIN.getX() + bounds.getMinX() + bounds.getWidth() / 2d;
		final double y = canvas.getScene().getWindow().getY() + Canvas.ORIGIN.getY() + bounds.getMinY() + bounds.getHeight() / 2d;
		Cmds.of(() -> clickOn(x, y), () -> drag(x + 10, y + 20),
			() -> drag(x + 50, y + 20), () -> drag(x + 60, y + 20)).execute();
		assertTrue(canvas.getDrawing().getSelection().isEmpty());
	}

	@Test
	public void testDnDToSelectGroup() {
		Cmds.of(addGroup).execute();
		final Bounds bounds = getPane().getChildren().get(0).getBoundsInLocal();
		final double x = canvas.getScene().getWindow().getX() + Canvas.ORIGIN.getX() + bounds.getMinX() - 20d;
		final double y = canvas.getScene().getWindow().getY() + Canvas.ORIGIN.getY() + bounds.getMinY();
		Cmds.of(() -> clickOn(x, y), () -> drag(x + 10, y + 20),
			() -> drag(x + 50, y + 20), () -> drag(x + 60, y + 20)).execute();
		assertEquals(1, canvas.getDrawing().getSelection().size());
		assertSame(addedGroup, canvas.getDrawing().getSelection().getShapeAt(0).orElseThrow());
	}

	@Test
	public void testDnDToSelectGrid() {
		Cmds.of(addGrid).execute();
		final Bounds bounds = getPane().getChildren().get(0).getBoundsInParent();
		final double x = canvas.getScene().getWindow().getX() + Canvas.ORIGIN.getX() + bounds.getMinX() - 20d;
		final double y = canvas.getScene().getWindow().getY() + Canvas.ORIGIN.getY() + bounds.getMinY();
		Cmds.of(() -> clickOn(x, y), () -> drag(x + 10, y + 20),
			() -> drag(x + 50, y + 20), () -> drag(x + 60, y + 20)).execute();
		assertEquals(1, canvas.getDrawing().getSelection().size());
		assertSame(addedGrid, canvas.getDrawing().getSelection().getShapeAt(0).orElseThrow());
	}

//	@Ignore("Monocle does not capture key modifiers https://github.com/TestFX/Monocle/pull/48")
	@Test
	public void testDnDToSelectGridWithCtrl() {
		assumeFalse(GraphicsEnvironment.isHeadless());
		Cmds.of(addRec, addRec2, clickOnAddedFirstShape).execute();
		final Bounds bounds = getPane().getChildren().get(1).getBoundsInParent();
		final double x = canvas.getScene().getWindow().getX() + Canvas.ORIGIN.getX() + bounds.getMinX() - 20d;
		final double y = canvas.getScene().getWindow().getY() + Canvas.ORIGIN.getY() + bounds.getMinY();
		Cmds.of(() -> press(KeyCode.CONTROL),
			() -> clickOn(x, y),
			() -> drag(x + 10, y + 20),
			() -> drag(x + 50, y + 20),
			() -> drag(x + 60, y + 20)).execute();
		assertEquals(2, canvas.getDrawing().getSelection().size());
	}

//	@Ignore("Monocle does not capture key modifiers https://github.com/TestFX/Monocle/pull/48")
	@Test
	public void testDnDToSelectGridWithShift() {
		assumeFalse(GraphicsEnvironment.isHeadless());
		Cmds.of(addRec, addRec2, clickOnAddedFirstShape, ctrlClickOnAddedRec2).execute();
		final Bounds bounds = getPane().getChildren().get(1).getBoundsInParent();
		final double x = canvas.getScene().getWindow().getX() + Canvas.ORIGIN.getX() + bounds.getMinX() - 20d;
		final double y = canvas.getScene().getWindow().getY() + Canvas.ORIGIN.getY() + bounds.getMinY();
		Cmds.of(() -> press(KeyCode.SHIFT),
			() -> clickOn(x, y),
			() -> drag(x + 10, y + 20),
			() -> drag(x + 50, y + 20),
			() -> drag(x + 60, y + 20)).execute();
		assertEquals(1, canvas.getDrawing().getSelection().size());
		assertSame(addedRec, canvas.getDrawing().getSelection().getShapeAt(0).orElseThrow());
	}

	@Test
	public void testDnDToSelectRecOutToInRotated() {
		Cmds.of(addRec, CmdFXVoid.of(() -> addedRec.setRotationAngle(Math.PI / 8d))).execute();
		final Bounds bounds = getPane().getChildren().get(0).getBoundsInParent();
		final double x = canvas.getScene().getWindow().getX() + Canvas.ORIGIN.getX() + bounds.getMinX() - 20d;
		final double y = canvas.getScene().getWindow().getY() + Canvas.ORIGIN.getY() + bounds.getMinY();
		Cmds.of(() -> clickOn(x, y), () -> drag(x + 10, y + 20),
			() -> drag(x + 60, y + 20)).execute();
		assertEquals(1, canvas.getDrawing().getSelection().size());
		assertSame(addedRec, canvas.getDrawing().getSelection().getShapeAt(0).orElseThrow());
	}

	@Test
	public void testEditTextDoesNotCreateANewOne() {
		when(editing.getCurrentChoice()).thenReturn(EditionChoice.TEXT);
		when(editing.createShapeInstance()).thenReturn(ShapeFactory.INST.createText());
		Cmds.of(addText).execute();
		final ViewText v = (ViewText) canvas.getViews().getChildren().get(0);
		Cmds.of(() -> doubleClickOn(v),
			() -> write("@bar bar"),
			() -> type(KeyCode.ENTER)).execute();
		assertEquals(1, canvas.getDrawing().size());
		assertEquals(1, canvas.getViews().getChildren().size());
	}

	@Test
	public void testEditPlotDoesNotCreateANewOne() {
		when(editing.getCurrentChoice()).thenReturn(EditionChoice.PLOT);
		when(editing.createShapeInstance()).thenReturn(ShapeFactory.INST.createPlot(ShapeFactory.INST.createPoint(), 0, 5, "x", false));
		final ShapePlotCustomiser plot = injector.getInstance(ShapePlotCustomiser.class);
		plot.maxXSpinner = Mockito.mock(Spinner.class);
		plot.minXSpinner = Mockito.mock(Spinner.class);
		plot.nbPtsSpinner = Mockito.mock(Spinner.class);
		when(plot.maxXSpinner.getValue()).thenReturn(10d);
		when(plot.minXSpinner.getValue()).thenReturn(0d);
		when(plot.nbPtsSpinner.getValue()).thenReturn(10);

		Cmds.of(addPlot).execute();
		final ViewPlot v = (ViewPlot) canvas.getViews().getChildren().get(0);

		Cmds.of(() -> doubleClickOn(v),
			() -> type(KeyCode.DELETE).write("x 2 mul"),
			() -> type(KeyCode.ENTER)).execute();

		assertEquals(1, canvas.getDrawing().size());
		assertEquals(1, canvas.getViews().getChildren().size());
	}

	@Test
	public void testClickGroupOfShapesSelectTheGroup() {
		Cmds.of(addGroup, () -> rightClickOn(
			((Group) ((ViewGroup) canvas.getViews().getChildren().get(0)).getChildren().get(0)).getChildren().get(0)))
			.execute();
		assertEquals(1, canvas.getDrawing().getSelection().size());
		assertSame(addedGroup, canvas.getDrawing().getSelection().getShapeAt(0).orElseThrow());
	}
}
