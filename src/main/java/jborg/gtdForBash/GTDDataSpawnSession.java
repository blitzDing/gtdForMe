package jborg.gtdForBash;

import java.io.IOException;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;


import allgemein.LittleTimeTools;


import consoleTools.*;

/**
 * This class is to get the Information needed to Setup
 * Projects or to note progress in those Projects. It
 * also has Methods to note termination of "Steps taken
 * in a Project. Or to Terminate a hole Progress. In 
 * both cases of termination it asks if it was a success
 * or a fail. Further more there it is possible to 
 * attache notes to a Project. And it's all done via
 * 
 * @author John Bernard
 * @author johnbernard@gmx.net
 */
public class GTDDataSpawnSession
{
	
	/** Project Name Error prompt.*/
	public static final String prjctNameError = "Name already in use.";	
	/** Project time and/or Goal not valid Error.*/
	public static final String prjctTimeOrGoalNotValideError = "Time and/or Goal of Project not valide.";
	/** Project time Error Array.*/
	public static final String prjctTimeError[] = new String[] 
			{"Project needs Deadline.", "Project dead before Living.", "Project Born after Now.", 
					"Project needs a Goal."};
	/** Project time Error index, Project needs DLDT*/
	public static final int indexOfPrjctTimeErrorNeedsDLDT = 0;
	/** Project time Error index, Project dead before born.*/
	public static final int indexOfPrjctTimeErrorDeadBeforeBorn = 1;
	/** Project time Error index, Project born after now.*/
	public static final int indexOfPrjctTimeErrorBornAfterNow = 2;
	/** Project time Error index, Project needs valid Goal.*/
	public static final int indexOfPrjctTimeErrorNeedsGoal = 3;

	/** Project Min. Deadline Prefix*/
	public static final String prjctDLDTHintPrefix = "Project Deadline. Min.: ";
	/** Project Minutes in Future Max. Prefix.*/
	public static final String prjctDLDTHintMid = " Minutes in Future. Max.: ";
	/** Project Years in Future Suffix.*/
	public static final String prjctDLDTHintSuffix = " Years in Future.";
	
	/** First Step Deadline Min. Prefix.*/
	public static final String stpDLDTHintPrefix = "\nDeadline must be between Projec NDDT: ";
	/** First Step Deadline Max. Prefix.*/
	public static final String stpDLDTHintMid = " and Project Deadline: ";
	
	/** Later than Now Deadline Min. Prefix.*/
	public static final String nowPrefix = "Now: ";
	/** Later than First Step Deadline Max. Prefix.*/
	public static final String extrStpDLDTHintMid = " and Project Deadline: ";
	
	public static final String stpTDTHintPrefix = "TDT must be between ";
	public static final String stpTDTHintMid = " and ";
		
	public static final String stepTerminationNotePhrase = "Termination Note:";
	public static final String wantToMakeTerminalNotePhrase = "want to make a Terminal note?";

	/** Question Prompt if u want to Change Project BDT.*/
	public static final String changeBDTQ = "Want to change Birthdatetime of Project? ";
	/** Question if there is a Project Deadline, Prompt.*/
	public static final String isThereAPrjctDeadlineQ = "Project got Deadline?";
	/** Question if TDT needs to be changed?*/
	public final static String wantToChangeTDTOfPrjctQ = "Wan't to change TDT of Project?";
	/** Question prompt.*/
	public final static String prjctSuccessQ = "Was Project a Success?";

	/** Project Name Request prompt.*/
	public static final String prjctNameR = "Project Name: ";
	/** Goal of Project Request prompt.*/
	public static final String goalR = "Goal of Project: ";
	/** BDT Request prompt.*/
	public static final String bdtR = "BDT of Project:";
	/** Project Deadline LDT Request, Prompt.*/
	public static final String prjctDLDTR = "Deadline for Project please!";
	/** Project Termination Note Request, Prompt.*/
	public final static String prjctTDTNoteR = "Please type ur TDT-Note?";
	/** Project TDT Request, Prompt.*/
	public final static String prjctWhenTDTR = "When took the Termination of this Project place?";

	/** Possible Project Deadline Status.*/
	public static final String prjctDeadlineNone = "No got no Deadline!";

	/** Step got Deadline Question, Prompt.*/
	public final static String stepDeadlineQ = "Step got Deadline?";
	/** Step got Success Question, Prompt.*/
	public static final String stepSuccessQ = "Was Step a Success?";

	/** Step TDT Request, Prompt.*/
	public final static String stepWhenTDTR = "When took the Termination of this Step place?";
	/** Step Desc Request, Prompt.*/
	public static final String stepDescR = "Describe Step:";
	/** Step Deadline Request, Prompt.*/
	public static final String stepDeadlineR = "Step DeadLine Please.";

	/** Possible Status for Step Deadline*/
	public final static String stepDeadlineNone = "No Step Deadline!";	

	/** first Step Index.*/
	public static final int firstStepIndex = 0;

	/** The minimal Minutes a Deadline has to be in the Future*/
	public static final int minMinutesInFutureDLDT = 5;
	/** The maximal Years a Deadline can be in the Future*/
	public static final int maxYearsInFutureDLDT = 100;
	
