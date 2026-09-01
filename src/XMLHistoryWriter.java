import exceptions.FunctionDatabaseException;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class XMLHistoryWriter {

    public static void exportFunctionDatabaseToXML(Path outputFilePath)
            throws SQLException, FunctionDatabaseException, IOException, XMLStreamException {

        try (FunctionDatabaseClient client = new FunctionDatabaseClient();
             FunctionDatabase database = new FunctionDatabase(client.getConnection());
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath.toString()))) {


            List<MyFunction> functionsHistory = new ArrayList<>(database.getAllFunctions());
            XMLStreamWriter xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);

            xmlWriter.writeStartDocument();

            xmlWriter.writeStartElement("functions");

            for (MyFunction function : functionsHistory) serializeFunction(function, xmlWriter);

            xmlWriter.writeEndElement();

            xmlWriter.writeEndDocument();
        }
    }

    private static void serializeFunction(MyFunction function, XMLStreamWriter xmlWriter)
            throws XMLStreamException {
        xmlWriter.writeStartElement("function");

        xmlWriter.writeStartElement("functionId");
        xmlWriter.writeCharacters(Integer.toString(function.functionId()));
        xmlWriter.writeEndElement();

        xmlWriter.writeStartElement("functionDefinition");
        xmlWriter.writeCharacters(function.functionDefinition());
        xmlWriter.writeEndElement();

        xmlWriter.writeStartElement("isConstant");
        xmlWriter.writeCharacters(Boolean.toString(function.isConstant()));
        xmlWriter.writeEndElement();

        xmlWriter.writeEndElement();
    }
}
