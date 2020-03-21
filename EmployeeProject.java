import java.time.LocalDate;
import java.io.Serializable;

/**
 * JavaBean Class containing data about employees worked over an project starting on a certain date and finishing on an other
 */
public class EmployeeProject implements Serializable
{
    private int employeeId;
    private int projectId;
    private LocalDate dateFrom;
    private LocalDate dateTo;

    /**
     * Default Constructor
     */
    public EmployeeProject()
    {
        this.employeeId = 0;
        this.projectId = 0;
        this.dateFrom = LocalDate.now();
        this.dateTo = LocalDate.now();
    }

    public int getEmployeeId()
    {
        return employeeId;
    }

    public void setEmployeeId(int employeeId)
    {
        this.employeeId = employeeId;
    }

    public LocalDate getDateTo()
    {
        return dateTo;
    }

    public void setDateTo(LocalDate dateTo)
    {
        this.dateTo = dateTo;
    }

    public LocalDate getDateFrom()
    {
        return dateFrom;
    }

    public void setDateFrom(LocalDate dateFrom)
    {
        this.dateFrom = dateFrom;
    }

    public int getProjectId()
    {
        return projectId;
    }

    public void setProjectId(int projectId)
    {
        this.projectId = projectId;
    }

    @Override
    public String toString()
    {
        return "EmployeeProjects{" +
                "employeeId=" + employeeId +
                ", projectId=" + projectId +
                ", dateFrom=" + dateFrom +
                ", dateTo=" + dateTo +
                '}';
    }

}
