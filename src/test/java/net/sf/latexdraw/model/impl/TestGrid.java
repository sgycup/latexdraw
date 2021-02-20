package net.sf.latexdraw.model.impl;

import net.sf.latexdraw.HelperTest;
import net.sf.latexdraw.data.DoubleData;
import net.sf.latexdraw.model.ShapeFactory;
import net.sf.latexdraw.model.api.shape.Circle;
import net.sf.latexdraw.model.api.shape.Grid;
import net.sf.latexdraw.model.api.shape.PositionShape;
import net.sf.latexdraw.model.api.shape.Rectangle;
import net.sf.latexdraw.model.api.shape.Shape;
import net.sf.latexdraw.model.api.shape.StandardGrid;
import net.sf.latexdraw.view.latex.DviPsColors;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.experimental.theories.suppliers.TestedOn;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

@RunWith(Theories.class)
public class TestGrid implements HelperTest {
	Grid shape;

	@Before
	public void setUp() {
		shape = ShapeFactory.INST.createGrid(ShapeFactory.INST.createPoint());
	}

	@Theory
	public void testGetStep(@DoubleData final double value) {
		assumeTrue(value > 0d);

		shape.setUnit(value);
		assertEqualsDouble(value * Shape.PPC, shape.getStep());
	}

	@Theory
	public void testGetStepKO(@DoubleData(bads = true, vals = {0d, -1d}) final double value) {
		shape.setUnit(11);
		shape.setUnit(value);
		assertEqualsDouble(11d * Shape.PPC, shape.getStep());
	}

	@Theory
	public void testIsSetXLabelSouth(final boolean value) {
		shape.setXLabelSouth(value);
		assertEquals(value, shape.isXLabelSouth());
	}

	@Theory
	public void testIsSetYLabelWest(final boolean value) {
		shape.setYLabelWest(value);
		assertEquals(value, shape.isYLabelWest());
	}

	@Theory
	public void testGetSetGridDots(@TestedOn(ints = {0, 1, 10}) final int value) {
		shape.setGridDots(value);
		assertEquals(value, shape.getGridDots());
	}

	@Test
	public void testGetSetGridDotsKO() {
		shape.setGridDots(11);
		shape.setGridDots(-1);
		assertEquals(11, shape.getGridDots());
	}

	@Test
	public void testGetSetGridLabelsColor() {
		shape.setGridLabelsColour(DviPsColors.BLUE);
		assertEquals(DviPsColors.BLUE, shape.getGridLabelsColour());
	}

	@Theory
	public void testGetSetGridWidth(@DoubleData final double value) {
		assumeTrue(value > 0d);

		shape.setGridWidth(value);
		assertEqualsDouble(value, shape.getGridWidth());
	}

	@Theory
	public void testGetSetGridWidthKO(@DoubleData(bads = true, vals = {-1d, 0d}) final double value) {
		shape.setGridWidth(11d);
		shape.setGridWidth(value);
		assertEqualsDouble(11d, shape.getGridWidth());
	}

	@Test
	public void testGetSetSubGridColor() {
		shape.setSubGridColour(DviPsColors.RED);
		assertEquals(DviPsColors.RED, shape.getSubGridColour());
	}

	@Theory
	public void testGetSetSubGridDiv(@TestedOn(ints = {0, 1, 10}) final int value) {
		shape.setSubGridDiv(value);
		assertEquals(value, shape.getSubGridDiv());
	}

	@Test
	public void testGetSetSubGridDivKO() {
		shape.setSubGridDiv(11);
		shape.setSubGridDiv(-1);
		assertEquals(11, shape.getSubGridDiv());
	}

	@Theory
	public void testGetSetSubGridDots(@TestedOn(ints = {0, 1, 10}) final int value) {
		shape.setSubGridDots(value);
		assertEquals(value, shape.getSubGridDots());
	}

	@Test
	public void testGetSetSubGridDotsKO() {
		shape.setSubGridDots(11);
		shape.setSubGridDots(-1);
		assertEquals(11, shape.getSubGridDots());
	}

	@Theory
	public void testGetSetSubGridWidth(@DoubleData final double value) {
		assumeTrue(value > 0d);

		shape.setSubGridWidth(value);
		assertEqualsDouble(value, shape.getSubGridWidth());
	}

	@Theory
	public void testGetSetSubGridWidthKO(@DoubleData(vals = {0d, -1d}, bads = true) final double value) {
		shape.setSubGridWidth(11d);
		shape.setSubGridWidth(value);
		assertEqualsDouble(11d, shape.getSubGridWidth());
	}

	@Theory
	public void testGetSetUnit(@DoubleData final double value) {
		assumeTrue(value > 0d);

		shape.setUnit(value);
		assertEqualsDouble(value, shape.getUnit());
	}

	@Theory
	public void testGetSetUnitKO(@DoubleData(vals = {0d, -1d}, bads = true) final double value) {
		shape.setUnit(11d);
		shape.setUnit(value);
		assertEqualsDouble(11d, shape.getUnit());
	}

