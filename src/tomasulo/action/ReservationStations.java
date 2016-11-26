package tomasulo.action;

import tomasulo.action.functionalunit.FunctionalUnit;
import tomasulo.configuration.action.FunctionalUnitsConfig;
import tomasulo.instructions.InstructionName;

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

	public class ReservationStation {

		private FunctionalUnit functionalUnit;
		private boolean busy;
		private InstructionName operation;
		private int Vj;
		private int Vk;
		private FunctionalUnit Qj;
		private FunctionalUnit Qk;
		private int destinationROBIndex;
		private int addressOrImmediateValue;

		public ReservationStation(FunctionalUnit functionalUnit) {
			this.functionalUnit = functionalUnit;
			busy = false;
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

		public FunctionalUnit getQj() {
			return Qj;
		}

		public void setQj(FunctionalUnit qj) {
			Qj = qj;
		}

		public FunctionalUnit getQk() {
			return Qk;
		}

		public void setQk(FunctionalUnit qk) {
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

	}

}
