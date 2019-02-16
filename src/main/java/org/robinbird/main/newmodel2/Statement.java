package org.robinbird.main.newmodel2;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class Statement {

    private final StatementCategory statementCategory;

    @Builder
    protected Statement(@NonNull StatementCategory statementCategory) {
        this.statementCategory = statementCategory;
    }

}
