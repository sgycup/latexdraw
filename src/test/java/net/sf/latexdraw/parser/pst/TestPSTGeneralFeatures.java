package net.sf.latexdraw.parser.pst;

import net.sf.latexdraw.model.ShapeFactory;
import net.sf.latexdraw.model.api.shape.Dot;
import net.sf.latexdraw.model.api.shape.Group;
import net.sf.latexdraw.model.api.shape.Rectangle;
import net.sf.latexdraw.model.api.shape.Shape;
import net.sf.latexdraw.model.api.shape.Text;
import net.sf.latexdraw.view.latex.DviPsColors;
import net.sf.latexdraw.view.pst.PSTricksConstants;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestPSTGeneralFeatures extends TestPSTParser {
	@Test
	void testBug756733() {
		// https://bugs.launchpad.net/latexdraw/+bug/756733
		parser("\\psset{unit=5}\\psdot(1,1)\\psdot(1,10pt)");
		Dot dot = getShapeAt(0);
		assertEquals(5d * Shape.PPC, dot.getX(), 0.000001);
		assertEquals(5d * -Shape.PPC, dot.getY(), 0.000001);
		dot = getShapeAt(1);
		assertEquals(5d * Shape.PPC, dot.getX(), 0.000001);
		assertEquals(10d * -Shape.PPC / PSTricksConstants.CM_VAL_PT, dot.getY(), 0.000001);
	}

	@Test
	void testBugpssetsetOfShapes() {
		parser("\\psframe(0.5,0.5)(1.5,1.5)\\psdot[linewidth=1cm,dotsize=1](1,1)\\psset{unit=2}\\psframe(0.5,0.5)(1.5,1.5)\\psdot(2," + "2)");
		Rectangle rec = getShapeAt(0);
		assertEquals(0.5 * Shape.PPC, rec.getX(), 0.000001);
		assertEquals(-0.5 * Shape.PPC, rec.getY(), 0.000001);
		assertEquals(Shape.PPC, rec.getWidth(), 0.000001);
		assertEquals(Shape.PPC, rec.getHeight(), 0.000001);

		Dot dot = getShapeAt(1);
		assertEquals(Shape.PPC, dot.getX(), 0.000001);
		assertEquals(-Shape.PPC, dot.getY(), 0.000001);

		rec = getShapeAt(2);
		assertEquals(0.5 * 2d * Shape.PPC, rec.getX(), 0.000001);
		assertEquals(-0.5 * 2d * Shape.PPC, rec.getY(), 0.000001);
		assertEquals(2d * Shape.PPC, rec.getWidth(), 0.000001);
		assertEquals(2d * Shape.PPC, rec.getHeight(), 0.000001);

		dot = getShapeAt(3);
		assertEquals(2d * 2d * Shape.PPC, dot.getX(), 0.000001);
		assertEquals(-2d * 2d * Shape.PPC, dot.getY(), 0.000001);
	}

	@Test
	void testPssetlinewidth() {
		parser("\\psset{linewidth=2cm}\\psframe(10,10)");
		final Rectangle rec = getShapeAt(0);
		assertEquals(2d * Shape.PPC, rec.getThickness(), 0.0001);
	}

	@Test
	void testUnknownCommand() {
		listener = new PSTLatexdrawListener();
		parser("\\fuhfisduf");
		final Text txt = getShapeAt(0);
		assertEquals("\\fuhfisduf", txt.getText());
	}

	@Test
	void testBeginCenterokWithBeginPsPicture() {
		parser("\\begin{center}\\begin{pspicture}(1,1)\\psline(1,1)(1,0)\\end{pspicture}\\end{center}");
		assertEquals(1, parsedShapes.size());
	}

	@Test
	void testBeginCenterokWithBeginPsPictureWithNoCoord() {
		parser("\\begin{center}\\begin{pspicture}(,)\\psline(1,1)(1,0)\\end{pspicture}\\end{center}");
		assertEquals(1, parsedShapes.size());
	}

	@Test
	void testBeginCenterok() {
		parser("\\begin{center}\\psline(1,1)(1,0)\\end{center}");
		assertEquals(1, parsedShapes.size());
	}

	@Test
	void testPsscalebox() {
		parser("\\psscalebox{1 1}{\\psframe(2,3)(5,1)}");
		assertEquals(1, parsedShapes.size());
	}

	@Test
	void testScalebox() {
		parser("\\scalebox{0.75}{\\psframe(2,3)(5,1)}");
		assertEquals(1, parsedShapes.size());
	}

	@Test
	void testTwoShapesDoNotShareTheirParameters() {
		parser("\\psframe[linecolor=blue,fillstyle=solid,fillcolor=red](6,0)(4,-1)\\psbezier(1, 0)(2,0)(4,0)(4,0)");
		assertEquals(2, parsedShapes.size());
		assertFalse(getShapeAt(1).isFilled());
		assertNotEquals(getShapeAt(0).getFillingCol(), getShapeAt(1).getFillingCol());
		assertNotEquals(getShapeAt(0).getLineColour(), getShapeAt(1).getLineColour());
	}

	@Test
	void testDefineColorhsb() {
		parser("\\definecolor{col}{hsb}{1, 0, 0.5}\\psframe[linecolor=col](6,0)(4,-1)");
		assertEquals(ShapeFactory.INST.createColorHSB(1d, 0d, 0.5), getShapeAt(0).getLineColour());
	}

	@Test
	void testDefineColorHTML() {
		parser("\\definecolor{col}{HTML}{#001eff}\\psframe[linecolor=col](6,0)(4,-1)");
		assertEquals(DviPsColors.INSTANCE.convertHTML2rgb("#001eff"), getShapeAt(0).getLineColour());
	}

	@Test
	void testDefineColorgray() {
		parser("\\definecolor{col}{gray}{0.4}\\psframe[linecolor=col](6,0)(4,-1)");
		assertEquals(ShapeFactory.INST.createColor(0.4, 0.4, 0.4), getShapeAt(0).getLineColour());
	}

	@Test
	void testDefineColorcmyk() {
		parser("\\definecolor{col}{cmyk}{0.2,0.6,0.5,0.3}\\psframe[linecolor=col](6,0)(4,-1)");
		assertEquals(DviPsColors.INSTANCE.convertcmyk2rgb(0.2, 0.6, 0.5, 0.3), getShapeAt(0).getLineColour());
	}

	@Test
	void testDefineColorcmy() {
		parser("\\definecolor{col}{cmy}{0.2,0.6,0.5}\\psframe[linecolor=col](6,0)(4,-1)");
		assertEquals(ShapeFactory.INST.createColor(0.8, 0.4, 0.5), getShapeAt(0).getLineColour());
	}

	@Test
	void testDefineColorRGB() {
		parser("\\definecolor{col}{RGB}{100,50,200}\\psframe[linecolor=col](6,0)(4,-1)");
		assertEquals(DviPsColors.INSTANCE.convertRGB2rgb(100d, 50d, 200d), getShapeAt(0).getLineColour());
	}

	@Test
	void testDefineColorrgb() {
		parser("\\definecolor{col}{rgb}{0.5,0.51,0.52}\\psframe[linecolor=col](6,0)(4,-1)");
		assertEquals(ShapeFactory.INST.createColor(0.5, 0.51, 0.52), getShapeAt(0).getLineColour());
	}

	@Test
	void testParseOrigin() {
		parser("\\psframe[origin={1,2}](5,10)");
	}

	@Test
	void testParseSwapaxes() {
		parser("\\psframe[swapaxes=true](5,10)");
	}

	@Test
	void testParseUnkownParam() {
		listener = new PSTLatexdrawListener();
		parser("\\psframe[foobar=true](5,10)");
		assertTrue(getShapeAt(0) instanceof Rectangle);
	}

	@Test
	void testParseUnkownColor() {
		listener = new PSTLatexdrawListener();
		parser("\\psframe[linecolor=col23](5,10)");
		assertTrue(getShapeAt(0) instanceof Rectangle);
	}

	@Test
	void testFlatShapeSingle() {
		parser("{\\psline(5,10)}");
		assertThat(parsedShapes).hasSize(1);
	}

	@Test
	void testFlatShapeNested() {
		parser("{\\psline(5,10) \\psline(5,10) {\\psline(5,10) \\psline(5,10) {\\psline(5,10) \\psline(5,10)}}}");
		assertThat(parsedShapes).hasSize(3);
		assertThat(parsedShapes.get(0)).isInstanceOf(Group.class);
		assertThat(parsedShapes.get(1)).isInstanceOf(Group.class);
		assertThat(parsedShapes.get(2)).isInstanceOf(Group.class);
	}

	@Test
	void testFlatShapeEmpty() {
		parser("{{} {{}}}");
		assertThat(parsedShapes).isEmpty();
	}

	@ParameterizedTest
	@ValueSource(doubles = {-1d, 2d})
	void testStrokeopacityKO(final double op) {
		parser("{\\psline[strokeopacity=" + op + "](5,10)(5,11)(5,12)}");
		assertThat(parsedShapes.get(0).getLineColour().getO()).isEqualTo(1d, Offset.offset(0.000001));
	}

	@Test
	void testStrokeopacityOK() {
		parser("{\\psline[strokeopacity=0.33](5,10)(5,11)(5,12)}");
		assertThat(parsedShapes.get(0).getLineColour().getO()).isEqualTo(0.33d, Offset.offset(0.000001));
	}

	@ParameterizedTest
	@ValueSource(doubles = {-1d, 2d})
	void testOpacityKO(final double op) {
		parser("{\\psline[fillstyle=solid, opacity=" + op + "](5,10)(5,11)(5,12)}");
		assertThat(parsedShapes.get(0).getFillingCol().getO()).isEqualTo(1d, Offset.offset(0.000001));
	}

	@Test
	void testOpacityOK() {
		parser("{\\psline[fillstyle=solid, opacity=0.33](5,10)(5,11)(5,12)}");
		assertThat(parsedShapes.get(0).getFillingCol().getO()).isEqualTo(0.33d, Offset.offset(0.000001));
	}
}
