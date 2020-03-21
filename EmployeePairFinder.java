
import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static java.time.temporal.ChronoUnit.DAYS;

/**
 * A class fetching data from a text file and manipulate it to find a pair of Employees.
 * @author Yozdzhan Ahmed
 */

public class EmployeePairFinder
{
    /** Static method to convert text data to a JavaBean.
     * @param fileName The name of the file to be traversed
     * @return Returns a List of type JavaBean "EmployeeProject"
     * @throws Exception May throw FileNotFoundException or Exception
     */
    public static List<EmployeeProject> getEmployeeProjects(String fileName) throws Exception
    {
        //Instantiating the return List
        List<EmployeeProject> employeeProjectsList = new ArrayList<>();
        //Checking if the given filename is empty and return empty List
        if(fileName.isEmpty()) return employeeProjectsList;
        //Getting the "src" folder Path where files are stored
        String systemPath = System.getProperty("user.dir") + "\\src";
        //Trying to create a File object with the given path and file name
        File file = new File(systemPath + "\\"+ fileName.trim());
        //Initialising our scanner object which is going to iterate through the file
        try (Scanner scanner = new Scanner(new BufferedReader(new FileReader(file))))
        {
            //Setting the delimiter to our scanner object
            scanner.useDelimiter(",");
            //Iterating trough our file
            while (scanner.hasNextLine())
            {
                String currentLine = scanner.nextLine();
                String[] lineData = currentLine.replaceAll("\\s+", "").split(",");
                //Checking the format of the data in the current Line of the file
                if (lineData.length == 4 && lineData[0].matches("\\d{3}") && lineData[1].matches("\\d{2}") && lineData[2].matches("^\\d{4}-\\d{2}-\\d{2}$") && (lineData[3].matches("^\\d{4}-\\d{2}-\\d{2}$") || lineData[3].toLowerCase().equals("null")))
                {
                    //Creating a JavaBean object of "EmployeeProjects" where the information from the file will be stored
                    EmployeeProject ep = new EmployeeProject();
                    ep.setEmployeeId(Integer.parseInt(lineData[0]));
                    ep.setProjectId(Integer.parseInt(lineData[1]));
                    ep.setDateFrom(LocalDate.parse(lineData[2]));
                    //Checking the value of the date and pass current date if "NULL"
                    if (lineData[3].toLowerCase().equals("null"))
                    {
                        ep.setDateTo(LocalDate.now());
                    }
                    else
                    {
                        ep.setDateTo(LocalDate.parse(lineData[3]));
                    }
                    //Adding the object to our result List
                    employeeProjectsList.add(ep);
                }
                else
                {
                    //If formatting of the data in the file is incorrect
                    throw new Exception("The data in the provided file is not formatted correctly!");
                }
            }
        }
        return employeeProjectsList;
    }

    /** Static method which accepts JavaBean of type "EmployeeProject" and sorts it over the ID's of projects
     * @param employeeProjects  The JavaBean object
     * @return Returns a sorted Map, having ID's of the projects as keys and List of employees as values
     */
    public static Map<Integer,List<PersonWorkedDays>> getProjectParticipants(List<EmployeeProject> employeeProjects)
    {
        Map<Integer, List<PersonWorkedDays>> projectParticipants = new HashMap<>();

        if(employeeProjects.isEmpty()) return projectParticipants;

        //Iterating through all the information collected from the file
        for(EmployeeProject emp : employeeProjects)
        {
            //Creating a JavaBean holding data for the EmployeeId and worked hours on a particular project
            PersonWorkedDays pwd = new PersonWorkedDays();
            pwd.setEmployeeId(emp.getEmployeeId());
            pwd.setWorkedDays(DAYS.between(emp.getDateFrom(),emp.getDateTo()));
            //Creating an ArrayList with the EmployeeId and hours participated for the current project
            List<PersonWorkedDays> tmpList = new ArrayList<>();
            //Adding data to the ArrayList, which will holds information about ID's of employees and days worked on a particular project
            tmpList.add(pwd);
            //Checking if our Map is empty and initialise it
            if(projectParticipants.isEmpty())
            {
                projectParticipants.put(emp.getProjectId(), tmpList);
            }
            //If the Map is not empty
            else
            {
                //Checking if we have already added the ID of the project as keys in to the Map
                if(projectParticipants.containsKey(emp.getProjectId()))
                {
                    //Adding information about the new Employee, participated in this project and merging the List corresponding to the current project ID
                    projectParticipants.merge(emp.getProjectId(), tmpList, (list1, list2) ->
                            Stream.of(list1, list2)
                                    .flatMap(Collection::stream)
                                    .collect(Collectors.toList()));
                }
                //When our Map is not empty and the current project is not present
                else
                {
                    projectParticipants.put(emp.getProjectId(), tmpList);
                }
            }
        }

        //Sorting the Map's values by the duration employees worked descending
        for (Map.Entry<Integer, List<PersonWorkedDays>> entry : projectParticipants.entrySet())
        {
            entry.getValue().sort((v1, v2) -> v2.getWorkedDays().compareTo(v1.getWorkedDays()));
        }
        //Returning the result
        return projectParticipants;
    }

