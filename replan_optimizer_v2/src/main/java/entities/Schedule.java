package entities;

import java.util.*;

/**
 * Represents the schedule of an employee during a certain number of weeks
 * and takes care of assigning features to said employee.
 */
public class Schedule implements Iterable<WeekSchedule> {
    private List<WeekSchedule> weeks;
    private Employee employee;
    private Set<PlannedFeature> plannedFeatures;

    // The number of hours left this employee has for the whole release
    private double totalHoursLeft;
    private final int nbWeeks;
    private final double hoursPerWeek;

    public Schedule(Employee employee, int nbWeeks, double hoursPerWeek) {
        this.employee = employee;
        this.nbWeeks = nbWeeks;
        this.hoursPerWeek = hoursPerWeek;

        totalHoursLeft = nbWeeks * employee.getWeekAvailability();

        weeks = new ArrayList<>();
        plannedFeatures = new HashSet<>();
    }

    // Copy constructor
    public Schedule(Schedule origin) {
        this(origin.employee, origin.nbWeeks, origin.hoursPerWeek);

        totalHoursLeft = origin.totalHoursLeft;

        weeks = new ArrayList<>();
        for (WeekSchedule week : weeks)
            weeks.add(new WeekSchedule(week));

        plannedFeatures = new HashSet<>();
        for (PlannedFeature pf : origin.plannedFeatures)
            plannedFeatures.add(new PlannedFeature(pf));
    }

    /* --- PUBLIC --- */

    public boolean scheduleFeature(PlannedFeature pf) {
        return scheduleFeature(pf, true);
    }

    /**
     * Tries to schedule a PlannedFeature in the first available week
     * @param pf the PlannedFeature to be scheduled
     * @return a boolean indicating whether the PlannedFeature could be scheduled
     */
    public boolean scheduleFeature(PlannedFeature pf, boolean adjustHours) {

        double featureHoursLeft = pf.getFeature().getDuration();

        // Not enough hours left for this feature in the iteration
        if (totalHoursLeft < featureHoursLeft)
            return false;

        WeekSchedule week = getCurrentWeek();

        double remainingWeekHours = week.getRemainingHours();

        WeekSchedule previousWeek = getPreviousWeek(week);


        PlannedFeature lastPlanned = getLastPlannedFeature(week, previousWeek);


        if (featureHoursLeft <= remainingWeekHours) {
            double newBeginHour = lastPlanned == null ? week.getEndHour() : lastPlanned.getEndHour();
            pf.setBeginHour(newBeginHour);

            week.addPlannedFeature(pf);
            week.setRemainingHours(remainingWeekHours - featureHoursLeft);

            pf.setEndHour(pf.getBeginHour() + featureHoursLeft);

            week.setEndHour(pf.getEndHour());

            plannedFeatures.add(pf);

            totalHoursLeft -= featureHoursLeft;
        } else {
            double pfBeginHour = lastPlanned == null ? week.getEndHour() : lastPlanned.getEndHour();
            double pfEndHour = pfBeginHour;
            while (featureHoursLeft > 0.0) {
                week.addPlannedFeature(pf);

                plannedFeatures.add(pf);

                double doneHours = Math.min(featureHoursLeft, remainingWeekHours);

                featureHoursLeft -= doneHours;
                totalHoursLeft -= doneHours;

                pfEndHour += featureHoursLeft > 0.0 ? normalizeHours(doneHours) : doneHours;

                week.setRemainingHours(remainingWeekHours - doneHours);
                week.setEndHour(pfEndHour);

                week = getCurrentWeek();
                remainingWeekHours = week.getRemainingHours();
            }
            pf.setBeginHour(pfBeginHour);
            pf.setEndHour(pfEndHour);
        }

        return true;
    }