	public static final int maxDLDTYear = 2020;
	public static final int minBDTYear = 1800;
	public static final int minMonth = 1;
	public static final int minDay = 1;
	public static final int minHour = 0;
	public static final int minMinute = 0;

	/** Min. LocalDateTime for BirthDateTime.*/
	public static final LocalDateTime ancient = LocalDateTime.of(minBDTYear, minMonth, minDay, minHour, minMinute);
	
	/** Max. LocalDateTime a Deadline can be in the Future.*/
	public static final LocalDateTime farInFuture = LocalDateTime.of(maxDLDTYear, minMonth, minDay, minHour, minMinute);

	public static final int dldtRange = 100;
	

	
	public static final String illAExceMsg = "Don't know that Beholder.";
	
	public static final String differentBDTQstn = "Do You want change BDT of Step?";
	
	public static final String noteAddPhrase = "Write Note:";
	public static final boolean notTxtAQstn = true;
	
	private static StatusMGMT statusMGMT = StatusMGMT.getInstance();
	private static Set<String> stepStartStatuses = statusMGMT.getStatesOfASet(StatusMGMT.atStartSetName);
		
	public static final String stepStatusPhrase = "Choose Status: ";
	
	public static final String wantToChangeBDTOfStepQstn = "Want to change bdt of Step?";
	public static final String stepBDTMsgPrefix = "BDT of Project(";
	public static final String stepBDTMsgMid = ") - Now!(";
	public static final String stepBDTMsgSuffix = ") Step BDT must be in that Range.";
	
	public static final String stepDateTimeQstn = "DateTime of Step BDT: ";
	public static final String stepChooseStatusQstn = "Choose Step Status";
	
	public static final String wantToChangeTDTOfStepQstn = "Wan't to change TDT of Step?";
	public static final String wantToMakeTDTNoteQstn = "Wan't to make a TDT Note for the Projekt?";
	public static final String waitingForPhrase = "What u waiting for?";
	
	public static final String infoAlertTxtPhrase = "Remember Step Termination Note is Project Termination Note at this last Step.";
	public static final String deadLinePrjctQstn = "Deadline for Project";
	public static final String unknownDLLblTxt = "Unkown Deadline";
	public static final String makeDLBtnTxt = "Make Deadline";

	public static final String invalidePrjctName = "There is already a Project with that Name.";
	public static final String invalideStep = "Invalide Step.";
	
	public static final String deadLineUnknownStr = "UNKNOWN";
	public static final String stepSpawnExceptionFormerStepIsntTerminated = "Sorry former Step isn't Terminated.";
	public static final String stepSpawnExceptionStepAintValide = "Step ain't valide";

	public static final String stepIsNotViolatingTimeframeOfProjectMsg = "Step is not violating Timeframe of Project.";
	public static final String stepIsViolatingTimeframeOfProjectMsg = "Step is violating timeframe of former Step";

	public static final String stepIsViolatingTimeframeOfFormerStepMsg = "Step is violating timeframe of former Step";
	public static final String stepIsNotViolatingTimeframeOfFormerStepMsg = "Step is not violating timeframe of former Step";
	
	public static final String stepTExcIllglArmntPrefix = "IllegalArgument!!!";
	public static final String stepTExcJSONErrorMsg = "JSON macht Probleme";
	public static final String stepTimeDataIsValide = "Step Time Data is valide.";
	
	public static final String prjctTExcAllreadyDeadMsg = "Project Already Terminated.";
	public static final String prjctTDTAfterDLDTMsg = "Termination can't be after Deadline.";
	public static final String prjctTDTBeforeNDDT = "Termination can't be before Note-Down-Date-Time";
	public static final String prjctTDTAfterNow = "TDT can't be after now.";
	
	public static final String stepIsOkToItSelfMsg = "Step is Ok to it Self.";
	
	public static final String lastStepIsNotTerminated = "Last Step is not Terminated.";
	public static final String projectHasNoStepArrayOrHasNoSteps = "Project Has no Step Array or Has no Steps.";
	public static final String cantTerminateNullDataProjectJSONObject = "Can't Terminate Null. Project JSON-Object is Null.";

	public static final String prjctJSONIsNull = "Project JSON is Null.";
	public static final String stepJSONIsNull = "Project JSON is Null.";
	public static final String prjctAlreadyTerminated = "Project Already Terminated.";
	public static final String stepAlreadyTerminated = "Step Already Terminated.";

	
	List<String> stepIsNotOkayToItSelfMsgList = 
			new ArrayList<>(Arrays.asList("Step is Born after Now.",
							"Step has no Description. Please write a Description",
							"Deadline of Step can't be before Step Born.", 
							"Step can't be noted down before Born.",
							"Step can't have deadline before noted down."
							));
	
	public static final int indexOfStpIsBornAfterNow = 0;
	public static final int indexOfStpHasNoDesc = 1;
	public static final int indexOfStpDLDTBeforeBDT = 2;
	public static final int indexOfStpNotedBeforeBorn = 3;
	public static final int indexOfStpDLDTBeforeNDDT = 4;
	
	public static final String stpTerminationExceptionMsg = "Sorry Step is Already Terminated.";
	
