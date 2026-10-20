package dev.locationcore.engine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/** Strict GPX track/route point parser. External entities and DTDs are refused. */
public final class GpxParser {
    private static final long MAX_BYTES = 5L * 1024 * 1024;
    private GpxParser() {}

    public static Route parse(InputStream input) throws IOException {
        if (input == null) throw new IllegalArgumentException("GPX input required");
        byte[] document = readBounded(input);
        if (contains(document, "<!DOCTYPE") || contains(document, "<!ENTITY") || contains(document, new byte[]{0})) {
            throw new IllegalArgumentException("Invalid or unsafe GPX");
        }
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        try {
            setFeatureIfSupported(factory, "http://apache.org/xml/features/disallow-doctype-decl", true);
            setFeatureIfSupported(factory, "http://xml.org/sax/features/external-general-entities", false);
            setFeatureIfSupported(factory, "http://xml.org/sax/features/external-parameter-entities", false);
            Handler handler = new Handler();
            XMLReader reader = factory.newSAXParser().getXMLReader();
            reader.setEntityResolver((publicId, systemId) -> { throw new SAXException("External entity refused"); });
            reader.setContentHandler(handler);
            reader.parse(new InputSource(new ByteArrayInputStream(document)));
            return new Route(handler.points);
        } catch (ParserConfigurationException | SAXException failure) {
            throw new IllegalArgumentException("Invalid or unsafe GPX", failure);
        }
    }

    private static void setFeatureIfSupported(SAXParserFactory factory, String feature, boolean value) {
        try { factory.setFeature(feature, value); }
        catch (ParserConfigurationException | SAXException ignored) { }
    }

    private static byte[] readBounded(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        long count = 0;
        int amount;
        while ((amount = input.read(buffer)) >= 0) {
            count += amount;
            if (count > MAX_BYTES) throw new IOException("GPX exceeds 5 MiB");
            output.write(buffer, 0, amount);
        }
        return output.toByteArray();
    }

    private static boolean contains(byte[] document, String ascii) {
        return contains(document, ascii.getBytes(java.nio.charset.StandardCharsets.US_ASCII));
    }

    private static boolean contains(byte[] document, byte[] needle) {
        outer: for (int i = 0; i <= document.length - needle.length; i++) {
            for (int j = 0; j < needle.length; j++) {
                if (document[i + j] != needle[j]) continue outer;
            }
            return true;
        }
        return false;
    }

    private static final class Handler extends DefaultHandler {
        final List<GeoPoint> points = new ArrayList<>();
        private Double latitude;
        private Double longitude;
        private Double altitude;
        private StringBuilder elevation;

        @Override public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            String name = localName.isEmpty() ? qName : localName;
            if (name.equals("trkpt") || name.equals("rtept")) {
                if (latitude != null) throw new SAXException("Nested route point");
                try {
                    latitude = Double.parseDouble(attributes.getValue("lat"));
                    longitude = Double.parseDouble(attributes.getValue("lon"));
                } catch (RuntimeException error) {
                    throw new SAXException("Invalid route coordinate", error);
                }
                altitude = null;
            } else if (name.equals("ele") && latitude != null) {
                elevation = new StringBuilder();
            }
        }

        @Override public void characters(char[] chars, int start, int length) throws SAXException {
            if (elevation != null) {
                if (elevation.length() + length > 64) throw new SAXException("Elevation field too long");
                elevation.append(chars, start, length);
            }
        }

        @Override public void endElement(String uri, String localName, String qName) throws SAXException {
            String name = localName.isEmpty() ? qName : localName;
            if (name.equals("ele") && elevation != null) {
                try { altitude = Double.parseDouble(elevation.toString().trim()); }
                catch (NumberFormatException error) { throw new SAXException("Invalid elevation", error); }
                elevation = null;
            } else if ((name.equals("trkpt") || name.equals("rtept")) && latitude != null) {
                try { points.add(new GeoPoint(latitude, longitude, altitude)); }
                catch (IllegalArgumentException error) { throw new SAXException("Invalid route point", error); }
                if (points.size() > 10000) throw new SAXException("Too many GPX points");
                latitude = null;
                longitude = null;
                altitude = null;
            }
        }
    }

}

