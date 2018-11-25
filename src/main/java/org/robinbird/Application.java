package org.robinbird;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.robinbird.main.model.AnalysisContext;
import org.robinbird.main.model.AnalysisUnit;
import org.robinbird.main.presentation.AbstractedClassesPresentation;
import org.robinbird.main.presentation.AnalysisContextPresentation;
import org.robinbird.main.presentation.ClusteringMethod;
import org.robinbird.main.presentation.GMLPresentation;
import org.robinbird.main.presentation.PlantUMLPresentation;
import org.robinbird.main.presentation.PresentationType;
import org.robinbird.main.presentation.SimplePresentation;
import org.robinbird.main.presentation.StringAppender;
import org.robinbird.main.util.Utils;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.robinbird.main.model.AnalysisUnit.Language.JAVA8;

@Slf4j
public class Application {

	private final static String SHELL_DIR = "shell.dir";

	private String shellDir;

	public void run(CommandLine commandLine) {
		shellDir = System.getProperty(SHELL_DIR);
		if (shellDir == null) {
			shellDir = System.getProperty("user.dir");
			String msg =
					"Cannot get current shell directory. Set shellDir with user.dir system property which is " + shellDir;
			System.out.println(msg);
			log.info(msg);
		}
		log.info("\n" + Utils.printMemoryInfo());

		AnalysisUnit au = new AnalysisUnit(JAVA8);
		au.addPath(getRootPath(commandLine));

		List<Pattern> terminalPatterns = convertStringsToPatterns(commandLine.getOptionValues("tc"));
		List<Pattern> excludedPatterns = convertStringsToPatterns(commandLine.getOptionValues("ec"));

		AnalysisContext ac = au.analysis(terminalPatterns, excludedPatterns);
		AnalysisContextPresentation acPresent = createPresentation(getPresentationType(commandLine), commandLine);
		System.out.print(acPresent.present(ac));
	}

	private Path getRootPath(CommandLine commandLine) {
		String rootPathStr = commandLine.getOptionValue("r");
		if (rootPathStr == null) {
			rootPathStr = "./";
		}
		Path rootPath = Paths.get(rootPathStr);
		if (rootPath.isAbsolute()) {
			return rootPath;
		} else {
			return (Paths.get(shellDir, rootPathStr));
		}
	}

	private List<Pattern> convertStringsToPatterns(String[] strings) {
		return Optional.ofNullable(strings)
				.map(strs -> Arrays.stream(strs).map(Pattern::compile).collect(Collectors.toList()))
				.orElse(null);
	}

	private PresentationType getPresentationType(CommandLine commandLine) {
		String presentation = commandLine.getOptionValue("p");
		// default presentation option
		if (presentation == null) {
			presentation = PresentationType.PLANTUML.name();
		}
		return PresentationType.valueOf(presentation);
	}

	private AnalysisContextPresentation createPresentation(PresentationType ptype, CommandLine commandLine) {
		AnalysisContextPresentation presentation;
		switch (ptype) {
			case ABSTRACTED_CLASSES:
				ClusteringMethod cmethod = ClusteringMethod.getClusteringMethod(commandLine.getOptionValue("ct"));
				presentation = new AbstractedClassesPresentation(cmethod,
																 Float.parseFloat(commandLine.getOptionValue("s1")),
																 Float.parseFloat(commandLine.getOptionValue("s2")));
				break;
			case GML:
				presentation = new GMLPresentation();
				break;
			case SIMPLE:
				presentation = new SimplePresentation();
				break;
			case PLANTUML:
			default:
				presentation = new PlantUMLPresentation();
				break;
		}
		return presentation;
	}

	public static void main(String[] args) {
		Path currentRelativePath = Paths.get("");
		System.out.println(currentRelativePath.toString());
		Options options = buildOptions();
		CommandLine commandLine = parseCommandLine(args, options);
		if (commandLine == null || commandLine.hasOption("h")) {
			printHelpMessages(options);
			return;
		}
		Application app = new Application();
		app.run(commandLine);
	}

	private static CommandLine parseCommandLine(@NonNull final String[] args, @NonNull final Options options) {
		final CommandLineParser parser = new DefaultParser();
		try {
			return parser.parse(options, args);
		} catch (Throwable e) {
			log.error("{}", e.getMessage(), e);
			return null;
		}
	}

	private static void printHelpMessages(@NonNull final Options options) {
		StringAppender sa = new StringAppender();
		sa.appendLine("Usage: robinbird option-type option-value ...\n");
		sa.appendLine("Examples:");
		sa.appendLine("robinbird -root your_root_path_for_source_codes");
		sa.appendLine("  . This will generate PlantUML class diagram script for the given root");
		sa.appendLine("robinbird -r root_path -excluded-class ExcludedClass.*");
		sa.appendLine("  . This will generate PlantUML class diagrams from root_path excluding classes matched with Java regular " +
							  "expression 'EscludedClass.*'\n");
		sa.appendLine("Options:");

		final HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(formatter.getWidth()*2, sa.toString(), null, options, null);
	}

	private static Options buildOptions() {
		final Option help = Option.builder("h")
									.longOpt("help")
									.desc("Print help messages.")
									.build();
		final Option root = Option.builder("r")
									.longOpt("root")
									.desc("specify root path of source codes")
									.hasArg()
									.build();
		final Option presentation =
				Option.builder("p")
						.longOpt("presentation")
						.desc("set presentation type. default is PLANTUML. " +
									  "Currently, supported types are PLANTUML, SIMPLE, GML, ABSTRACTED_CLASSES")
						.hasArg()
						.build();
		final Option db =
				Option.builder("db")
						.longOpt("database-file")
						.desc("Local h2 database file")
						.hasArg()
						.build();
		final Option terminalClass =
				Option.builder("tc")
						.longOpt("terminal-class")
						.numberOfArgs(Option.UNLIMITED_VALUES)
						.desc("Classes matched with this regular expression will be only shown their names in class diagram")
						.build();
		final Option excludedClass =
				Option.builder("ec")
						.longOpt("excluded-class")
						.numberOfArgs(Option.UNLIMITED_VALUES)
						.desc("Classes matched with this regular expression will not be shown in class diagram")
						.build();
		final Option clusteringType =
				Option.builder("ct")
						.longOpt("clustering-type")
						.desc("experimental clustering type for ABSTRACTED_CLASSES")
						.hasArg()
						.build();
		final Option score1 =
				Option.builder("s1")
						.longOpt("score1")
						.desc("experimental things for ABSTRACTED_CLASSES")
						.hasArg()
						.build();
		final Option score2 =
				Option.builder("s2")
						.longOpt("score2")
						.desc("experimental things for ABSTRACTED_CLASSES")
						.hasArg()
						.build();
		return new Options()
					   .addOption(help)
					   .addOption(root)
					   .addOption(presentation)
					   .addOption(db)
					   .addOption(terminalClass)
					   .addOption(excludedClass)
					   .addOption(clusteringType)
					   .addOption(score1)
					   .addOption(score2);
	}
}
