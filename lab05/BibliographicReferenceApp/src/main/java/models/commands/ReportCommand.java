package models.commands;

import interfaces.CommandOperation;
import lombok.AllArgsConstructor;
import models.RepositoryControl;


@AllArgsConstructor

public class ReportCommand implements CommandOperation {
    private RepositoryControl catalog;

    @Override
    public void execute() {
        catalog.report();
    }
}
