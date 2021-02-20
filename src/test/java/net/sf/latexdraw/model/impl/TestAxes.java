package net.sf.latexdraw.model.impl;

import net.sf.latexdraw.HelperTest;
import net.sf.latexdraw.data.DoubleData;
import net.sf.latexdraw.model.ShapeFactory;
import net.sf.latexdraw.model.api.shape.ArrowStyle;
import net.sf.latexdraw.model.api.shape.Axes;
import net.sf.latexdraw.model.api.shape.AxesStyle;
import net.sf.latexdraw.model.api.shape.Circle;
import net.sf.latexdraw.model.api.shape.PlottingStyle;
import net.sf.latexdraw.model.api.shape.Rectangle;
import net.sf.latexdraw.model.api.shape.Shape;
import net.sf.latexdraw.model.api.shape.StandardGrid;
import net.sf.latexdraw.model.api.shape.TicksStyle;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

@RunWith(Theories.class)
public class TestAxes implements HelperTest {
	Axes shape;
	Axes shape2;

	@Before
	public void setUp() {
		shape = ShapeFactory.INST.createAxes(ShapeFactory.INST.createPoint());
		shape2 = ShapeFactory.INST.createAxes(ShapeFactory.INST.createPoint());
	}

	@Test
	public void testIsTypeOf() {
		assertFalse(shape.isTypeOf(Rectangle.class));
		assertFalse(shape.isTypeOf(Circle.class));
		assertTrue(shape.isTypeOf(Shape.class));
		assertTrue(shape.isTypeOf(StandardGrid.class));
		assertTrue(shape.isTypeOf(Axes.class));
		assertTrue(shape.isTypeOf(shape.getClass()));
	}

	@Test
	public void testSetArrowStyleLast() {
		shape.setArrowStyle(ArrowStyle.BAR_IN, -1);
		assertEquals(ArrowStyle.BAR_IN, shape.getArrowStyle(-1));
		assertEquals(ArrowStyle.BAR_IN, shape.getArrowStyle(1));
	}

	@Test
	public void testConstructor3OK() {
		final Axes axes = ShapeFactory.INST.createAxes(ShapeFactory.INST.createPoint(10, -20));

		assertNotNull(axes.getPtAt(0));
		assertEqualsDouble(10d, axes.getPtAt(0).getX());
		assertEqualsDouble(-20d, axes.getPtAt(0).getY());
	}

	@Test
	public void testConstructor3NotOKNAN0() {
		final Axes axes = ShapeFactory.INST.createAxes(ShapeFactory.INST.createPoint(Double.NaN, 0));
		assertNotNull(axes.getPtAt(0));
		assertEqualsDouble(0d, axes.getPtAt(0).getX());
		assertEqualsDouble(0d, axes.getPtAt(0).getY());
	}

	@Test
	public void testConstructor3NotOK0NAN() {
		final Axes axes = ShapeFactory.INST.createAxes(ShapeFactory.INST.createPoint(0, Double.NaN));
		assertNotNull(axes.getPtAt(0));
		assertEqualsDouble(0d, axes.getPtAt(0).getX());
		assertEqualsDouble(0d, axes.getPtAt(0).getY());
	}

	@Test
	public void testConstructor3NotOKINF0() {
		final Axes axes = ShapeFactory.INST.createAxes(ShapeFactory.INST.createPoint(Double.POSITIVE_INFINITY, 0));
		assertNotNull(axes.getPtAt(0));
		assertEqualsDouble(0d, axes.getPtAt(0).getX());
		assertEqualsDouble(0d, axes.getPtAt(0).getY());
	}

	@Test
	public void testGetStep() {
		assertEqualsDouble(Shape.PPC, shape.getStep());
	}

	@Theory
	public void testGetSetIncrementX(@DoubleData final double val) {
		assumeTrue(val > 0d);

		shape.setIncrementX(val);
		assertEqualsDouble(val, shape.getIncrementX());
	}

	@Theory
	public void testGetSetIncrementXKO(@DoubleData(vals = {0d, -10d}, bads = true) final double val) {
		shape.setIncrementX(10d);
		shape.setIncrementX(val);
		assertEqualsDouble(10d, shape.getIncrementX());
	}

	@Theory
	public void testGetSetIncrementY(@DoubleData final double val) {
		assumeTrue(val > 0d);

		shape.setIncrementY(val);
		assertEqualsDouble(val, shape.getIncrementY());
	}

