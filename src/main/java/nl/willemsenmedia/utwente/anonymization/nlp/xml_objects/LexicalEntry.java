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
import java.util.ArrayList;
import java.util.List;


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
 *         &lt;choice>
 *           &lt;sequence>
 *             &lt;element ref="{}Lemma"/>
 *             &lt;element ref="{}WordForms"/>
 *           &lt;/sequence>
 *           &lt;element ref="{}MultiwordExpression"/>
 *         &lt;/choice>
 *         &lt;element ref="{}RelatedForms" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}Morphology" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}MorphoSyntax" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}SyntacticBehaviour" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}Sense"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{}attlist.LexicalEntry"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"lemma",
		"wordForms",
		"multiwordExpression",
		"relatedForms",
		"morphology",
		"morphoSyntax",
		"syntacticBehaviour",
		"sense"
})
@XmlRootElement(name = "LexicalEntry")
public class LexicalEntry {

	@XmlElement(name = "Lemma")
	protected Lemma lemma;
	@XmlElement(name = "WordForms")
	protected WordForms wordForms;
	@XmlElement(name = "MultiwordExpression")
	protected MultiwordExpression multiwordExpression;
	@XmlElement(name = "RelatedForms")
	protected List<RelatedForms> relatedForms;
	@XmlElement(name = "Morphology")
	protected List<Morphology> morphology;
	@XmlElement(name = "MorphoSyntax")
	protected List<MorphoSyntax> morphoSyntax;
	@XmlElement(name = "SyntacticBehaviour")
	protected List<SyntacticBehaviour> syntacticBehaviour;
	@XmlElement(name = "Sense", required = true)
	protected Sense sense;
	@XmlAttribute(name = "id", required = true)
	@XmlSchemaType(name = "anySimpleType")
	protected String id;
	@XmlAttribute(name = "partOfSpeech")
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	protected String partOfSpeech;
	@XmlAttribute(name = "formType")
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	protected String formType;

	/**
	 * Gets the value of the lemma property.
	 *
	 * @return possible object is
	 * {@link Lemma }
	 */
	public Lemma getLemma() {
		return lemma;
	}

	/**
	 * Sets the value of the lemma property.
	 *
	 * @param value allowed object is
	 *              {@link Lemma }
	 */
	public void setLemma(Lemma value) {
		this.lemma = value;
	}

	/**
	 * Gets the value of the wordForms property.
	 *
	 * @return possible object is
	 * {@link WordForms }
	 */
	public WordForms getWordForms() {
		return wordForms;
	}

	/**
	 * Sets the value of the wordForms property.
	 *
	 * @param value allowed object is
	 *              {@link WordForms }
	 */
	public void setWordForms(WordForms value) {
		this.wordForms = value;
	}

	/**
	 * Gets the value of the multiwordExpression property.
	 *
	 * @return possible object is
	 * {@link MultiwordExpression }
	 */
	public MultiwordExpression getMultiwordExpression() {
		return multiwordExpression;
	}

	/**
	 * Sets the value of the multiwordExpression property.
	 *
	 * @param value allowed object is
	 *              {@link MultiwordExpression }
	 */
	public void setMultiwordExpression(MultiwordExpression value) {
		this.multiwordExpression = value;
	}

	/**
	 * Gets the value of the relatedForms property.
	 * <p>
	 * <p>
	 * This accessor method returns a reference to the live list,
	 * not a snapshot. Therefore any modification you make to the
	 * returned list will be present inside the JAXB object.
	 * This is why there is not a <CODE>set</CODE> method for the relatedForms property.
	 * <p>
	 * <p>
	 * For example, to add a new item, do as follows:
	 * <pre>
	 *    getRelatedForms().add(newItem);
	 * </pre>
	 * <p>
	 * <p>
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link RelatedForms }
	 */
	public List<RelatedForms> getRelatedForms() {
		if (relatedForms == null) {
			relatedForms = new ArrayList<RelatedForms>();
		}
		return this.relatedForms;
	}

	/**
	 * Gets the value of the morphology property.
	 * <p>
	 * <p>
	 * This accessor method returns a reference to the live list,
	 * not a snapshot. Therefore any modification you make to the
	 * returned list will be present inside the JAXB object.
	 * This is why there is not a <CODE>set</CODE> method for the morphology property.
	 * <p>
	 * <p>
	 * For example, to add a new item, do as follows:
	 * <pre>
	 *    getMorphology().add(newItem);
	 * </pre>
	 * <p>
	 * <p>
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link Morphology }
	 */
	public List<Morphology> getMorphology() {
		if (morphology == null) {
			morphology = new ArrayList<Morphology>();
		}
		return this.morphology;
	}

	/**
	 * Gets the value of the morphoSyntax property.
	 * <p>
	 * <p>
	 * This accessor method returns a reference to the live list,
	 * not a snapshot. Therefore any modification you make to the
	 * returned list will be present inside the JAXB object.
	 * This is why there is not a <CODE>set</CODE> method for the morphoSyntax property.
	 * <p>
	 * <p>
	 * For example, to add a new item, do as follows:
	 * <pre>
	 *    getMorphoSyntax().add(newItem);
	 * </pre>
	 * <p>
	 * <p>
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link MorphoSyntax }
	 */
	public List<MorphoSyntax> getMorphoSyntax() {
		if (morphoSyntax == null) {
			morphoSyntax = new ArrayList<MorphoSyntax>();
		}
		return this.morphoSyntax;
	}

	/**
	 * Gets the value of the syntacticBehaviour property.
	 * <p>
	 * <p>
	 * This accessor method returns a reference to the live list,
	 * not a snapshot. Therefore any modification you make to the
	 * returned list will be present inside the JAXB object.
	 * This is why there is not a <CODE>set</CODE> method for the syntacticBehaviour property.
	 * <p>
	 * <p>
	 * For example, to add a new item, do as follows:
	 * <pre>
	 *    getSyntacticBehaviour().add(newItem);
	 * </pre>
	 * <p>
	 * <p>
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link SyntacticBehaviour }
	 */
	public List<SyntacticBehaviour> getSyntacticBehaviour() {
		if (syntacticBehaviour == null) {
			syntacticBehaviour = new ArrayList<SyntacticBehaviour>();
		}
		return this.syntacticBehaviour;
	}

	/**
	 * Gets the value of the sense property.
	 *
	 * @return possible object is
	 * {@link Sense }
	 */
	public Sense getSense() {
		return sense;
	}

	/**
	 * Sets the value of the sense property.
	 *
	 * @param value allowed object is
	 *              {@link Sense }
	 */
	public void setSense(Sense value) {
		this.sense = value;
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
	 * Gets the value of the partOfSpeech property.
	 *
	 * @return possible object is
	 * {@link String }
	 */
	public String getPartOfSpeech() {
		return partOfSpeech;
	}

	/**
	 * Sets the value of the partOfSpeech property.
	 *
	 * @param value allowed object is
	 *              {@link String }
	 */
	public void setPartOfSpeech(String value) {
		this.partOfSpeech = value;
	}

	/**
	 * Gets the value of the formType property.
	 *
	 * @return possible object is
	 * {@link String }
	 */
	public String getFormType() {
		return formType;
	}

	/**
	 * Sets the value of the formType property.
	 *
	 * @param value allowed object is
	 *              {@link String }
	 */
	public void setFormType(String value) {
		this.formType = value;
	}

}