	public static final String modifierSetName = "Project_modifier";
	public static final String showDataSetName = "Data_view";
	public static final String otherSetName = "other";

	public static final Set<String> commands = new HashSet<>();


	
	final InputStreamSession iss;
	
	/**
	 * The Constructor
	 * 
	 * @param iss InputStreamSession which is a wrapped up InputStream
	 * from System.in.
	 */
	public GTDDataSpawnSession(InputStreamSession iss)
	{
		this.iss = iss;
	}
	
	/**
	 * This Method asks for Information for a Maybe-one-day(MOD) Project. It forces valid Data.
	 * It receives Data via InputStreamSession It returns than a JSONObject with that Data. 
	 * MOD-Projects don't have any Steps or Deadline. It is possible to make an MOD-Project to
	 * an active Project by "Waking" it up There is Method that does that. The parameter: 
	 * knownProjectNames is the list of forbidden Project Names. Otherwise it would cause 
	 * problems when saving it. 
	 * 
	 * @param knownProjectsNames forbidden Names.
	 * @param statusMGMT allowed Statuses for Projects.
	 * @return JSONObject MOD-Project-Data.
	 * @throws IOException only when something with the InputStreamSession goes wrong.
	 */
	public JSONObject spawnMODProject(Set<String> knownProjectsNames, StatusMGMT statusMGMT) throws IOException 
	{

		System.out.println("");
		String name = iss.forcedString(prjctNameR);
		name = name.trim();
		if(knownProjectsNames.contains(name))
		{
			System.out.println(invalidePrjctName);
			return spawnMODProject(knownProjectsNames, statusMGMT);
		}

		JSONObject pJson = new JSONObject();
 
		String status = StatusMGMT.mod;
		LocalDateTime bdt = null;
		LocalDateTime nddt = LocalDateTime.now();

			
		System.out.println("");
		String goal = iss.getString(goalR);
			
		System.out.println("");
		boolean changeBDT = iss.forcedYesOrNo(changeBDTQ);

		LocalDateTime jetzt = LocalDateTime.now();
		if(changeBDT)
		{
			System.out.println("");
			bdt = iss.forcedDateTimeInOneLine(bdtR, ancient, jetzt);//must be born before now.
		}
		else bdt = nddt;
				
		pJson.put(ProjectJSONKeyz.nameKey, name);
		pJson.put(ProjectJSONKeyz.goalKey, goal);
		pJson.put(ProjectJSONKeyz.statusKey, status);
			
		pJson.put(ProjectJSONKeyz.DLDTKey, deadLineUnknownStr);
				
		String bdtStr = LittleTimeTools.timeString(bdt);
		pJson.put(ProjectJSONKeyz.BDTKey, bdtStr);
			
		String nddtStr = LittleTimeTools.timeString(nddt);
		pJson.put(ProjectJSONKeyz.NDDTKey, nddtStr);
		
		return pJson;

	}
	
	/**
	 * Creates an active Project from Data it receives from The User via InputStreamSession.
	 * It forces valid Data.
	 * 
	 * @param knownProjectsNames forbidden Names for the Project.
	 * @param statusMGMT allowed Statuses for the Project.
	 * @return JSONObject Project-Data.
	 * @throws IOException only if something with the InputStreamSession goes wrong.
	 */
	public JSONObject spawnNewProject(Set<String> knownProjectsNames, StatusMGMT statusMGMT) throws IOException
	{
		
		System.out.println("");
		String name = iss.forcedString(prjctNameR);
		name = name.trim();
		if(knownProjectsNames.contains(name))
		{
			System.out.println(invalidePrjctName);
			return spawnNewProject(knownProjectsNames, statusMGMT);//Force valide name.
		}

		JSONObject pJson = new JSONObject();
 
		String status = "";
		LocalDateTime bdt = null;
		LocalDateTime nddt = LocalDateTime.now();
		LocalDateTime dldt = null;
			
		System.out.println("");
		String goal = iss.forcedString(goalR);

		System.out.println("");
		boolean changeBDT = iss.forcedYesOrNo(changeBDTQ);

		if(changeBDT)
		{
			
			System.out.println("");
			bdt = iss.forcedDateTimeInOneLine(bdtR, ancient, LocalDateTime.now());//must be born before now.
		}
		else bdt = nddt;
				
		pJson.put(ProjectJSONKeyz.nameKey, name);
		pJson.put(ProjectJSONKeyz.goalKey, goal);
		pJson.put(ProjectJSONKeyz.statusKey, status);

		pJson.put(ProjectJSONKeyz.DLDTKey, deadLineUnknownStr);

		String bdtStr = LittleTimeTools.timeString(bdt);
		pJson.put(ProjectJSONKeyz.BDTKey, bdtStr);
			
		String nddtStr = LittleTimeTools.timeString(nddt);
		pJson.put(ProjectJSONKeyz.NDDTKey, nddtStr);

		boolean wantDLDT = iss.forcedYesOrNo(isThereAPrjctDeadlineQ);
		
		if(wantDLDT)
		{
			System.out.println("");
			System.out.println(prjctDLDTHintPrefix + minMinutesInFutureDLDT + prjctDLDTHintMid + maxYearsInFutureDLDT + prjctDLDTHintSuffix);
			
			dldt = iss.forcedDateTimeInOneLine(prjctDLDTR, LocalDateTime.now().plusMinutes(minMinutesInFutureDLDT), LocalDateTime.now().plusYears(maxYearsInFutureDLDT));
			String deadLineStr = LittleTimeTools.timeString(dldt);
			pJson.put(ProjectJSONKeyz.DLDTKey, deadLineStr);//Overwrites current "UNKNOWN" value.
		}
		else pJson.put(ProjectJSONKeyz.DLDTKey, prjctDeadlineNone);

		if(timeAndGoalOfActiveProjectIsValide(nddt, bdt, dldt, goal))
		{
			
			spawnStep(pJson);//Here status will be overwritten. Here step status will be equal project status.
			
			return pJson;
		}
		else
		{
			System.out.println(prjctTimeOrGoalNotValideError);
			return spawnNewProject(knownProjectsNames, statusMGMT);//Force valide Time.;
		}
	}
	
