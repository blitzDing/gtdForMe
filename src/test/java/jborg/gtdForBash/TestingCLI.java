package jborg.gtdForBash;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import java.net.URISyntaxException;

import java.time.LocalDateTime;
import java.time.Month;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import allgemein.LittleTimeTools;

import consoleTools.BashSigns;
import consoleTools.InputArgumentException;
import consoleTools.InputStreamSession;

import someMath.NaturalNumberException;


public class TestingCLI
{

	//Remember: the '\n' are gone!!!
	String wakeProjectName = "Wakeup_MOD_Project";

	String terminatePrjctName = "Terminate_Project";
	
	String addNotePrjctName = "Add_Note_Project";
	
	String appendStpPrjctName = "Append_Step_Project";
	
	String killStepPrjctName = "Kill_Step_Project";
	
	String killPrjctNameNoDLDT = "Kill_Project_NODLDT";
	String killPrjctName = "Kill_Project";
	
	String modPrjctName = "MOD_Project";
	String modPrjctGoal = "MOD-Project Test";
	
	String newPrjctName = "Project_Nuovo";
	String newPrjctGoal = "Testing this here";
	
	String newPrjctNoDLDT = "No_DLDT_Project";
	
	String stepDesc = "Hello Bello GoodBye!";
	String stepDesc2 = "Grrrl";
	String stepDesc3 = "Bla bla";
	
	String noticeOne = "Note1";
	String noticeTwo = "Note2";
	
	GTDCLI gtdCli;
	
	LocalDateTime jetzt = LocalDateTime.now();
	LocalDateTime prjctDLDT = jetzt.plusHours(1);
	LocalDateTime stepDLDT = jetzt.plusMinutes(30);

	@BeforeAll
	public static void clearFolder()
	{

    	File[] listOfFiles = GTDCLI.getListOfFilesFromDataFolder(GTDCLI.projectDataFolderRelativePath);
    	
    	for(File file: listOfFiles)
    	{
    		
    		if(file.isFile())file.delete();
    	}
	}

	private String sequenzNXTStep(String prjctName)
	{

		String changeStepBDT = "No";
		String chosenFromStatieList = "1";//ATBD
		String dldtQuestion = "yes";
		String stepDLDTStr = translateTimeToAnswerString(stepDLDT);

		String data = SomeCommands.next_Step + " " + prjctName + '\n'
				+ changeStepBDT + '\n'
				+ chosenFromStatieList + '\n'
				+ stepDesc2 + '\n'
				+ dldtQuestion + '\n'
				+ stepDLDTStr;
		
		return data;
	}
	
	private String sequenzKillStep(String prjctName)
	{
	
		String stepWasSuccessQstn  = "No";
		String wantToMakeTDTNote = "No";
		String wantToChangeTDT = "No";

		String data = SomeCommands.terminate_Step + " " + prjctName + '\n'
					+ stepWasSuccessQstn + '\n'
					+ wantToMakeTDTNote + '\n'
					+ wantToChangeTDT + '\n';
		
		return data;
	}

	private String sequenzNewProject(String prjctName)
	{
		
		String changePrjctBDT = "No";
		String dldtQuestion = "yes";
		String prjctDLDTStr = translateTimeToAnswerString(prjctDLDT);
		String changeStepBDT = "No";
		String chosenFromStatieList = "2";//ATBD//TODO: make it bullet proof. it works for now.
		String stepDLDTStr = translateTimeToAnswerString(stepDLDT);
		
		String data = SomeCommands.new_Project + '\n'
				+ prjctName + '\n'
				+ newPrjctGoal + '\n'
				+ changePrjctBDT + '\n'
				+ dldtQuestion + '\n'
				+ prjctDLDTStr
				+ changeStepBDT + '\n'
				+ chosenFromStatieList + '\n'
				+ stepDesc + '\n'
				+ dldtQuestion + '\n'
				+ stepDLDTStr;
				
		return data;
	}


	private String sequenzNewProjectNoDLDT(String prjctName)
	{
		
		String changePrjctBDT = "No";
		String dldtQuestion = "no";
		String changeStepBDT = "No";
		String chosenFromStatieList = "2";//ATBD//TODO: make it bullet proof. it works for now.
		
		String data = SomeCommands.new_Project + '\n'
				+ prjctName + '\n'
				+ newPrjctGoal + '\n'
				+ changePrjctBDT + '\n'
				+ dldtQuestion + '\n'
				+ changeStepBDT + '\n'
				+ chosenFromStatieList + '\n'
				+ stepDesc + '\n'
				+ dldtQuestion + '\n';
				
		return data;
	}

