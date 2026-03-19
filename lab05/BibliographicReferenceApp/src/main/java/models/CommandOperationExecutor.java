package models;

import interfaces.CommandOperation;

import java.util.ArrayList;
import java.util.List;

public class CommandOperationExecutor {
    private final List<CommandOperation> commandOperations = new ArrayList<>();

    public void executeOpration(CommandOperation commandOperation) {
        commandOperations.add(commandOperation);
        commandOperation.execute();
    }
}
