package tomasulo.action;

import tomasulo.action.functionalunit.*;
import tomasulo.configuration.action.FunctionalUnitsConfig;
import tomasulo.instructions.*;

public class ReservationStations {

	private ReservationStation[] entries;

	public ReservationStations(FunctionalUnits functionalUnits, FunctionalUnitsConfig config) {
		int size = config.getAdditionUnitConfig().getUnitsCount()
				+ config.getMultiplicationUnitConfig().getUnitsCount()
				+ config.getSubtractionUnitConfig().getUnitsCount() + config.getNandUnitConfig().getUnitsCount() + 2;

		entries = new ReservationStation[size];
		initializeEntries(functionalUnits);
	}

	public void initializeEntries(FunctionalUnits functionalUnits) {
		int index = 0;

		for (int i = 0; i < functionalUnits.getAdditionFUs().length; i++) {
			entries[index++] = new ReservationStation(functionalUnits.getAdditionFUs()[i]);
		}

		for (int i = 0; i < functionalUnits.getSubtractionFUs().length; i++) {
			entries[index++] = new ReservationStation(functionalUnits.getSubtractionFUs()[i]);
		}

		for (int i = 0; i < functionalUnits.getMultiplicationFUs().length; i++) {
			entries[index++] = new ReservationStation(functionalUnits.getMultiplicationFUs()[i]);
		}

		for (int i = 0; i < functionalUnits.getNandFUs().length; i++) {
			entries[index++] = new ReservationStation(functionalUnits.getNandFUs()[i]);
		}

		entries[index++] = new ReservationStation(functionalUnits.getLoadStoreFU());
		entries[index++] = new ReservationStation(functionalUnits.getBranchJumpFU());
	}
	
	public Integer hasAvailableStation(Instruction instruction){
		
		InstructionName instructionName = instruction.getName();
		
		switch(instructionName){
			case ADDI:
			case ADD:
				for (int i = 0; i < entries.length; i++) {
					if(entries[i].getFunctionalUnit() instanceof AdditionFunctionalUnit){
						if (!entries[i].isBusy()) 
							return i;
					}
				}
				return null;
				
			case SUB:
				for (int i = 0; i < entries.length; i++) {
					if(entries[i].getFunctionalUnit() instanceof SubtractionFunctionalUnit){
						if (!entries[i].isBusy()) 
							return i;
					}
				}
				return null;
				
			case MUL:
				for (int i = 0; i < entries.length; i++) {
					if(entries[i].getFunctionalUnit() instanceof MultiplicationFunctionalUnit){
						if (!entries[i].isBusy()) 
							return i;
					}
				}
				return null;
				
			case NAND:
				for (int i = 0; i < entries.length; i++) {
					if(entries[i].getFunctionalUnit() instanceof NandFunctionalUnit){
						if (!entries[i].isBusy()) 
							return i;
					}
				}
				return null;
				
			case LW:
			case SW:
				for (int i = 0; i < entries.length; i++) {
					if(entries[i].getFunctionalUnit() instanceof LoadStoreUnit){
						if (!entries[i].isBusy()) 
							return i;
					}
				}
				return null;
				
			case JMP:
			case BEQ:
			case JALR:
				for (int i = 0; i < entries.length; i++) {
					if(entries[i].getFunctionalUnit() instanceof BranchJumpUnit){
						if (!entries[i].isBusy()) 
							return i;
					}
				}
				return null;
				
			default: return null;	
		}	
	}
	
	public void issue(Instruction instruction, int reservationStationIndex, int robEntryIndex, Integer source1, Integer source2, Integer robEntrySource1, Integer robEntrySource2 ){
		
		ReservationStation reservationStation = entries[reservationStationIndex]; 
		   
		reservationStation.setState(ReservationStationState.ISSUED);
		reservationStation.setBusy(true); 
		reservationStation.setOperation(instruction.getName()); 
		reservationStation.setDestinationROBIndex(robEntryIndex);
		
		if(instruction.getName().equals(InstructionName.LW) || instruction.getName().equals(InstructionName.SW) || 
			instruction.getName().equals(InstructionName.BEQ) || instruction.getName().equals(InstructionName.JMP) || 
			instruction.getName().equals(InstructionName.ADDI)){
			reservationStation.setAddressOrImmediateValue(instruction.getImmediate());
		} 
		
		if(instruction.getName().equals(InstructionName.SW)){
			
			if (source1 != null){
				reservationStation.setVk(source1);
			}
			else{
				reservationStation.setQk(robEntrySource1);
			}
			
			if (source2 != null){
				reservationStation.setVj(source2);
			}
			else{
				reservationStation.setQj(robEntrySource2);
			}
			
			if (reservationStation.getQj() == null && reservationStation.getQk() == null){
				reservationStation.setState(ReservationStationState.READYTOEXECUTE);
			}
		}
		else{
			
			if (instruction.getName().equals(InstructionName.LW)){
				
				if (source1 != null){
					reservationStation.setVj(source1);
				}
				else{
					reservationStation.setQj(robEntrySource1);
				}
				
				if (reservationStation.getQj() == null){
					reservationStation.setState(ReservationStationState.READYTOEXECUTE);
				}
			}
			else{
				
				if (source1 != null){
					reservationStation.setVj(source1);
				}
				else{
					reservationStation.setQj(robEntrySource1);
				}
				if (source2 != null){
					reservationStation.setVk(source2);
				}
				else{
					reservationStation.setQk(robEntrySource2);
				}
				
				if (reservationStation.getQj() == null && reservationStation.getQk() == null){
					reservationStation.setState(ReservationStationState.READYTOEXECUTE);
				}
			}
			
		}
		
	}

	class ReservationStation {

		private FunctionalUnit functionalUnit;
		private boolean busy;
		private InstructionName operation;
		private int Vj;
		private int Vk;
		private Integer Qj;
		private Integer Qk;
		private int destinationROBIndex;
		private int addressOrImmediateValue;
		private ReservationStationState state;

		public ReservationStation(FunctionalUnit functionalUnit) {
			this.functionalUnit = functionalUnit;
			busy = false;
			state = ReservationStationState.EMPTY;
		}

		public FunctionalUnit getFunctionalUnit() {
			return functionalUnit;
		}

		public void setFunctionalUnit(FunctionalUnit functionalUnit) {
			this.functionalUnit = functionalUnit;
		}

		public boolean isBusy() {
			return busy;
		}

		public void setBusy(boolean busy) {
			this.busy = busy;
		}

		public InstructionName getOperation() {
			return operation;
		}

		public void setOperation(InstructionName operation) {
			this.operation = operation;
		}

		public int getVj() {
			return Vj;
		}

		public void setVj(int vj) {
			Vj = vj;
		}

		public int getVk() {
			return Vk;
		}

		public void setVk(int vk) {
			Vk = vk;
		}

		public Integer getQj() {
			return Qj;
		}

		public void setQj(Integer qj) {
			Qj = qj;
		}

		public Integer getQk() {
			return Qk;
		}

		public void setQk(Integer qk) {
			Qk = qk;
		}

		public int getAddressOrImmediateValue() {
			return addressOrImmediateValue;
		}

		public void setAddressOrImmediateValue(int addressOrImmediateValue) {
			this.addressOrImmediateValue = addressOrImmediateValue;
		}

		public int getDestinationROBIndex() {
			return destinationROBIndex;
		}

		public void setDestinationROBIndex(int destinationROBIndex) {
			this.destinationROBIndex = destinationROBIndex;
		}

		public ReservationStationState getState() {
			return state;
		}

		public void setState(ReservationStationState state) {
			this.state = state;
		}
		
		

	}

}