	/**
	 * Checks if Goal and the DateTimes make Sense.
	 * The oldest DateTime is BirthDateTime (bdt). The equally old or younger NoteDownDateTime (nddt).
	 * The youngest always younger than nddt is if exists DeadlineDateTime (dldt).
	 * 
	 * @param nddt NoteDownDateTime. Always close to now.
	 * @param bdt BirthDateTime. Supposed to be the Time when Project was born.
	 * @param dldt DeadlineDateTime. If exists it is the in the Future and will be checked from Time to time. If a "Check"
	 * finds out dldt of Project X is no longer in the Future it will Terminate this Project.
	 * @param goal. The Goal of Project can't be just whitespace or just nothing.
	 * 
	 * @return true if DateTimes make Sense and the Goal is not nothing or whitespace.
	 */
	private boolean timeAndGoalOfActiveProjectIsValide(LocalDateTime nddt, LocalDateTime bdt, 
			LocalDateTime dldt, String goal)
	{
		
		if(dldt!=null&&bdt.isAfter(dldt))//Maybe it is mod Project!!!
		{
			System.out.println(prjctTimeError[indexOfPrjctTimeErrorDeadBeforeBorn]);
			return false;
		}
		
		LocalDateTime jetzt = LocalDateTime.now();
		
		if(jetzt.isBefore(bdt))
		{
			System.out.println(prjctTimeError[indexOfPrjctTimeErrorBornAfterNow]);
			return false;
		}
		
		if(goal.trim().equals(""))
		{
			System.out.println(prjctTimeError[indexOfPrjctTimeErrorNeedsGoal]);
			return false;
		}
		
		return true;
	}

