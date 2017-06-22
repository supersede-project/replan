package entities;

import java.util.*;

/**
 * Represents the schedule of an employee during a certain number of weeks
 * and takes care of assigning features to said employee.
 */
public class Schedule {
    private List<EmployeeWeekAvailability> weeks;
    private Employee employee;
    private Set<PlannedFeature> plannedFeatures;

    // The number of hours left this employee has for the whole iteration
    private double totalHoursLeft;
    private int nbWeeks;
    private double hoursPerWeek;

    public Schedule(Employee employee, int nbWeeks, double hoursPerWeek) {
        this.employee = employee;
        this.nbWeeks = nbWeeks;
        this.hoursPerWeek = hoursPerWeek;

        totalHoursLeft = nbWeeks * employee.getWeekAvailability();

        weeks = new ArrayList<>();
        plannedFeatures = new HashSet<>();
    }

    /**
     * Tries to schedule a PlannedFeature in the first available week
     * @param pf the PlannedFeature to be scheduled
     * @return a boolean indicating whether the PlannedFeature could be scheduled or not
     */
    public boolean scheduleFeature(PlannedFeature pf) {

        double featureHoursLeft = pf.getFeature().getDuration();

        // Not enough hours left for this feature in the iteration
        if (totalHoursLeft < featureHoursLeft) return false;

        EmployeeWeekAvailability week = getCurrentWeek();

        double remainingWeekHours = week.getRemainHoursAvailable();

        EmployeeWeekAvailability previousWeek = getPreviousWeek(week);


        PlannedFeature lastPlanned = getLastPlannedFeature(week, previousWeek);


        if (featureHoursLeft <= remainingWeekHours) {
            double newBeginHour = lastPlanned == null ? week.getEndHour() : lastPlanned.getEndHour();
            pf.setBeginHour(newBeginHour);

            week.addPlannedFeature(pf);
            week.setRemainHoursAvailable(remainingWeekHours - featureHoursLeft);

            pf.setEndHour(pf.getBeginHour() + featureHoursLeft);

            week.setEndHour(pf.getEndHour());

            plannedFeatures.add(pf);

            totalHoursLeft -= featureHoursLeft;
        } else {
            double pfBeginHour = lastPlanned == null ? week.getEndHour() : lastPlanned.getEndHour();;
            double pfEndHour = pfBeginHour;
            while (featureHoursLeft > 0.0) {
                week.addPlannedFeature(pf);

                plannedFeatures.add(pf);

                double doneHours = Math.min(featureHoursLeft, remainingWeekHours);

                featureHoursLeft -= doneHours;
                totalHoursLeft -= doneHours;

                pfEndHour += featureHoursLeft > 0.0 ? normalizeDoneHours(doneHours) : doneHours;

                week.setRemainHoursAvailable(remainingWeekHours - doneHours);
                week.setEndHour(pfEndHour);

                week = getCurrentWeek();
                remainingWeekHours = week.getRemainHoursAvailable();
            }
            pf.setBeginHour(pfBeginHour);
            pf.setEndHour(pfEndHour);
        }

        return true;
    }


    /* --- PRIVATE --- */
    private double normalizeDoneHours(double doneHours) {
        return doneHours * (hoursPerWeek/employee.getWeekAvailability());
    }

    // Normalizes the ending hour of a PlannedFeature to a week of hoursPerWeek hours
    private void normalize(PlannedFeature pf, EmployeeWeekAvailability week) {
        int weekNumber = weeks.indexOf(week) + 1;
        pf.setEndHour(hoursPerWeek * weekNumber);
    }

    private boolean isOver(EmployeeWeekAvailability week) {
        return week.getRemainHoursAvailable() == 0;
    }

    // Returns the first non-full week of the employee, or a new one if there isn't any.
    private EmployeeWeekAvailability getCurrentWeek() {
        for (EmployeeWeekAvailability week : this.weeks)
            if (week.getRemainHoursAvailable() > 0.0)
                return week;

        EmployeeWeekAvailability week = new EmployeeWeekAvailability(0.0, employee.getWeekAvailability());

        weeks.add(week);

        EmployeeWeekAvailability previous = getPreviousWeek(week);

        if (previous != null) {
            week.setBeginHour(previous.getEndHour());
            week.setEndHour(week.getBeginHour());
        }

        return week;
    }

    // Returns the previous week to the given one, null if the given one is the first
    private EmployeeWeekAvailability getPreviousWeek(EmployeeWeekAvailability week) {
        int index = weeks.indexOf(week) - 1;

        return index < 0 ? null : weeks.get(index);
    }

    private PlannedFeature getLastPlannedFeature(EmployeeWeekAvailability week, EmployeeWeekAvailability previousWeek) {
        List<PlannedFeature> jobs = week.getPlannedFeatures();
        if (!jobs.isEmpty())
            return jobs.get(jobs.size() - 1);

        if (previousWeek == null) return null;

        jobs = previousWeek.getPlannedFeatures();
        if (!jobs.isEmpty())
            return jobs.get(jobs.size() - 1);

        return null;
    }


    /* --- PUBLIC --- */

    public List<EmployeeWeekAvailability> getAllWeeks() {
        weeks.removeIf(week -> week.getPlannedFeatures().isEmpty());
        return weeks;
    }

    public boolean contains(PlannedFeature pf) {
        for (EmployeeWeekAvailability week : weeks) {
            if (week.getPlannedFeatures().contains(pf)) {
                return true;
            }
        }
        return false;
    }

    public List<PlannedFeature> getPlannedFeatures() {
        return new ArrayList<>(plannedFeatures);
    }

    @Override
    public String toString() {
        double availability = employee.getWeekAvailability();
        double hours = availability * nbWeeks - totalHoursLeft;

        return String.format(
                "%f hours planned over %d weeks with an availability of %f hours",
                hours, getAllWeeks().size(), availability);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Schedule schedule = (Schedule) o;

        if (Double.compare(schedule.totalHoursLeft, totalHoursLeft) != 0) return false;
        if (nbWeeks != schedule.nbWeeks) return false;
        if (weeks != null ? !weeks.equals(schedule.weeks) : schedule.weeks != null) return false;
        if (!employee.equals(schedule.employee)) return false;
        return plannedFeatures != null ? plannedFeatures.equals(schedule.plannedFeatures) : schedule.plannedFeatures == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = weeks != null ? weeks.hashCode() : 0;
        result = 31 * result + employee.hashCode();
        result = 31 * result + (plannedFeatures != null ? plannedFeatures.hashCode() : 0);
        temp = Double.doubleToLongBits(totalHoursLeft);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + nbWeeks;
        return result;
    }
}
