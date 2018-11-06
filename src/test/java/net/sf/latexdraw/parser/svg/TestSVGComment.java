package net.sf.latexdraw.parser.svg;

import org.junit.Test;
import org.w3c.dom.Node;

import static org.junit.Assert.assertEquals;

public class TestSVGComment extends TestSVGText {
	@Override
	@Test
	public void testGetNodeType() {
		final SVGComment cdata = createSVGText("test", doc);
		assertEquals(Node.COMMENT_NODE, cdata.getNodeType());
	}

	@Override
	protected SVGComment createSVGText(final String str, final SVGDocument document) {
		return new SVGComment(str, document);
	}
}