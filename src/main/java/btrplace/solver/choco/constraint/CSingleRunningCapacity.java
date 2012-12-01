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

package btrplace.solver.choco.constraint;

import btrplace.model.Mapping;
import btrplace.model.Model;
import btrplace.model.SatConstraint;
import btrplace.model.constraint.SingleRunningCapacity;
import btrplace.plan.ReconfigurationPlan;
import btrplace.solver.SolverException;
import btrplace.solver.choco.ChocoSatConstraint;
import btrplace.solver.choco.ChocoSatConstraintBuilder;
import btrplace.solver.choco.ReconfigurationProblem;
import choco.cp.solver.CPSolver;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Choco implementation of {@link btrplace.model.constraint.SingleRunningCapacity}.
 *
 * @author Fabien Hermenier
 */
public class CSingleRunningCapacity implements ChocoSatConstraint {

    private SingleRunningCapacity cstr;

    /**
     * Make a new constraint.
     *
     * @param c the sat constraint to rely on
     */
    public CSingleRunningCapacity(SingleRunningCapacity c) {
        cstr = c;
    }

    @Override
    public void inject(ReconfigurationProblem rp) throws SolverException {
        CPSolver s = rp.getSolver();
        for (UUID u : cstr.getInvolvedNodes()) {
            IntDomainVar v = rp.getVMsCountOnNodes()[rp.getNode(u)];
            s.post(s.leq(v, cstr.getAmount()));
        }
    }

    @Override
    public SingleRunningCapacity getAssociatedConstraint() {
        return cstr;
    }

    @Override
    public Set<UUID> getMisPlacedVMs(Model m) {
        Mapping map = m.getMapping();
        Set<UUID> bad = new HashSet<UUID>();
        for (UUID n : cstr.getInvolvedNodes()) {
            if (map.getRunningVMs(n).size() > cstr.getAmount()) {
                bad.addAll(map.getRunningVMs(n));
                break;
            }
        }
        return bad;
    }

    @Override
    public boolean isSatisfied(ReconfigurationPlan plan) {
        return cstr.isSatisfied(plan.getResult()).equals(SatConstraint.Sat.SATISFIED);
    }

    /**
     * The builder associated to that constraint.
     */
    public static class Builder implements ChocoSatConstraintBuilder {
        @Override
        public Class<? extends SatConstraint> getKey() {
            return SingleRunningCapacity.class;
        }

        @Override
        public CSingleRunningCapacity build(SatConstraint cstr) {
            return new CSingleRunningCapacity((SingleRunningCapacity) cstr);
        }
    }
}