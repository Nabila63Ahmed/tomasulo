package tomasulo.main;

import tomasulo.action.FunctionalUnits;
import tomasulo.action.ReorderBuffer;
import tomasulo.action.ReservationStations;
import tomasulo.configuration.Config;
import tomasulo.configuration.action.FunctionalUnitConfig;
import tomasulo.configuration.memory.CacheConfig;
import tomasulo.configuration.memory.WritingPolicy;
import tomasulo.instructions.Assembler;
import tomasulo.instructions.Instruction;
import tomasulo.instructions.InstructionBuffer;
import tomasulo.memory.Memory;
import tomasulo.registers.RegisterFile;
import tomasulo.registers.RegisterStatus;
import tomasulo.util.filesystem.FileReader;
import tomasulo.util.logging.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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
        config.getMemoryConfig().setBlockSizeBytes(16);
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
        InstructionBuffer instructionBuffer = new InstructionBuffer(config.getInstructionBufferSize());
        ReorderBuffer reorderBuffer = new ReorderBuffer(config.getReorderBufferSize());
        RegisterFile registerFile = new RegisterFile();
        RegisterStatus registerStatus = new RegisterStatus();
        Memory memory = new Memory(config.getMemoryConfig());
        FunctionalUnits functionalUnits = new FunctionalUnits(config.getFunctionalUnitsConfig());
        ReservationStations reservationStations = new ReservationStations(functionalUnits, config.getFunctionalUnitsConfig());
        Logger l = new Logger();

        /////////////// PRE-EXECUTION ///////////////
        String[] stringInstructions = fileReader.readFile("assembly/arithmetic-1.asm");
        ArrayList<Instruction> instructions = assembler.parseInstructions(stringInstructions);
        memory.loadProgram(instructions, 0);

        // for (int i = 0; i < instructions.size(); i++) {
        // System.out.println(memory.readBlock(i * blockSizeInBytes));
        // }

        /////////////// EXECUTION ///////////////
        // TODO: Tomasulo's algorithm
        
        while(true) {
        	
        	for (int i = 0; i < config.getPipelineWidth(); i++) {
        		instructionBuffer.insertInstructions(memory.readInstruction());
			}
        	
        	Instruction instruction = instructionBuffer.readFirstInstruction();
        	
        	if (reservationStations.hasAvailableStation() && !reorderBuffer.isFull()){
        		int robEntryIndex = reorderBuffer.addInstruction(instruction.getName(), instruction.getDestinationRegister());
        		registerStatus.setROBEntryIndex(instruction.getDestinationRegister(), robEntryIndex);
        		reservationStations.issue(instruction);
        		instructionBuffer.removeFirstInstruction();
        	}
        	
        	HashMap<String, Integer> executed = reservationStations.executeExecutables() ;
        	if(executed != null){
        		reorderBuffer.setRegisterValue(executed.get("dest"), executed.get("value"));
        	}
        	
        	if (reorderBuffer.commit()){
        		registerFile.writeRegister(reorderBuffer.getRegisterIndex(reorderBuffer.getHead()), reorderBuffer.getRegisterValue(reorderBuffer.getHead()));
        		registerStatus.clearROBEntryIndex(reorderBuffer.getRegisterIndex(reorderBuffer.getHead()));
        		reorderBuffer.incrementHead();
        	}
        	
        }

        /////////////// PERFORMANCE METRICS ///////////////
        l.printMetrics();

    }

}
