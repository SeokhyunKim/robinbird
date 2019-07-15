package org.robinbird.presentation;

import lombok.NonNull;

public class PresentationFactory {

    public Presentation create(@NonNull final PresentationType type) {
        final Presentation presentation;
        switch (type) {
            default:
            case PLANTUML:
                presentation = new PlantUMLPresentation();
                break;
            case GML:
                presentation = new GMLPresentation();
                break;
        }
        return presentation;
    }
}