	private String sequenzMODProject(String prjctName)
	{
		
		String changePrjctBDT = "No";
		
		String data = SomeCommands.new_MOD + '\n'
				+ prjctName + '\n'
				+ modPrjctGoal + '\n'
				+ changePrjctBDT + '\n';
				
		return data;
	}
	
	private String sequenzAddNote(String prjctName)
	{

		String data = SomeCommands.add_Note + " " + prjctName + '\n'
				+ noticeOne + "\n"
				+ SomeCommands.add_Note + " " + prjctName + '\n'
				+ noticeTwo + "\n";
				
		return data;
	}
	
	private String sequenzWakeMODProject(String prjctName)
	{

		String prjctDLDTStr = translateTimeToAnswerString(prjctDLDT);
		String changeStepBDT = "No";
		String chosenFromStatieList = "1";
		String dldtQuestion = "yes";
		String stepDLDTStr = translateTimeToAnswerString(stepDLDT);

		String data = SomeCommands.wake_MOD + prjctName + '\n'
					+ dldtQuestion + '\n'
					+ prjctDLDTStr
					+ changeStepBDT + '\n'
					+ chosenFromStatieList + '\n'
					+ stepDesc3 + '\n'
					+ dldtQuestion + '\n'
					+ stepDLDTStr;

		return data;
	}

	@Test
	public void testNewPrjct() throws JSONException, IOException, URISyntaxException, NaturalNumberException
	{

		String data = sequenzNewProject(newPrjctName);
		data = data + SomeCommands.exit + '\n';

		ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
		InputStreamSession iss = new InputStreamSession(bais);

        gtdCli = new GTDCLI(iss);				
		
		Set<JSONObject> projects = GTDCLI.loadProjects();
		
		JSONObject newProject = pickProjectByName(newPrjctName, projects);
		assert(newProject!=null);
		
		assert(newProject.has(ProjectJSONKeyz.statusKey));
		String status = newProject.getString(ProjectJSONKeyz.statusKey);
		assert(status.equals(StatusMGMT.atbd));
		
		assert(newProject.has(ProjectJSONKeyz.BDTKey));
		String bdtStr = newProject.getString(ProjectJSONKeyz.BDTKey);
		LocalDateTime bdt = LittleTimeTools.LDTfromTimeString(bdtStr);
		assert(jetzt.minusSeconds(4).isBefore(bdt));//bdt not older than 4 seconds!
		
		assert(newProject.has(ProjectJSONKeyz.NDDTKey));
		String nddtStr = newProject.getString(ProjectJSONKeyz.NDDTKey);
		LocalDateTime nddt = LittleTimeTools.LDTfromTimeString(nddtStr);
		assert(jetzt.minusSeconds(4).isBefore(nddt));//nddt ist nicht älter als 4 Sekunden.
		
		assert(newProject.has(ProjectJSONKeyz.DLDTKey));
		String dldtStr = newProject.getString(ProjectJSONKeyz.DLDTKey);
		LocalDateTime dldt = LittleTimeTools.LDTfromTimeString(dldtStr);
		assert(jetzt.minusSeconds(4).isBefore(dldt));//dldt is not older than 4 seconds.
		
		assert(newProject.has(ProjectJSONKeyz.stepArrayKey));
		JSONArray stpArr = newProject.getJSONArray(ProjectJSONKeyz.stepArrayKey);
		JSONObject step = stpArr.getJSONObject(0);
		
		bdtStr = step.getString(StepJSONKeyz.BDTKey);
		bdt = LittleTimeTools.LDTfromTimeString(bdtStr);
		assert(jetzt.minusSeconds(5).isBefore(bdt));//bdt not older than 5 seconds!
		
		nddtStr = step.getString(StepJSONKeyz.NDDTKey);
		nddt = LittleTimeTools.LDTfromTimeString(nddtStr);
		assert(jetzt.minusSeconds(5).isBefore(nddt));//nddt ist nicht älter als 5 Sekunden.
		
		dldtStr = step.getString(StepJSONKeyz.DLDTKey);
		dldt = LittleTimeTools.LDTfromTimeString(dldtStr);
		assert(jetzt.minusSeconds(5).isBefore(dldt));//dldt is not older than 5 seconds.

		String goal = newProject.getString(ProjectJSONKeyz.goalKey);
		assert(goal.equals(newPrjctGoal));
		
		String stepDesc = step.getString(StepJSONKeyz.descKey);
		assert(stepDesc.equals(this.stepDesc));
	}
	
