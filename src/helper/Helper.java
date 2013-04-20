package helper;

import model.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * This class performs all database related functions as well as a few other necessary functions
 * @author Will Henry
 * @author David Sawyer
 * 
 *
 */
public class Helper {

	/**
	 * Statement to retrieve the list of requirements
	 */
	private PreparedStatement getRequirementListStatement;

	/**
	 * Statement to retrieve a list of courses for a particular requirement
	 */
	private PreparedStatement getCourseListStatement;

	/**
	 * Statement to retrieve a list of sections for a particular course
	 */
	private PreparedStatement getSectionListStatement;

	/**
	 * Statement to retrieve a list of meetings for a particular section
	 */
	private PreparedStatement getMeetingListStatement;

	/**
	 * Statement to retrieve a list of sections for a particular course
	 */
	private PreparedStatement addSectionStatement;

	/**
	 * Statement to retrieve a list of meetings for a particular section
	 */
	private PreparedStatement addMeetingStatement;

	/**
	 * Finds a course for the section to link up with
	 */
	private PreparedStatement getCourseStatement;

	/**
	 * Resets the Section table
	 */
	private PreparedStatement resetSection;

	/**
	 * Resets the Meeting table
	 */
	private PreparedStatement resetMeeting;

	/**
	 * Statement to retrieve a specific section
	 */
	private PreparedStatement getSectionStatement;

	/**
	 * Empty constructor. Opens a connection to the database and sets up PreparedStatements
	 */
	public Helper() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			//System.out.println("---Instantiated MySQL driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/csci4300", "root", "mysql");
			//System.out.println("---Connected to MySQL!");

