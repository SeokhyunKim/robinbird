package org.robinbird;

import static org.robinbird.model.AnalysisJob.Language.JAVA8;

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
import org.robinbird.clustering.AgglomerativeClusteringNode;
import org.robinbird.clustering.ClusteringMethod;
import org.robinbird.clustering.ClusteringMethodFactory;
import org.robinbird.clustering.ClusteringMethodType;
import org.robinbird.clustering.ClusteringNode;
import org.robinbird.clustering.ClusteringNodeFactory;
import org.robinbird.clustering.RelationSelectors;
import org.robinbird.model.AnalysisContext;
import org.robinbird.model.AnalysisJob;
import org.robinbird.model.Component;
import org.robinbird.model.ComponentCategory;
import org.robinbird.presentation.Presentation;
import org.robinbird.presentation.PresentationFactory;
import org.robinbird.presentation.PresentationType;
import org.robinbird.repository.RbRepository;
import org.robinbird.repository.dao.EntityDao;
import org.robinbird.repository.dao.EntityDaoH2Factory;
import org.robinbird.util.StringAppender;
import org.robinbird.util.Utils;

import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class Application {

	private final static String SHELL_DIR = "shell.dir";

	private String shellDir;

	private enum DB_OPTION {
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

		final EntityDao entityDao;
		if (dbOption == DB_OPTION.GENERATE_DB_FILE) {
			entityDao = EntityDaoH2Factory.createDao(Paths.get(shellDir, dbFileName).toString(),
													 true);
		} else if (dbOption == DB_OPTION.NOT_PARSING_AND_READ_DB_FILE) {
			entityDao = EntityDaoH2Factory.createDao(Paths.get(shellDir, dbFileName).toString(),
													 false);
		} else {
			entityDao = EntityDaoH2Factory.createDao();
		}
		final RbRepository rbRepository = new RbRepository(entityDao);

		// real analysis job
		final AnalysisContext analysisContext = analysisJob.analysis(rbRepository,
																	 dbOption != DB_OPTION.NOT_PARSING_AND_READ_DB_FILE,
																	 terminalPatterns, excludedPatterns);
		log.info("Recognized {} components.", entityDao.getNumComponentEntities());

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
					new ClusteringMethodFactory(new ClusteringNodeFactory());
			final ClusteringMethod clusteringMethod = clusteringMethodFactory.create(clusteringMethodType);
			final List<ClusteringNode> clusteringNodes =
					clusteringMethod.cluster(analysisContext.getComponents(ComponentCategory.classCategories()),
											 RelationSelectors::getComponentRelations,
											 clusteringMethodFactory.convertToParameters(params));
			//printClusteringNodesForDebug(clusteringNodes);
			final PresentationFactory presentationFactory = new PresentationFactory();
			final Presentation presentation = presentationFactory.create(presentationType, commandLine);
			presentationText = presentation.presentClusteringNodes(clusteringNodes);
		} else {
			final PresentationFactory presentationFactory = new PresentationFactory();
			final Presentation presentation = presentationFactory.create(presentationType, commandLine);
			presentationText = presentation.presentClasses(analysisContext);
		}

		System.out.println(presentationText);
		entityDao.close();
		log.info("Database closed.");
	}

	private void printClusteringNodesForDebug(@NonNull final List<ClusteringNode> nodes) {
		nodes.forEach(node -> printClusteringNodeForDebug(node, 0));
	}

	private void printClusteringNodeForDebug(@NonNull final ClusteringNode node, final int indent) {
		StringAppender sa = new StringAppender();
		if (node instanceof AgglomerativeClusteringNode) {
			final AgglomerativeClusteringNode aggNode = (AgglomerativeClusteringNode)node;
			System.out.println(sa.repeatedAppend("  ", indent)
								 .append(aggNode.getName())
								 .append(" ")
								 .append(String.format("%.2f", aggNode.getScore())).toString());
		} else {
			System.out.println(sa.repeatedAppend("  ", indent).append(node.getName()).toString());
		}
		for (final Component c : node.getMemberNodes()) {
			if (c instanceof ClusteringNode) {
				printClusteringNodeForDebug((ClusteringNode) c, indent + 1);
			} else {
				StringAppender childsa = new StringAppender();
				System.out.println(childsa.repeatedAppend("  ", indent + 1).append(c.getName()).toString());
			}
		}
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
		final Option skipMembers =
                Option.builder("sm")
                      .longOpt("skip-members")
                      .desc("skip member variables and methods in a class diagram")
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
                            .addOption(skipMembers)
							.addOption(clusteringType)
							.addOption(params);
	}
}
