package entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the schedule of an employee during a certain number of weeks
 * and takes care of assigning features to said employee.
 */
public class Schedule {
    private List<EmployeeWeekAvailability> weeks;

    // The number of hours left this employee has for the whole iteration
    private double totalHoursLeft;

    public Schedule(Employee employee, int nbWeeks, double hoursPerWeek) {
        totalHoursLeft = nbWeeks * employee.getWeekAvailability();

        weeks = new ArrayList<>(nbWeeks);

        for (int i = 0; i < nbWeeks; ++i)
            weeks.add(new EmployeeWeekAvailability(0.0, employee.getWeekAvailability()));

    }

    /**
     * Tries to schedule a PlannedFeature in the first available week
     * @param pf the PlannedFeature to be scheduled
     * @return a boolean indicating whether the PlannedFeature could be scheduled or not
     */
    public boolean scheduleFeature(PlannedFeature pf) {
        EmployeeWeekAvailability week = getCurrentWeek();

        // All weeks are full
        if (week == null) return false;

        double remainingWeekHours = week.getRemainHoursAvailable();
        double featureHoursLeft = pf.getFeature().getDuration();

        // Not enough hours left for this feature in the iteration
        if (totalHoursLeft < featureHoursLeft) return false;

        // We need to do this the first time we schedule a feature because EmployeeWeekAvailability defaults to 0.0
        // and that would make Math.min useless.
        if (week.getPlannedFeatures().isEmpty()) week.setBeginHour(pf.getBeginHour());
        else week.setBeginHour(Math.min(pf.getBeginHour(), week.getBeginHour()));

        if (featureHoursLeft >= remainingWeekHours) {
            week.addPlannedFeature(pf);
            week.setRemainHoursAvailable(remainingWeekHours - featureHoursLeft);
            week.setEndHour(week.getEndHour() + featureHoursLeft);

            totalHoursLeft -= featureHoursLeft;
        } else {
            while (featureHoursLeft > 0.0) {
                week.addPlannedFeature(pf);

                double doneHours = Math.min(featureHoursLeft, remainingWeekHours);
                featureHoursLeft -= doneHours;
                totalHoursLeft -= doneHours;
                week.setRemainHoursAvailable(remainingWeekHours - doneHours);

                week = getCurrentWeek();
                remainingWeekHours = week.getRemainHoursAvailable();
            }
        }
        return true;
    }


    /**
     * @return The first non-full week of the employee, or null if there isn't any.
     */
    public EmployeeWeekAvailability getCurrentWeek() {
        for (EmployeeWeekAvailability week : weeks)
            if (week.getRemainHoursAvailable() > 0)
                return week;

        return null;
    }

    public List<EmployeeWeekAvailability> getAllWeeks() {
        return weeks;
    }

    public List<EmployeeWeekAvailability> getNonEmptyWeeks() {
        List<EmployeeWeekAvailability> aux = new ArrayList<>();
        for (EmployeeWeekAvailability week : weeks)
            if (!week.getPlannedFeatures().isEmpty())
                aux.add(week);

        return aux;
    }
}
