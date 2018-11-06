package net.sf.latexdraw.parser.svg;

import java.text.ParseException;
import net.sf.latexdraw.parser.svg.parsers.SVGPathParser;
import net.sf.latexdraw.parser.svg.path.SVGPathSegLinetoHorizontal;
import net.sf.latexdraw.parser.svg.path.SVGPathSegMoveto;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestSVGPathSegLinetoHorizontal {
	SVGPathSegLinetoHorizontal seg;
	int cpt;

	@Before
	public void setUp() {
		seg = new SVGPathSegLinetoHorizontal(-1d, false);
		cpt = 0;
	}

	@Test
	public void testGetters() {
		assertEquals(-1d, seg.getX(), 0.0001);
		assertFalse(seg.isRelative());
	}

	@Test
	public void testToString() throws ParseException {
		final SVGPathSegMoveto m = new SVGPathSegMoveto(0d, 0d, false);
		final SVGPathParser parser = new SVGPathParser(m.toString() + " " + seg.toString(), pathSeg -> {
			if(pathSeg instanceof SVGPathSegMoveto && cpt == 0) {
				cpt++;
				return;
			}
			assertTrue(pathSeg instanceof SVGPathSegLinetoHorizontal);
			final SVGPathSegLinetoHorizontal seg2 = (SVGPathSegLinetoHorizontal) pathSeg;
			assertEquals(seg.getX(), seg2.getX(), 0.0001);
			assertEquals(seg.isRelative(), seg2.isRelative());
		});
		parser.parse();
	}
}