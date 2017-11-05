package org.robinbird;

import lombok.extern.slf4j.Slf4j;
import org.robinbird.model.AnalysisContext;
import org.robinbird.model.AnalysisUnit;
import org.robinbird.presentation.AnalysisContextPresentation;
import org.robinbird.presentation.GMLPresentation;
import org.robinbird.presentation.PlantUMLPresentation;
import org.robinbird.presentation.PresentationType;
import org.robinbird.presentation.SimplePresentation;
import org.robinbird.utils.AppArguments;
import org.slf4j.impl.SimpleLogger;

import java.nio.file.Paths;

import static org.robinbird.model.AnalysisUnit.Language.JAVA8;

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
		AnalysisContextPresentation acPresent = createPresentation(appArgs.getPresentationType());
		System.out.print(acPresent.present(ac));
	}

	private AnalysisContextPresentation createPresentation(PresentationType ptype) {
		AnalysisContextPresentation presentation;
		switch (ptype) {
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
		Application app = new Application();
		AppArguments appArgs = AppArguments.parseArguments(args);
		app.run(appArgs);
	}
}
