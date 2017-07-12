package entities.parameters;

import logic.SolverNRP;

/**
 * The default values of algorithm parameters
 * @author Vavou
 *
 */
public class AlgorithmParameters {

	public SolverNRP.AlgorithmType algorithmType = SolverNRP.AlgorithmType.NSGAII;

	public double crossoverProbability;

	// The proportion of solutions that will not be generated entirely randomly
	public double rateOfNotRandomSolution;

	public int numberOfIterations;

	public int populationSize;





	/* --- CONSTRUCTORS ---*/

	// Same values for all algorithm types unless manually set
	public AlgorithmParameters() {
	    setCommonDefaultParameters();
	    setDefaultParametersFor(SolverNRP.AlgorithmType.NSGAII);
    }

	// Sets iterations and population to some optimal values depending on the AlgorithmType
    public AlgorithmParameters(SolverNRP.AlgorithmType algorithmType) {
        setCommonDefaultParameters();
	    setDefaultParametersFor(algorithmType);
    }



    /* --- SETTERS/GETTERS ---*/
    private void setCommonDefaultParameters() {
        setCrossOverProbability(0.8);
        setRateOfNotRandomSolution(0.25);
    }

    private void setDefaultParametersFor(SolverNRP.AlgorithmType algorithmType) {
        switch (algorithmType) {
            case MOCell:
                setNumberOfIterations(25000);
                setPopulationSize(2500);
                break;
            default:
                setNumberOfIterations(1000);
                setPopulationSize(110);
        }
    }


    public void setAlgorithmType(SolverNRP.AlgorithmType type) { algorithmType = type; }

    public void setCrossOverProbability(double probability) { crossoverProbability = probability; }

    public void setRateOfNotRandomSolution(double rate) { rateOfNotRandomSolution = rate; }

    public void setNumberOfIterations(int nbIterations) { numberOfIterations = nbIterations; }

    public void setPopulationSize(int size) { populationSize = size; }


    public SolverNRP.AlgorithmType getAlgorithmType() { return algorithmType; }

    public double getCrossoverProbability() { return crossoverProbability; }

    public double getRateOfNotRandomSolution() { return rateOfNotRandomSolution; }

    public int getNumberOfIterations() { return numberOfIterations; }

    public int getPopulationSize() { return populationSize; }

    public double getMutationProbability(int numberOfFeatures) {
        return 1.0/numberOfFeatures;
    }
}
