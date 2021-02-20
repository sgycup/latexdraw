package net.sf.latexdraw.instrument;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import net.sf.latexdraw.HelperTest;
import net.sf.latexdraw.instrument.robot.FxRobotListSelection;
import net.sf.latexdraw.instrument.robot.FxRobotSpinner;
import net.sf.latexdraw.model.ShapeFactory;
import net.sf.latexdraw.model.api.shape.Drawing;
import net.sf.latexdraw.service.LaTeXDataService;
import net.sf.latexdraw.util.Injector;
import net.sf.latexdraw.view.latex.LaTeXGenerator;
import net.sf.latexdraw.view.latex.VerticalPosition;
import net.sf.latexdraw.view.pst.PSTCodeGenerator;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class TestDrawingPropGUI extends TestLatexdrawGUI implements FxRobotSpinner, FxRobotListSelection {
	TextField titleField;
	TextField labelField;
	CheckBox middleHorizPosCB;
	ComboBox<VerticalPosition> positionCB;
	Spinner<Double> scaleField;
	DrawingPropertiesCustomiser ins;
	LaTeXDataService data;

	final CmdVoid typeLabel = () -> clickOn(labelField).write("newLabel");
	final CmdVoid typeTitle = () -> clickOn(titleField).write("new Title");
	final CmdVoid changeScale = () -> incrementSpinner(scaleField);
	final CmdVoid checkMiddleHoriz = () -> clickOn(middleHorizPosCB);
	final Cmd<VerticalPosition> changePosition = pos -> selectGivenComboBoxItem(positionCB, pos);

	@Override
	public String getFXMLPathFromLatexdraw() {
		return "/fxml/DrawingProps.fxml";
	}

	@Override
	protected Injector createInjector() {
		return new ShapePropInjector() {
			@Override
			protected void configure() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
				super.configure();
				bindToSupplier(Stage.class, () -> stage);
				hand = mock(Hand.class);
				bindAsEagerSingleton(PSTCodeGenerator.class);
				bindAsEagerSingleton(DrawingPropertiesCustomiser.class);
				bindWithCommand(LaTeXGenerator.class, PSTCodeGenerator.class, gen -> gen);
			}
		};
	}

	@Before
	public void setUp() {
		titleField = find("#titleField");
		labelField = find("#labelField");
		middleHorizPosCB = find("#middleHorizPosCB");
		positionCB = find("#positionCB");
		scaleField = find("#scaleField");
		injector.getInstance(Drawing.class).addShape(ShapeFactory.INST.createCircle());
		ins = injector.getInstance(DrawingPropertiesCustomiser.class);
		data = injector.getInstance(LaTeXDataService.class);
		ins.setActivated(true);
	}

	@Test
	public void testSetCaption() {
		Cmds.of(typeTitle, () -> HelperTest.waitForTimeoutTransitions()).execute();
		assertEquals("new Title", data.getCaption());
	}

	@Test
	public void testSetLabel() {
		Cmds.of(typeLabel, () -> HelperTest.waitForTimeoutTransitions()).execute();
		assertEquals("newLabel", data.getLabel());
	}

	@Test
	public void testSetScale() {
		doTestSpinner(Cmds.of(), scaleField, changeScale, Collections.singletonList(() -> data.getScale()));
	}

	@Test
	public void testSetMiddleHoriz() {
		Cmds.of(checkMiddleHoriz).execute();
		assertEquals(middleHorizPosCB.isSelected(), data.isPositionHoriCentre());
	}

	@Test
	public void testSePositionBOTTOM() {
		Cmds.of(() -> changePosition.execute(VerticalPosition.BOTTOM)).execute();
		assertEquals(VerticalPosition.BOTTOM, data.getPositionVertToken());
	}

	@Test
	public void testSePositionFLOAT() {
		Cmds.of(() -> changePosition.execute(VerticalPosition.FLOATS_PAGE)).execute();
		assertEquals(VerticalPosition.FLOATS_PAGE, data.getPositionVertToken());
	}

	@Test
	public void testSePositionHERE() {
		Cmds.of(() -> changePosition.execute(VerticalPosition.HERE)).execute();
		assertEquals(VerticalPosition.HERE, data.getPositionVertToken());
	}

	@Test
	public void testSePositionHEREHERE() {
		Cmds.of(() -> changePosition.execute(VerticalPosition.HERE_HERE)).execute();
		assertEquals(VerticalPosition.HERE_HERE, data.getPositionVertToken());
	}

	@Test
	public void testSePositionTOP() {
		Cmds.of(() -> changePosition.execute(VerticalPosition.TOP)).execute();
		assertEquals(VerticalPosition.TOP, data.getPositionVertToken());
	}

	@Test
	public void testSePositionNONE() {
		Cmds.of(() -> changePosition.execute(VerticalPosition.NONE)).execute();
		assertEquals(VerticalPosition.NONE, data.getPositionVertToken());
	}

	@Test
	public void testTitledPaneVisible() {
		assertTrue(titledPane.isVisible());
	}
}
