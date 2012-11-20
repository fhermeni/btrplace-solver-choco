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

package btrplace.solver.choco.actionModel;

import btrplace.plan.Action;
import btrplace.plan.SolverException;
import btrplace.plan.action.BootVM;
import btrplace.solver.choco.ActionModel;
import btrplace.solver.choco.ReconfigurationProblem;
import btrplace.solver.choco.Slice;
import btrplace.solver.choco.SliceUtils;
import choco.cp.solver.variables.integer.IntDomainVarAddCste;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Model an action that boot a VM in the waiting state.
 *
 * @author Fabien Hermenier
 */
public class BootVMModel extends ActionModel {

    /**
     * Make a new model.
     *
     * @param rp the RP to use as a basis.
     * @param e  the VM managed by the action
     * @throws SolverException if an error occurred
     */
    public BootVMModel(ReconfigurationProblem rp, UUID e) throws SolverException {
        super(rp, e);

        int d = rp.getDurationEvaluator().evaluate(BootVM.class, e);

        start = rp.makeDuration("", 0, rp.getEnd().getSup() - d);
        end = new IntDomainVarAddCste(rp.getSolver(), "", getStart(), d);
        duration = rp.makeDuration("", d, d);

        dSlice = new Slice("dSlice(" + e + ")", e, start, rp.getEnd(), rp.makeDuration(""), rp.makeHostVariable(""), rp.getSolver().createBooleanVar(""));
        SliceUtils.linkMoments(rp, dSlice);
    }

    @Override
    public List<Action> getResultingActions(ReconfigurationProblem rp) {
        List<Action> l = new ArrayList<Action>(1);
        l.add(new BootVM(getSubject(), rp.getNode(dSlice.getHoster().getVal()), start.getVal(), end.getVal()));
        return l;
    }
}