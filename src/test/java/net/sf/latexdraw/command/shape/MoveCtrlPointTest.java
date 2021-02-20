package net.sf.latexdraw.command.shape;

import io.github.interacto.jfx.test.UndoableCmdTest;
import java.util.List;
import java.util.stream.Stream;
import net.sf.latexdraw.LatexdrawExtension;
import net.sf.latexdraw.model.ShapeFactory;
import net.sf.latexdraw.model.api.shape.ControlPointShape;
import net.sf.latexdraw.model.api.shape.Point;
import net.sf.latexdraw.service.PreferencesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for the command MoveCtrlPoint. Generated by Interacto test-gen.
 */
@Tag("command")
@ExtendWith(LatexdrawExtension.class)
class MoveCtrlPointTest extends UndoableCmdTest<MoveCtrlPoint> {
	ControlPointShape shape;
	boolean isFirstCtrlPt;
	Point point;
	Point newCoord;
	Point memento;

	@BeforeEach
	void setUp() {
		bundle = new PreferencesService().getBundle();
	}

	@Override
	protected Stream<Runnable> canDoFixtures() {
		return Stream.of(() -> {
			point = shape.getFirstCtrlPtAt(1);
			memento = ShapeFactory.INST.createPoint(point);
			isFirstCtrlPt = true;
			newCoord = ShapeFactory.INST.createPoint(20, -50);
			cmd = new MoveCtrlPoint(shape, point, isFirstCtrlPt);
			cmd.setNewCoord(newCoord);
		}, () -> {
			point = shape.getSecondCtrlPtAt(1);
			memento = ShapeFactory.INST.createPoint(point);
			isFirstCtrlPt = false;
			newCoord = ShapeFactory.INST.createPoint(-100, 500);
			cmd = new MoveCtrlPoint(shape, point, isFirstCtrlPt);
			cmd.setNewCoord(newCoord);
		});
	}

	@Override
	protected void commonCanDoFixture() {
		shape = ShapeFactory.INST.createBezierCurve(List.of(
			ShapeFactory.INST.createPoint(),
			ShapeFactory.INST.createPoint(10, 23),
			ShapeFactory.INST.createPoint(45, 2)));
	}

	@Override
	protected Stream<Runnable> cannotDoFixtures() {
		return Stream.of(
			() -> cmd = new MoveCtrlPoint(ShapeFactory.INST.createBezierCurve(
				List.of(ShapeFactory.INST.createPoint())), ShapeFactory.INST.createPoint(1, 2), true),
			() -> cmd = new MoveCtrlPoint(ShapeFactory.INST.createBezierCurve(
				List.of(ShapeFactory.INST.createPoint())), ShapeFactory.INST.createPoint(1, 2), false),
			() -> {
			commonCanDoFixture();
			point = shape.getFirstCtrlPtAt(1);
			cmd = new MoveCtrlPoint(shape, point, true);
			cmd.setNewCoord(ShapeFactory.INST.createPoint(10, Double.NaN));
		});
	}

	@Override
	protected Stream<Runnable> doCheckers() {
		return Stream.of(() -> assertThat(shape.getFirstCtrlPts().get(1)).isEqualTo(ShapeFactory.INST.createPoint(20, -50)),
			() -> assertThat(shape.getSecondCtrlPts().get(1)).isEqualTo(ShapeFactory.INST.createPoint(-100, 500)));
	}

	@Override
	protected void commonDoCheckers() {
		assertThat(shape.isModified()).isTrue();
	}

	@Override
	protected Stream<Runnable> undoCheckers() {
		return Stream.of(() -> assertThat(shape.getFirstCtrlPts().get(1)).isEqualTo(memento),
			() -> assertThat(shape.getSecondCtrlPts().get(1)).isEqualTo(memento));
	}

	@Override
	protected void commonUndoCheckers() {
		assertThat(shape.isModified()).isFalse();
	}

	@ParameterizedTest
	@MethodSource({"undoProvider"})
	void testTwoTimesUndo(final Runnable fixture, final Runnable undoOracle) {
		fixture.run();
		cmd.doIt();
		cmd.setNewCoord(ShapeFactory.INST.createPoint(20, 40));
		cmd.doIt();
		cmd.done();
		cmd.undo();
		undoOracle.run();
	}
}