			getRequirementListStatement = conn.prepareStatement("select * from Requirement order by id");
			getCourseListStatement = conn.prepareStatement("select * from Course where reqFulfilled=?");
			getSectionListStatement = conn.prepareStatement("select * from Section where courseId=?");
			getMeetingListStatement = conn.prepareStatement("select * from Meeting where sectionId=?");
			addSectionStatement = conn.prepareStatement("insert into Section (callNum, creditHours," + 
					"instructor, courseId) values (?,?,?,?)");
			addMeetingStatement = conn.prepareStatement("insert into Meeting (timeStart, timeEnd, meetingDay," + 
					"roomNumber, buildingNumber, sectionId) values (?,?,?,?,?,?)");
			getCourseStatement = conn.prepareStatement("select * from Course where coursePrefix=? and courseNum=?");
			getSectionStatement = conn.prepareStatement("select * from Section where callNum=?");
			resetSection = conn.prepareStatement("truncate table Section");
			resetMeeting = conn.prepareStatement("truncate table Meeting");
		}
		catch(Exception e) {
			System.out.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	//	/**
	//	* prepared statement for getting requirements
	//	*/
	//	private PreparedStatement listRequirementsStatement;
	//	/**
	//	* prepared statement for getting courses
	//	*/
	//	private PreparedStatement listCoursesStatement;
	//	/**
	//	* prepared statement for getting sections
	//	*/
	//	private PreparedStatement listSectionsStatement;

	/**
	 * gets all requirements from the database
	 * @return list - an arrayList of all the requirements
	 */
	public ArrayList<Requirement> getRequirementList(){
		ArrayList<Requirement> list = new ArrayList<Requirement>();
		int id;
		String requirementArea;
		try{
			ResultSet set = getRequirementListStatement.executeQuery();
			// set the received values to create a Requirement object
			while(set.next()) {
				id=set.getInt("id");
				requirementArea=set.getString("requirementArea");
				Requirement requirement = new Requirement(id, requirementArea);
				list.add(requirement);
			}
		}
		catch(Exception e) {
			System.out.println("Error retrieving Requirements List\n " + e.getClass().getName() + ": " + e.getMessage());
		}
		return list;
	}


	/**
	 * get all courses that belong to a particular requirement
	 * @param r the requirement
	 * @return list - an array list of all courses of type r
	 */
	public ArrayList<Course> getCourseList(int reqFulfilled){
		ArrayList<Course> list = new ArrayList<Course>();
		int id;
		String coursePrefix, courseNum;
		try{
			getCourseListStatement.setInt(1, reqFulfilled);
			ResultSet set = getCourseListStatement.executeQuery();
			// set the received values to create a Course object
			while(set.next()) {
				id=set.getInt("id");
				coursePrefix=set.getString("coursePrefix");
				courseNum=set.getString("courseNum");
				Course course = new Course(id, reqFulfilled, coursePrefix, courseNum);
				list.add(course);
			}
		}
		catch(Exception e) {
			System.out.println("Error retrieving Course List\n " + e.getClass().getName() + ": " + e.getMessage());
		}
		return list;
	}


	/**
	 * get all sections that belong to a particular course
	 * @param c the course
	 * @return list - an arrayList of all sections of course c
	 */
	public ArrayList<Section> getSectionList(int courseId){
		ArrayList<Section> list = new ArrayList<Section>();
		int id;
		String /*title,*/ instructor, callNum, creditHours;
		ArrayList<Meeting> meetings = new ArrayList<Meeting>();
		try{
			getSectionListStatement.setInt(1, courseId);
			ResultSet set = getSectionListStatement.executeQuery();
			// set the received values to create a Section object
			while(set.next()) {
				id=set.getInt("id");
				callNum=set.getString("callNum");
				creditHours=set.getString("creditHours");
				instructor=set.getString("instructor");
				meetings = getMeetingList(id);
				Section section = new Section(id, callNum, creditHours, instructor, courseId, meetings);
				list.add(section);
			}
		}
		catch(Exception e) {
			System.out.println("Error retrieving Section List\n " + e.getClass().getName() + ": " + e.getMessage());
		}
		return list;
	}


	/**
	 * get all meetings that belong to a particular section
	 * @param c the course
	 * @return list - an arrayList of all meetings of section c
	 */
	public ArrayList<Meeting> getMeetingList(int sectionId){
		ArrayList<Meeting> list = new ArrayList<Meeting>();
		int id;
		String timeStart, timeEnd, meetingDay, roomNumber, buildingNumber;
		try{
			getMeetingListStatement.setInt(1, sectionId);
			ResultSet set = getMeetingListStatement.executeQuery();
			// set the received values to create a Meeting object
			while(set.next()) {
				id=set.getInt("id");
				timeStart=set.getString("timeStart");
				timeEnd=set.getString("timeEnd");
				meetingDay=set.getString("meetingDay");
				roomNumber=set.getString("roomNumber");
				buildingNumber=set.getString("buildingNumber");
				Meeting meeting = new Meeting(id, timeStart, timeEnd, meetingDay, roomNumber, buildingNumber, sectionId);
				list.add(meeting);
			}
		}
		catch(Exception e) {
			System.out.println("Error retrieving Section List\n " + e.getClass().getName() + ": " + e.getMessage());
		}
		return list;
	}


	/**
	 * Sets a new Section
	 * @param callNum		the call number of the section
	 * @param creditHours	the amount of credit hours the course gives
	 * @param title			the title of the section
	 * @param instructor	the instructor of the section
	 * @param courseId		the course that the section is of
	 * @return true if the Section was successfully added, false if failed 
	 */
	public boolean addSection(String callNum, String creditHours, /*String title,*/ String instructor, int courseId) {

		try {
			addSectionStatement.setString(1, callNum);
			addSectionStatement.setString(2, creditHours);
			//addSectionStatement.setString(3, title);
			addSectionStatement.setString(3, instructor);
			addSectionStatement.setInt(4, courseId);
			addSectionStatement.executeUpdate();
			//System.out.println("Added Section!");
		} catch (Exception e) {
			if (!e.getClass().getName().toString().equalsIgnoreCase("com.mysql.jdbc." +
					"exceptions.jdbc4.MySQLIntegrityConstraintViolationException")) {				
				System.out.println("Error adding section \n " + e.getClass().getName() + ": " + e.getMessage());
			}
			return false;
		}
		return true;
	}


	/**
	 * Sets a new Meeting
	 * @param timeStart			the time of the meeting
	 * @param timeEnd			the the ending time of the meeting
	 * @param meetingDay		the day of the meeting
	 * @param roomNumber		the instructor of the section
	 * @param buildingNumber	the building number of the meeting
	 * @param sectionId			the section the meeting is of
	 * @return true if the Meeting was successfully added, false if failed 
	 */
	public boolean addMeeting(String timeStart, String timeEnd, String meetingDay, String roomNumber,
			String buildingNumber, int sectionId) {

		try {
			addMeetingStatement.setString(1, timeStart);
			addMeetingStatement.setString(2, timeEnd);
			addMeetingStatement.setString(3, meetingDay);
			addMeetingStatement.setString(4, roomNumber);
			addMeetingStatement.setString(5, buildingNumber);
			addMeetingStatement.setInt(6, sectionId);
			addMeetingStatement.executeUpdate();
			//System.out.println("Added Meeting!");
		} catch (Exception e) {
			System.out.println("Error adding meeting \n " + e.getClass().getName() + ": " + e.getMessage());
			return false;
		}
		return true;
	}


	/**
	 * get a specific course
	 * @param r the requirement
	 * @return list - an array list of all courses of type r
	 */
	public Course getCourse(String coursePrefix, String courseNum){
		//System.out.println("touched the beginning");
		Course course = null;
		int id, reqFulfilled;
		String returnedCoursePrefix, returnedCourseNum;
		try{
			getCourseStatement.setString(1, coursePrefix);
			getCourseStatement.setString(2, courseNum);
			ResultSet set = getCourseStatement.executeQuery();
			// set the received values to create a Course object
			//System.out.println("touched out");
			if (set.next()) {
				//System.out.println("touched in");
				id=set.getInt("id");
				reqFulfilled=set.getInt("reqFulfilled");
				returnedCoursePrefix=set.getString("coursePrefix");
				returnedCourseNum=set.getString("courseNum");
				course = new Course(id, reqFulfilled, returnedCoursePrefix, returnedCourseNum);
			}
		}
		catch(Exception e) {
			System.out.println("Error retrieving single Course\n " + e.getClass().getName() + ": " + e.getMessage());
		}
		return course;
	}


	/**
	 * get section that has a certain call number
	 * @param callNum - the call number for the Section
	 * @return list - a section with the callNum
	 */
	public Section getSection(String callNum){
		Section section = null;
		int id, courseId;
		String instructor, newCallNum, creditHours;
		// we haven't populated meetings yet, so there is nothing to put in the list of meetings
		ArrayList<Meeting> meetings = null;
		try{
			getSectionStatement.setString(1, callNum);
			ResultSet set = getSectionStatement.executeQuery();
			// set the received values to create a Section object
			if (set.next()) {
				id=set.getInt("id");
				newCallNum=set.getString("callNum");
				creditHours=set.getString("creditHours");
				instructor=set.getString("instructor");
				courseId=set.getInt("courseId");
				section = new Section(id, newCallNum, creditHours, instructor, courseId, meetings);
			}
		}
		catch(Exception e) {
			System.out.println("Error retrieving Section List\n " + e.getClass().getName() + ": " + e.getMessage());
		}
		return section;
	}

	/**
	 * Resets the Section and Meeting tables
	 */
	public boolean resetSectionAndMeeting() {
		try{
			resetMeeting.executeQuery();
			resetSection.executeQuery();
		}
		catch(Exception e) {
			System.out.println("Error retrieving single Course\n " + e.getClass().getName() + ": " + e.getMessage());
			return false;
		}
		return true;
	}


	/**
	 * adds a section of a course to class list
	 * @param s the section
	 * @param classes the list of classes currently signed up for
	 */
	public void addClass(Section s, ArrayList<Section> classes){

	}
}
