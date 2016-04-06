package nl.willemsenmedia.utwente.anonymization.nlp;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static javax.xml.stream.XMLStreamConstants.*;

public class PartialUnmarshaller<T> {
	XMLStreamReader reader;
	Class<T> clazz;
	Unmarshaller unmarshaller;

	public PartialUnmarshaller(InputStream stream, Class<T> clazz) throws XMLStreamException, FactoryConfigurationError, JAXBException {
		this.clazz = clazz;
		this.unmarshaller = JAXBContext.newInstance(clazz).createUnmarshaller();
		this.reader = XMLInputFactory.newInstance().createXMLStreamReader(stream);

        /* ignore headers */
		skipElements(START_DOCUMENT, DTD);
		/* ignore root element */
		reader.nextTag();
        /* if there's no tag, ignore root element's end */
		skipElements(END_ELEMENT);
	}

	public T next() throws XMLStreamException, JAXBException {
		if (!hasNext())
			throw new NoSuchElementException();

		T value = unmarshaller.unmarshal(reader, clazz).getValue();

		skipElements(CHARACTERS, END_ELEMENT);
		return value;
	}

	public boolean hasNext() throws XMLStreamException {
		return reader.hasNext();
	}

	public void close() throws XMLStreamException {
		reader.close();
	}

	void skipElements(int... elements) throws XMLStreamException {
		int eventType = reader.getEventType();

		List<Integer> types = new ArrayList<>();
		for (int index = 0; index < elements.length; index++) {
			types.add(elements[index]);
		}

		while (types.contains(eventType))
			eventType = reader.next();
	}
}