	@Theory
	public void testGetSetIncrementYKO(@DoubleData(vals = {0d, -10d}, bads = true) final double val) {
		shape.setIncrementY(10d);
		shape.setIncrementY(val);
		assertEqualsDouble(10d, shape.getIncrementY());
	}

	@Theory
	public void testGetSetDistLabelsX(@DoubleData final double val) {
		assumeTrue(val > 0d);

		shape.setDistLabelsX(val);
		assertEqualsDouble(val, shape.getDistLabelsX());
	}

	@Theory
	public void testGetSetDistLabelsXKO(@DoubleData(vals = {0d, -10d}, bads = true) final double val) {
		shape.setDistLabelsX(10d);
		shape.setDistLabelsX(val);
		assertEqualsDouble(10d, shape.getDistLabelsX());
	}

	@Theory
	public void testGetSetDistLabelsY(@DoubleData final double val) {
		assumeTrue(val > 0d);

		shape.setDistLabelsY(val);
		assertEqualsDouble(val, shape.getDistLabelsY());
	}

	@Theory
	public void testGetSetDistLabelsYKO(@DoubleData(vals = {0d, -10d}, bads = true) final double val) {
		shape.setDistLabelsY(10d);
		shape.setDistLabelsY(val);
		assertEqualsDouble(10d, shape.getDistLabelsY());
	}

	@Theory
	public void testGetSetLabelsDisplayed(final PlottingStyle style) {
		shape.setLabelsDisplayed(style);
		assertEquals(style, shape.getLabelsDisplayed());
	}


	@Test
	public void testGetSetLabelsDisplayedKO() {
		shape.setLabelsDisplayed(PlottingStyle.ALL);
		shape.setLabelsDisplayed(null);
		assertEquals(PlottingStyle.ALL, shape.getLabelsDisplayed());
	}

	@Theory
	public void testGetSetLabelsDisplayed(final boolean origin) {
		shape.setShowOrigin(origin);
		assertEquals(origin, shape.isShowOrigin());
	}

	@Theory
	public void testGetSetTicksDisplayed(final PlottingStyle style) {
		shape.setTicksDisplayed(style);
		assertEquals(style, shape.getTicksDisplayed());
	}

	@Theory
	public void testGetSetTicksStyle(final TicksStyle style) {
		shape.setTicksStyle(style);
		assertEquals(style, shape.getTicksStyle());
	}

	@Theory
	public void testGetSetTicksSize(@DoubleData final double val) {
		assumeTrue(val > 0d);

		shape.setTicksSize(val);
		assertEqualsDouble(val, shape.getTicksSize());
	}

	@Theory
	public void testGetSetTicksSizeKO(@DoubleData(vals = {0d, -10d}, bads = true) final double val) {
		shape.setTicksSize(10d);
		shape.setTicksSize(val);
		assertEqualsDouble(10d, shape.getTicksSize());
	}

	@Theory
	public void testGetSetAxesStyle(final AxesStyle style) {
		shape.setAxesStyle(style);
		assertEquals(style, shape.getAxesStyle());
	}

	@Test
	public void testCopy() {
		shape2.setIncrementX(24);
		shape2.setIncrementY(28);
		shape2.setAxesStyle(AxesStyle.FRAME);
		shape2.setTicksStyle(TicksStyle.BOTTOM);
		shape2.setDistLabelsX(12);
		shape2.setDistLabelsY(112);
		shape2.setShowOrigin(false);
		shape2.setTicksDisplayed(PlottingStyle.NONE);
		shape2.setTicksSize(34);

		shape.copy(shape2);

		assertEqualsDouble(shape2.getIncrementX(), shape.getIncrementX());
		assertEqualsDouble(shape2.getIncrementY(), shape.getIncrementY());
		assertEquals(shape2.getAxesStyle(), shape.getAxesStyle());
		assertEqualsDouble(shape2.getTicksSize(), shape.getTicksSize());
		assertEquals(shape2.getTicksDisplayed(), shape.getTicksDisplayed());
		assertEquals(shape2.getTicksStyle(), shape.getTicksStyle());
		assertEqualsDouble(shape2.getDistLabelsX(), shape.getDistLabelsX());
		assertEqualsDouble(shape2.getDistLabelsY(), shape.getDistLabelsY());
		assertEquals(shape2.isShowOrigin(), shape.isShowOrigin());
	}

