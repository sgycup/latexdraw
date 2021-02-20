package net.sf.latexdraw.instrument;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import javafx.stage.Stage;
import net.sf.latexdraw.model.api.shape.Drawing;
import net.sf.latexdraw.model.api.shape.Shape;
import net.sf.latexdraw.util.Injector;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;

public class TestShapePositioner extends SelectionBasedTesting<ShapePositioner> {
	Drawing drawing;

	@Override
	protected String getFXMLPathFromLatexdraw() {
		return "/fxml/PositionZ.fxml";
	}

	@Override
	@Before
	public void setUp() {
		super.setUp();
		ins = injector.getInstance(ShapePositioner.class);
		drawing = injector.getInstance(Drawing.class);
		ins.setActivated(true);
		ins.update();
	}

	@Override
	protected Injector createInjector() {
		return new ShapePropInjector() {
			@Override
			protected void configure() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
				super.configure();
				bindToSupplier(Stage.class, () -> stage);
				pencil = mock(Pencil.class);
				hand = mock(Hand.class);
				bindToInstance(TextSetter.class, Mockito.mock(TextSetter.class));
				bindToInstance(Hand.class, hand);
				bindToInstance(Pencil.class, pencil);
				bindAsEagerSingleton(ShapePositioner.class);
				bindToInstance(MetaShapeCustomiser.class, Mockito.mock(MetaShapeCustomiser.class));
			}
		};
	}

	@Test
	public void testDeactivatedEmpty() {
		assertFalse(ins.isActivated());
		assertFalse(find("#mainPane").isVisible());
	}

	@Test
	public void testOneShapeBackground() {
		Cmds.of(selectOneShape, selectOneShape).execute();
		final Shape s1 = drawing.getShapeAt(0).orElseThrow();
		final Shape s2 = drawing.getShapeAt(1).orElseThrow();
		Cmds.of(() -> clickOn("#backgroundB")).execute();
		assertEquals(s2, drawing.getShapeAt(0).orElseThrow());
		assertEquals(s1, drawing.getShapeAt(1).orElseThrow());
	}

	@Test
	public void testOneShapeForeground() {
		Cmds.of(selectOneShape, selectOneShape).execute();
		selectShapeAt.execute(Collections.singletonList(0));
		final Shape s1 = drawing.getShapeAt(0).orElseThrow();
		final Shape s2 = drawing.getShapeAt(1).orElseThrow();
		Cmds.of(() -> clickOn("#foregroundB")).execute();
		assertEquals(s2, drawing.getShapeAt(0).orElseThrow());
		assertEquals(s1, drawing.getShapeAt(1).orElseThrow());
	}

	@Test
	public void testSeveralShapesBackground() {
		Cmds.of(selectThreeShapes, selectTwoShapes).execute();
		selectShapeAt.execute(Arrays.asList(1, 2));
		final Shape s2 = drawing.getShapeAt(1).orElseThrow();
		final Shape s3 = drawing.getShapeAt(2).orElseThrow();
		Cmds.of(() -> clickOn("#backgroundB")).execute();
		assertEquals(s2, drawing.getShapeAt(0).orElseThrow());
		assertEquals(s3, drawing.getShapeAt(1).orElseThrow());
	}

	@Test
	public void testSeveralShapesForeground() {
		Cmds.of(selectThreeShapes, selectTwoShapes).execute();
		selectShapeAt.execute(Arrays.asList(1, 2));
		final Shape s2 = drawing.getShapeAt(1).orElseThrow();
		final Shape s3 = drawing.getShapeAt(2).orElseThrow();
		Cmds.of(() -> clickOn("#foregroundB")).execute();
		assertEquals(s2, drawing.getShapeAt(drawing.size() - 2).orElseThrow());
		assertEquals(s3, drawing.getShapeAt(-1).orElseThrow());
	}
}
