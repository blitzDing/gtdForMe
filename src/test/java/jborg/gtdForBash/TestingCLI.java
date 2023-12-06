package jborg.gtdForBash;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;

import java.util.InputMismatchException;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import allgemein.LittleTimeTools;
import consoleTools.InputStreamSession;


class TestingCLI
{

	//Remember: the '\n' are gone!!!
	String wakeProjectName = "Wakeup_MOD_Project";

	String terminatePrjctName = "Terminate_Project";
	
	String addNotePrjctName = "Add_Note_Project";
	
	String appendStpPrjctName = "Append_Step_Project";
	
	String killStepPrjctName = "Kill_Step_Project";
	
	String modPrjctName = "MOD_Project";
	String modPrjctGoal = "MOD-Project Test";
	
	String newPrjctName = "Project_Nuovo";
	String newPrjctGoal = "Testing this here";
	
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

    	File[] listOfFiles = GTDCLI.getListOfFilesFromDataFolder();
    	
    	for(File file: listOfFiles)
    	{
    		
    		if(file.isFile())file.delete();
    	}
	}

	public String nxtStpSequenz(String prjctName)
	{

		String changeStepBDT = "No";
		String chosenFromStatieList = "1";//ATBD
		String stepDLDTStr = translateTimeToAnswerString(stepDLDT);

		String data = GTDCLI.next_Step + " " + prjctName + '\n'
				+ changeStepBDT + '\n'
				+ chosenFromStatieList + '\n'
				+ stepDesc2 + '\n'
				+ stepDLDTStr;
		
		return data;
	}
	public String killStepSequenz(String prjctName)
	{
	
		String stepWasSuccessQstn  = "No";
		String wantToMakeTDTNote = "No";
		String wantToChangeTDT = "No";

		String data = GTDCLI.terminate_Step + " " + prjctName + '\n'
					+ stepWasSuccessQstn + '\n'
					+ wantToMakeTDTNote + '\n'
					+ wantToChangeTDT + '\n';
		
		return data;
	}

	public String newProjectSequenz(String prjctName)
	{
		
		String changePrjctBDT = "No";
		String prjctDLDTStr = translateTimeToAnswerString(prjctDLDT);
		String changeStepBDT = "No";
		String chosenFromStatieList = "1";//ATBD
		String stepDLDTStr = translateTimeToAnswerString(stepDLDT);
		
		String data = GTDCLI.new_Project + '\n'
				+ prjctName + '\n'
				+ newPrjctGoal + '\n'
				+ changePrjctBDT + '\n'
				+ prjctDLDTStr
				+ changeStepBDT + '\n'
				+ chosenFromStatieList + '\n'
				+ stepDesc + '\n'
				+ stepDLDTStr;
				
		return data;
	}

	public String modProjectSequenz(String prjctName)
	{
		
		String changePrjctBDT = "No";
		
		String data = GTDCLI.new_MOD + '\n'
				+ prjctName + '\n'
				+ modPrjctGoal + '\n'
				+ changePrjctBDT + '\n';
				
		return data;
	}
	
	public String addNoteSequenz(String prjctName)
	{

		String data = GTDCLI.add_Note + " " + prjctName + '\n'
				+ noticeOne + "\n"
				+ GTDCLI.add_Note + " " + prjctName + '\n'
				+ noticeTwo + "\n";
				
		return data;
	}

	@Test
	public void testNewPrjct() throws Exception
	{
		
		String data = newProjectSequenz(newPrjctName);
		data = data + GTDCLI.exit + '\n';
		
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
	public void testNewMODProject() throws InputMismatchException, JSONException, IOException, URISyntaxException, StepTerminationException, ProjectTerminationException, SpawnStepException, SpawnProjectException, TimeGoalOfProjectException
	{

		String data = modProjectSequenz(modPrjctName);
		data = data + GTDCLI.exit + '\n';
		
		ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
		InputStreamSession iss = new InputStreamSession(bais);

        gtdCli = new GTDCLI(iss);				
		
		Set<JSONObject> projects = GTDCLI.loadProjects();
		
		JSONObject project = pickProjectByName(modPrjctName, projects);
		String ziel = project.getString(ProjectJSONKeyz.goalKey);
		assert(ziel.equals(modPrjctGoal));
	}

	@Test
	public void testAddNoteToProject() throws InputMismatchException, JSONException, IOException, URISyntaxException, StepTerminationException, ProjectTerminationException, SpawnStepException, SpawnProjectException, TimeGoalOfProjectException
	{
				
		String data = newProjectSequenz(addNotePrjctName);
		data = data + addNoteSequenz(addNotePrjctName);
		data = data + GTDCLI.exit + '\n';
		
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
	public void testKillStep() throws InputMismatchException, JSONException, IOException, URISyntaxException, StepTerminationException, ProjectTerminationException, SpawnStepException, SpawnProjectException, TimeGoalOfProjectException
	{
		
		String data = newProjectSequenz(killStepPrjctName);
		data = data + killStepSequenz(killStepPrjctName);
		data = data + GTDCLI.exit + '\n';
		
		ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
		InputStreamSession iss = new InputStreamSession(bais);


		gtdCli = new GTDCLI(iss);
		
		Set<JSONObject> projects = GTDCLI.loadProjects();

		JSONObject project = pickProjectByName(killStepPrjctName, projects);
		assert(project!=null);
		
		JSONObject step = GTDCLI.getLastStep(project);
		StatusMGMT statusMGMT = StatusMGMT.getInstance();
		Set<String> terminalSet = statusMGMT.getStatesOfASet(StatusMGMT.terminalSetName);
		assert(terminalSet.contains(step.getString(StepJSONKeyz.statusKey)));

	}

	@Test
	public void testNextStep() throws InputMismatchException, JSONException, IOException, URISyntaxException, StepTerminationException, ProjectTerminationException, SpawnStepException, SpawnProjectException, TimeGoalOfProjectException
	{
		
		String data = newProjectSequenz(appendStpPrjctName);
		data = data + killStepSequenz(appendStpPrjctName);
		data = data + nxtStpSequenz(appendStpPrjctName);
		data = data + GTDCLI.exit + '\n';
		System.out.println(data);
		
		ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
		InputStreamSession iss = new InputStreamSession(bais);
		

		gtdCli = new GTDCLI(iss);
	
		Set<JSONObject> projects = GTDCLI.loadProjects();

		JSONObject project = pickProjectByName(appendStpPrjctName, projects);
		assert(project!=null);
		
		JSONObject step2 = GTDCLI.getLastStep(project);
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
	
	public String translateTimeToAnswerString(LocalDateTime ldt)
	{
		
		int hour = ldt.getHour();
		int minute = ldt.getMinute();
		int year = ldt.getYear();
		int month = ldt.getMonthValue();
		int day = ldt.getDayOfMonth();
		
		return hour + "\n" + minute +"\n" + year + "\n" + month + "\n" + day +"\n";

	}
	
	public JSONObject pickProjectByName(String pName, Set<JSONObject> projects)
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