/*
 * This file is part of LaTeXDraw.
 * Copyright (c) 2005-2020 Arnaud BLOUIN
 * LaTeXDraw is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later version.
 * LaTeXDraw is distributed without any warranty; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 */
package net.sf.latexdraw.parser.svg;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import javax.xml.parsers.DocumentBuilder;
import net.sf.latexdraw.util.BadaboomCollector;
import net.sf.latexdraw.util.SystemUtils;
import org.jetbrains.annotations.NonNls;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Defines an SVG document.
 * @author Arnaud BLOUIN
 */
public class SVGDocument implements Document {
	public static final String ACTION_NOT_IMPLEMENTED = "Action not implemented."; //NON-NLS
	public static final String SVG_NAMESPACE = "http://www.w3.org/2000/svg"; //NON-NLS

	/** The root of the SVG drawing. */
	private SVGSVGElement root;
	/** Defines if the document is standalone. */
	private boolean xmlStandalone;
	/** The document URI. */
	private String documentURI;
	/** The version of XML. */
	private String xmlVersion;
	/** The encoding of the document. */
	private String xmlEncoding;


	/**
	 * The constructor. An URI defines the location of the SVG document to parse. If the document is valid,
	 * the document is read an place in the <code>root</code> attribute.
	 * @param uri The file to parse.
	 * @throws IOException If the document cannot be opened.
	 * @throws IllegalArgumentException If an argument is not valid.
	 */
	public SVGDocument(final URI uri) throws IOException {
		super();
		if(uri == null) {
			throw new IllegalArgumentException();
		}

		final DocumentBuilder builder = SystemUtils.getInstance().createXMLDocumentBuilder()
			.orElseThrow(() -> new IllegalArgumentException());

		builder.setEntityResolver(new SVGEntityResolver());
		final Document doc = readXMLDocument(builder, uri.getPath())
			.orElseGet(() -> readXMLDocument(builder, "file:" + uri.getPath()).orElse(null));

		if(doc == null) {
			throw new IOException("Cannot open the XML document " + uri);
		}

		final NodeList nl;

		setXmlStandalone(doc.getXmlStandalone());
		setXmlVersion(doc.getXmlVersion());
		xmlEncoding = doc.getXmlEncoding();
		root = null;
		nl = doc.getChildNodes();
		Node n;

		for(int i = 0, size = nl.getLength(); i < size && root == null; i++) {
			n = nl.item(i);

			if(n instanceof Element && n.getNodeName().endsWith(SVGElements.SVG_SVG)) {
				root = new SVGSVGElement(this, nl.item(i));
			}
		}
	}


	/**
	 * Creates an SVG document with an empty SVG element.
	 */
	public SVGDocument() {
		super();
		setDocumentURI(null);
		setXmlVersion("1.1"); //NON-NLS
		setXmlStandalone(false);

		root = new SVGSVGElement(this);
	}

	private Optional<Document> readXMLDocument(final DocumentBuilder builder, @NonNls final String path) {
		try {
			return Optional.of(builder.parse(path));
		}catch(final SAXException | IOException ex) {
			return Optional.empty();
		}
	}

	@Override
	public String toString() {
		return "SVG Document:" + root; //NON-NLS
	}


	@Override
	public SVGSVGElement adoptNode(final Node source) {
		if(!(source instanceof SVGSVGElement)) {
			throw new DOMException(DOMException.TYPE_MISMATCH_ERR, "SVGSVGElement expected here.");
		}

		root = (SVGSVGElement) source;
		root.setOwnerDocument(this);

		return root;
	}


	@Override
	public SVGSVGElement getDocumentElement() {
		return root;
	}


	@Override
	public String getDocumentURI() {
		return documentURI;
	}


	@Override
	public String getXmlEncoding() {
		return xmlEncoding;
	}


	@Override
	public boolean getXmlStandalone() {
		return xmlStandalone;
	}


	@Override
	public String getXmlVersion() {
		return xmlVersion;
	}


	@Override
	public void setDocumentURI(final String documentURI) {
		this.documentURI = documentURI;
	}


