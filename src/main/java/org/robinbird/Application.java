package org.robinbird;

import lombok.extern.slf4j.Slf4j;
import org.robinbird.code.model.AnalysisContext;
import org.robinbird.code.model.AnalysisUnit;
import org.robinbird.code.presentation.AnalysisContextPresentation;
import org.robinbird.code.presentation.GMLPresentation;
import org.robinbird.code.presentation.PlantUMLPresentation;
import org.robinbird.code.presentation.PresentationType;
import org.robinbird.code.presentation.SimplePresentation;
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
