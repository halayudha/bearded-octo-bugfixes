package sg.edu.astar.dsi;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import sg.edu.nus.bestpeer.queryprocessing.SelectExecutor;
//import sg.edu.nus.gui.test.peer.DatabaseQueryTableModel;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


@Path("/query")
public class gatewayRestInterface{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response SqlQuery(InputStream incomingData){
		Instruction theInstruction = null;
		ArrayList<String[]> result = new ArrayList<String[]>();
		//DatabaseQueryTableModel model = null;
		StringBuilder incomingDataString =new StringBuilder();
		try{
			BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
			String line = null;
			while ((line = in.readLine()) != null){
				incomingDataString.append(line);
			}
		}
		catch (Exception e){
				System.out.println("Error Parsing: - ");
		}

		System.out.println("Data Received: " + incomingDataString.toString());

		//Convert to instruction.class
		ObjectMapper mapper = new ObjectMapper();
		try{
			theInstruction = mapper.readValue(incomingDataString.toString(), Instruction.class);
			System.out.println(theInstruction); 
		
		}	
		catch (JsonGenerationException e){
			e.printStackTrace();
		}
		catch (JsonMappingException e){
			e.printStackTrace();
		}
		catch (IOException e){
			e.printStackTrace();
		}
		
		// Using SelectExecutor
		SelectExecutor  selectExecutor = new SelectExecutor(theInstruction.getCommand());
		/*
		model = new DatabaseQueryTableModel();
		selectExecutor.setTableModelForViewingResult(model);
		selectExecutor.setUserName("u1");
		selectExecutor.setQueryID(-1);
		selectExecutor.start();
		*/
		selectExecutor.setUserName("u1");
		if (!selectExecutor.isLegalQuery()){
			System.out.println("INSIDE GATEWAY: isLegalQuery() Before");
			result.clear();
			System.out.println("INSIDE GATEWAY: isLegalQuery() After");
		}
		if (selectExecutor.isSingleTableQuery()){
			System.out.println("INSIDE GATEWAY: isSingleTableQuery() Before");
			result = new ArrayList<String[]>(selectExecutor.singleTableWebSearch());
			System.out.println("INSIDE GATEWAY: isSingleTableQuery() After");
		}
		
		//System.out.println("Result contains: " + result);
		System.out.println("Result size    : " + result.size());



		Map<String,Object> tableStruct = new HashMap<String,Object>();
		Map<String,String> contentStruct = new HashMap<String,String>();
		ArrayList<Object> arrayStruct = new ArrayList<Object>();
		int j = (result.get(0)).length;
		for (int i = 0; i < result.size();i++){
			for (int k = 0; k < j ;k++){ //COLUMN COUNT
				//System.out.println ("row: " + i + " column: " + k + " value: " + (result.get(i))[k]);
				contentStruct.put((result.get(0))[k],(result.get(i))[k]);
			}
			arrayStruct.add(contentStruct);
			contentStruct = new HashMap<String,String>();//HAS TO BE RENEW!!!OR OTHERWISE YOU WILL GET ONLY LAST RECORD!.
			
		}
		tableStruct.put("content",arrayStruct);
		

		String tableJson = null;
		try{
		tableJson = mapper.writeValueAsString(tableStruct);
		}
		catch (JsonGenerationException e){
			e.printStackTrace();
		}
		catch (JsonMappingException e){
			e.printStackTrace();
		}
		catch (IOException e){
			e.printStackTrace();
		}
	
		//System.out.println("tableJson: " + tableJson);

		

		//return Response.status(200).entity(incomingDataString.toString()).build();
		  return Response.status(200).entity(tableJson).build();
	}
}