	/**
	 * Every non MOD-Project has at least one Step. With Project progress
	 * more Steps might be noted. This Method provides the Data for Steps
	 * it does so by asking the User via InputStreamSession.
	 * 
	 * @param pJson Project-Data. The Step belongs to that Project.
	 * @throws IOException only if something with the InputStreamSession 
	 * goes wrong.
	 */
	public void spawnStep(JSONObject pJson) throws IOException
	{


		JSONObject newStep = new JSONObject();
		int index = getIndexOfLastStepInPrjct(pJson);
		JSONObject oldStep;
		boolean isFirstStep = (index==firstStepIndex-1);
		
		JSONArray steps;
		if(isFirstStep)
		{
			steps = new JSONArray();
			oldStep = null;
		}
		else
		{
			steps = pJson.getJSONArray(ProjectJSONKeyz.stepArrayKey);
			oldStep = getLastStepOfProject(pJson);
			if(!stepIsAlreadyTerminated(oldStep))
			{
				System.out.println(stepSpawnExceptionFormerStepIsntTerminated);
				return;
			}
		}
		
		LocalDateTime nddtOfStep = LocalDateTime.now();
		LocalDateTime bdtOfStep;
		
		boolean differentBDT;

		String stepStatus = "";

			
		String bdtOfPrj = pJson.getString(ProjectJSONKeyz.BDTKey);
		LocalDateTime ldtBDTOfPrj = LittleTimeTools.LDTfromTimeString(bdtOfPrj);
		String jetzt = LittleTimeTools.timeString(LocalDateTime.now());
			
		System.out.println("");
		differentBDT = iss.forcedYesOrNo(wantToChangeBDTOfStepQstn);
	
		System.out.println("");
		
		if(differentBDT)
		{
					
			System.out.println(stepBDTMsgPrefix + bdtOfPrj + stepBDTMsgMid + jetzt + stepBDTMsgSuffix);
			bdtOfStep = iss.forcedDateTimeInOneLine(stepDateTimeQstn, ldtBDTOfPrj, LocalDateTime.now());
		}
		else bdtOfStep = nddtOfStep;
			
		List<String> sss = new ArrayList<>();
		sss.addAll(stepStartStatuses);
		System.out.println("");
		stepStatus = iss.forcedOutOfList(stepChooseStatusQstn, sss);
					
		String phrase;
		if(stepStatus.equals(StatusMGMT.waiting))phrase = waitingForPhrase;
		else phrase = stepDescR;

		String descriptionOfStep = iss.getString(phrase);
		
		String prjctNDDT = pJson.getString(ProjectJSONKeyz.NDDTKey);
		LocalDateTime ldtNDDTOfPrjct = LittleTimeTools.LDTfromTimeString(prjctNDDT);
		
		String prjctDeadLine = pJson.getString(ProjectJSONKeyz.DLDTKey);
		String deadLineStr = "";
		
		LocalDateTime prjctDLDTYear;
		if(!prjctDeadLine.equals(prjctDeadlineNone))prjctDLDTYear = LittleTimeTools.LDTfromTimeString(prjctDeadLine);
		else prjctDLDTYear = farInFuture;
		
		boolean gotDeadline = iss.forcedYesOrNo(stepDeadlineQ);
		
		if(gotDeadline)
		{
			if(isFirstStep)
			{
				System.out.println(stpDLDTHintPrefix + prjctNDDT + stpDLDTHintMid  + prjctDeadLine);
				LocalDateTime deadLineLDT = iss.forcedDateTimeInOneLine(stepDeadlineR, ldtNDDTOfPrjct, prjctDLDTYear);
				deadLineStr = LittleTimeTools.timeString(deadLineLDT);
			}
			else //iss got other parameters
			{
				System.out.println("");
				LocalDateTime minLDT = LocalDateTime.now();
				String minStr = LittleTimeTools.timeString(minLDT);
				System.out.println(nowPrefix + minStr + extrStpDLDTHintMid + prjctDeadLine);
				LocalDateTime deadLineLDT = iss.forcedDateTimeInOneLine(stepDeadlineR, minLDT, prjctDLDTYear);
				deadLineStr = LittleTimeTools.timeString(deadLineLDT);
			}
		}
		else 
		{	
			if(!prjctDeadLine.equals(prjctDeadlineNone))deadLineStr = prjctDeadLine;
			else deadLineStr = stepDeadlineNone;
		}
		
		newStep.put(StepJSONKeyz.DLDTKey, deadLineStr);
		newStep.put(StepJSONKeyz.statusKey, stepStatus);
		newStep.put(StepJSONKeyz.descKey, descriptionOfStep);
		newStep.put(StepJSONKeyz.NDDTKey, LittleTimeTools.timeString(nddtOfStep));
		newStep.put(StepJSONKeyz.BDTKey, LittleTimeTools.timeString(bdtOfStep));
		
		if(stepDataIsValide(pJson, newStep))
		{
			pJson.put(ProjectJSONKeyz.statusKey, stepStatus);//this overwrites old status!
						
			steps.put(index + 1, newStep);
			
			pJson.put(ProjectJSONKeyz.stepArrayKey, steps);
			
		}
		else
		{
			System.out.println(stepSpawnExceptionStepAintValide);
			spawnStep(pJson);
		}
	}

	/**
	 * Checks if the DateTimes of the Project and the last two Steps make Sense.
	 * 
	 * @param pJson Project-Data. Before the new Step is noted in Project-JSON.
	 * @param newStep last Step. Not noted in Project-JSON yet. Here to test if
	 * that is Okay.
	 * 
	 * @return true if adding the new Step to Project doesn't create a Time Paradox.
	 */
	public boolean stepDataIsValide(JSONObject pJson, JSONObject newStep)
	{
	
		int index = getIndexOfLastStepInPrjct(pJson);
		JSONObject oldStep = null;
		if(index>firstStepIndex)oldStep = getLastStepOfProject(pJson);
			
		String msg = stepIsOkToItsSelf(newStep);
		System.out.println("");
		if(!msg.equals(stepIsOkToItSelfMsg))
		{
			System.out.println(msg);
			return false;
		}
		else System.out.println(msg);

		msg = stepIsNotViolatingTimeframeOfProject(newStep, pJson);
		if(msg.equals(stepIsViolatingTimeframeOfProjectMsg))
		{
			System.out.println(msg);
			return false;
		}
		else System.out.println(msg);

		if(index>firstStepIndex)
		{
			msg = stepIsNotViolatingTimeframeOfFormerStep(oldStep, newStep);
			if(msg.equals(stepIsViolatingTimeframeOfFormerStepMsg))
			{
				System.out.println(msg);
				return false;
			}
			else System.out.println(msg);
		}
		
		System.out.println(stepTimeDataIsValide);
		return true;
	}
	
	/**
	 * As the name suggest this Method checks if the Step given does not violate
	 * Project Time Frame.
	 * 
	 * @param step not yet added to Project.
	 * @param pJson Project-Data.
	 *
	 * @return boolean if and only if step does not violate the given Projects 
	 * Time Frame by adding it to it.
	 */
	public String stepIsNotViolatingTimeframeOfProject(JSONObject step, JSONObject pJson)
	{
		

			
		String prjctDLStr = pJson.getString(ProjectJSONKeyz.DLDTKey);
		if(prjctDLStr.equals(prjctDeadlineNone))return stepIsNotViolatingTimeframeOfProjectMsg;

		LocalDateTime prjctDeadLine = LittleTimeTools.LDTfromTimeString(prjctDLStr);

		String dldtOfStepStr = step.getString(StepJSONKeyz.DLDTKey);
		LocalDateTime dldtOfStep = LittleTimeTools.LDTfromTimeString(dldtOfStepStr);
		
		if(prjctDeadLine.isBefore(dldtOfStep))return stepIsViolatingTimeframeOfProjectMsg;

		return stepIsNotViolatingTimeframeOfProjectMsg;
	}

