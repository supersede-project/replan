package wrapper;

import entities.PlannedFeature;
import logic.PlanningSolution;

import java.util.List;

public class PlanningSolutionWrapper extends PlanningSolution {


    public PlanningSolutionWrapper(PlanningSolution origin, List<PlannedFeature> listPlannedFeature) {
        super(origin);
        for (PlannedFeature plannedFeature : listPlannedFeature) {
            super.scheduleAtTheEnd(plannedFeature.getFeature(), plannedFeature.getEmployee());
        }
    }
}