	@Test
	public void testDuplicate() {
		shape.setIncrementX(24);
		shape.setIncrementY(28);
		shape.setAxesStyle(AxesStyle.FRAME);
		shape.setTicksStyle(TicksStyle.BOTTOM);
		shape.setDistLabelsX(12);
		shape.setDistLabelsY(112);
		shape.setShowOrigin(false);
		shape.setTicksDisplayed(PlottingStyle.NONE);
		shape.setTicksSize(34);

		final Axes s2 = shape.duplicate();

		assertNotNull(s2);
		assertEqualsDouble(s2.getIncrementX(), shape.getIncrementX());
		assertEqualsDouble(s2.getIncrementY(), shape.getIncrementY());
		assertEquals(s2.getAxesStyle(), shape.getAxesStyle());
		assertEqualsDouble(s2.getTicksSize(), shape.getTicksSize());
		assertEquals(s2.getTicksDisplayed(), shape.getTicksDisplayed());
		assertEquals(s2.getTicksStyle(), shape.getTicksStyle());
		assertEqualsDouble(s2.getDistLabelsX(), shape.getDistLabelsX());
		assertEqualsDouble(s2.getDistLabelsY(), shape.getDistLabelsY());
		assertEquals(s2.isShowOrigin(), shape.isShowOrigin());
	}

	@Theory
	public void testGetBottomLeftPoint(@DoubleData final double x, @DoubleData final double y) {
		shape.setPosition(x, y);
		assertEqualsDouble(x, shape.getBottomLeftPoint().getX());
		assertEqualsDouble(y, shape.getBottomLeftPoint().getY());
	}

	@Theory
	public void testGetBottomRightPoint(@DoubleData final double x, @DoubleData final double y, @DoubleData final double endX,
										@DoubleData final double startY) {
		assumeTrue(endX > -200d);
		assumeTrue(startY < 75d);

		shape.setPosition(x, y);
		shape.setGridStart(-200d, startY);
		shape.setGridEnd(endX, 75d);

		assertEqualsDouble(x + Shape.PPC * endX, shape.getBottomRightPoint().getX());
		assertEqualsDouble(y - Shape.PPC * startY, shape.getBottomRightPoint().getY());
	}

	@Theory
	public void testGetTopLeftPoint(@DoubleData final double x, @DoubleData final double y, @DoubleData final double startX,
									@DoubleData final double endY) {
		assumeTrue(endY > -100d);
		assumeTrue(startX < 50d);

		shape.setPosition(x, y);
		shape.setGridStart(startX, -100d);
		shape.setGridEnd(50d, endY);

		assertEqualsDouble(x + startX * Shape.PPC, shape.getTopLeftPoint().getX());
		assertEqualsDouble(y - endY * Shape.PPC, shape.getTopLeftPoint().getY());
	}

	@Theory
	public void testGetTopRightPoint(@DoubleData final double startX, @DoubleData final double endX,
									@DoubleData final double startY, @DoubleData final double endY) {
		assumeTrue(endY > startY);
		assumeTrue(startX < endX);

		shape.setPosition(0d, 0d);
		shape.setGridStart(startX, startY);
		shape.setGridEnd(endX, endY);

		assertEqualsDouble(Shape.PPC * (endX - startX), shape.getTopRightPoint().getX());
		assertEqualsDouble(-Shape.PPC * (endY - startY), shape.getTopRightPoint().getY());
	}

	@Test
	public void testMirrorHorizontal() {
		shape.setPosition(0d, 0d);
		shape.setGridStart(0d, 0d);
		shape.setGridEnd(10d, 10d);

		shape.mirrorHorizontal(Shape.PPC * 10d);
		assertEqualsDouble(Shape.PPC * 10d, shape.getPosition().getX());
		assertEqualsDouble(0d, shape.getPosition().getY());
	}

	@Test
	public void testMirrorVertical() {
		shape.setPosition(0d, 0d);
		shape.setGridStart(0d, 0d);
		shape.setGridEnd(10d, 10d);

		shape.mirrorVertical(-Shape.PPC * 10d);
		assertEqualsDouble(0d, shape.getPosition().getX());
		assertEqualsDouble(-Shape.PPC * 10d, shape.getPosition().getY());
	}
}