	/**
	 * A Simple Test if oldStep is terminated before newStep is born.
	 * 
	 * @param oldStep former Step of a Project
	 * @param newStep successor of oldStep.
	 *
	 * @return stepIsNotViolatingTimeframeOfFormerStepMsg if DateTimes are Okay.
	 * else stepIsViolatingTimeframeOfFormerStepMsg.
	 */
	public String stepIsNotViolatingTimeframeOfFormerStep(JSONObject oldStep, JSONObject newStep)
	{
		
		String tdtOfOldStepStr = oldStep.getString(StepJSONKeyz.TDTKey);
		LocalDateTime tdtOfOldStep = LittleTimeTools.LDTfromTimeString(tdtOfOldStepStr);
		
		String bdtOfNewStepStr = newStep.getString(StepJSONKeyz.BDTKey);
		LocalDateTime bdtOfNewStep = LittleTimeTools.LDTfromTimeString(bdtOfNewStepStr);
		
		if(bdtOfNewStep.isBefore(tdtOfOldStep))return stepIsViolatingTimeframeOfFormerStepMsg;
		
		return stepIsNotViolatingTimeframeOfFormerStepMsg;
	}
	
	/**
	 * Checks if bdt, nddt and dldt make sense.
	 * 
	 * @param step Data in question.
	 * 
	 * @return if everything is alright: stepIsOkToItSelfMsg.
	 * else some: stepIsNotOkayToItSelfMsgList.get(x).
	 */
	public String stepIsOkToItsSelf(JSONObject step)
	{

		//stepIsNotOkayToitSelfMsgSet
		String bdtOfStepStr = step.getString(StepJSONKeyz.BDTKey);
		LocalDateTime bdtOfStep = LittleTimeTools.LDTfromTimeString(bdtOfStepStr);
		
		if(LocalDateTime.now().isBefore(bdtOfStep))return stepIsNotOkayToItSelfMsgList.get(indexOfStpIsBornAfterNow);

		String deadLineStr = step.getString(StepJSONKeyz.DLDTKey);
		String desc= step.getString(StepJSONKeyz.descKey);
		String nddtStr = step.getString(StepJSONKeyz.NDDTKey);
		String bdtStr = step.getString(StepJSONKeyz.BDTKey);

		if(desc.equals(""))return stepIsNotOkayToItSelfMsgList.get(indexOfStpHasNoDesc);

		LocalDateTime born = LittleTimeTools.LDTfromTimeString(bdtStr);
		
		LocalDateTime nddt = LittleTimeTools.LDTfromTimeString(nddtStr);
		
		if(nddt.isBefore(born)) return stepIsNotOkayToItSelfMsgList.get(indexOfStpNotedBeforeBorn);
		
		if(!deadLineStr.equals(stepDeadlineNone)&&!deadLineStr.equals(deadLineUnknownStr))
		{
			LocalDateTime dldt = LittleTimeTools.LDTfromTimeString(deadLineStr);
			
			if(dldt.isBefore(born)) return stepIsNotOkayToItSelfMsgList.get(indexOfStpDLDTBeforeBDT);
			if(dldt.isBefore(nddt)) return stepIsNotOkayToItSelfMsgList.get(indexOfStpDLDTBeforeNDDT);
		}

		return stepIsOkToItSelfMsg;
	}
	
	/**
	 * Receives A text from the User via InputStreamSession. If
	 * the text is equal to whitespace only or just nothing. then
	 * Nothing happens. Otherwise it adds a Note to the Root of
	 * the Project JSONObject given.
	 *
	 * @param pJson to be edited.
	 */
	public void addNote(JSONObject pJson)
	{
		JSONArray ja;
		boolean hasNoteArray = pJson.has(ProjectJSONKeyz.noteArrayKey);
		
		if(hasNoteArray)ja = pJson.getJSONArray(ProjectJSONKeyz.noteArrayKey);
		else ja = new JSONArray();
		
		
		String noteTxt = iss.getString(noteAddPhrase);
		if(!noteTxt.trim().equals(""))
		{
			ja.put(noteTxt);
			pJson.put(ProjectJSONKeyz.noteArrayKey, ja);
		}
	}
	
