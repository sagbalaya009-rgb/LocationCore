package dev.locationcore.engine;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/** Strict GPX track/route point parser. External entities and DTDs are refused. */
public final class GpxParser {
    private static final long MAX_BYTES = 5L * 1024 * 1024;
    private GpxParser() {}

    public static Route parse(InputStream input) throws IOException {
        if (input == null) throw new IllegalArgumentException("GPX input required");
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        try {
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            Handler handler = new Handler();
            factory.newSAXParser().parse(new BoundedInputStream(input), handler);
            return new Route(handler.points);
        } catch (ParserConfigurationException | SAXException failure) {
            throw new IllegalArgumentException("Invalid or unsafe GPX", failure);
        }
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

    private static final class BoundedInputStream extends FilterInputStream {
        private long count;
        BoundedInputStream(InputStream input) { super(input); }
        @Override public int read() throws IOException {
            int value = super.read();
            if (value >= 0 && ++count > MAX_BYTES) throw new IOException("GPX exceeds 5 MiB");
            return value;
        }
        @Override public int read(byte[] bytes, int offset, int length) throws IOException {
            int amount = super.read(bytes, offset, length);
            if (amount > 0 && (count += amount) > MAX_BYTES) throw new IOException("GPX exceeds 5 MiB");
            return amount;
        }
    }
}

