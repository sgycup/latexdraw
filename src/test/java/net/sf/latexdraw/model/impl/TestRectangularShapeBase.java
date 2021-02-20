package net.sf.latexdraw.model.impl;

import net.sf.latexdraw.HelperTest;
import net.sf.latexdraw.data.DoubleData;
import net.sf.latexdraw.data.RectangularData;
import net.sf.latexdraw.model.ShapeFactory;
import net.sf.latexdraw.model.api.shape.Point;
import net.sf.latexdraw.model.api.shape.RectangularShape;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.experimental.theories.suppliers.TestedOn;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(Theories.class)
public class TestRectangularShapeBase implements HelperTest {
	@Theory
	public void testCopy(@RectangularData final RectangularShape shape, @RectangularData final RectangularShape shape2) {
		shape2.setPosition(-120d, 200d);
		shape2.setWidth(385d);
		shape2.setHeight(300d);
		shape.copy(shape2);

		assertEquals(shape.getPoints(), shape2.getPoints());
	}

	@Theory
	public void testGetSetWidthOK(@RectangularData final RectangularShape sh, @DoubleData(vals = {0.001, 2d, 13.3d}) final double w) {
		sh.setPosition(0d, 0d);
		sh.setWidth(w);
		assertEqualsDouble(w, sh.getWidth());
	}

	@Theory
	public void testGetSetWidthKO(@RectangularData final RectangularShape sh, @DoubleData(vals = {0d, -1d}, bads = true) final double w) {
		final double value = sh.getWidth();
		sh.setWidth(w);
		assertEqualsDouble(value, sh.getWidth());
	}

	@Theory
	public void testGetSetHeightOK(@RectangularData final RectangularShape shape, @DoubleData(vals = {0.001, 2d, 13.3d}) final double h) {
		shape.setPosition(0d, 0d);
		shape.setHeight(h);
		assertEqualsDouble(h, shape.getHeight());
	}

	@Theory
	public void testGetSetHeightKO(@RectangularData final RectangularShape sh, @DoubleData(vals = {0d, -1d}, bads = true) final double h) {
		final double value = sh.getHeight();
		sh.setHeight(h);
		assertEqualsDouble(value, sh.getHeight());
	}

	@Theory
	public void testGetNbPoints(@RectangularData final RectangularShape shape) {
		assertEquals(4, shape.getNbPoints());
	}

	@Theory
	public void testGetPtAt(@RectangularData final RectangularShape shape, @TestedOn(ints = {-1, 0, 1, 2, 3}) final int i) {
		assertNotNull(shape.getPtAt(i));
	}

	@Theory
	public void testGetPtAtKO(@RectangularData final RectangularShape shape, @TestedOn(ints = {-2, 4}) final int i) {
		assertNull(shape.getPtAt(i));
	}

	@Theory
	public void testGetBottomLeftPoint(@RectangularData final RectangularShape shape) {
		shape.setPosition(-5, 0);
		shape.setWidth(10);
		shape.setHeight(20);

		assertEqualsDouble(-5d, shape.getBottomLeftPoint().getX());
		assertEqualsDouble(0d, shape.getBottomLeftPoint().getY());
	}

	@Theory
	public void testGetBottomRightPoint(@RectangularData final RectangularShape shape) {
		shape.setPosition(-15, 100);
		shape.setWidth(10);
		shape.setHeight(20);

		assertEqualsDouble(-5d, shape.getBottomRightPoint().getX());
		assertEqualsDouble(100d, shape.getBottomRightPoint().getY());
	}

	@Theory
	public void testGetTopLeftPoint(@RectangularData final RectangularShape shape) {
		shape.setPosition(20, 10);
		shape.setWidth(10);
		shape.setHeight(20);

		assertEqualsDouble(20d, shape.getTopLeftPoint().getX());
		assertEqualsDouble(-10d, shape.getTopLeftPoint().getY());
	}

	@Theory
	public void testGetTopRightPoint(@RectangularData final RectangularShape shape) {
		shape.setPosition(20, 10);
		shape.setWidth(10);
		shape.setHeight(20);

		assertEqualsDouble(30d, shape.getTopRightPoint().getX());
		assertEqualsDouble(-10d, shape.getTopRightPoint().getY());
	}

