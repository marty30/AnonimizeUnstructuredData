//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.03.22 at 01:30:51 PM CET 
//


package nl.willemsenmedia.utwente.anonymization.nlp.xml_objects;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for anonymous complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attGroup ref="{}attlist.lex-collocator"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "lex-collocator")
public class LexCollocator {

	@XmlAttribute(name = "collocator")
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	protected String collocator;

	/**
	 * Gets the value of the collocator property.
	 *
	 * @return possible object is
	 * {@link String }
	 */
	public String getCollocator() {
		return collocator;
	}

	/**
	 * Sets the value of the collocator property.
	 *
	 * @param value allowed object is
	 *              {@link String }
	 */
	public void setCollocator(String value) {
		this.collocator = value;
	}

}
