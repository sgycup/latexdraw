package net.sf.latexdraw.parser.svg;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TestSVGSVGElement extends TestBaseSVGElement {
	SVGSVGElement e;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		e = new SVGSVGElement(node, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testContructorFail() {
		new SVGSVGElement(null);
	}

	@Test
	public void testContructorOK() throws MalformedSVGDocument {
		new SVGSVGElement(node, null);
	}

	@Test
	public void testGetDefsNULL() {
		assertNull(e.getDefs());
	}

	@Test
	public void testGetDefs() throws MalformedSVGDocument, ParserConfigurationException {
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder builder = factory.newDocumentBuilder();
		final Document document = builder.newDocument();
		final Element n = document.createElement(SVGElements.SVG_SVG);
		n.setAttribute("xmlns", SVGDocument.SVG_NAMESPACE);
		n.appendChild(document.createElement(SVGElements.SVG_DEFS));
		e = new SVGSVGElement(n, null);
		assertNotNull(e.getDefs());
	}

	@Test
	public void testGetMetaNULL() {
		assertNull(e.getMeta());
	}

	@Test
	public void testGetMeta() throws MalformedSVGDocument, ParserConfigurationException {
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder builder = factory.newDocumentBuilder();
		final Document document = builder.newDocument();
		final Element n = document.createElement(SVGElements.SVG_SVG);
		n.setAttribute("xmlns", SVGDocument.SVG_NAMESPACE);
		n.appendChild(document.createElement(SVGElements.SVG_METADATA));
		e = new SVGSVGElement(n, null);
		assertNotNull(e.getMeta());
	}

	@Test
	public void testGetHeight() throws MalformedSVGDocument {
		node.setAttribute(SVGAttributes.SVG_HEIGHT, "200");
		final SVGSVGElement e = new SVGSVGElement(node, null);
		assertEquals(200d, e.getHeight(), 0.0001);
	}

	@Test
	public void testEnableRenderingOK() {
		assertTrue(e.enableRendering());
	}

	@Test
	public void testEnableRenderingKO0() throws MalformedSVGDocument {
		node.setAttribute(SVGAttributes.SVG_WIDTH, "-1");
		e = new SVGSVGElement(node, null);
		assertFalse(e.enableRendering());
	}

	@Test
	public void testEnableRenderingKO00() throws MalformedSVGDocument {
		node.setAttribute(SVGAttributes.SVG_WIDTH, "10");
		node.setAttribute(SVGAttributes.SVG_HEIGHT, "-1");
		e = new SVGSVGElement(node, null);
		assertFalse(e.enableRendering());
	}

	@Test
	public void testEnableRenderingOK1010() throws MalformedSVGDocument {
		node.setAttribute(SVGAttributes.SVG_WIDTH, "10");
		node.setAttribute(SVGAttributes.SVG_HEIGHT, "10");
		e = new SVGSVGElement(node, null);
		assertTrue(e.enableRendering());
	}

	@Test
	public void testGetY0() {
		assertEquals(0d, e.getY(), 0.0001);
	}

	@Test
	public void testGetY() throws MalformedSVGDocument {
		node.setAttribute(SVGAttributes.SVG_Y, "-10");
		e = new SVGSVGElement(node, null);
		assertEquals(-10d, e.getY(), 0.0001);
	}

	@Test
	public void testGetX0() {
		assertEquals(0d, e.getX(), 0.0001);
	}

	@Test
	public void testGetX() throws MalformedSVGDocument {
		node.setAttribute(SVGAttributes.SVG_X, "10");
		e = new SVGSVGElement(node, null);
		assertEquals(10d, e.getX(), 0.0001);
	}

	@Test
	public void testGetWidth() throws MalformedSVGDocument {
		node.setAttribute(SVGAttributes.SVG_WIDTH, "100");
		final SVGSVGElement e = new SVGSVGElement(node, null);
		assertEquals(100d, e.getWidth(), 0.0001);
	}

	@Test
	public void testVersion() throws MalformedSVGDocument {
		node.setAttribute(SVGAttributes.SVG_VERSION, "1.1");
		final SVGSVGElement e = new SVGSVGElement(node, null);
		assertEquals("1.1", e.getVersion());
	}

	@Override
	public String getNameNode() {
		return SVGElements.SVG_SVG;
	}
}