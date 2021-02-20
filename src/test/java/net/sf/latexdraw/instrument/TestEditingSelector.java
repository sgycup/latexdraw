package net.sf.latexdraw.instrument;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.HostServices;
import net.sf.latexdraw.model.ShapeFactory;
import net.sf.latexdraw.model.api.shape.Drawing;
import net.sf.latexdraw.model.api.shape.Text;
import net.sf.latexdraw.service.EditingService;
import net.sf.latexdraw.service.LaTeXDataService;
import net.sf.latexdraw.service.PreferencesService;
import net.sf.latexdraw.ui.TextAreaAutoSize;
import net.sf.latexdraw.util.Injector;
import net.sf.latexdraw.view.jfx.Canvas;
import net.sf.latexdraw.view.jfx.MagneticGrid;
import net.sf.latexdraw.view.jfx.ViewFactory;
import net.sf.latexdraw.view.latex.LaTeXGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;

@RunWith(Theories.class)
public class TestEditingSelector extends TestLatexdrawGUI {
	EditingSelector selector;
	TextSetter textSetter;
	Pencil pencil;
	Drawing drawing;
	Hand hand;

	final CmdVoid clickHand = () -> clickOn(selector.handB);

	@Override
	protected String getFXMLPathFromLatexdraw() {
		return "/fxml/EditingModes.fxml";
	}

