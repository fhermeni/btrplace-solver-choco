/*
 * Copyright (c) 2012 University of Nice Sophia-Antipolis
 *
 * This file is part of btrplace.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package btrplace.solver.choco;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

/**
 * Statistics related to a solving process.
 *
 * @author Fabien Hermenier
 */
public class SolvingStatistics {

    /**
     * The number of VMs actually managed by the problem.
     */
    private int nbManagedVMs;

    /**
     * The total duration of the solving process in milliseconds.
     */
    private int time;

    /**
     * The total number of opened nodes.
     */
    private int nbNodes;

    /**
     * The total number of backtracks.
     */
    private int nbBacktracks;

    /**
     * Indicates whether or not the solver hits the timeout.
     */
    private boolean timeout;

    private Set<SolutionStatistics> solutions;

    /**
     * Compare the solution by their moment. If equal, the number of nodes then the number of backtracks.
     */
    private static Comparator<SolutionStatistics> solutionsCmp = new Comparator<SolutionStatistics>() {
        @Override
        public int compare(SolutionStatistics sol1, SolutionStatistics sol2) {
            if (sol1.getTime() == sol2.getTime()) {
                //Compare wrt. the number of nodes or backtracks
                if (sol1.getNbNodes() == sol2.getNbNodes()) {
                    return sol1.getNbBacktracks() - sol2.getNbBacktracks();
                }
                return sol1.getNbNodes() - sol2.getNbNodes();
            }
            return sol1.getTime() - sol2.getTime();
        }
    };

    /**
     * Make new statistics.
     *
     * @param managedVMs the number of VMs managed by the algorithm.
     * @param t          the moment in milliseconds
     * @param nbN        the number of opened nodes at the moment
     * @param nbB        the number of backtracks at the moment
     * @param to         {@code true} to indicate the solver hit a timeout
     */
    public SolvingStatistics(int managedVMs, int t, int nbN, int nbB, boolean to) {
        this.nbManagedVMs = managedVMs;
        time = t;
        nbNodes = nbN;
        nbBacktracks = nbB;
        timeout = to;
        solutions = new TreeSet<SolutionStatistics>(solutionsCmp);
    }

    /**
     * Get the time since the beginning of the solving process.
     *
     * @return a duration in milliseconds
     */
    public int getTime() {
        return time;
    }

    /**
     * Get the number of opened nodes.
     *
     * @return a positive number
     */
    public int getNbNodes() {
        return nbNodes;
    }

    /**
     * Get the number of backtracks.
     *
     * @return a positive number
     */
    public int getNbBacktracks() {
        return nbBacktracks;
    }

    /**
     * Indicates if the solver hit a timeout.
     *
     * @return {@code true} iff the solver hit a timeout
     */
    public boolean isTimeout() {
        return timeout;
    }

    /**
     * Add a solution to the statistics.
     *
     * @param so the solution to add
     */
    public void addSolution(SolutionStatistics so) {
        this.solutions.add(so);
    }

    /**
     * Get all the computed solutions ordered by time.
     *
     * @return a list of solutions that may be empty
     */
    public Set<SolutionStatistics> getSolutions() {
        return solutions;
    }

    /**
     * Get the number of VMs managed by the algorithm.
     *
     * @return a positive number
     */
    public int getNbManagedVMs() {
        return nbManagedVMs;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("After ").append(time).append("ms");
        if (timeout) {
            b.append(" (timeout)");
        } else {
            b.append(" (terminated)");
        }
        b.append(": ")
                .append(nbManagedVMs).append(" VMs to manage. ")
                .append(nbNodes).append(" opened search nodes, ")
                .append(nbBacktracks).append(" backtracks, ")
                .append(solutions.size()).append(" solutions:\n");
        int i = 0;
        for (SolutionStatistics st : solutions) {
            b.append("\t").append(i).append(")")
                    .append(" at ").append(st.getTime()).append("ms: ")
                    .append(st.getNbNodes()).append(" nodes, ")
                    .append(st.getNbBacktracks()).append(" backtracks");
            if (st.hasObjective()) {
                b.append(", obj: ").append(st.getOptValue());
            }
            b.append("\n");
        }
        return b.toString();
    }
}