	@Theory
	public void testMirrorHorizontal(@RectangularData final RectangularShape shape) {
		final Point p1 = ShapeFactory.INST.createPoint(4, 1);
		final Point p2 = ShapeFactory.INST.createPoint(1, 3);

		shape.setPosition(p2);
		shape.setWidth(p1.getX() - p2.getX());
		shape.setHeight(p2.getY() - p1.getY());

		shape.mirrorHorizontal(shape.getGravityCentre().getX());
		assertEqualsDouble(1d, shape.getTopLeftPoint().getX());
		assertEqualsDouble(4d, shape.getTopRightPoint().getX());
		assertEqualsDouble(1d, shape.getTopLeftPoint().getY());
		assertEqualsDouble(3d, shape.getBottomLeftPoint().getY());
		assertEqualsDouble(1d, shape.getPtAt(0).getX());
		assertEqualsDouble(1d, shape.getPtAt(0).getY());
		assertEqualsDouble(4d, shape.getPtAt(2).getX());
		assertEqualsDouble(3d, shape.getPtAt(2).getY());
	}

	@Theory
	public void testMirrorVertical(@RectangularData final RectangularShape shape) {
		final Point p1 = ShapeFactory.INST.createPoint(4, 1);
		final Point p2 = ShapeFactory.INST.createPoint(1, 3);

		shape.setPosition(p2);
		shape.setWidth(p1.getX() - p2.getX());
		shape.setHeight(p2.getY() - p1.getY());

		shape.mirrorVertical(shape.getGravityCentre().getY());
		assertEqualsDouble(1d, shape.getTopLeftPoint().getX());
		assertEqualsDouble(4d, shape.getTopRightPoint().getX());
		assertEqualsDouble(1d, shape.getTopLeftPoint().getY());
		assertEqualsDouble(3d, shape.getBottomLeftPoint().getY());
		assertEqualsDouble(1d, shape.getPtAt(0).getX());
		assertEqualsDouble(1d, shape.getPtAt(0).getY());
		assertEqualsDouble(4d, shape.getPtAt(2).getX());
		assertEqualsDouble(3d, shape.getPtAt(2).getY());
	}

	@Theory
	public void testTranslate(@RectangularData final RectangularShape shape, @DoubleData final double tx, @DoubleData final double ty) {
		final Point p1 = ShapeFactory.INST.createPoint(3, 1);
		final Point p2 = ShapeFactory.INST.createPoint(1, 3);

		shape.setPosition(p2);
		shape.setWidth(p1.getX() - p2.getX());
		shape.setHeight(p2.getY() - p1.getY());
		shape.translate(tx, ty);

		assertEqualsDouble(p2.getX() + tx, shape.getPtAt(0).getX());
		assertEqualsDouble(p1.getX() + tx, shape.getPtAt(1).getX());
		assertEqualsDouble(p1.getX() + tx, shape.getPtAt(2).getX());
		assertEqualsDouble(p2.getX() + tx, shape.getPtAt(-1).getX());
		assertEqualsDouble(p1.getY() + ty, shape.getPtAt(0).getY());
		assertEqualsDouble(p1.getY() + ty, shape.getPtAt(1).getY());
		assertEqualsDouble(p2.getY() + ty, shape.getPtAt(2).getY());
		assertEqualsDouble(p2.getY() + ty, shape.getPtAt(-1).getY());
	}

	@Theory
	public void testTranslateKO(@RectangularData final RectangularShape shape, @DoubleData(vals = {0d}, bads = true) final double tx,
								@DoubleData(vals = {0d}, bads = true) final double ty) {
		final Point p1 = ShapeFactory.INST.createPoint(3, 1);
		final Point p2 = ShapeFactory.INST.createPoint(1, 3);

		shape.setPosition(p2);
		shape.setWidth(p1.getX() - p2.getX());
		shape.setHeight(p2.getY() - p1.getY());
		shape.translate(tx, ty);

		assertEqualsDouble(1d, shape.getPtAt(0).getX());
		assertEqualsDouble(3d, shape.getPtAt(1).getX());
		assertEqualsDouble(3d, shape.getPtAt(2).getX());
		assertEqualsDouble(1d, shape.getPtAt(-1).getX());
		assertEqualsDouble(1d, shape.getPtAt(0).getY());
		assertEqualsDouble(1d, shape.getPtAt(1).getY());
		assertEqualsDouble(3d, shape.getPtAt(2).getY());
		assertEqualsDouble(3d, shape.getPtAt(-1).getY());
	}
}
