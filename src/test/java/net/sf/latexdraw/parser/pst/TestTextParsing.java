package net.sf.latexdraw.parser.pst;

import net.sf.latexdraw.model.ShapeFactory;
import net.sf.latexdraw.model.api.shape.Dot;
import net.sf.latexdraw.model.api.shape.Text;
import net.sf.latexdraw.view.latex.DviPsColors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestTextParsing extends TestPSTParser {
	@BeforeEach
	void setUpText() {
		listener = new PSTLatexdrawListener();
	}

	@Test
	public void testBracket() {
		parser("{( )}");
		final Text txt = getShapeAt(0);
		assertEquals("( )", txt.getText());
	}

	@ParameterizedTest
	@ValueSource(strings = {"foo", "\\bf coucou", "\\sc coucou", "\\sl coucou", "\\it coucou", "\\scshape coucou", "\\slshape coucou",
		"\\itshape coucou", "\\upshape coucou", "\\bfseries coucou", "\\mdseries coucou", "\\ttfamily coucou", "\\sffamily coucou", "\\rmfamily coucou",
		"\\textsf{coucou}", "\\foo command unknown", "\\(coucou\\)", "121", "121.1248 -.1 ++1", "foo \\bloodyCmd $math formula_{r}$ bar",
		"\\[foo_{test}\\]", "\\[\\|\\]", "\\[\\mathcal{M}\\]", "\\[coucou\\]", "\\(foo_{test}\\)", "\\(\\|\\)", "\\(\\mathcal{M}\\)",
		"$foo_{test}$", "$\\|$", "$\\mathcal{M}$", "$coucou$", "\\t e", "\\t{ee}", "\\b e", "\\= e", "\\~ e", "\\#", "\\.{ee}", "\\={ee}", "\\~{ee}",
		"\\^ e", "\\^{ee}", "\\'{ee}", "\\`{ee}", "\\\"{ee}", "\\\" e", "\\` a", "\\@", "\\. o", "\\/", "\\,", "\\*", "\\' e", "\\\" e", "\\$", "\\{",
		"\\}", "\\&", "\\_", "\\%", "\\\\", "word second word"})
	public void testSingleText(final String txt) {
		parser(txt);
		assertEquals(1, parsedShapes.size());
		assertEquals(txt, ((Text) getShapeAt(0)).getText());
	}

	@Test
	public void testSeveralTextChuncks() {
		parser("{foo} {barr}");
		assertEquals(2, parsedShapes.size());
		assertTrue(getShapeAt(0) instanceof Text);
		assertEquals("foo", ((Text) getShapeAt(0)).getText());
		assertTrue(getShapeAt(1) instanceof Text);
		assertEquals("barr", ((Text) getShapeAt(1)).getText());
	}

	@Test
	public void testTextbugParsingSeveralRputCmds() {
		parser("\\rput(0.9,0.6){aa}\\rput(7.4,0){bb}\\rput(1,0){cc}");
		assertEquals(3, parsedShapes.size());
		assertEquals("aa", ((Text) getShapeAt(0)).getText());
		assertEquals("bb", ((Text) getShapeAt(1)).getText());
		assertEquals("cc", ((Text) getShapeAt(2)).getText());
	}

	@Test
	public void testTextWithSpecialColour() {
		parser("\\definecolor{color0}{rgb}{0.5,0.5,0.5}\\color{color0}foo");
		final Text txt = getShapeAt(0);
		assertNotNull(txt);
		assertEquals(ShapeFactory.INST.createColor(0.5, 0.5, 0.5), txt.getLineColour());
	}

	@Test
	public void testBug7220753() {
		// https://bugs.launchpad.net/latexdraw/+bug/722075
		parser("\\textcolor{blue}{xyz} foobar");
		assertEquals(2, parsedShapes.size());
		final Text text = getShapeAt(1);
		assertEquals("foobar", text.getText());
		assertEquals(DviPsColors.BLACK, text.getLineColour());
	}

	@Test
	public void testBug7220752() {
		// https://bugs.launchpad.net/latexdraw/+bug/722075
		parser("\\textcolor{blue}{xyz}");
		assertEquals(1, parsedShapes.size());
		final Text text = getShapeAt(0);
		assertEquals("xyz", text.getText());
		assertEquals(DviPsColors.BLUE, text.getLineColour());
	}

	@Test
	public void testBug7220751() {
		// https://bugs.launchpad.net/latexdraw/+bug/722075
		parser("\\color{blue} xyz");
		assertEquals(1, parsedShapes.size());
		final Text text = getShapeAt(0);
		assertEquals("xyz", text.getText());
		assertEquals(DviPsColors.BLUE, text.getLineColour());
	}

	@Test
	public void testBug911816() {
		// https://bugs.launchpad.net/latexdraw/+bug/911816
		parser("\\psframebox{$E=mc^2$}");
		assertEquals(1, parsedShapes.size());
		final Text text = getShapeAt(0);
		assertEquals("\\psframebox{$E=mc^2$}", text.getText());
	}

	@Test
	public void testParseTexttiny() {
		parser("\\rput(1,2){\\tiny coucou}");
		assertEquals(1, parsedShapes.size());
		final Text text = getShapeAt(0);
		assertEquals("\\tiny coucou", text.getText());
	}

	@Test
	public void testParseTextfootnotesize() {
		parser("\\rput(1,2){\\footnotesize coucou}");
		assertEquals(1, parsedShapes.size());
		final Text text = getShapeAt(0);
		assertEquals("\\footnotesize coucou", text.getText());
	}

	@Test
	public void testParse1WordBracketedInto1TextShape() {
		parser("{ foo }");
		assertEquals(1, parsedShapes.size());
		assertTrue(getShapeAt(0) instanceof Text);
		assertEquals("foo", ((Text) getShapeAt(0)).getText());
	}

	@Test
	public void testParseMixedTextAndShapeInto2TextShapesAnd1Shape() {
		parser("foo bar \\psdot(1,1) foo $math formula_{r}$ bar");
		assertEquals(2, parsedShapes.size());
		assertTrue(getShapeAt(0) instanceof Dot);
		assertTrue(getShapeAt(1) instanceof Text);
		assertEquals("foo bar foo $math formula_{r}$ bar", ((Text) getShapeAt(1)).getText());
	}
}