	@Override
	public void setXmlStandalone(final boolean xmlStandalone) {
		this.xmlStandalone = xmlStandalone;
	}


	@Override
	public void setXmlVersion(final String xmlVersion) {
		this.xmlVersion = xmlVersion;
	}


	@Override
	public NamedNodeMap getAttributes() {
		return null;
	}


	@Override
	public NodeList getChildNodes() {
		return new SVGNodeList();
	}


	@Override
	public SVGSVGElement getFirstChild() {
		return root;
	}


	@Override
	public SVGSVGElement getLastChild() {
		return root;
	}


	@Override
	public Node getNextSibling() {
		return null;
	}


	@Override
	public String getNodeName() {
		return "#document"; //NON-NLS
	}


	@Override
	public short getNodeType() {
		return Node.DOCUMENT_NODE;
	}


	@Override
	public String getNodeValue() {
		return null;
	}


	@Override
	public Document getOwnerDocument() {
		return null;
	}


	@Override
	public Node getParentNode() {
		return null;
	}


	@Override
	public Node getPreviousSibling() {
		return null;
	}


	@Override
	public boolean hasAttributes() {
		return false;
	}


	@Override
	public boolean hasChildNodes() {
		return root != null;
	}


	@Override
	public boolean isEqualNode(final Node node) {
		if(!(node instanceof SVGDocument)) {
			return false;
		}
		final SVGDocument doc = (SVGDocument) node;
		final boolean encod = Objects.equals(xmlEncoding, doc.xmlEncoding);
		final boolean uri = Objects.equals(documentURI, doc.documentURI);
		return uri && encod && xmlStandalone == doc.xmlStandalone && root.isEqualNode(doc.root);
	}


	@Override
	public boolean isSameNode(final Node other) {
		return other == this;
	}


	@Override
	public DocumentType getDoctype() {
		return null;
	}


	@Override
	public Element createElement(final String tagName) {
		if(tagName == null) {
			throw new DOMException(DOMException.INVALID_CHARACTER_ERR, "Invalid tagName.");
		}

		final OtherNSElement elt = new OtherNSElement(this);
		elt.setNodeName(tagName);

		return elt;
	}


	@Override
	public Text createTextNode(final String data) {
		if(data == null) {
			throw new DOMException(DOMException.INVALID_CHARACTER_ERR, "Invalid data.");
		}

		return new SVGText(data, this);
	}


	@Override
	public Comment createComment(final String data) {
		if(data == null) {
			throw new DOMException(DOMException.INVALID_CHARACTER_ERR, "Invalid data.");
		}

		return new SVGComment(data, this);
	}


	@Override
	public CDATASection createCDATASection(final String data) {
		throw new DOMException(DOMException.INVALID_ACCESS_ERR, ACTION_NOT_IMPLEMENTED);
	}

	@Override
	public DOMConfiguration getDomConfig() {
		throw new DOMException(DOMException.INVALID_ACCESS_ERR, ACTION_NOT_IMPLEMENTED);
	}

	@Override
	public DOMImplementation getImplementation() {
		return new SVGDOMImplementation();
	}

	@Override
	public String getInputEncoding() {
		throw new DOMException(DOMException.INVALID_ACCESS_ERR, ACTION_NOT_IMPLEMENTED);
	}

	@Override
	public boolean getStrictErrorChecking() {
		throw new DOMException(DOMException.INVALID_ACCESS_ERR, ACTION_NOT_IMPLEMENTED);
	}

	@Override
	public Node importNode(final Node importedNode, final boolean deep) {
		throw new DOMException(DOMException.INVALID_ACCESS_ERR, ACTION_NOT_IMPLEMENTED);
	}

	@Override
	public void normalizeDocument() {
		throw new DOMException(DOMException.INVALID_ACCESS_ERR, ACTION_NOT_IMPLEMENTED);
	}

	@Override
	public Node renameNode(final Node n, final String namespaceURI, final String qualifiedName) {
		throw new DOMException(DOMException.INVALID_ACCESS_ERR, ACTION_NOT_IMPLEMENTED);
	}

