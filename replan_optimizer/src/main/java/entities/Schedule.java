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

    public Schedule(Employee employee, int nbWeeks) {
        this.employee = employee;
        this.nbWeeks = nbWeeks;
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
            pf.setEndHour(pf.getBeginHour() + featureHoursLeft);

            if (lastPlanned != null && pf.getBeginHour() < lastPlanned.getEndHour()) {
                String s = "dummy";
            }

            week.addPlannedFeature(pf);
            week.setRemainHoursAvailable(remainingWeekHours - featureHoursLeft);
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

                pfEndHour += doneHours;

                week.setRemainHoursAvailable(remainingWeekHours - doneHours);
                week.setEndHour(pfEndHour);

                /*
                EmployeeWeekAvailability nextWeek = getCurrentWeek();
                if (nextWeek != week) {
                    nextWeek.setBeginHour(week.getEndHour());
                    nextWeek.setEndHour(nextWeek.getBeginHour());
                }
                week = nextWeek;
                */
                week = getCurrentWeek();
                remainingWeekHours = week.getRemainHoursAvailable();
            }
            pf.setBeginHour(pfBeginHour);
            pf.setEndHour(pfEndHour);

            if (lastPlanned != null && pf.getBeginHour() < lastPlanned.getEndHour()) {
                String s = "dummy";
            }
        }

        if (lastPlanned != null && pf.getBeginHour() < lastPlanned.getEndHour()) {
            String s = "dummy";
        }


        return true;
    }


    /**
     * @return The first non-full week of the employee, or a new one if there isn't any.
     */
    public EmployeeWeekAvailability getCurrentWeek() {
        for (EmployeeWeekAvailability week : weeks)
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

    /**
     *
     * @param week
     * @return The previous week to the given one, null if the given one is the first
     */
    public EmployeeWeekAvailability getPreviousWeek(EmployeeWeekAvailability week) {
        int index = weeks.indexOf(week) - 1;

        return index < 0 ? null : weeks.get(index);
    }

    public PlannedFeature getLastPlannedFeature(EmployeeWeekAvailability week, EmployeeWeekAvailability previousWeek) {
        List<PlannedFeature> jobs = week.getPlannedFeatures();
        if (!jobs.isEmpty())
            return jobs.get(jobs.size() - 1);

        if (previousWeek == null) return null;

        jobs = previousWeek.getPlannedFeatures();
        if (!jobs.isEmpty())
            return jobs.get(jobs.size() - 1);

        return null;
    }

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




}
