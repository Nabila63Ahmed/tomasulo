package tomasulo.action.functionalunit;

import tomasulo.configuration.action.FunctionalUnitConfig;
import tomasulo.instructions.Instruction;
import tomasulo.instructions.InstructionName;

public class SubtractionFunctionalUnit extends FunctionalUnit {

    private int numberOfcycles;
    private int result;

    public SubtractionFunctionalUnit(FunctionalUnitConfig subtractionFunctionalUnitConfig) {
        this.numberOfcycles = subtractionFunctionalUnitConfig.getExecutionCycles();
    }

    public void execute(Instruction instruction) {
        if (instruction.getName().equals(InstructionName.SUB)) {
            this.result = instruction.getSourceRegister1() - instruction.getSourceRegister2();
        }
    }

    public int getResult() {
        return result;
    }

    public int getNumberOfcycles() {
        return numberOfcycles;
    }

}
