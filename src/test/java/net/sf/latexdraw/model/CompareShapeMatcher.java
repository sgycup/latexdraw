package net.sf.latexdraw.model;

import net.sf.latexdraw.HelperTest;
import net.sf.latexdraw.model.api.property.LineArcProp;
import net.sf.latexdraw.model.api.shape.Arc;
import net.sf.latexdraw.model.api.shape.Arrow;
import net.sf.latexdraw.model.api.shape.Axes;
import net.sf.latexdraw.model.api.shape.Dot;
import net.sf.latexdraw.model.api.shape.Grid;
import net.sf.latexdraw.model.api.shape.Plot;
import net.sf.latexdraw.model.api.shape.Shape;
import net.sf.latexdraw.model.api.shape.StandardGrid;
import net.sf.latexdraw.model.api.shape.Text;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertEquals;

public final class CompareShapeMatcher implements HelperTest {
	public static final CompareShapeMatcher INST = new CompareShapeMatcher();

	private CompareShapeMatcher() {
		super();
	}

	public void assertEqualShapeLineStyle(final Shape s1, final Shape s2) {
		if(s1.isThicknessable()) {
			assertEquals(s2.getThickness(), s1.getThickness(), 0.001);
		}
		if(s2.isLineStylable()) {
			assertEquals(s2.getLineStyle(), s1.getLineStyle());
			assertEquals(s2.getDashSepBlack(), s1.getDashSepBlack(), 0.001);
			assertEquals(s2.getDashSepWhite(), s1.getDashSepWhite(), 0.001);
			assertEquals(s2.getDotSep(), s1.getDotSep(), 0.001);
		}
		assertEquals(s2.getLineColour(), s1.getLineColour());
	}

	public void assertEqualShapeBorderMov(final Shape s1, final Shape s2) {
		assertEquals(s2.getBordersPosition(), s1.getBordersPosition());
	}

	public void assertEqualShapeDbleBorder(final Shape s1, final Shape s2) {
		if(s2.isDbleBorderable()) {
			assertEquals(s2.hasDbleBord(), s1.hasDbleBord());
			if(s2.hasDbleBord()) {
				assertEquals(s2.getDbleBordCol(), s1.getDbleBordCol());
				assertEquals(s2.getDbleBordSep(), s1.getDbleBordSep(), 0.001);
			}
		}
	}

	public void assertEqualShapeFill(final Shape s1, final Shape s2) {
		if(s1.isFillable()) {
			assertEquals(s2.isFilled(), s1.isFilled());
			assertEquals(s2.getFillingCol(), s1.getFillingCol());
		}
		if(s1.isInteriorStylable()) {
			assertEquals(s2.getFillingStyle(), s1.getFillingStyle());
		}
	}

	public void assertEqualShapeFillStyle(final Shape s1, final Shape s2) {
		if(s2.getFillingStyle().isGradient()) {
			assertEquals(s2.getGradAngle(), s1.getGradAngle(), 0.001);
			assertEquals(s2.getGradColEnd(), s1.getGradColEnd());
			assertEquals(s2.getGradColStart(), s1.getGradColStart());
			assertEquals(s2.getGradMidPt(), s1.getGradMidPt(), 0.001);
		}
		if(s2.getFillingStyle().isHatchings()) {
			assertEquals(s2.getHatchingsAngle(), s1.getHatchingsAngle(), 0.001);
			assertEquals(s2.getHatchingsCol(), s1.getHatchingsCol());
			assertEquals(s2.getHatchingsSep(), s1.getHatchingsSep(), 0.001);
			assertEquals(s2.getHatchingsWidth(), s1.getHatchingsWidth(), 0.001);
		}
	}

	public void assertEqualShapeShadow(final Shape s1, final Shape s2) {
		assertEquals(s2.hasShadow(), s1.hasShadow());
		if(s2.hasShadow()) {

			assertThat((s2.getShadowAngle() + 2d * Math.PI) % (2d * Math.PI))
				.isCloseTo((s1.getShadowAngle() + 2d * Math.PI) % (2d * Math.PI), within(0.0001));
			assertEquals(s2.getShadowSize(), s1.getShadowSize(), 0.001);
			assertEquals(s2.getShadowCol(), s1.getShadowCol());
		}
	}

	public void assertEqualShapeShowPts(final Shape s1, final Shape s2) {
		assertEquals(s2.isShowPts(), s1.isShowPts());
	}

	public void assertEqualShapeRotationAngle(final Shape s1, final Shape s2) {
		assertEquals(s2.getRotationAngle(), s1.getRotationAngle(), 0.001);
	}

	public void assertEqualArcParams(final Arc a1, final Arc a2) {
		assertEquals(a2.getAngleEnd(), a1.getAngleEnd(), 0.001);
		assertEquals(a2.getAngleStart(), a1.getAngleStart(), 0.001);
		assertEquals(a2.getArcStyle(), a1.getArcStyle());
	}

	public void assertEqualsLineArc(final LineArcProp la1, final LineArcProp la2) {
		assertEquals(la1.getLineArc(), la2.getLineArc(), 0.001);
	}

	public void assertEqualsText(final Text sh1, final Text sh2) {
		assertEquals(sh1.getText(), sh2.getText());
		assertEquals(sh1.getTextPosition(), sh2.getTextPosition());
	}

	public void assertEqualsArc(final Arc sh1, final Arc sh2) {
		assertEquals(sh1.getAngleStart(), sh2.getAngleStart(), 0.001);
		assertEquals(sh1.getAngleEnd(), sh2.getAngleEnd(), 0.001);
		assertEquals(sh1.getArcStyle(), sh2.getArcStyle());
	}

