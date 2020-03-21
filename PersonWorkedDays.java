import java.io.Serializable;

/**
 * JavaBean Class storing information about an employee's duration of work on a project
 */
public class PersonWorkedDays implements Serializable
{
    private int employeeId;
    private Long workedDays;

    /**
     * Default constructor
     */
    public PersonWorkedDays()
    {
        this.employeeId = 0;
        this.workedDays = 0L;
    }

    public int getEmployeeId()
    {
        return employeeId;
    }

    public void setEmployeeId(int employeeId)
    {
        this.employeeId = employeeId;
    }

    public Long getWorkedDays()
    {
        return workedDays;
    }

    public void setWorkedDays(Long workedDays)
    {
        this.workedDays = workedDays;
    }
}
