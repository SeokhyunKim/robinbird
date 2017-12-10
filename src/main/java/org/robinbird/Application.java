package org.robinbird;

import lombok.extern.slf4j.Slf4j;
import org.robinbird.code.model.AnalysisContext;
import org.robinbird.code.model.AnalysisUnit;
import org.robinbird.code.presentation.AbstractedClassesPresentation;
import org.robinbird.code.presentation.AnalysisContextPresentation;
import org.robinbird.code.presentation.CLUSTERING_METHOD;
import org.robinbird.code.presentation.GMLPresentation;
import org.robinbird.code.presentation.PlantUMLPresentation;
import org.robinbird.code.presentation.PRESENTATION_TYPE;
import org.robinbird.code.presentation.SimplePresentation;
import org.robinbird.code.presentation.StringAppender;
import org.robinbird.common.utils.AppArguments;

import java.nio.file.Paths;

import static org.robinbird.code.model.AnalysisUnit.Language.JAVA8;

/**
 * Created by seokhyun on 5/26/17.
 */
@Slf4j
public class Application {

	public void run(AppArguments appArgs) {
		log.info("Start app with args: " + appArgs.toString());
		AnalysisUnit au = new AnalysisUnit(JAVA8);
		au.addPath(Paths.get(appArgs.getSourceRootPath()));
		AnalysisContext ac = au.analysis(appArgs.getTerminalClassPatterns(), appArgs.getExcludedClassPatterns());
		AnalysisContextPresentation acPresent = createPresentation(appArgs.getPresentationType(), appArgs);
		System.out.print(acPresent.present(ac));
	}

	private AnalysisContextPresentation createPresentation(PRESENTATION_TYPE ptype, AppArguments args) {
		AnalysisContextPresentation presentation;
		switch (ptype) {
			case ABSTRACTED_CLASSES:
				CLUSTERING_METHOD cmethod = CLUSTERING_METHOD.getClusteringMethod(args.getClusteringType());
				presentation = new AbstractedClassesPresentation(cmethod, args.getScore1(), args.getScore2());
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
		if (args.length<1 || args[0].equalsIgnoreCase("help")) {
			printHelp();
			return;
		}
		Application app = new Application();
		AppArguments appArgs = AppArguments.parseArguments(args);
		app.run(appArgs);
	}

	public static void printHelp() {
		StringAppender sa = new StringAppender();
		sa.appendLine("Usage:");
		sa.appendLine("robinbird [option-type option-value]*\n");
		sa.appendLine("Examples:");
		sa.appendLine("robinbird -root your_root_path_for_source_codes");
		sa.appendLine("  // will generate PlantUML class diagram script for the given root");
		sa.appendLine("robinbird -r root_path -excluded-class ExcludedClass.*");
		sa.appendLine("  // will generate PlantUML class diagrams from root_path excluding classes matched with Java regular expression 'EscludedClass.*'\n");
		sa.appendLine("Optjon Types");
		sa.appendLine("-r  or  -root\tspecify root path of source codes");
		sa.appendLine("-p  or  -presentation\tset presentation type. default is PLANTUML. Currently, supported types are PLANTUML, SIMPLE, GML, ABSTRACTED_CLASSES");
		sa.appendLine("-tc or  -terminal-class\tClasses matched with this regular expression will be only shown their names in class diagram");
		sa.appendLine("-ec or  -excluded-class\tClasses matched with this regular expression will not be shown in class diagram");
		sa.appendLine("-s1 or --score1, and -s2 or --score2\texperimental things for ABSTRACTED_CLASSES");
		System.out.println(sa.toString());
	}
}
