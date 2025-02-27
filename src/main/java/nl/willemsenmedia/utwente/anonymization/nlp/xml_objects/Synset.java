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
 *       &lt;sequence>
 *         &lt;element ref="{}Definitions" minOccurs="0"/>
 *         &lt;element ref="{}SynsetRelations" minOccurs="0"/>
 *         &lt;element ref="{}MonolingualExternalRefs" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{}attlist.Synset"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"definitions",
		"synsetRelations",
		"monolingualExternalRefs"
})
@XmlRootElement(name = "Synset")
public class Synset {

	@XmlElement(name = "Definitions")
	protected Definitions definitions;
	@XmlElement(name = "SynsetRelations")
	protected SynsetRelations synsetRelations;
	@XmlElement(name = "MonolingualExternalRefs")
	protected MonolingualExternalRefs monolingualExternalRefs;
	@XmlAttribute(name = "baseConcept")
	@XmlSchemaType(name = "anySimpleType")
	protected String baseConcept;
	@XmlAttribute(name = "id", required = true)
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	@XmlID
	@XmlSchemaType(name = "ID")
	protected String id;
	@XmlAttribute(name = "ili")
	@XmlSchemaType(name = "anySimpleType")
	protected String ili;

	/**
	 * Gets the value of the definitions property.
	 *
	 * @return possible object is
	 * {@link Definitions }
	 */
	public Definitions getDefinitions() {
		return definitions;
	}

	/**
	 * Sets the value of the definitions property.
	 *
	 * @param value allowed object is
	 *              {@link Definitions }
	 */
	public void setDefinitions(Definitions value) {
		this.definitions = value;
	}

	/**
	 * Gets the value of the synsetRelations property.
	 *
	 * @return possible object is
	 * {@link SynsetRelations }
	 */
	public SynsetRelations getSynsetRelations() {
		return synsetRelations;
	}

	/**
	 * Sets the value of the synsetRelations property.
	 *
	 * @param value allowed object is
	 *              {@link SynsetRelations }
	 */
	public void setSynsetRelations(SynsetRelations value) {
		this.synsetRelations = value;
	}

	/**
	 * Gets the value of the monolingualExternalRefs property.
	 *
	 * @return possible object is
	 * {@link MonolingualExternalRefs }
	 */
	public MonolingualExternalRefs getMonolingualExternalRefs() {
		return monolingualExternalRefs;
	}

	/**
	 * Sets the value of the monolingualExternalRefs property.
	 *
	 * @param value allowed object is
	 *              {@link MonolingualExternalRefs }
	 */
	public void setMonolingualExternalRefs(MonolingualExternalRefs value) {
		this.monolingualExternalRefs = value;
	}

	/**
	 * Gets the value of the baseConcept property.
	 *
	 * @return possible object is
	 * {@link String }
	 */
	public String getBaseConcept() {
		return baseConcept;
	}

	/**
	 * Sets the value of the baseConcept property.
	 *
	 * @param value allowed object is
	 *              {@link String }
	 */
	public void setBaseConcept(String value) {
		this.baseConcept = value;
	}

	/**
	 * Gets the value of the id property.
	 *
	 * @return possible object is
	 * {@link String }
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the value of the id property.
	 *
	 * @param value allowed object is
	 *              {@link String }
	 */
	public void setId(String value) {
		this.id = value;
	}

	/**
	 * Gets the value of the ili property.
	 *
	 * @return possible object is
	 * {@link String }
	 */
	public String getIli() {
		return ili;
	}

	/**
	 * Sets the value of the ili property.
	 *
	 * @param value allowed object is
	 *              {@link String }
	 */
	public void setIli(String value) {
		this.ili = value;
	}

}
