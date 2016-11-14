package tomasulo.main;

import java.util.ArrayList;
import java.util.Scanner;

public class AssemblyParser {

	static String instructions;
	static String cacheInfo;
	static String cache2Info;
	static int numOfInstructions;
	ArrayList decodedInstructions;
	ArrayList decodedCacheInfo;

	public AssemblyParser() // constructor
	{

		decodedInstructions = new ArrayList();
		decodedCacheInfo = new ArrayList();

		Scanner sc = new Scanner(System.in);
		System.out.println("Please enter cache geometry");
		cacheInfo = sc.nextLine();

		if (parseCacheInfo(cacheInfo) != null) {
			decodedCacheInfo.add(parseCacheInfo(cacheInfo));
		}

		System.out.print("Do you want another level of cache?");
		String newcache = sc.nextLine();
		if (newcache.equalsIgnoreCase("yes")
				|| newcache.equalsIgnoreCase("true")) {
			System.out.println("Please enter cache 2 geometry");
			cache2Info = sc.nextLine();
			if (parseCacheInfo(cache2Info) != null)
				decodedCacheInfo.add(parseCacheInfo(cache2Info));

		}
		System.out.println("Enter the instructions"); // one instruction format
														// should be ex: add
														// r1,r2,r3
		instructions = sc.nextLine();
		// several instructions should be ex: add r1,r2,r3 , sub r4,r5,6

		String[] instructionsSeperate = instructions.split(" , ");

		numOfInstructions = instructionsSeperate.length;

		for (int i = 0; i < instructionsSeperate.length; i++) {
			if (parseInstruction(instructionsSeperate[i]) != null) {
				decodedInstructions
						.add(parseInstruction(instructionsSeperate[i]));
			}

		}

	}

	public static int getNumOfInstructions() {
		return numOfInstructions;
	}

	public static String getCacheInfo() {
		return cacheInfo;
	}

	public static String getCache2Info() {
		return cache2Info;
	}

	public static ArrayList parseCacheInfo(String s) {
		ArrayList cacheInfoDecoded = new ArrayList();

		String[] info = s.split(", ");

		for (int i = 0; i < info.length; i++) {
			System.out.println(info[i]);
			cacheInfoDecoded.add(info[i]);
		}

		return cacheInfoDecoded;
	}

	public static ArrayList parseInstruction(String s) {
		ArrayList instructionDecoded = new ArrayList();

		String[] inst = s.split(" ");
		instructionDecoded.add(inst[0]); // the instruction

		switch (inst[0]) {
		case "ADD":
			Instruction add = Instruction.ADD;
			instructionDecoded.add(add);
			break;
		case "SUB":
			Instruction sub = Instruction.SUB;
			instructionDecoded.add(sub);
			break;
		case "ADDI":
			Instruction addi = Instruction.ADDI;
			instructionDecoded.add(addi);
			break;
		case "NAND":
			Instruction nand = Instruction.NAND;
			instructionDecoded.add(nand);
			break;
		case "LW":
			Instruction lw = Instruction.LW;
			instructionDecoded.add(lw);
			break;
		case "SW":
			Instruction sw = Instruction.SW;
			instructionDecoded.add(sw);
			break;
		case "MULT":
			Instruction mult = Instruction.MULT;
			instructionDecoded.add(mult);
			break;
		case "JMP":
			Instruction jmp = Instruction.JMP;
			instructionDecoded.add(jmp);
			break;
		case "JALR":
			Instruction jalr = Instruction.JALR;
			instructionDecoded.add(jalr);
			break;
		case "RET":
			Instruction ret = Instruction.RET;
			instructionDecoded.add(ret);
			break;
		case "BEQ":
			Instruction beq = Instruction.BEQ;
			instructionDecoded.add(beq);
			break;
		default:
			System.out.println("invalid instruction");
			return null;
		}

		String[] regs = inst[1].split(","); // the registers
		for (int i = 0; i < regs.length; i++) {

			switch (regs[i]) {
			case "R0":
				instructionDecoded.add(regs[i]);
				break;
			case "R1":
				instructionDecoded.add(regs[i]);
				break;
			case "R2":
				instructionDecoded.add(regs[i]);
				break;
			case "R3":
				instructionDecoded.add(regs[i]);
				break;
			case "R4":
				instructionDecoded.add(regs[i]);
				break;
			case "R5":
				instructionDecoded.add(regs[i]);
				break;
			case "R6":
				instructionDecoded.add(regs[i]);
				break;
			case "R7":
				instructionDecoded.add(regs[i]);
				break;
			default:
				System.out
						.println("invalid registers, only registers from 0 to 7");
				return null;
			}

		}

		for (int i = 0; i < instructionDecoded.size(); i++) {
			System.out.println(instructionDecoded.get(i));
		}
		return instructionDecoded;
	}

	public ArrayList getDecodedInstructions() {
		return decodedInstructions;
	}

	public ArrayList getDecodedCacheInfo() {
		return decodedCacheInfo;
	}

	public static void main(String[] args) { // for testing
		AssemblyParser a = new AssemblyParser();

	}

}