    /**
     * Schedules the given PlannedFeature forcing a time skip if necessary.
     * To be used only in postprocessing (and maybe in frozen features if you see it fits).
     * For regular, proper scheduling use {@link Schedule#scheduleFeature(PlannedFeature)}
     */
    public void forceSchedule(PlannedFeature pf) {
        WeekSchedule week = getCurrentWeek();

        PlannedFeature lastPlanned = getLastPlannedFeature(week, getPreviousWeek(week));
        if (lastPlanned != null) {
            pf.setBeginHour(Math.max(pf.getBeginHour(), lastPlanned.getEndHour()));
            pf.setEndHour(pf.getBeginHour() + pf.getFeature().getDuration());
        }

        int i = weeks.indexOf(week);
        while (pf.getBeginHour() > (i+1)*hoursPerWeek) {

            totalHoursLeft -= (hoursPerWeek - week.getEndHour());

            week.setBeginHour(i*hoursPerWeek);
            week.setEndHour((i+1)*hoursPerWeek);

            ++i;

            if (i < weeks.size())
                week = weeks.get(i);
            else break;
        }

        double featureHoursLeft = pf.getFeature().getDuration();
        double remainingWeekHours = week.getRemainingHours();
        double pfEndHour = pf.getBeginHour();
        while (featureHoursLeft > 0.0) {
            week.addPlannedFeature(pf);

            plannedFeatures.add(pf);

            double doneHours = Math.min(featureHoursLeft, remainingWeekHours);

            featureHoursLeft -= doneHours;
            totalHoursLeft -= doneHours;

            pfEndHour += featureHoursLeft > 0.0 ? normalizeHours(doneHours) : doneHours;

            week.setRemainingHours(remainingWeekHours - doneHours);
            week.setEndHour(pfEndHour);

            week = getCurrentWeek();
            remainingWeekHours = week.getRemainingHours();
        }
    }


    public WeekSchedule getWeek(int i) { return weeks.get(i); }

    public int size() { return weeks.size(); }

    public boolean isEmpty() { return plannedFeatures.isEmpty(); }

    public List<WeekSchedule> getAllWeeks() {
        weeks.removeIf(week -> week.getPlannedFeatures().isEmpty());
        return weeks;
    }

    public boolean contains(PlannedFeature pf) { return plannedFeatures.contains(pf); }

    public Employee getEmployee() { return employee; }

    public List<PlannedFeature> getPlannedFeatures() {
        return new ArrayList<>(plannedFeatures);
    }

    public double getTotalHoursLeft() { return totalHoursLeft; }

    public void clear() {
        weeks.clear();
        plannedFeatures.clear();
        totalHoursLeft = nbWeeks * employee.getWeekAvailability();
    }

    @Override
    public Iterator<WeekSchedule> iterator() { return weeks.iterator(); }



    /* --- PRIVATE --- */
    private double normalizeHours(double doneHours) {
        return doneHours * (hoursPerWeek/employee.getWeekAvailability());
    }

    // Returns the first non-full week of the employee, or a new one if there isn't any.
    private WeekSchedule getCurrentWeek() {
        for (WeekSchedule week : this.weeks)
            if (week.getRemainingHours() > 0.0)
                return week;

        WeekSchedule week = new WeekSchedule(0.0, employee.getWeekAvailability());

        weeks.add(week);

        WeekSchedule previous = getPreviousWeek(week);

        if (previous != null) {
            week.setBeginHour(previous.getEndHour());
            week.setEndHour(week.getBeginHour());
        }

        return week;
    }

    // Returns the previous week to the given one, null if the given one is the first
    private WeekSchedule getPreviousWeek(WeekSchedule week) {
        int index = weeks.indexOf(week) - 1;

        return index < 0 ? null : weeks.get(index);
    }

    public PlannedFeature getLastPlannedFeature(WeekSchedule week, WeekSchedule previousWeek) {
        List<PlannedFeature> jobs = week.getPlannedFeatures();
        if (!jobs.isEmpty())
            return jobs.get(jobs.size() - 1);

        if (previousWeek == null) return null;

        jobs = previousWeek.getPlannedFeatures();
        if (!jobs.isEmpty())
            return jobs.get(jobs.size() - 1);

        return null;
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