	@Override
	public void setStrictErrorChecking(final boolean strictErrorChecking) {
		throw new DOMException(DOMException.INVALID_ACCESS_ERR, ACTION_NOT_IMPLEMENTED);
	}

	@Override
	public Node appendChild(final Node newChild) {
		return null;
	}

	@Override
	public Node cloneNode(final boolean deep) {
		throw new DOMException(DOMException.INVALID_ACCESS_ERR, ACTION_NOT_IMPLEMENTED);
	}

	@Override
	public short compareDocumentPosition(final Node other) {
		throw new DOMException(DOMException.INVALID_ACCESS_ERR, ACTION_NOT_IMPLEMENTED);
	}

	@Override
	public String getBaseURI() {
		throw new DOMException(DOMException.INVALID_ACCESS_ERR, ACTION_NOT_IMPLEMENTED);
	}

	@Override
	public Object getFeature(final String feature, final String version) {
		throw new DOMException(DOMException.INVALID_ACCESS_ERR, ACTION_NOT_IMPLEMENTED);
	}

	@Override
	public String getLocalName() {
		throw new DOMException(DOMException.INVALID_ACCESS_ERR, ACTION_NOT_IMPLEMENTED);
	}

	@Override
	public String getNamespaceURI() {
		throw new DOMException(DOMException.INVALID_ACCESS_ERR, ACTION_NOT_IMPLEMENTED);
	}

	@Override
	public String getPrefix() {
		throw new DOMException(DOMException.INVALID_ACCESS_ERR, ACTION_NOT_IMPLEMENTED);
	}

	@Override
	public String getTextContent() {
		throw new DOMException(DOMException.INVALID_ACCESS_ERR, ACTION_NOT_IMPLEMENTED);
	}

	@Override
	public Object getUserData(final String key) {
		throw new DOMException(DOMException.INVALID_ACCESS_ERR, ACTION_NOT_IMPLEMENTED);
	}

	@Override
	public boolean isDefaultNamespace(final String namespaceURI) {
		throw new DOMException(DOMException.INVALID_ACCESS_ERR, ACTION_NOT_IMPLEMENTED);
	}

	@Override
	public Attr createAttribute(final String name) {
		throw new DOMException(DOMException.INVALID_ACCESS_ERR, ACTION_NOT_IMPLEMENTED);
	}

	@Override
	public Attr createAttributeNS(final String namespaceURI, final String qualifiedName) {
		throw new DOMException(DOMException.INVALID_ACCESS_ERR, ACTION_NOT_IMPLEMENTED);
	}

	@Override
	public DocumentFragment createDocumentFragment() {
		throw new DOMException(DOMException.INVALID_ACCESS_ERR, ACTION_NOT_IMPLEMENTED);
	}

	@Override
	public Element createElementNS(final String namespaceURI, final String qualifiedName) {
		throw new DOMException(DOMException.INVALID_ACCESS_ERR, ACTION_NOT_IMPLEMENTED);
	}

	@Override
	public EntityReference createEntityReference(final String name) {
		throw new DOMException(DOMException.INVALID_ACCESS_ERR, ACTION_NOT_IMPLEMENTED);
	}

	@Override
	public ProcessingInstruction createProcessingInstruction(final String target, final String data) {
		throw new DOMException(DOMException.INVALID_ACCESS_ERR, ACTION_NOT_IMPLEMENTED);
	}

	@Override
	public boolean isSupported(final String feature, final String version) {
		throw new DOMException(DOMException.INVALID_ACCESS_ERR, ACTION_NOT_IMPLEMENTED);
	}

	@Override
	public String lookupNamespaceURI(final String prefix) {
		throw new DOMException(DOMException.INVALID_ACCESS_ERR, ACTION_NOT_IMPLEMENTED);
	}

	@Override
	public String lookupPrefix(final String namespaceURI) {
		throw new DOMException(DOMException.INVALID_ACCESS_ERR, ACTION_NOT_IMPLEMENTED);
	}

