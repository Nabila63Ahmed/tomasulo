package tomasulo.main;

import java.io.IOException;
import java.util.ArrayList;

import tomasulo.action.ReorderBuffer;
import tomasulo.configuration.Config;
import tomasulo.configuration.action.FunctionalUnitConfig;
import tomasulo.configuration.memory.CacheConfig;
import tomasulo.configuration.memory.WritingPolicy;
import tomasulo.instructions.Assembler;
import tomasulo.instructions.Instruction;
import tomasulo.instructions.InstructionBuffer;
import tomasulo.registers.RegisterFile;
import tomasulo.registers.RegisterStatus;
import tomasulo.util.filesystem.FileReader;

public class Main {

	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException {

		/////////////// CONFIGURATIONS ///////////////
		Config config = new Config();

		// General configurations
		config.setPipelineWidth(4);
		config.setInstructionBufferSize(20);
		config.setReorderBufferSize(10);

		// Memory configurations
		config.getMemoryConfig().setHitWritingPolicy(WritingPolicy.ALLOCATE);
		config.getMemoryConfig().setMissWritingPolicy(WritingPolicy.AROUND);

		// Main memory configurations
		config.getMemoryConfig().getMainMemoryConfig().setAccessCycles(100);

		// Cache(s) configurations
		config.getMemoryConfig().addCacheConfig(new CacheConfig(16 * 1024, 16, 1, 5));
		config.getMemoryConfig().addCacheConfig(new CacheConfig(32 * 1024, 16, 2, 10));

		// Functional units configurations
		config.getFunctionalUnitsConfig().setAdditionUnitConfig(new FunctionalUnitConfig(2, 1));
		config.getFunctionalUnitsConfig().setMultiplicationUnitConfig(new FunctionalUnitConfig(2, 10));
		config.getFunctionalUnitsConfig().setLoadUnitConfig(new FunctionalUnitConfig(2, 15));
		config.getFunctionalUnitsConfig().setStoreUnitConfig(new FunctionalUnitConfig(2, 15));

		/////////////// INIT ///////////////
		FileReader fileReader = new FileReader();
		Assembler assembler = new Assembler();
		InstructionBuffer instructionBuffer = new InstructionBuffer();
		ReorderBuffer reorderBuffer = new ReorderBuffer();
		RegisterFile registerFile = new RegisterFile();
		RegisterStatus registerStatus = new RegisterStatus();
		// TODO: Memory memory = new Memory();
		// TODO: FunctionalUnits functionalUnits = new FunctionalUnits();
		// TODO: ReservationStations reservationStations = new ReservationStations();

		/////////////// PRE-EXECUTION ///////////////
		String[] stringInstructions = fileReader.readFile("assembly/arithmetic-1.asm");
		ArrayList<Instruction> instructions = assembler.parseInstructions(stringInstructions);
		// TODO: memory.loadProgram(instructions);

		/////////////// EXECUTION ///////////////
		// TODO: Tomasulo's algorithm

	}

}