	@Test
	public void testDuplicate() {
		shape.setGridDots(45);
		shape.setSubGridDots(55);
		shape.setGridLabelsColour(DviPsColors.CYAN);
		shape.setSubGridColour(DviPsColors.GREEN);
		shape.setUnit(0.6);
		shape.setGridWidth(12);
		shape.setSubGridWidth(24);
		shape.setSubGridDiv(32);
		shape.setXLabelSouth(false);
		shape.setYLabelWest(false);

		final Grid dup = shape.duplicate();

		assertEquals(dup.getGridDots(), shape.getGridDots());
		assertEquals(dup.getSubGridDiv(), shape.getSubGridDiv());
		assertEquals(dup.getGridLabelsColour(), shape.getGridLabelsColour());
		assertEquals(dup.getSubGridColour(), shape.getSubGridColour());
		assertEqualsDouble(dup.getUnit(), shape.getUnit());
		assertEqualsDouble(dup.getGridWidth(), shape.getGridWidth());
		assertEqualsDouble(dup.getSubGridWidth(), shape.getSubGridWidth());
		assertEquals(dup.getSubGridDiv(), shape.getSubGridDiv());
		assertFalse(dup.isXLabelSouth());
		assertFalse(dup.isYLabelWest());
	}

	@Test
	public void testGetBottomLeftPoint() {
		shape.setPosition(10, 20);
		assertEqualsDouble(10., shape.getBottomLeftPoint().getX());
		assertEqualsDouble(20., shape.getBottomLeftPoint().getY());
		shape.setPosition(-10, -20);
		assertEqualsDouble(-10., shape.getBottomLeftPoint().getX());
		assertEqualsDouble(-20., shape.getBottomLeftPoint().getY());
	}

	@Test
	public void testGetBottomRightPoint() {
		shape.setPosition(0, 0);
		shape.setGridStart(-200, -100);
		shape.setGridEnd(50, 75);
		shape.setUnit(2);

		assertEqualsDouble(2. * Shape.PPC * 50., shape.getBottomRightPoint().getX());
		assertEqualsDouble(-Shape.PPC * -100., shape.getBottomRightPoint().getY());
	}

	@Test
	public void testGetTopLeftPoint() {
		shape.setPosition(0, 0);
		shape.setGridStart(-200, -100);
		shape.setGridEnd(50, 75);
		shape.setUnit(2);

		assertEqualsDouble(Shape.PPC * -200., shape.getTopLeftPoint().getX());
		assertEqualsDouble(-2. * Shape.PPC * 75., shape.getTopLeftPoint().getY());
	}

	@Test
	public void testGetTopRightPoint() {
		shape.setPosition(0, 0);
		shape.setGridStart(-200, -100);
		shape.setGridEnd(50, 75);
		shape.setUnit(2);

		assertEqualsDouble(2. * Shape.PPC * 250., shape.getTopRightPoint().getX());
		assertEqualsDouble(-2. * Shape.PPC * 175., shape.getTopRightPoint().getY());
	}

	@Test
	public void testMirrorHorizontal() {
		shape.setPosition(0, 0);
		shape.setGridStart(0, 0);
		shape.setGridEnd(10, 10);
		shape.setUnit(1);

		shape.mirrorHorizontal(Shape.PPC * 10.);
		assertEqualsDouble(Shape.PPC * 10., shape.getPosition().getX());
		assertEqualsDouble(0., shape.getPosition().getY());
	}

	@Test
	public void testMirrorVertical() {
		shape.setPosition(0, 0);
		shape.setGridStart(0, 0);
		shape.setGridEnd(10, 10);
		shape.setUnit(1);

		shape.mirrorVertical(-Shape.PPC * 10.);
		assertEqualsDouble(0., shape.getPosition().getX());
		assertEqualsDouble(-Shape.PPC * 10., shape.getPosition().getY());
	}


	@Test
	public void testCopy() {
		final Grid shape2 = ShapeFactory.INST.createGrid(ShapeFactory.INST.createPoint());

		shape2.setGridDots(45);
		shape2.setSubGridDots(55);
		shape2.setGridLabelsColour(DviPsColors.CYAN);
		shape2.setSubGridColour(DviPsColors.GREEN);
		shape2.setUnit(0.6);
		shape2.setGridWidth(12);
		shape2.setSubGridWidth(24);
		shape2.setSubGridDiv(32);
		shape2.setXLabelSouth(false);
		shape2.setYLabelWest(false);

		shape.copy(shape2);

		assertEquals(shape2.getGridDots(), shape.getGridDots());
		assertEquals(shape2.getSubGridDiv(), shape2.getSubGridDiv());
		assertEquals(shape2.getGridLabelsColour(), shape.getGridLabelsColour());
		assertEquals(shape2.getSubGridColour(), shape.getSubGridColour());
		assertEqualsDouble(shape2.getUnit(), shape.getUnit());
		assertEqualsDouble(shape2.getGridWidth(), shape.getGridWidth());
		assertEqualsDouble(shape2.getSubGridWidth(), shape.getSubGridWidth());
		assertEquals(shape2.getSubGridDiv(), shape.getSubGridDiv());
		assertFalse(shape2.isXLabelSouth());
		assertFalse(shape2.isYLabelWest());
	}

	@Test
	public void testIsTypeOf() {
		assertFalse(shape.isTypeOf(Rectangle.class));
		assertFalse(shape.isTypeOf(Circle.class));
		assertTrue(shape.isTypeOf(Shape.class));
		assertTrue(shape.isTypeOf(PositionShape.class));
		assertTrue(shape.isTypeOf(StandardGrid.class));
		assertTrue(shape.isTypeOf(Grid.class));
		assertTrue(shape.isTypeOf(shape.getClass()));
	}

	@Test
	public void testConstructors() {
		final Grid grid = ShapeFactory.INST.createGrid(ShapeFactory.INST.createPoint());
		assertThat(grid.getGridEndX()).isGreaterThanOrEqualTo(grid.getGridStartX());
		assertThat(grid.getGridEndY()).isGreaterThanOrEqualTo(grid.getGridStartY());
		assertEqualsDouble(0d, grid.getPosition().getX());
		assertEqualsDouble(0d, grid.getPosition().getY());
	}
}
