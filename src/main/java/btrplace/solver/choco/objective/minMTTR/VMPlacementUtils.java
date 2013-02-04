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

package btrplace.solver.choco.objective.minMTTR;

import btrplace.model.Mapping;
import btrplace.solver.choco.ReconfigurationProblem;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: fhermeni
 * Date: 04/02/13
 * Time: 16:32
 * To change this template use File | Settings | File Templates.
 */
public class VMPlacementUtils {

    private VMPlacementUtils() {
    }

    public static Map<IntDomainVar, UUID> makePlacementMap(ReconfigurationProblem rp) {
        Map<IntDomainVar, UUID> m = new HashMap<IntDomainVar, UUID>();
        for (UUID vm : rp.getFutureRunningVMs()) {
            IntDomainVar v = rp.getVMAction(vm).getDSlice().getHoster();
            m.put(v, vm);
        }
        return m;
    }

    public static boolean canStay(ReconfigurationProblem rp, UUID vm) {
        Mapping m = rp.getSourceModel().getMapping();
        if (m.getRunningVMs().contains(vm)) {
            int curPos = rp.getNode(m.getVMLocation(vm));
            return rp.getVMAction(vm).getDSlice().getHoster().canBeInstantiatedTo(curPos);
        }
        return false;
    }
}
