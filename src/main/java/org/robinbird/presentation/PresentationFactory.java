package org.robinbird.presentation;

import lombok.NonNull;
import org.apache.commons.cli.CommandLine;

public class PresentationFactory {

    public Presentation create(@NonNull final PresentationType type, @NonNull final CommandLine commandLine) {
        final Presentation presentation;
        switch (type) {
            default:
            case PLANTUML:
                presentation = new PlantUMLPresentation(commandLine);
                break;
            case GML:
                presentation = new GMLPresentation();
                break;
        }
        return presentation;
    }
}
