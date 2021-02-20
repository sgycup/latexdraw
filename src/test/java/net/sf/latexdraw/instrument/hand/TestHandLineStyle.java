package net.sf.latexdraw.instrument.hand;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import net.sf.latexdraw.instrument.Cmds;
import net.sf.latexdraw.instrument.Hand;
import net.sf.latexdraw.instrument.MetaShapeCustomiser;
import net.sf.latexdraw.instrument.Pencil;
import net.sf.latexdraw.instrument.ShapeBorderCustomiser;
import net.sf.latexdraw.instrument.ShapePropInjector;
import net.sf.latexdraw.instrument.TestLineStyleGUI;
import net.sf.latexdraw.instrument.TextSetter;
import net.sf.latexdraw.model.api.property.ClosableProp;
import net.sf.latexdraw.model.api.shape.BorderPos;
import net.sf.latexdraw.model.api.shape.LineStyle;
import net.sf.latexdraw.model.api.shape.Rectangle;
import net.sf.latexdraw.util.Injector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class TestHandLineStyle extends TestLineStyleGUI {
	@Override
	protected Injector createInjector() {
		return new ShapePropInjector() {
			@Override
			protected void configure() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
				super.configure();
				bindToSupplier(Stage.class, () -> stage);
				pencil = mock(Pencil.class);
				bindToInstance(Pencil.class, pencil);
				bindToInstance(TextSetter.class, mock(TextSetter.class));
				bindAsEagerSingleton(Hand.class);
				bindAsEagerSingleton(ShapeBorderCustomiser.class);
				bindToInstance(MetaShapeCustomiser.class, mock(MetaShapeCustomiser.class));
			}
		};
	}

	@Test
	public void testControllerNotActivatedWhenSelectionEmpty() {
		Cmds.of(activateHand, updateIns, checkInsDeactivated).execute();
	}

	@Test
	public void testControllerActivatedWhenSelectionNotEmpty() {
		Cmds.of(selectionAddRec, activateHand, updateIns).execute();
		assertTrue(ins.isActivated());
		assertTrue(thicknessField.isVisible());
		assertFalse(thicknessField.isDisabled());
		assertTrue(lineColButton.isVisible());
		assertFalse(lineColButton.isDisabled());
		assertTrue(lineCB.isVisible());
		assertFalse(lineCB.isDisabled());
		assertTrue(bordersPosCB.isVisible());
		assertFalse(bordersPosCB.isDisabled());
		assertTrue(frameArcField.isVisible());
		assertFalse(frameArcField.isDisabled());
		assertFalse(showPoints.isVisible());
		assertFalse(opened.isVisible());
	}

	@Test
	public void testOpenVisibleIfBezierAndActivated() {
		Cmds.of(selectionAddBezier, activateHand, updateIns).execute();
		assertTrue(opened.isVisible());
	}

	@Test
	public void testChangeFrameArcSelection() {
		doTestSpinner(Cmds.of(Cmds.of(activateHand, selectionAddRec, selectionAddRec, updateIns)), frameArcField,
			incrementFrameArc, Arrays.asList(
				() -> ((Rectangle) drawing.getSelection().getShapeAt(0).orElseThrow()).getLineArc(),
				() -> ((Rectangle) drawing.getSelection().getShapeAt(1).orElseThrow()).getLineArc()));
	}

	@Test
	public void testChangeThicknessSelection() {
		doTestSpinner(Cmds.of(Cmds.of(activateHand, selectionAddRec, selectionAddRec, updateIns)), thicknessField,
			incrementThickness, Arrays.asList(
				() -> drawing.getSelection().getShapeAt(0).orElseThrow().getThickness(),
				() -> drawing.getSelection().getShapeAt(1).orElseThrow().getThickness()));
	}

	@Test
	public void testSelectBorderPosSelection() {
		Cmds.of(activateHand, selectionAddRec, selectionAddRec, updateIns).execute();
		final BorderPos style = bordersPosCB.getSelectionModel().getSelectedItem();
		Cmds.of(selectBorderPos).execute();
		final BorderPos newStyle = bordersPosCB.getSelectionModel().getSelectedItem();
		assertEquals(newStyle, drawing.getSelection().getShapeAt(0).orElseThrow().getBordersPosition());
		assertEquals(newStyle, drawing.getSelection().getShapeAt(1).orElseThrow().getBordersPosition());
		assertNotEquals(style, newStyle);
	}

	@Test
	public void testSelectLineStyleSelection() {
		Cmds.of(activateHand, selectionAddRec, selectionAddRec, updateIns).execute();
		final LineStyle style = lineCB.getSelectionModel().getSelectedItem();
		Cmds.of(selectLineStyle).execute();
		final LineStyle newStyle = lineCB.getSelectionModel().getSelectedItem();
		assertEquals(newStyle, drawing.getSelection().getShapeAt(0).orElseThrow().getLineStyle());
		assertEquals(newStyle, drawing.getSelection().getShapeAt(1).orElseThrow().getLineStyle());
		assertNotEquals(style, newStyle);
	}

	@Test
	public void testCheckShowPointSelection() {
		Cmds.of(activateHand, selectionAddBezier, selectionAddBezier, updateIns).execute();
		final boolean sel = showPoints.isSelected();
		Cmds.of(checkShowPts).execute();
		assertEquals(!sel, drawing.getSelection().getShapeAt(0).orElseThrow().isShowPts());
		assertEquals(!sel, drawing.getSelection().getShapeAt(1).orElseThrow().isShowPts());
	}

	@Test
	public void testCheckCloseOKSelection() {
		Cmds.of(activateHand, selectionAddBezier, selectionAddFreehand, updateIns).execute();
		assertEquals(((ClosableProp) drawing.getSelection().getShapeAt(0).orElseThrow()).isOpened(), opened.isSelected());
	}

	@Test
	public void testCheckCloseSelection() {
		Cmds.of(activateHand, selectionAddBezier, selectionAddFreehand, updateIns).execute();
		final boolean isOpen = opened.isSelected();
		Cmds.of(checkOpened).execute();
		assertEquals(!isOpen, ((ClosableProp) drawing.getSelection().getShapeAt(0).orElseThrow()).isOpened());
		assertEquals(!isOpen, ((ClosableProp) drawing.getSelection().getShapeAt(1).orElseThrow()).isOpened());
	}

	@Test
	public void testPickLineColourSelection() {
		Cmds.of(activateHand, selectionAddRec, selectionAddBezier, updateIns).execute();
		final Color col = lineColButton.getValue();
		Cmds.of(pickLineCol).execute();
		assertEquals(lineColButton.getValue(), drawing.getSelection().getShapeAt(0).orElseThrow().getLineColour().toJFX());
		assertEquals(lineColButton.getValue(), drawing.getSelection().getShapeAt(1).orElseThrow().getLineColour().toJFX());
		assertNotEquals(col, lineColButton.getValue());
	}
}
