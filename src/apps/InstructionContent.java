package apps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InstructionContent {
	private String blockName;
	private List<String> instructions = new ArrayList<String>();
	private List<String> altInstructions = new ArrayList<String>();
	private List<String> paramNames = new ArrayList<String>();
	private int numOfParams;
	HashMap<String, String> localVarMap = new HashMap<>(); 
	HashMap<String, InstructionContent> blockMap = new HashMap<>();
	
	//Constructor for Functions
	InstructionContent(String blockName, List<String> instructions, List<String> paramNames, int numOfParams, HashMap<String, InstructionContent> instructionBlock){
		this.blockName = blockName;
		this.instructions = instructions;
		this.numOfParams = numOfParams;
		this.paramNames = paramNames;	
		this.blockMap = instructionBlock;
	}
	
	//Constructor for If Statments & ForLoops
	InstructionContent(String blockName, List<String> instructions){
		this.blockName = blockName;
		this.instructions = instructions;	
	}
	
	public HashMap<String, InstructionContent> getBlockMap() {
		return blockMap;
	}

	//Constructor for IF Statments with Else Statments
	InstructionContent(String blockName, List<String> instructions, List<String> altInstructions){
		this.blockName = blockName;
		this.instructions = instructions;
		this.altInstructions = altInstructions;
		
	}
	
	public HashMap<String, String> getLocalVarMap() {
		return localVarMap;
	}
	
	public int getNumOfParams() {
		return numOfParams;
	}
	public List<String> getParamNames(){
		return paramNames;
	}
	
	void addInstructions(String s){
		this.instructions.add(s);
	}
	List<String> getInstructions(){
		return this.instructions;
	}
	List<String> getAltInstructions(){
		return this.altInstructions;
	}
	
	String getBlockName(){
		return this.blockName;
	}
	
}