	@Test
	public void testWakeMOD() throws JSONException, IOException, URISyntaxException, NaturalNumberException
	{
		String data = sequenzMODProject(wakeProjectName);
		data = data + sequenzWakeMODProject(wakeProjectName);
		data = data + SomeCommands.exit + '\n';
		
		ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
		InputStreamSession iss = new InputStreamSession(bais);

        gtdCli = new GTDCLI(iss);				
		
		Set<JSONObject> projects = GTDCLI.loadProjects();
		
		JSONObject project = pickProjectByName(wakeProjectName, projects);
		assert(project!=null);
		
		String prjctStatus = project.getString(ProjectJSONKeyz.statusKey);
		assert(!prjctStatus.equals(StatusMGMT.mod));
		
		String goal = project.getString(ProjectJSONKeyz.goalKey);
		assert(goal.equals(modPrjctGoal));
	}

	@Test
	public void testNewMODProject() throws JSONException, IOException, URISyntaxException, NaturalNumberException
	{

		String data = sequenzMODProject(modPrjctName);
		data = data + SomeCommands.exit + '\n';
		
		ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
		InputStreamSession iss = new InputStreamSession(bais);

        gtdCli = new GTDCLI(iss);				
		
		Set<JSONObject> projects = GTDCLI.loadProjects();
		
		JSONObject project = pickProjectByName(modPrjctName, projects);
		String ziel = project.getString(ProjectJSONKeyz.goalKey);
		assert(ziel.equals(modPrjctGoal));
	}

	@Test
	public void testAddNoteToProject() throws JSONException, IOException, URISyntaxException, NaturalNumberException
	{
				
		String data = sequenzNewProject(addNotePrjctName);
		data = data + sequenzAddNote(addNotePrjctName);
		data = data + SomeCommands.exit + '\n';
		
		ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
		InputStreamSession iss = new InputStreamSession(bais);

		gtdCli = new GTDCLI(iss);
		
		Set<JSONObject> projects = GTDCLI.loadProjects();
		
		JSONObject project = pickProjectByName(addNotePrjctName, projects);
		assert(project!=null);
		
		JSONArray notesArr = project.getJSONArray(ProjectJSONKeyz.noteArrayKey);
		String note1 = notesArr.getString(0);
		String note2 = notesArr.getString(1);
		assert(note1.equals(this.noticeOne));
		assert(note2.equals(this.noticeTwo));
		
	}

	@Test
	public void testKillProjectWithNoDLDT() throws JSONException, IOException, URISyntaxException, NaturalNumberException
	{

		String data = sequenzNewProjectNoDLDT(killPrjctNameNoDLDT);
		data = data + sequenzKillStep(killPrjctNameNoDLDT);
		data = data + sequenzKillProject(killPrjctNameNoDLDT);
		data = data + SomeCommands.exit + "\n";
		
		ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
		InputStreamSession iss = new InputStreamSession(bais);

		gtdCli = new GTDCLI(iss);
		
		Set<JSONObject> projects = GTDCLI.loadProjects();
		

		JSONObject project = pickProjectByName(killPrjctNameNoDLDT, projects);
		assert(project!=null);
		
		JSONObject step = SomeCommands.getLastStep(project);
		StatusMGMT statusMGMT = StatusMGMT.getInstance();
		Set<String> terminalSet = statusMGMT.getStatesOfASet(StatusMGMT.terminalSetName);
		
		String stepStatus = step.getString(StepJSONKeyz.statusKey);
		
		System.out.println(BashSigns.boldRBCPX+stepStatus+BashSigns.boldRBCSX);
		assert(terminalSet.contains(stepStatus));

		assert(terminalSet.contains(project.get(ProjectJSONKeyz.statusKey)));
	}
	
