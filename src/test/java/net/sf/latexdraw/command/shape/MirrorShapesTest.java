package net.sf.latexdraw.command.shape;

import io.github.interacto.jfx.test.UndoableCmdTest;
import java.util.stream.Stream;
import net.sf.latexdraw.LatexdrawExtension;
import net.sf.latexdraw.model.ShapeFactory;
import net.sf.latexdraw.model.api.shape.Group;
import net.sf.latexdraw.service.PreferencesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for the command MirrorShapes. Generated by Interacto test-gen.
 */
@Tag("command")
@ExtendWith(LatexdrawExtension.class)
class MirrorShapesTest extends UndoableCmdTest<MirrorShapes> {
	boolean horizontally;
	Group shape;

	@BeforeEach
	void setUp() {
		bundle = new PreferencesService().getBundle();
	}

	@Override
	protected Stream<Runnable> canDoFixtures() {
		return Stream.of(() -> {
			horizontally = true;
			cmd = new MirrorShapes(horizontally, shape);
		}, () -> {
			horizontally = false;
			cmd = new MirrorShapes(horizontally, shape);
		});
	}

	@Override
	protected void commonCanDoFixture() {
		shape = ShapeFactory.INST.createGroup();
		shape.addShape(ShapeFactory.INST.createRectangle(ShapeFactory.INST.createPoint(10, 20), 100, 200));
		shape.addShape(ShapeFactory.INST.createEllipse(ShapeFactory.INST.createPoint(150, 250), ShapeFactory.INST.createPoint(260, 270)));
	}

	@Override
	protected Stream<Runnable> doCheckers() {
		return Stream.of(() -> {
			assertThat(shape.getShapeAt(0).orElseThrow().getTopLeftPoint()).isEqualTo(ShapeFactory.INST.createPoint(160, 20));
			assertThat(shape.getShapeAt(0).orElseThrow().getBottomRightPoint()).isEqualTo(ShapeFactory.INST.createPoint(260, 220));
			assertThat(shape.getShapeAt(1).orElseThrow().getTopLeftPoint()).isEqualTo(ShapeFactory.INST.createPoint(10, 250));
			assertThat(shape.getShapeAt(1).orElseThrow().getBottomRightPoint()).isEqualTo(ShapeFactory.INST.createPoint(120, 270));
			assertThat(shape.getShapes()).allMatch(sh -> sh.isModified());
		}, () -> {
			assertThat(shape.getShapeAt(0).orElseThrow().getTopLeftPoint()).isEqualTo(ShapeFactory.INST.createPoint(10, 70));
			assertThat(shape.getShapeAt(0).orElseThrow().getBottomRightPoint()).isEqualTo(ShapeFactory.INST.createPoint(110, 270));
			assertThat(shape.getShapeAt(1).orElseThrow().getTopLeftPoint()).isEqualTo(ShapeFactory.INST.createPoint(150, 20));
			assertThat(shape.getShapeAt(1).orElseThrow().getBottomRightPoint()).isEqualTo(ShapeFactory.INST.createPoint(260, 40));
			assertThat(shape.getShapes()).allMatch(sh -> sh.isModified());
		});
	}

	@Override
	protected Stream<Runnable> undoCheckers() {
		return Stream.of(() -> {
			assertThat(shape.getShapeAt(0).orElseThrow().getTopLeftPoint()).isEqualTo(ShapeFactory.INST.createPoint(10, 20));
			assertThat(shape.getShapeAt(0).orElseThrow().getBottomRightPoint()).isEqualTo(ShapeFactory.INST.createPoint(110, 220));
			assertThat(shape.getShapeAt(1).orElseThrow().getTopLeftPoint()).isEqualTo(ShapeFactory.INST.createPoint(150, 250));
			assertThat(shape.getShapeAt(1).orElseThrow().getBottomRightPoint()).isEqualTo(ShapeFactory.INST.createPoint(260, 270));
			assertThat(shape.getShapes()).allMatch(sh -> !sh.isModified());
		});
	}
}
