package jborg.gtdForBash;


import java.io.File;
import java.io.IOException;


import java.net.URISyntaxException;


import java.nio.file.Files;
import java.nio.file.Path;


import java.time.LocalDateTime;

import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;


import org.json.JSONException;
import org.json.JSONObject;

import allgemein.Beholder;

import allgemein.LittleTimeTools;


import consoleTools.BashSigns;
import consoleTools.InputArgumentException;
import consoleTools.InputStreamSession;

import someMath.NaturalNumberException;

import static fileShortCuts.TextAndObjSaveAndLoad.*;

import allgemein.SimpleLogger;


public class GTDCLI implements Beholder<String>
{
	
	
	private final String statesFileName = "statusMGMT.states";
	private final StatusMGMT states = loadStates();
	
	//private final List<String> history = new ArrayList<>();
	
	public static final String projectDataFolderRelativePath = "projectDATA/";
	private static final String actionLog = "activityLog";
	private final SimpleLogger sLog = new SimpleLogger(projectDataFolderRelativePath+actionLog, "Log of GTD ");
	
	private final String thereIsAStatesFileLoading = "There is a States File. Trying to Load it";
	private final String somethinWrongStates = "Something is wrong. Using default States.";
	private final String thereAreNoStates = "There is no States File using default States.";
	
	public static final String fileMarker = ".prjct";
	
	private Map<String, JSONObject> knownProjects = new HashMap<>();

	public static final String isModProjectQ = "Maybe one Day-Project?(yes) or are we actually try "
			+ "to do it soon enough?(no): ";	
	
	
	private final Map<String, CLICommand<?>> commandMap;
	
	
	boolean noMoreLoops = false;
	
	/*TODO: some only need Testing.
 
	stepsView -> show all Last Steps of all Projects or all Steps of one Project
	
	JSONView -> View JSONObject of a Project.
	 */
 
	private final GTDDataSpawnSession ds;
	private final InputStreamSession iss;
	private final SomeCommands scds;
	
	private final String sayGoodBye = "Bye!";

	//Command related
	private final String hereAListOfCmds = "Here a list of Commands.";
	private final String unknownCmdStr = "Unknown command!";

	
	//File related.
	private final String dataFolderCreated = "Data Folder created successfully.";
	private final String dataFolderFound = "Data Folder found.";
	private final String thereIsNoDataFolder = "There is no Data Folder";
	private final String failedToCreateDirectory = "Failed to create the directory.";

	public final int jsonPrintStyle = 4;
	
    public GTDCLI(InputStreamSession iss) throws JSONException, IOException, URISyntaxException, NaturalNumberException
	{

    	this.iss = iss;
    	System.out.println(sLog.getSessionString());
    	
    	ds = new GTDDataSpawnSession(this.iss);
    	Path p = Path.of(getPathToDataFolder());
    	
		boolean isThereDataFolder = Files.exists(p)&&Files.isDirectory(p);

		if(isThereDataFolder)
		{
			System.out.println(dataFolderFound);
	    	
			Set<JSONObject> prjctSet = loadProjects();
			for(JSONObject json: prjctSet)
			{
				String pName = json.getString(ProjectJSONKeyz.nameKey);
				knownProjects.put(pName, json);
			}
				
			greetings();
			
			scds = new SomeCommands(this, knownProjects, states, ds, sLog);
			commandMap = scds.getCommandMap();

			loopForCommands();
		}
		else 
		{
			System.out.println(thereIsNoDataFolder);
			String directoryPath = getPathToDataFolder();

	        File directory = new File(directoryPath);

	        // Create the directory
	        if (directory.mkdir())
	        {
	            System.out.println(dataFolderCreated);
	        	
	    		Set<JSONObject> prjctSet = loadProjects();
	    		for(JSONObject json: prjctSet)
	    		{
	    			String pName = json.getString(ProjectJSONKeyz.nameKey);
	    			knownProjects.put(pName, json);
	    		}
	    			
	    		greetings();
	    		
				scds = new SomeCommands(this, knownProjects, states, ds, sLog);
				commandMap = scds.getCommandMap();

				loopForCommands();
	        }
	        else
	        {
	        	
	        	scds = null;
	        	commandMap = null;
	            System.out.println(failedToCreateDirectory);
	            System.out.println(sayGoodBye);
	            System.exit(0);
	        }
		}		
	}
         
    public void greetings() throws IOException
    {
    	LocalDateTime inTheMoment = LocalDateTime.now();
    	String day = inTheMoment.getDayOfWeek().toString();
    	int day2 = inTheMoment.getDayOfMonth();
    	int year = inTheMoment.getYear();
    	String time = LittleTimeTools.timeString(inTheMoment.toLocalTime());
    	
    	System.out.println("Hello, it is " + day + " the " + day2 + " in the Year "+year);
    	System.out.println("Time: " + time + '\n');
    }
    
