package sg.edu.astar.dsi;

public class Instruction{
	private String operation = "query";
	private String command = "select * from allergy";

	public void setOperation (String theOperation){
		this.operation = theOperation;
	}
	
	public void setCommand (String theCommand){
		this.command = theCommand;
	}

	public String getOperation(){
		return operation;
	}
	
	public String getCommand(){
		return command;
	}
	
	@Override
	public String toString(){
		return "operation = " + operation + "\ncommand = " + command +"\n";
	}
}