	@Override
	public void normalize() {
		throw new DOMException(DOMException.INVALID_ACCESS_ERR, ACTION_NOT_IMPLEMENTED);
	}

	@Override
	public Object setUserData(final String key, final Object data, final UserDataHandler handler) {
		throw new DOMException(DOMException.INVALID_ACCESS_ERR, ACTION_NOT_IMPLEMENTED);
	}


	@Override
	public Node insertBefore(final Node newChild, final Node refChild) {
		return null;
	}

	@Override
	public void setPrefix(final String prefix) {
		/* Nothing to do. */
	}

	@Override
	public void setTextContent(final String textContent) {
		/* Nothing to do. */
	}

	@Override
	public Node removeChild(final Node oldChild) {
		return null;
	}

	@Override
	public Node replaceChild(final Node newChild, final Node oldChild) {
		return null;
	}

	@Override
	public void setNodeValue(final String nodeValue) {
		/* Nothing to do. */
	}

	@Override
	public Element getElementById(final String elementId) {
		return null;
	}

	@Override
	public NodeList getElementsByTagName(final String tagname) {
		return null;
	}

	@Override
	public NodeList getElementsByTagNameNS(final String namespaceURI, final String localName) {
		return null;
	}


	/**
	 * Serialise the given SVG document.
	 * @param path The file of the future serialised document.
	 * @return True: the document has been successfully saved.
	 */
	public boolean saveSVGDocument(final String path) {
		if(path == null) {
			return false;
		}

		boolean ok = true;
		try {
			final DOMImplementationLS impl = (DOMImplementationLS) DOMImplementationRegistry.newInstance().getDOMImplementation("XML 3.0 LS 3.0"); //NON-NLS
			final LSSerializer serializer = impl.createLSSerializer();
			serializer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE); //NON-NLS
			serializer.getDomConfig().setParameter("namespaces", Boolean.FALSE); //NON-NLS
			final LSOutput output = impl.createLSOutput();
			final Charset charset = Charset.defaultCharset();
			try(final OutputStreamWriter fw = new OutputStreamWriter(Files.newOutputStream(Path.of(path)), charset.newEncoder())) {
				output.setEncoding(charset.name());
				output.setCharacterStream(fw);
				serializer.write(getDocumentElement(), output);
			}
		}catch(final ClassNotFoundException | InstantiationException | IllegalAccessException | ClassCastException | IOException ex) {
			BadaboomCollector.INSTANCE.add(ex);
			ok = false;
		}
		return ok;
	}


	/**
	 * Used to avoid freeze when opening an SVG document.
	 */
	static class SVGEntityResolver implements EntityResolver {
		@Override
		public InputSource resolveEntity(final String publicId, final String systemId) {
			return new InputSource(new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes(Charset.defaultCharset()))); //NON-NLS
		}
	}

	static class SVGDOMImplementation implements DOMImplementation {
		@Override
		public boolean hasFeature(final String feature, final String version) {
			return false;
		}

		@Override
		public DocumentType createDocumentType(final String qualifiedName, final String publicId, final String systemId) {
			return new SVGDocumentType();
		}

		@Override
		public Document createDocument(final String namespaceURI, final String qualifiedName, final DocumentType doctype) {
			return new SVGDocument();
		}

		@Override
		public Object getFeature(final String feature, final String version) {
			return new Object();
		}
	}

	static class SVGDocumentType extends SVGElement implements DocumentType {
		@Override
		public boolean checkAttributes() {
			return false;
		}

		@Override
		public boolean enableRendering() {
			return false;
		}

		@Override
		public String getName() {
			return "";
		}

		@Override
		public NamedNodeMap getEntities() {
			return new SVGNamedNodeMap();
		}

		@Override
		public NamedNodeMap getNotations() {
			return new SVGNamedNodeMap();
		}

		@Override
		public String getPublicId() {
			return "";
		}

		@Override
		public String getSystemId() {
			return "";
		}

		@Override
		public String getInternalSubset() {
			return "";
		}
	}
}