	@Test
	public void testKillProject() throws JSONException, IOException, URISyntaxException, NaturalNumberException
	{

		String data = sequenzNewProject(killPrjctName);
		data = data + sequenzKillStep(killPrjctName);
		data = data + sequenzKillProject(killPrjctName);
		data = data + SomeCommands.exit + "\n";
		
		ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
		InputStreamSession iss = new InputStreamSession(bais);

		gtdCli = new GTDCLI(iss);
		
		Set<JSONObject> projects = GTDCLI.loadProjects();
		

		JSONObject project = pickProjectByName(killPrjctName, projects);
		assert(project!=null);
		
		JSONObject step = SomeCommands.getLastStep(project);
		StatusMGMT statusMGMT = StatusMGMT.getInstance();
		Set<String> terminalSet = statusMGMT.getStatesOfASet(StatusMGMT.terminalSetName);
		
		String stepStatus = step.getString(StepJSONKeyz.statusKey);
		
		System.out.println(BashSigns.boldRBCPX+stepStatus+BashSigns.boldRBCSX);
		assert(terminalSet.contains(stepStatus));

		assert(terminalSet.contains(project.get(ProjectJSONKeyz.statusKey)));
	}
	
	private String sequenzKillProject(String prjctName)
	{

		String projectWasSuccessQstn  = "No";
		String wantToMakeTDTNote = "No";
		String wantToChangeTDT = "No";

		String data = SomeCommands.terminate_Project + " " + prjctName + '\n'
				+ projectWasSuccessQstn + '\n'
				+ wantToMakeTDTNote + '\n'
				+ wantToChangeTDT + '\n';

		return data;
	}

	@Test
	public void testKillStep() throws JSONException, IOException, URISyntaxException, NaturalNumberException
	{
		
		String data = sequenzNewProject(killStepPrjctName);
		data = data + sequenzKillStep(killStepPrjctName);
		data = data + SomeCommands.exit + '\n';
		
		ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
		InputStreamSession iss = new InputStreamSession(bais);


		gtdCli = new GTDCLI(iss);
		
		Set<JSONObject> projects = GTDCLI.loadProjects();

		JSONObject project = pickProjectByName(killStepPrjctName, projects);
		assert(project!=null);
		
		JSONObject step = SomeCommands.getLastStep(project);
		StatusMGMT statusMGMT = StatusMGMT.getInstance();
		Set<String> terminalSet = statusMGMT.getStatesOfASet(StatusMGMT.terminalSetName);
		assert(terminalSet.contains(step.getString(StepJSONKeyz.statusKey)));

	}

	@Test
	public void testNextStep() throws JSONException, IOException, URISyntaxException, NaturalNumberException
	{
		
		String data = sequenzNewProject(appendStpPrjctName);
		data = data + sequenzKillStep(appendStpPrjctName);
		data = data + sequenzNXTStep(appendStpPrjctName);
		data = data + SomeCommands.exit + '\n';
		System.out.println(data);
		
		ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
		InputStreamSession iss = new InputStreamSession(bais);
		

		gtdCli = new GTDCLI(iss);
	
		Set<JSONObject> projects = GTDCLI.loadProjects();

		JSONObject project = pickProjectByName(appendStpPrjctName, projects);
		assert(project!=null);
		
		JSONObject step2 = SomeCommands.getLastStep(project);
		StatusMGMT statusMGMT = StatusMGMT.getInstance();
		Set<String> atStartSet = statusMGMT.getStatesOfASet(StatusMGMT.atStartSetName);
		String stepStatus = step2.getString(StepJSONKeyz.statusKey);
		assert(atStartSet.contains(stepStatus));
		
		Set<String> onTheWaySet = statusMGMT.getStatesOfASet(StatusMGMT.onTheWaySetName);
		assert(onTheWaySet.contains(stepStatus));
		
		JSONArray steps = project.getJSONArray(ProjectJSONKeyz.stepArrayKey);
		assert(steps.length()==2);
		
		String desc1 = step2.getString(StepJSONKeyz.descKey);
		assert(desc1.equals(stepDesc2));
		
		JSONObject step1 = steps.getJSONObject(0);
		String desc0 = step1.getString(StepJSONKeyz.descKey);
		assert(desc0.equals(stepDesc));
	}
	
