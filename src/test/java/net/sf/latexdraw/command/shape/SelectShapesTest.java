package net.sf.latexdraw.command.shape;

import io.github.interacto.command.Command;
import io.github.interacto.jfx.test.CommandTest;
import java.util.List;
import java.util.stream.Stream;
import net.sf.latexdraw.LatexdrawExtension;
import net.sf.latexdraw.model.ShapeFactory;
import net.sf.latexdraw.model.api.shape.Circle;
import net.sf.latexdraw.model.api.shape.Drawing;
import net.sf.latexdraw.model.api.shape.Ellipse;
import net.sf.latexdraw.model.api.shape.Rectangle;
import net.sf.latexdraw.model.api.shape.Shape;
import net.sf.latexdraw.model.api.shape.Triangle;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for the command SelectShapes. Generated by Interacto test-gen.
 */
@Tag("command")
@ExtendWith(LatexdrawExtension.class)
class SelectShapesTest extends CommandTest<SelectShapes> {
	List<Shape> shapes;
	Drawing drawing;
	Shape s0;
	Shape s1;
	Shape s2;
	Shape s3;

	@Override
	protected Stream<Runnable> canDoFixtures() {
		return Stream.of(() -> {
			drawing = ShapeFactory.INST.createDrawing();
			s0 = Mockito.mock(Rectangle.class);
			s1 = Mockito.mock(Ellipse.class);
			s2 = Mockito.mock(Circle.class);
			s3 = Mockito.mock(Triangle.class);
			drawing.addShape(s0);
			drawing.addShape(s1);
			drawing.addShape(s2);
			drawing.addShape(s3);
			shapes = List.of(s1, s3);
			cmd = new SelectShapes(drawing);
			cmd.setShape(s1);
			cmd.addShape(s3);
		});
	}

	@Override
	protected Stream<Runnable> doCheckers() {
		return Stream.of(() -> {
			assertThat(drawing.getSelection().getShapes().get()).isEqualTo(shapes);
			assertThat(drawing.isModified()).isFalse();
		});
	}

	@ParameterizedTest
	@MethodSource({"canDoFixtures"})
	void testGetRegistrationPolicy(final Runnable config) {
		config.run();
		assertThat(cmd.getRegistrationPolicy()).isSameAs(Command.RegistrationPolicy.UNLIMITED);
	}

	@ParameterizedTest
	@MethodSource({"canDoFixtures"})
	void testUnregisteredBy(final Runnable config) {
		config.run();
		assertThat(cmd.unregisteredBy(Mockito.mock(SelectShapes.class))).isTrue();
		assertThat(cmd.unregisteredBy(Mockito.mock(CutShapes.class))).isTrue();
		assertThat(cmd.unregisteredBy(Mockito.mock(DeleteShapes.class))).isTrue();
		assertThat(cmd.unregisteredBy(Mockito.mock(Command.class))).isFalse();
	}
}
