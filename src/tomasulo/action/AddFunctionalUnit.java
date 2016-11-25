package tomasulo.action;

import tomasulo.instructions.*;

public class AddFunctionalUnit extends FunctionalUnit {
     
  
	private int numberOfcycles; 
	private int result;

	public AddFunctionalUnit(int cycles)
	{ 
	   this.numberOfcycles = cycles;   	
	}
	public void execute(Instruction instruction) {
		if (instruction.getName().equals(InstructionName.ADD)) {
			this.result = instruction.getSourceRegister1() + instruction.getSourceRegister2();
			
			
		} 
		if (instruction.getName().equals(InstructionName.ADDI)) {
			this.result = instruction.getSourceRegister1() + instruction.getImmediate();
			
		}
		
	}
	public int getResult() {
		return result;
	}
	
	public int getNumberOfcycles() {
		return numberOfcycles;
	} 
}