	@Test
	public void testNewProjectWithoutDeadline() throws JSONException, IOException, URISyntaxException, NaturalNumberException
	{
		
		String data = sequenzNewProjectNoDLDT(newPrjctNoDLDT);
		data = data + SomeCommands.exit + '\n';
		
		ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
		InputStreamSession iss = new InputStreamSession(bais);

        gtdCli = new GTDCLI(iss);				
		
		Set<JSONObject> projects = GTDCLI.loadProjects();
		
		JSONObject newProject = pickProjectByName(newPrjctNoDLDT, projects);
		assert(newProject!=null);
		
		assert(newProject.has(ProjectJSONKeyz.statusKey));
		String status = newProject.getString(ProjectJSONKeyz.statusKey);
		assert(status.equals(StatusMGMT.atbd));
		
		assert(newProject.has(ProjectJSONKeyz.BDTKey));
		String bdtStr = newProject.getString(ProjectJSONKeyz.BDTKey);
		LocalDateTime bdt = LittleTimeTools.LDTfromTimeString(bdtStr);
		assert(jetzt.minusSeconds(4).isBefore(bdt));//bdt not older than 4 seconds!
		
		assert(newProject.has(ProjectJSONKeyz.NDDTKey));
		String nddtStr = newProject.getString(ProjectJSONKeyz.NDDTKey);
		LocalDateTime nddt = LittleTimeTools.LDTfromTimeString(nddtStr);
		assert(jetzt.minusSeconds(4).isBefore(nddt));//nddt ist nicht älter als 4 Sekunden.
		
		assert(newProject.has(ProjectJSONKeyz.DLDTKey));
		assert(GTDDataSpawnSession.prjctDeadlineNone.equals(newProject.getString(ProjectJSONKeyz.DLDTKey)));
		
		assert(newProject.has(ProjectJSONKeyz.stepArrayKey));
		JSONArray stpArr = newProject.getJSONArray(ProjectJSONKeyz.stepArrayKey);
		JSONObject step = stpArr.getJSONObject(0);
		
		bdtStr = step.getString(StepJSONKeyz.BDTKey);
		bdt = LittleTimeTools.LDTfromTimeString(bdtStr);
		assert(jetzt.minusSeconds(5).isBefore(bdt));//bdt not older than 5 seconds!
		
		nddtStr = step.getString(StepJSONKeyz.NDDTKey);
		nddt = LittleTimeTools.LDTfromTimeString(nddtStr);
		assert(jetzt.minusSeconds(5).isBefore(nddt));//nddt ist nicht älter als 5 Sekunden.
		
		assert(step.has(StepJSONKeyz.DLDTKey));
		assert(GTDDataSpawnSession.stepDeadlineNone.equals(step.getString(StepJSONKeyz.DLDTKey)));

		String goal = newProject.getString(ProjectJSONKeyz.goalKey);
		assert(goal.equals(newPrjctGoal));
		
		String stepDesc = step.getString(StepJSONKeyz.descKey);
		assert(stepDesc.equals(this.stepDesc));
	}
	
	private String translateTimeToAnswerString(LocalDateTime ldt)
	{
		int day = ldt.getDayOfMonth();
		String dayStr = "" + day;
		if(day<10) dayStr = "0" + day;
		
		int month = ldt.getMonthValue();
		Month m = Month.of(month);
		String monthStr = "";
		for(String s: InputStreamSession.monthMap.keySet())
		{
			Month d = InputStreamSession.monthMap.get(s);
			if(m.equals(d))
			{
				monthStr = s;
				break;
			}
		}

		int hour = ldt.getHour();
		String hourStr = ""+hour;
		if(hour<10) hourStr = "0" + hour;
		
		int year = ldt.getYear();
		String yearStr = year+"";
		if(yearStr.length()==3)yearStr = "0" + yearStr;
		if(yearStr.length()==2)yearStr = "00" + yearStr;
		if(yearStr.length()==1)yearStr = "000" + yearStr;

		int minute = ldt.getMinute();
		String minStr = "" + minute;
		if(minute<10) minStr = "0" + minute;
		
		return dayStr + monthStr + year + "T" + hourStr + ":" + minStr +"\n";

	}
	
	private JSONObject pickProjectByName(String pName, Set<JSONObject> projects)
	{

		for(JSONObject pJSON: projects)
		{
			assert(pJSON.has(ProjectJSONKeyz.nameKey));
			String name = pJSON.getString(ProjectJSONKeyz.nameKey);
			if(name.equals(pName)) return pJSON;
		}
		
		return null;
	}

}
