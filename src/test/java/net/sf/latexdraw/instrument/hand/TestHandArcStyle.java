package net.sf.latexdraw.instrument.hand;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import javafx.stage.Stage;
import net.sf.latexdraw.instrument.Cmds;
import net.sf.latexdraw.instrument.Hand;
import net.sf.latexdraw.instrument.MetaShapeCustomiser;
import net.sf.latexdraw.instrument.Pencil;
import net.sf.latexdraw.instrument.ShapeArcCustomiser;
import net.sf.latexdraw.instrument.ShapePropInjector;
import net.sf.latexdraw.instrument.TestArcStyleGUI;
import net.sf.latexdraw.instrument.TextSetter;
import net.sf.latexdraw.model.api.shape.Arc;
import net.sf.latexdraw.model.api.shape.ArcStyle;
import net.sf.latexdraw.util.Injector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class TestHandArcStyle extends TestArcStyleGUI {
	@Override
	protected Injector createInjector() {
		return new ShapePropInjector() {
			@Override
			protected void configure() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
				super.configure();
				bindToSupplier(Stage.class, () -> stage);
				pencil = mock(Pencil.class);
				bindToInstance(TextSetter.class, mock(TextSetter.class));
				bindAsEagerSingleton(Hand.class);
				bindToInstance(Pencil.class, pencil);
				bindAsEagerSingleton(ShapeArcCustomiser.class);
				bindToInstance(MetaShapeCustomiser.class, mock(MetaShapeCustomiser.class));
			}
		};
	}

	@Test
	public void testControllerNotActivatedWhenSelectionEmpty() {
		Cmds.of(activateHand, updateIns, checkInsDeactivated).execute();
	}

	@Test
	public void testControllerActivatedWhenSelectionArc() {
		Cmds.of(selectionAddArc, activateHand, updateIns).execute();
		assertTrue(ins.isActivated());
		assertTrue(titledPane.isVisible());
	}

	@Test
	public void testControllerDeactivatedWhenSelectionNotArc() {
		Cmds.of(selectionAddRec, activateHand, updateIns).execute();
		assertFalse(ins.isActivated());
		assertFalse(titledPane.isVisible());
	}

	@Test
	public void testControllerDeactivatedWhenSelectionEmpty() {
		Cmds.of(activateHand, updateIns).execute();
		assertFalse(ins.isActivated());
		assertFalse(titledPane.isVisible());
	}

	@Test
	public void testArcTypeChordSelection() {
		Cmds.of(activateHand, selectionAddArc, selectionAddRec, selectionAddArc, updateIns, selectWedge, selectChord).execute();
		assertEquals(ArcStyle.CHORD, ((Arc) drawing.getSelection().getShapeAt(0).orElseThrow()).getArcStyle());
		assertEquals(ArcStyle.CHORD, ((Arc) drawing.getSelection().getShapeAt(2).orElseThrow()).getArcStyle());
	}

	@Test
	public void testArcTypeArcSelection() {
		Cmds.of(activateHand, selectionAddArc, selectionAddRec, selectionAddArc, updateIns, selectChord, selectArc).execute();
		assertEquals(ArcStyle.ARC, ((Arc) drawing.getSelection().getShapeAt(0).orElseThrow()).getArcStyle());
		assertEquals(ArcStyle.ARC, ((Arc) drawing.getSelection().getShapeAt(2).orElseThrow()).getArcStyle());
	}

	@Test
	public void testArcTypeWedgeSelection() {
		Cmds.of(activateHand, selectionAddArc, selectionAddRec, selectionAddArc, updateIns, selectChord, selectWedge).execute();
		assertEquals(ArcStyle.WEDGE, ((Arc) drawing.getSelection().getShapeAt(0).orElseThrow()).getArcStyle());
		assertEquals(ArcStyle.WEDGE, ((Arc) drawing.getSelection().getShapeAt(2).orElseThrow()).getArcStyle());
	}

	@Test
	public void testArcEndAngleSelection() {
		doTestSpinner(Cmds.of(activateHand, selectionAddArc, selectionAddRec, selectionAddArc, updateIns), endAngleS,
			incrementEndAngle, Arrays.asList(
			() ->  Math.toDegrees(((Arc) drawing.getSelection().getShapeAt(0).orElseThrow()).getAngleEnd()),
			() ->  Math.toDegrees(((Arc) drawing.getSelection().getShapeAt(2).orElseThrow()).getAngleEnd())));
	}

	@Test
	public void testArcStartAngleSelection() {
		doTestSpinner(Cmds.of(activateHand, selectionAddArc, selectionAddRec, selectionAddArc, updateIns), startAngleS,
			incrementStartAngle, Arrays.asList(
			() ->  Math.toDegrees(((Arc) drawing.getSelection().getShapeAt(0).orElseThrow()).getAngleStart()),
			() ->  Math.toDegrees(((Arc) drawing.getSelection().getShapeAt(2).orElseThrow()).getAngleStart())));
	}
}