	/**
	 * Wakes MOD-Project up. This means it has a first Step
	 * a new Status and maybe deadline after this. Answers
	 * are forced via InputStreamSession.
	 * 
	 * @param pJson MOD-Project Data.
	 
	 * @throws IOException only when something goes wrong with InputStreamSession.
	 */
	public void wakeMODProject(JSONObject pJson) throws IOException
	{
		
		LocalDateTime bdt = LittleTimeTools.LDTfromTimeString(pJson.getString(ProjectJSONKeyz.BDTKey));
		LocalDateTime nddt = LocalDateTime.now();
		LocalDateTime dldt = null;
				
		String nddtStr = LittleTimeTools.timeString(nddt);

		System.out.println("");
		boolean gotDLDT = iss.forcedYesOrNo(isThereAPrjctDeadlineQ);
		
		String deadLineStr = "";
		if(gotDLDT)
		{
			dldt = iss.forcedDateTimeInOneLine(prjctDLDTR, LocalDateTime.now().plusMinutes(minMinutesInFutureDLDT), LocalDateTime.now().plusYears(maxYearsInFutureDLDT));
			deadLineStr = LittleTimeTools.timeString(dldt);
		}
		else deadLineStr = prjctDeadlineNone;
		
		String goal = pJson.getString(ProjectJSONKeyz.goalKey);
		if(timeAndGoalOfActiveProjectIsValide(nddt, bdt, dldt, goal))
		{
			
			pJson.put(ProjectJSONKeyz.NDDTKey, nddtStr);
			pJson.put(ProjectJSONKeyz.DLDTKey, deadLineStr);//Overwrites current "UNKNOWN" value.

			spawnStep(pJson);//Here status will be overwritten.;
		}
		else
		{
			System.out.println(prjctTimeOrGoalNotValideError);
			wakeMODProject(pJson);
		}
	}
	
	/**
	 * Simple check if Status of Step is a terminal one.
	 * 
	 * @param sJson Step Data.
	 * 
	 * @return true if Status of Step is a terminal one.
	 */
	public boolean stepIsAlreadyTerminated(JSONObject sJson)
	{
		
		String status = sJson.getString(StepJSONKeyz.statusKey);
		StatusMGMT statusMGMT = StatusMGMT.getInstance();
		String terminalSetName = StatusMGMT.terminalSetName;
		
		Set<String> terminalSet = statusMGMT.getStatesOfASet(terminalSetName);
		if(terminalSet.contains(status))return true;
		
		return false;
	}

	/**
	 * Might terminate the last Step of Project. Obstacles are:
	 * 1.Project or last Step already Terminated.
	 * 2.Project has no StepArray or no last Step.
	 *
	 * It asks Questions about Termination and forces Them via
	 * InputStreamSession. Then finally it alters the JSONData
	 * of that Project.
	 * 
	 * @param pJson the Project(-Data) in question.
	 * 
	 * @throws IOException only if something with InputStreamSession goes wrong.
	 */
	public void terminateStep(JSONObject pJson) throws IOException
	{

		if(pJson==null)
		{
			System.out.println(prjctJSONIsNull);
			return;
		}
		
		if(projectIsAlreadyTerminated(pJson))
		{
			System.out.println(prjctAlreadyTerminated);
			return;
		}

		JSONObject sJson = getLastStepOfProject(pJson);
		
		if(sJson==null)
		{
			System.out.println(stepJSONIsNull);
			return;
		}

		if(stepIsAlreadyTerminated(sJson))
		{
			System.out.println(stepAlreadyTerminated);
			return;
		}

		LocalDateTime jetzt = LocalDateTime.now();
		String jetztStr = LittleTimeTools.timeString(jetzt);
		
		String nddtOfStepStr = sJson.getString(StepJSONKeyz.NDDTKey);
		LocalDateTime nddtOfStep = LittleTimeTools.LDTfromTimeString(nddtOfStepStr);
		
		boolean wasItASuccess = iss.forcedYesOrNo(stepSuccessQ);

		String stepStatus;
		if(wasItASuccess)stepStatus = StatusMGMT.success;
		else stepStatus = StatusMGMT.failed;
		
		String terminalNote = "";
		boolean thereIsATerminalNote = iss.forcedYesOrNo(wantToMakeTerminalNotePhrase);
		if(thereIsATerminalNote)terminalNote = iss.forcedString(stepTerminationNotePhrase);
			
		LocalDateTime tdt = LocalDateTime.now();
		boolean wantToChangeTDTOfStep = iss.forcedYesOrNo(wantToChangeTDTOfStepQstn);
		if(wantToChangeTDTOfStep)
		{
			System.out.println(stpTDTHintPrefix + nddtOfStepStr + stpTDTHintMid + jetztStr);
			tdt = iss.forcedDateTimeInOneLine(stepWhenTDTR, nddtOfStep, jetzt);
		}
			
		sJson.put(StepJSONKeyz.statusKey, stepStatus);
		pJson.put(ProjectJSONKeyz.statusKey, StatusMGMT.needsNewStep);
		String when = LittleTimeTools.timeString(tdt);
		sJson.put(StepJSONKeyz.TDTKey, when);
		if(!terminalNote.trim().equals(""))sJson.put(StepJSONKeyz.TDTNoteKey, terminalNote);
	}

	/**
	 * Simple Check does what the Name suggests.
	 *
	 * @param pJson Project-Data.
	 *
	 * @return the answer to the question: "Is Project Terminated".
	 */
	public boolean projectIsAlreadyTerminated(JSONObject pJson)
	{
		
		String status = pJson.getString(ProjectJSONKeyz.statusKey);
		StatusMGMT statusMGMT = StatusMGMT.getInstance();
		String terminalSetName = StatusMGMT.terminalSetName;		
		Set<String> terminalSet = statusMGMT.getStatesOfASet(terminalSetName);
		
		if(terminalSet.contains(status))return true;
		
		return false;
	}
	
