package com.signavio.warehouse.query.util;

import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XMLUtil {
	public static String getCharacterDataFromElement(Element e) {
		Node child = e.getFirstChild();
		if (child instanceof CharacterData) {
			CharacterData cd = (CharacterData) child;
			return cd.getData();
		}
		return "";
	}

	@SuppressWarnings("deprecation")
	public static String serializeDoc(Element doc) {
		String xmlString = new String();
		StringWriter stringOut = new StringWriter();
		if (doc != null) {
			OutputFormat opfrmt = new OutputFormat(doc.getOwnerDocument(),
					"UTF-8", true);
			opfrmt.setOmitXMLDeclaration(true);
			opfrmt.setIndenting(false);
			opfrmt.setPreserveSpace(false);
			XMLSerializer serial = new XMLSerializer(stringOut, opfrmt);
			try {
				serial.asDOMSerializer();
				serial.serialize(doc);
				xmlString = stringOut.toString();
			} catch (java.io.IOException ioe) {
				xmlString = null;
			}
		} else
			xmlString = null;
		return xmlString;
	}

	public static String transformNodeToString(Node node) {
		StringWriter sw = new StringWriter();
		try {
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.transform(new DOMSource(node), new StreamResult(sw));
		} catch (TransformerException te) {
			System.out.println("nodeToString Transformer Exception");
		}
		return sw.toString();
	}
}
