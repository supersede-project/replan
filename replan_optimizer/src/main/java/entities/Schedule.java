package entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the schedule of an employee during a certain number of weeks
 * and takes care of assigning features to said employee.
 */
public class Schedule {
    private List<EmployeeWeekAvailability> weeks;
    private Employee employee;

    // The number of hours left this employee has for the whole iteration
    private double totalHoursLeft;

    public Schedule(Employee employee, int nbWeeks, double hoursPerWeek) {
        this.employee = employee;
        totalHoursLeft = nbWeeks * employee.getWeekAvailability();

        weeks = new ArrayList<>();

        //for (int i = 0; i < nbWeeks; ++i)
            //weeks.add(new EmployeeWeekAvailability(0.0, employee.getWeekAvailability()));

    }

    /**
     * Tries to schedule a PlannedFeature in the first available week
     * @param pf the PlannedFeature to be scheduled
     * @return a boolean indicating whether the PlannedFeature could be scheduled or not
     */
    public boolean scheduleFeature(PlannedFeature pf) {
        EmployeeWeekAvailability week = getCurrentWeek();

        // All weeks are full
        //if (week == null) return false;

        double remainingWeekHours = week.getRemainHoursAvailable();
        double featureHoursLeft = pf.getFeature().getDuration();

        // Not enough hours left for this feature in the iteration
        //if (totalHoursLeft < featureHoursLeft) return false;

        EmployeeWeekAvailability previousWeek = getPreviousWeek(week);

        if (week.getPlannedFeatures().isEmpty()) {
            if (previousWeek != null) {
                week.setBeginHour(Math.max(pf.getBeginHour(), previousWeek.getEndHour()));
                week.setEndHour(week.getBeginHour());
                //pf.setBeginHour(week.getBeginHour());
                //pf.setEndHour(week.getBeginHour() + pf.getFeature().getDuration());
            } else {
                week.setBeginHour(pf.getBeginHour());
                week.setEndHour(week.getBeginHour());
            }
        }

        // Can be done entirely this week
        if (featureHoursLeft <= remainingWeekHours) {
            pf.setBeginHour(week.getEndHour());
            pf.setEndHour(pf.getBeginHour() + pf.getFeature().getDuration());

            week.addPlannedFeature(pf);
            week.setRemainHoursAvailable(remainingWeekHours - featureHoursLeft);
            week.setEndHour(week.getEndHour() + featureHoursLeft);

            totalHoursLeft -= featureHoursLeft;
        } else {
            double pfBeginHour = week.getEndHour();
            double pfEndHour = week.getEndHour();
            while (featureHoursLeft > 0.0) {
                week.addPlannedFeature(pf);

                double doneHours = Math.min(featureHoursLeft, remainingWeekHours);

                featureHoursLeft -= doneHours;
                totalHoursLeft -= doneHours;

                week.setRemainHoursAvailable(remainingWeekHours - doneHours);
                week.setEndHour(week.getEndHour() + doneHours);

                pfEndHour = week.getEndHour();

                EmployeeWeekAvailability nextWeek = getCurrentWeek();
                if (nextWeek != week) {
                    nextWeek.setBeginHour(week.getEndHour());
                    nextWeek.setEndHour(nextWeek.getBeginHour());
                }

                week = nextWeek;
                remainingWeekHours = week.getRemainHoursAvailable();
            }
            pf.setBeginHour(pfBeginHour);
            pf.setEndHour(pfEndHour);
        }
        return true;
    }


    /**
     * @return The first non-full week of the employee, or null if there isn't any.
     */
    public EmployeeWeekAvailability getCurrentWeek() {
        for (EmployeeWeekAvailability week : weeks)
            if (week.getRemainHoursAvailable() > 0.0)
                return week;

        EmployeeWeekAvailability week =
                new EmployeeWeekAvailability(0.0, employee.getWeekAvailability());
        weeks.add(week);
        return week;
        //return null;
    }

    /**
     *
     * @param week
     * @return The previous week to the given one, null if the given one is the first
     */
    public EmployeeWeekAvailability getPreviousWeek(EmployeeWeekAvailability week) {
        int index = weeks.indexOf(week) - 1;

        return index < 0 ? null : weeks.get(index);
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