	@Override
	protected Injector createInjector() {
		return new Injector() {
			@Override
			protected void configure() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
				bindToInstance(Injector.class, this);
				bindAsEagerSingleton(PreferencesService.class);
				bindToInstance(LaTeXDataService.class, Mockito.mock(LaTeXDataService.class));
				bindToInstance(EditingService.class, Mockito.mock(EditingService.class));
				bindWithCommand(ResourceBundle.class, PreferencesService.class, pref -> pref.getBundle());
				bindAsEagerSingleton(ViewFactory.class);
				bindToInstance(TextSetter.class, Mockito.mock(TextSetter.class));
				bindToInstance(LaTeXGenerator.class, Mockito.mock(LaTeXGenerator.class));
				bindToInstance(HostServices.class, Mockito.mock(HostServices.class));
				bindToInstance(ShapeTextCustomiser.class, Mockito.mock(ShapeTextCustomiser.class));
				bindToInstance(ShapePlotCustomiser.class, Mockito.mock(ShapePlotCustomiser.class));
				bindToInstance(Drawing.class, ShapeFactory.INST.createDrawing());
				bindToInstance(Hand.class, Mockito.mock(Hand.class));
				bindToInstance(Canvas.class, Mockito.mock(Canvas.class));
				bindToInstance(Pencil.class, Mockito.mock(Pencil.class));
				bindToInstance(ShapeAxesCustomiser.class, Mockito.mock(ShapeAxesCustomiser.class));
				bindToInstance(ShapeDoubleBorderCustomiser.class, Mockito.mock(ShapeDoubleBorderCustomiser.class));
				bindToInstance(ShapeFreeHandCustomiser.class, Mockito.mock(ShapeFreeHandCustomiser.class));
				bindToInstance(ShapeCoordDimCustomiser.class, Mockito.mock(ShapeCoordDimCustomiser.class));
				bindToInstance(ShapeShadowCustomiser.class, Mockito.mock(ShapeShadowCustomiser.class));
				bindToInstance(ShapeTransformer.class, Mockito.mock(ShapeTransformer.class));
				bindToInstance(ShapeGrouper.class, Mockito.mock(ShapeGrouper.class));
				bindToInstance(ShapePositioner.class, Mockito.mock(ShapePositioner.class));
				bindToInstance(ShapeRotationCustomiser.class, Mockito.mock(ShapeRotationCustomiser.class));
				bindToInstance(ShapeDotCustomiser.class, Mockito.mock(ShapeDotCustomiser.class));
				bindToInstance(ShapeBorderCustomiser.class, Mockito.mock(ShapeBorderCustomiser.class));
				bindToInstance(ShapeArcCustomiser.class, Mockito.mock(ShapeArcCustomiser.class));
				bindToInstance(ShapeFillingCustomiser.class, Mockito.mock(ShapeFillingCustomiser.class));
				bindToInstance(ShapeGridCustomiser.class, Mockito.mock(ShapeGridCustomiser.class));
				bindToInstance(ShapeArrowCustomiser.class, Mockito.mock(ShapeArrowCustomiser.class));
				bindToInstance(ShapeStdGridCustomiser.class, Mockito.mock(ShapeStdGridCustomiser.class));
				bindToInstance(MetaShapeCustomiser.class, Mockito.mock(MetaShapeCustomiser.class));
				bindToInstance(MagneticGrid.class, Mockito.mock(MagneticGrid.class));
				bindToInstance(Border.class, Mockito.mock(Border.class));
				bindToInstance(ShapeDeleter.class, Mockito.mock(ShapeDeleter.class));
				bindToInstance(CodeInserter.class, Mockito.mock(CodeInserter.class));
				bindAsEagerSingleton(EditingSelector.class);
			}
		};
	}

	@Before
	public void setUp() {
		selector = injector.getInstance(EditingSelector.class);
		textSetter = injector.getInstance(TextSetter.class);
		drawing = injector.getInstance(Drawing.class);
		pencil = injector.getInstance(Pencil.class);
		hand = injector.getInstance(Hand.class);
		Mockito.when(injector.getInstance(Canvas.class).getDrawing()).thenReturn(drawing);
	}

	@Theory
	public void testClickShapeMode(final EditionChoice mode) {
		assumeFalse(mode == EditionChoice.HAND);
		final Map<EditionChoice, String> modeToID = new HashMap<>();
		modeToID.put(EditionChoice.PLOT, "#plotB");
		modeToID.put(EditionChoice.PICTURE, "#picB");
		modeToID.put(EditionChoice.CIRCLE_ARC, "#arcB");
		modeToID.put(EditionChoice.TRIANGLE, "#triangleB");
		modeToID.put(EditionChoice.RHOMBUS, "#rhombusB");
		modeToID.put(EditionChoice.AXES, "#axesB");
		modeToID.put(EditionChoice.GRID, "#gridB");
		modeToID.put(EditionChoice.BEZIER_CURVE, "#bezierB");
		modeToID.put(EditionChoice.POLYGON, "#polygonB");
		modeToID.put(EditionChoice.LINES, "#linesB");
		modeToID.put(EditionChoice.CIRCLE, "#circleB");
		modeToID.put(EditionChoice.ELLIPSE, "#ellipseB");
		modeToID.put(EditionChoice.SQUARE, "#squareB");
		modeToID.put(EditionChoice.RECT, "#recB");
		modeToID.put(EditionChoice.FREE_HAND, "#freeHandB");
		modeToID.put(EditionChoice.TEXT, "#textB");
		modeToID.put(EditionChoice.DOT, "#dotB");

		clickOn(modeToID.get(mode));
		waitFXEvents.execute();
		Mockito.verify(injector.getInstance(EditingService.class), Mockito.times(1)).setCurrentChoice(mode);
		// Do not want to write another test case to limit the number of GUI test (memory and time issues)
		if(mode != EditionChoice.TEXT) {
			Mockito.verify(textSetter, Mockito.times(1)).setActivated(false, false);
		}
		Mockito.verify(hand, Mockito.times(1)).setActivated(false, false);
		Mockito.verify(injector.getInstance(Border.class), Mockito.times(1)).setActivated(false, false);
		Mockito.verify(injector.getInstance(ShapeDeleter.class), Mockito.times(1)).setActivated(false, false);
		Mockito.verify(pencil, Mockito.times(1)).setActivated(true);
		Mockito.verify(injector.getInstance(MetaShapeCustomiser.class), Mockito.times(1)).setActivated(true);
	}

	@Test
	public void testClickTextNoTextCauseNotActivated() {
		final TextAreaAutoSize textfield = Mockito.mock(TextAreaAutoSize.class);
		Mockito.when(textSetter.getTextField()).thenReturn(textfield);
		Mockito.when(textSetter.isActivated()).thenReturn(false);
		Cmds.of(clickHand).execute();
		assertTrue(drawing.isEmpty());
	}

	@Test
	public void testClickTextNoTextCauseNoText() {
		final TextAreaAutoSize textfield = Mockito.mock(TextAreaAutoSize.class);
		Mockito.when(textSetter.getTextField()).thenReturn(textfield);
		Mockito.when(textSetter.isActivated()).thenReturn(true);
		Mockito.when(textfield.getText()).thenReturn("");
		Cmds.of(clickHand).execute();
		assertTrue(drawing.isEmpty());
	}

	@Test
	public void testClickTextCreatesText() {
		final TextAreaAutoSize textfield = Mockito.mock(TextAreaAutoSize.class);
		Mockito.when(textSetter.getTextField()).thenReturn(textfield);
		Mockito.when(textSetter.isActivated()).thenReturn(true);
		Mockito.when(textfield.getText()).thenReturn("gridGapProp");

		Cmds.of(clickHand).execute();
		assertEquals(1, drawing.size());
		assertTrue(drawing.getShapeAt(0).orElseThrow() instanceof Text);
		assertEquals("gridGapProp", ((Text) drawing.getShapeAt(0).orElseThrow()).getText());
	}

	@Test
	public void testClickHandActivationNoSelection() {
		Cmds.of(clickHand).execute();
		Mockito.verify(pencil, Mockito.times(1)).setActivated(false, false);
		Mockito.verify(injector.getInstance(Border.class), Mockito.times(1)).setActivated(false, false);
		Mockito.verify(injector.getInstance(MetaShapeCustomiser.class), Mockito.times(1)).setActivated(false, false);
	}

	@Test
	public void testClickHandActivationSelection() {
		drawing.getSelection().addShape(ShapeFactory.INST.createRectangle());
		Cmds.of(clickHand).execute();
		Mockito.verify(injector.getInstance(ShapeDeleter.class), Mockito.times(1)).setActivated(true);
		Mockito.verify(pencil, Mockito.times(1)).setActivated(false, false);
		Mockito.verify(injector.getInstance(Border.class), Mockito.times(1)).setActivated(true);
		Mockito.verify(injector.getInstance(MetaShapeCustomiser.class), Mockito.times(1)).setActivated(true);
	}

	@Test
	public void testClickCodeInsert() {
		Cmds.of(() -> clickOn(selector.codeB)).execute();
		Mockito.verify(injector.getInstance(Hand.class), Mockito.times(1)).setActivated(true);
	}
}
