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
 *       &lt;attGroup ref="{}attlist.Complementation"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "Complementation")
public class Complementation {

	@XmlAttribute(name = "complement")
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	protected String complement;
	@XmlAttribute(name = "preposition")
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	protected String preposition;

	/**
	 * Gets the value of the complement property.
	 *
	 * @return possible object is
	 * {@link String }
	 */
	public String getComplement() {
		return complement;
	}

	/**
	 * Sets the value of the complement property.
	 *
	 * @param value allowed object is
	 *              {@link String }
	 */
	public void setComplement(String value) {
		this.complement = value;
	}

	/**
	 * Gets the value of the preposition property.
	 *
	 * @return possible object is
	 * {@link String }
	 */
	public String getPreposition() {
		return preposition;
	}

	/**
	 * Sets the value of the preposition property.
	 *
	 * @param value allowed object is
	 *              {@link String }
	 */
	public void setPreposition(String value) {
		this.preposition = value;
	}

}
