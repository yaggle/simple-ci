package co.yaggle.simpleci.core.pipeline.parser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static co.yaggle.simpleci.core.pipeline.parser.UnexpectedPipelineParserException.Type.*;
import static co.yaggle.simpleci.core.pipeline.parser.XmlUtil.*;
import static javax.xml.XMLConstants.*;
import static org.apache.commons.lang3.StringUtils.*;

public class PipelineParser {

    /**
     * Validate the root directory of a project, ensuring that a Simple CI
     * configuration file is present and that it's valid.
     *
     * @param projectRootDirectory the root directory of the project to validate
     * @throws SAXException             if the XML is not valid
     * @throws IOException              if an error occurred while reading
     * @throws MissingPipelineException if the configuration file is missing
     */
    public static void validatePipeline(File projectRootDirectory) throws SAXException, IOException, MissingPipelineException {

        if (!projectRootDirectory.isDirectory()) {
            throw PROJECT_ROOT_NOT_A_DIRECTORY.newException();
        }

        Optional<File> optionalXmlFile = getConfigFile(projectRootDirectory);

        if (!optionalXmlFile.isPresent()) {
            throw new MissingPipelineException();
        }

        final SchemaFactory schemaFactory = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
        final Schema schema = schemaFactory.newSchema(PipelineParser.class.getResource(SCHEMA_FILENAME));
        final Validator validator = schema.newValidator();
        validator.validate(new StreamSource(optionalXmlFile.get()));
    }


    /**
     * Parse the Simple CI configuration file in the specified project root.
     * </p>
     * It is assumed that the configuration file is present and valid, which
     * can be asserted by first calling {@link #validatePipeline(File)}.
     *
     * @param projectRootDirectory the root directory of the project
     * @return the project's build pipeline configuration
     */
    public static PipelineElement parsePipeline(File projectRootDirectory) {

        File xmlFile = getConfigFile(projectRootDirectory).get();

        Document document;
        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile);
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw PROJECT_CONFIG_NOT_VALIDATED.newException();
        }
        document.normalize();

        // Get pipeline element
        Element pipelineElement = document.getDocumentElement();
        String dockerImageName = pipelineElement.getAttribute(IMAGE_ATTR);
        String volume = pipelineElement.getAttribute(VOLUME_ATTR);

        List<TaskElement> tasks = elementsInside(pipelineElement)
                .stream()
                .map(PipelineParser::toTaskElement)
                .collect(Collectors.toList());

        return PipelineElement
                .builder()
                .image(dockerImageName)
                .volume(volume)
                .tasks(tasks)
                .build();
    }


    private static Optional<File> getConfigFile(File projectRootDirectory) {
        return Stream
                .of(projectRootDirectory.listFiles((dir, name) -> name.equals(CONFIG_FILENAME)))
                .findFirst();
    }


    private static TaskElement toTaskElement(Element element) {
        String id = trimToNull(element.getAttribute(ID_ATTR));
        String name = trimToNull(element.getAttribute(NAME_ATTR));
        List<String> dependsOn = Stream
                .of(element.getAttribute(DEPENDS_ON_ATTR).split("\\s+"))
                .filter(otherTaskId -> !otherTaskId.isEmpty())
                .collect(Collectors.toList());
        String branch = trimToNull(element.getAttribute(BRANCH_ATTR));
        List<String> commands = textLinesInside(element);

        return TaskElement
                .builder()
                .id(id)
                .name(name)
                .dependsOn(dependsOn)
                .branch(branch)
                .commands(commands)
                .build();
    }


    private static final String CONFIG_FILENAME = "simple-ci.xml";
    private static final String SCHEMA_FILENAME = "/simple-ci.xsd";
    private static final String ID_ATTR = "id";
    private static final String NAME_ATTR = "name";
    private static final String IMAGE_ATTR = "image";
    private static final String VOLUME_ATTR = "volume";
    private static final String BRANCH_ATTR = "branch";
    private static final String DEPENDS_ON_ATTR = "dependsOn";
}