    /** Static method which finds the pair of the employees worked the longest on a particular project
     * @param projectParticipants The sorted Map from which the pair will be taken out
     * @return A Map containing one entry with the Key being the ID of the project and a pair of employee ID's and days worked on the project
     */
    public static Map<Integer,List<PersonWorkedDays>> getLongestParticipatedEmployeePair(Map<Integer, List<PersonWorkedDays>> projectParticipants)
    {
        Map<Integer,List<PersonWorkedDays>> pairList = new HashMap<>();
        if(projectParticipants.isEmpty()) return pairList;
        //Iterating trough the Map
        projectParticipants.forEach((k,v)->{
                //Checking if the value of the map has 2 or more values(meaning we have at least one pair of employees)
                if(v.size() >= 2)
                {
                    //If our Map holding the pair of employees is empty we assign the first value
                    if(pairList.isEmpty())
                    {
                        //Adding only the first two values
                        List<PersonWorkedDays> tmp = new ArrayList<>(); // Temporary ArrayList to store the pair of employees to be added to the pairList Map
                        for(int i = 0; i < 2; i++)
                        {
                            tmp.add(v.get(i));
                        }
                        pairList.put(k,tmp);

                    }
                    //If the pair List is not empty we check whether the current value in the map has more worked days than the current pair in the List
                    else
                    {
                        //Checking if the worked days of the pairList is less than the current pair and replace the existing pair with the new one if true
                        if((pairList.entrySet().iterator().next().getValue().get(0).getWorkedDays() + pairList.entrySet().iterator().next().getValue().get(1).getWorkedDays()) < (v.get(0).getWorkedDays() + v.get(1).getWorkedDays()))
                        {
                            pairList.clear();
                            List<PersonWorkedDays> tmp = new ArrayList<>();
                            for(int i = 0; i < 2; i++)
                            {
                                tmp.add(v.get(i));
                            }
                            pairList.put(k,tmp);
                        }
                    }
                }

        });

        return pairList;
    }

    public static void main(String[] args)
    {
        System.out.println("Please enter the name of the file you would like to fetch data from along with it's extension. Example: \"test.txt\"\n");
        Scanner scanner = new Scanner(System.in);
        String fileName = scanner.nextLine();

        try{
            List<EmployeeProject> employeeProjectList = new ArrayList<>(EmployeePairFinder.getEmployeeProjects(fileName));
            if(employeeProjectList.isEmpty()){
                System.out.println("No Data in the File..");
            }
            else
            {
                Map<Integer,List<PersonWorkedDays>> projectParticipants = new HashMap<>(EmployeePairFinder.getProjectParticipants(employeeProjectList));
                Map<Integer,List<PersonWorkedDays>> longestParticipatedEmployeesPairs = new HashMap<>(EmployeePairFinder.getLongestParticipatedEmployeePair(projectParticipants));
                if(longestParticipatedEmployeesPairs.isEmpty())
                {
                    System.out.println("No pairs found..");
                }
                else
                {
                    longestParticipatedEmployeesPairs.forEach((key,val)->{
                        System.out.println("The ID of the project with the most worked hours by pair of employees is: " + key);
                        System.out.println("The following Employees worked on the project:");
                        val.forEach((v)-> System.out.println("| Employee ID: " + v.getEmployeeId()+" | " + "Worked days on the project:"+ v.getWorkedDays()+ " |"));
                    });
                }
            }
        }

        catch ( FileNotFoundException ex)
        {
            System.out.println("The specified file was not found  " + ex.getMessage() + " Stack trace: " + Arrays.toString(ex.getStackTrace()));
        }
        catch ( Exception e)
        {
            System.out.println(e.getMessage() + Arrays.toString(e.getStackTrace()));
        }
    }
}