	public void assertEqualsDot(final Dot sh1, final Dot sh2) {
		assertEquals(sh1.getDotStyle(), sh2.getDotStyle());
		assertEquals(sh1.getDiametre(), sh2.getDiametre(), 0.001);
	}

	public void assertEqualsPlot(final Plot sh1, final Plot sh2) {
		assertEquals(sh1.getPlotStyle(), sh2.getPlotStyle());
		assertEquals(sh1.getPlotMinX(), sh2.getPlotMinX(), 0.001);
		assertEquals(sh1.getPlotMaxX(), sh2.getPlotMaxX(), 0.001);
		assertEquals(sh1.getNbPlottedPoints(), sh2.getNbPlottedPoints());
		assertEquals(sh1.getPlotEquation(), sh2.getPlotEquation());
	}

	public void assertEqualsGrid(final Grid sh1, final Grid sh2) {
		assertEquals(sh1.getGridLabelsColour(), sh2.getGridLabelsColour());
		assertEquals(sh1.getGridDots(), sh2.getGridDots());
		assertEquals(sh1.getSubGridColour(), sh2.getSubGridColour());
		assertEquals(sh1.getSubGridDots(), sh2.getSubGridDots());
		assertEquals(sh1.getSubGridDiv(), sh2.getSubGridDiv());
		assertEquals(sh1.isXLabelSouth(), sh2.isXLabelSouth());
		assertEquals(sh1.isYLabelWest(), sh2.isYLabelWest());
		assertEquals(sh1.getUnit(), sh2.getUnit(), 0.001);
		assertEquals(sh1.getSubGridWidth(), sh2.getSubGridWidth(), 0.001);
		assertEquals(sh1.getGridWidth(), sh2.getGridWidth(), 0.001);
	}

	public void assertEqualsAxes(final Axes sh1, final Axes sh2) {
		assertEquals(sh1.getLabelsDisplayed(), sh2.getLabelsDisplayed());
		assertEquals(sh1.getTicksDisplayed(), sh2.getTicksDisplayed());
		assertEquals(sh1.isShowOrigin(), sh2.isShowOrigin());
		assertEquals(sh1.getTicksStyle(), sh2.getTicksStyle());
		assertEquals(sh1.getAxesStyle(), sh2.getAxesStyle());
		assertEquals(sh1.getIncrementX(), sh2.getIncrementX(), 0.001);
		assertEquals(sh1.getIncrementY(), sh2.getIncrementY(), 0.001);
		assertEquals(sh1.getDistLabelsX(), sh2.getDistLabelsX(), 0.001);
		assertEquals(sh1.getDistLabelsY(), sh2.getDistLabelsY(), 0.001);
		assertEquals(sh1.getTicksSize(), sh2.getTicksSize(), 0.001);
	}

	public void assertEqualsStdGrid(final StandardGrid sh1, final StandardGrid sh2) {
		assertEquals(sh1.getGridEndX(), sh2.getGridEndX(), 0.001);
		assertEquals(sh1.getGridEndY(), sh2.getGridEndY(), 0.001);
		assertEquals(sh1.getGridStartX(), sh2.getGridStartX(), 0.001);
		assertEquals(sh1.getGridStartY(), sh2.getGridStartY(), 0.001);
		assertEquals(sh1.getOriginX(), sh2.getOriginX(), 0.001);
		assertEquals(sh1.getOriginY(), sh2.getOriginY(), 0.001);
		assertEquals(sh1.getLabelsSize(), sh2.getLabelsSize());
	}

	public void assertEqualsArrowStyle(final Arrow arr1, final Arrow arr2) {
		assertEquals(arr1.getArrowStyle(), arr2.getArrowStyle());
	}

	public void assertEqualsArrowArrow(final Arrow arr1, final Arrow arr2) {
		assertEquals(arr1.getArrowInset(), arr2.getArrowInset(), 0.001);
		assertEquals(arr1.getArrowLength(), arr2.getArrowLength(), 0.001);
		assertEquals(arr1.getArrowSizeDim(), arr2.getArrowSizeDim(), 0.001);
		assertEquals(arr1.getArrowSizeNum(), arr2.getArrowSizeNum(), 0.001);
	}

	public void assertEqualsArrowBar(final Arrow arr1, final Arrow arr2) {
		assertEquals(arr1.getTBarSizeNum(), arr2.getTBarSizeNum(), 0.001, arr1.getArrowStyle() + " " + arr2.getArrowStyle());
		assertEquals(arr1.getTBarSizeDim(), arr2.getTBarSizeDim(), 0.001, arr1.getArrowStyle() + " " + arr2.getArrowStyle());
	}

	public void assertEqualsArrowBracket(final Arrow arr1, final Arrow arr2) {
		assertEquals(arr1.getBracketNum(), arr2.getBracketNum(), 0.001, arr1.getArrowStyle() + " " + arr2.getArrowStyle());
	}

	public void assertEqualsArrowRBracket(final Arrow arr1, final Arrow arr2) {
		assertEquals(arr1.getRBracketNum(), arr2.getRBracketNum(), 0.001, arr1.getArrowStyle() + " " + arr2.getArrowStyle());
	}

	public void assertEqualsArrowCircleDisk(final Arrow arr1, final Arrow arr2) {
		assertEquals(arr1.getDotSizeNum(), arr2.getDotSizeNum(), 0.001, arr1.getArrowStyle() + " " + arr2.getArrowStyle());
		assertEquals(arr1.getDotSizeDim(), arr2.getDotSizeDim(), 0.001, arr1.getArrowStyle() + " " + arr2.getArrowStyle());
	}
}