	/**
	 * If U Invoke this Method a give it a Project JSONObject. It will ask
	 * a few Informations from U. If the Project isn't already Terminated.
	 * I which case it wouldn't do anything. It forces u to give some
	 * Information about the Termination and then change the Status of the
	 * Project according to that. Infos are transfered via 
	 * InputStreamSession
	 *
	 * @param pJson JSONObject Project-Data.
	 * 
	 * @throws IOException if something goes wrong with the InputStreamSession.
	 */
	public void terminateProject(JSONObject pJson) throws IOException
	{
		
		if(pJson==null)
		{
			System.out.println(cantTerminateNullDataProjectJSONObject);
			return;
		}

		if(projectIsAlreadyTerminated(pJson)) 
		{
			System.out.println(prjctTExcAllreadyDeadMsg);
			return;
		}

		JSONObject stepJson = getLastStepOfProject(pJson);
		
		if(stepJson==null)
		{
			System.out.println(projectHasNoStepArrayOrHasNoSteps);
			return;
		}
		
		if(!stepIsAlreadyTerminated(stepJson))
		{
			System.out.println(lastStepIsNotTerminated);
			return;
		}
		
		
		System.out.println(infoAlertTxtPhrase);
		
		
		LocalDateTime jetzt = LocalDateTime.now();
		
		String prjctStatus = "";
		boolean success = iss.forcedYesOrNo(prjctSuccessQ);
			
		if(success)prjctStatus = StatusMGMT.success;
		else prjctStatus = StatusMGMT.failed;
		
		String terminalNote = "";
		boolean wantToMakeTDTNoteQuestion = iss.forcedYesOrNo(wantToMakeTDTNoteQstn);
		if(wantToMakeTDTNoteQuestion) terminalNote = iss.getString(prjctTDTNoteR);
				
		boolean wantChangeTDTQuestion = iss.forcedYesOrNo(wantToChangeTDTOfPrjctQ);
		LocalDateTime tdt = jetzt;
		
		LocalDateTime lastAction = getLastDateTimeOfProject(pJson);
		if(wantChangeTDTQuestion)tdt = iss.forcedDateTimeInOneLine(prjctWhenTDTR, lastAction, jetzt);

		String dldtStr = pJson.getString(ProjectJSONKeyz.DLDTKey);
		LocalDateTime dldt = LittleTimeTools.LDTfromTimeString(dldtStr);
		if(dldtStr.equals(prjctDeadlineNone))dldt = farInFuture;

		if(tdt.isAfter(dldt))
		{
			System.out.println(prjctTDTAfterDLDTMsg);
			return;
		}
				
		String nddtStr = pJson.getString(ProjectJSONKeyz.NDDTKey);
		LocalDateTime nddt = LittleTimeTools.LDTfromTimeString(nddtStr);
				
		if(tdt.isBefore(nddt))
		{
			System.out.println(prjctTDTBeforeNDDT);
			return;
		}
			
		if(tdt.isAfter(jetzt))
		{
			System.out.println(prjctTDTAfterNow);
			return;
		}
				
		pJson.put(ProjectJSONKeyz.statusKey, prjctStatus);
				
		String tdtStr = LittleTimeTools.timeString(tdt);
		pJson.put(ProjectJSONKeyz.TDTKey, tdtStr);

		if(!terminalNote.trim().equals(""))pJson.put(ProjectJSONKeyz.TDTNoteKey, terminalNote);
	}
	
	/**
	 * Returns the Index of last(youngest) Step JSON Object in
	 * Project JSON Object. If there is no StepArray or no Steps
	 * it returns a negative value;
	 * 
	 * @param pJson Project-Data.
	 * 
	 * @return int index of last Step.
	 */
	public int getIndexOfLastStepInPrjct(JSONObject pJson)
	{
		JSONArray stepArray;
		
		if(pJson.has(ProjectJSONKeyz.stepArrayKey))
		{
			stepArray = pJson.getJSONArray(ProjectJSONKeyz.stepArrayKey);
			return stepArray.length()-1;
		}
		
		return firstStepIndex-1;
	}
	
	/**
	 * This gets u the last (youngest) step of the Project in question.
	 * If for some Reason the Project has no StepArray or no Step JSON
	 * Objects in that Array it returns null.
	 * 
	 * @param pJson Project JSONObject
	 * 
	 * @return step JSONObject
	 */
	public JSONObject getLastStepOfProject(JSONObject pJson)
	{
		int indexOfLastStep = getIndexOfLastStepInPrjct(pJson);
		if(indexOfLastStep<firstStepIndex)return null;
		
		JSONArray stepArray = pJson.getJSONArray(ProjectJSONKeyz.stepArrayKey);

		return stepArray.getJSONObject(indexOfLastStep);
	}
	
	public LocalDateTime getLastDateTimeOfProject(JSONObject pJson)
	{
		JSONObject step = getLastStepOfProject(pJson);
		String tdtStr = step.getString(StepJSONKeyz.TDTKey);
		
		return LittleTimeTools.LDTfromTimeString(tdtStr);
	}
}