    public static void main(String... args) throws IOException, URISyntaxException, JSONException, NaturalNumberException
    {
    	new GTDCLI(new InputStreamSession(System.in));
    }
    
    public void loopForCommands() throws NaturalNumberException, IOException, JSONException, URISyntaxException
    {
    	
    	String px = BashSigns.boldBBCPX;
    	String sx = BashSigns.boldBBCSX;
    	
    	String fullCmdWithOptArgTyped = iss.getString(px + "Type" + sx + " command. (ex. help or exit).");
    	fullCmdWithOptArgTyped = fullCmdWithOptArgTyped.trim();
    	
    	try
    	{
        	int numberOfCmds = commandMap.size();
        	int cmdCounter = 0;

        	for(String commandKnown: commandMap.keySet())
    		{
    			if(fullCmdWithOptArgTyped.startsWith(commandKnown))
    			{
    				CLICommand<?> clicmd = commandMap.get(commandKnown);
    		
    				String argument = getArgumentOfCommand(fullCmdWithOptArgTyped, commandKnown);
    				
    				/*Object obj = */ clicmd.executeCmd(argument);
    				break;
    			}
    			cmdCounter++;
    		}

    		if(cmdCounter==numberOfCmds)
    		{
    			System.out.println('\n'+unknownCmdStr);
    			System.out.println(hereAListOfCmds);
    			CLICommand<?> clicmd = commandMap.get(SomeCommands.list_commands);
    			clicmd.executeCmd("");
    		}
    	}
    	catch(CLICMDException | NaturalNumberException | IOException e)
    	{
    		System.out.println(e);
    	}
    	
    	if(!noMoreLoops)loopForCommands();
    }

    public String getArgumentOfCommand(String commandTyped, String commandKnown)
    {
    	
    	String argument = "";
   		int l = commandKnown.length();
   		argument = commandTyped.substring(l);

   		return argument;
    }
   
    
	public void nxtStp(JSONObject pJSON) throws InputArgumentException, IOException
    {
    	ds.spawnStep(pJSON);
    }
  
    private static String getPathToDataFolder()
    {
    	return projectDataFolderRelativePath;
    }

    
    public StatusMGMT loadStates()
    {
    	
    	Path p = Path.of(getPathToDataFolder()+statesFileName);
    	
    	boolean thereAreStates = (Files.exists(p));
    	StatusMGMT tmpStates;
    	
    	if(thereAreStates)
    	{
    		
    		System.out.println(thereIsAStatesFileLoading);
    		
    		try
    		{
				tmpStates =(StatusMGMT) loadObject(p);
				return tmpStates;
			}
    		catch (ClassNotFoundException | IOException e)
    		{
				e.printStackTrace();
				System.out.println(somethinWrongStates);
				return StatusMGMT.getInstance();
			}
    	}
    	
    	System.out.println(thereAreNoStates);
    	return StatusMGMT.getInstance();
    }

    public static Set<JSONObject> loadProjects() throws IOException, URISyntaxException
    {
    	
    	String path = getPathToDataFolder();
    	
    	return loadProjects(path);
    }
    
    public static Set<JSONObject> loadProjects(String path) throws IOException, URISyntaxException
    {

    	Set<JSONObject> prjctSet = new HashSet<>();

    	File[] listOfFiles = getListOfFilesFromDataFolder(path);

    	if(listOfFiles==null)return prjctSet;
    	
    	for(File file: listOfFiles)
    	{
    		String name = file.getName();
    		if(file.isFile()&&name.endsWith(fileMarker))
    		{
    			
    			String joText = loadText(path + name);
    			
    			JSONObject jo = new JSONObject(joText);
    			
    			prjctSet.add(jo);
    		}
    	}
    	
    	return prjctSet;
    }

    public static File[] getListOfFilesFromDataFolder(String path)
    {
    	
    	File folder = new File(path);

    	File[] listOfFiles = folder.listFiles();
    	
    	return listOfFiles;
    }
    
    
    public void saveProjects() throws JSONException, IOException
    {
    	for(JSONObject jo: knownProjects.values())
    	{
    		String path = getPathToDataFolder();
    		saveText(path + jo.getString(ProjectJSONKeyz.nameKey)+fileMarker, jo.toString(jsonPrintStyle));
    	}
    }
     
    private void saveStatusMGMT() throws IOException
    {
    	saveObject(getPathToDataFolder()+statesFileName, StatusMGMT.getInstance());
    }

    public void saveAll() throws JSONException, IOException
    { 	
    	saveProjects();
    	saveStatusMGMT();
    }
    
    public void stop() throws JSONException, IOException
    {

    	saveAll();
    	System.out.println(sayGoodBye);
    	sLog.saveLog();
    	noMoreLoops = true;
    }

	@Override
	public void refresh(String arg0) 
	{
		// TODO Auto-generated method stub
		
	}
	
	public Map<String, JSONObject> getProjectMap()
	{
		return knownProjects;
	}
  
    public InputStreamSession getInputStreamSession()
    {
    	return iss;
    }

}