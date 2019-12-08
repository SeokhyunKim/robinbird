package org.robinbird;

import static org.robinbird.model.AnalysisJob.Language.JAVA8;
import static org.robinbird.model.ComponentCategory.CLASS;

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
import org.robinbird.clustering.ClusteringMethod;
import org.robinbird.clustering.ClusteringMethodFactory;
import org.robinbird.clustering.ClusteringMethodType;
import org.robinbird.clustering.ClusteringNode;
import org.robinbird.clustering.ClusteringNodeFactory;
import org.robinbird.clustering.RelationSelectors;
import org.robinbird.model.AnalysisContext;
import org.robinbird.model.AnalysisJob;
import org.robinbird.model.ComponentCategory;
import org.robinbird.presentation.GMLPresentation;
import org.robinbird.presentation.PlantUMLPresentation;
import org.robinbird.presentation.Presentation;
import org.robinbird.presentation.PresentationFactory;
import org.robinbird.presentation.PresentationType;
import org.robinbird.repository.ComponentRepository;
import org.robinbird.repository.dao.ComponentEntityDao;
import org.robinbird.repository.dao.ComponentEntityDaoH2Factory;
import org.robinbird.util.StringAppender;
import org.robinbird.util.Utils;

import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class Application {

	private final static String SHELL_DIR = "shell.dir";

	private String shellDir;

	private static enum DB_OPTION {
		GENERATE_DB_FILE,
		NOT_PARSING_AND_READ_DB_FILE,
		NO_DB_FILE
	}

	public void run(CommandLine commandLine) {
		shellDir = System.getProperty(SHELL_DIR);
		if (shellDir == null) {
			shellDir = System.getProperty("user.dir");
			String msg =
					"Cannot get current shell directory. Set shellDir with user.dir system property which is " + shellDir;
			System.out.println(msg);
			log.info(msg);
		}
		log.info("Working directory: " + shellDir);
		log.info("\n" + Utils.printMemoryInfo());

		// create analysis job for the given root path
		final AnalysisJob analysisJob = new AnalysisJob(JAVA8);
		analysisJob.addPath(getRootPath(commandLine));

		// read terminal classes and excludes classes
		List<Pattern> terminalPatterns = convertStringsToPatterns(commandLine.getOptionValues("tc"));
		List<Pattern> excludedPatterns = convertStringsToPatterns(commandLine.getOptionValues("ec"));

		// create dao, repository
		final DB_OPTION dbOption;
		final String dbFileName;
		if (commandLine.hasOption("gdb")) {
			// generate database file with given file name
			dbOption = DB_OPTION.GENERATE_DB_FILE;
			dbFileName = commandLine.getOptionValue("gdb");
		} else if (commandLine.hasOption("db")) {
			// not parsing source co
			dbOption = DB_OPTION.NOT_PARSING_AND_READ_DB_FILE;
			dbFileName = commandLine.getOptionValue("db");
		} else {
			dbOption = DB_OPTION.NO_DB_FILE;
			dbFileName = "";
		}

		final ComponentEntityDao componentEntityDao;
		if (dbOption == DB_OPTION.GENERATE_DB_FILE) {
			componentEntityDao = ComponentEntityDaoH2Factory.createDao(dbFileName, true);
		} else if (dbOption == DB_OPTION.NOT_PARSING_AND_READ_DB_FILE) {
			componentEntityDao = ComponentEntityDaoH2Factory.createDao(dbFileName, false);
		} else {
			componentEntityDao = ComponentEntityDaoH2Factory.createDao();
		}
		final ComponentRepository componentRepository = new ComponentRepository(componentEntityDao);

		// real analysis job
		final AnalysisContext analysisContext = analysisJob.analysis(componentRepository,
																	 dbOption != DB_OPTION.NOT_PARSING_AND_READ_DB_FILE,
																	 terminalPatterns, excludedPatterns);
		log.info("Recognized {} components.", componentEntityDao.getNumComponentEntities());

		// presentation type
		final PresentationType presentationType = getPresentationType(commandLine);

		// get clustering method
		final Optional<ClusteringMethodType> clusteringMethodTypeOpt = getClusteringMethodType(commandLine);

		// get final presentation string
		final String presentationText;
		if (clusteringMethodTypeOpt.isPresent()) {
			final ClusteringMethodType clusteringMethodType = clusteringMethodTypeOpt.get();
			final String[] params = getClusteringParameters(commandLine);
			final ClusteringMethodFactory clusteringMethodFactory =
					new ClusteringMethodFactory(new ClusteringNodeFactory(componentRepository));
			final ClusteringMethod clusteringMethod = clusteringMethodFactory.create(clusteringMethodType, params);
			final List<ClusteringNode> clusteringNodes =
					clusteringMethod.cluster(analysisContext.getComponents(ComponentCategory.classCategories()),
											 RelationSelectors::getComponentRelations,
											 clusteringMethodFactory.getNodeMatcher(clusteringMethodType));
			final PresentationFactory presentationFactory = new PresentationFactory();
			final Presentation presentation = presentationFactory.create(presentationType);
			presentationText = presentation.presentClusteringNodes(clusteringNodes);
		} else {
			final PresentationFactory presentationFactory = new PresentationFactory();
			final Presentation presentation = presentationFactory.create(presentationType);
			presentationText = presentation.presentClasses(analysisContext);
		}

		System.out.println(presentationText);
		componentEntityDao.close();
		log.info("Database closed.");
	}

	private Path getRootPath(CommandLine commandLine) {
		final String rootPathStr;
		if (commandLine.hasOption("r")) {
			rootPathStr = commandLine.getOptionValue("r");
			log.debug("Given root path: " + rootPathStr);
		} else {
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
					   .map(strs -> Arrays.stream(strs)
										  .map(Pattern::compile)
										  .collect(Collectors.toList()))
					   .orElse(null);
	}

	private Optional<ClusteringMethodType> getClusteringMethodType(CommandLine commandLine) {
		if (!commandLine.hasOption("ct")) {
			return Optional.empty();
		}
		return Optional.of(commandLine.getOptionValue("ct"))
					   .map(ClusteringMethodType::valueOf);
	}

	private String[] getClusteringParameters(CommandLine commandLine) {
		if (!commandLine.hasOption("cp")) {
			return new String[0];
		}
		return commandLine.getOptionValues("cp");
	}

	private PresentationType getPresentationType(CommandLine commandLine) {
		String presentation = commandLine.getOptionValue("p");
		// default presentation option
		if (presentation == null) {
			presentation = PresentationType.PLANTUML.name();
		}
		return PresentationType.valueOf(presentation);
	}

	private Presentation createPresentation(PresentationType ptype, CommandLine commandLine) {
		Presentation presentation;
		switch (ptype) {
			case GML:
				presentation = new GMLPresentation();
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
		sa.appendLine("robinbird option-type option-value ...\n");
		sa.appendLine("Examples:");
		sa.appendLine("robinbird -root your_root_path_for_source_codes");
		sa.appendLine("  . This will generate PlantUML class diagram script for the given root");
		sa.appendLine("robinbird -r root_path -excluded-class ExcludedClass.*");
		sa.appendLine("  . This will generate PlantUML class diagrams from root_path excluding classes matched with Java regular " +
							  "expression 'ExcludedClass.*'");
		sa.appendLine("robinbird -gdb mydb");
		sa.appendLine("  . Generate a database file which can be used for generating diagrams later. Db file name will be \'mydb.h2.db\'");
		sa.appendLine("robinbird -db mydb");
		sa.appendLine("  . Not parsing source codes and generating PlantUML class diagram using mydb.h2.db file\n");
		sa.appendLine("robinbird -r root_path ");
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
							"Currently, supported types are PLANTUML and GML (Graph Modeling Language).")
					  .hasArg()
					  .build();
		final Option db =
				Option.builder("db")
					  .longOpt("database-file")
					  .desc("Not parsing source codes, but readling given h2 database file to generate diagrams.")
					  .hasArg()
					  .build();
		final Option gdb =
				Option.builder("gdb")
					  .longOpt("generate-database-file")
					  .desc("Generate database file by parsing source codes")
					  .hasArgs()
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
					  .desc("Robinbird provides abstrated components diagram. Currently, only has AGGLOMERATIVE clustering")
					  .hasArg()
					  .build();
		final Option params =
				Option.builder("cp")
					  .longOpt("clustering-parameters")
					  .desc("Parameters for clustering. Depends on clustering type.")
					  .hasArgs()
					  .build();
		return new Options().addOption(help)
							.addOption(root)
							.addOption(presentation)
							.addOption(db)
							.addOption(gdb)
							.addOption(terminalClass)
							.addOption(excludedClass)
							.addOption(clusteringType)
							.addOption(params);
	}
}
