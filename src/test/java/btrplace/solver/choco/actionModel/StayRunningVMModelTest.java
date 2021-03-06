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

import btrplace.model.DefaultMapping;
import btrplace.model.DefaultModel;
import btrplace.model.Mapping;
import btrplace.model.Model;
import btrplace.plan.ReconfigurationPlan;
import btrplace.solver.SolverException;
import btrplace.solver.choco.DefaultReconfigurationProblemBuilder;
import btrplace.solver.choco.ReconfigurationProblem;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.UUID;

/**
 * Unit tests for {@link StayRunningVMModel}.
 *
 * @author Fabien Hermenier
 */
public class StayRunningVMModelTest {

    @Test
    public void testBasic() throws SolverException {

        UUID vm1 = UUID.randomUUID();
        UUID n1 = UUID.randomUUID();

        Mapping map = new DefaultMapping();
        map.addOnlineNode(n1);
        map.addRunningVM(vm1, n1);

        Model mo = new DefaultModel(map);
        ReconfigurationProblem rp = new DefaultReconfigurationProblemBuilder(mo)
                .setManageableVMs(Collections.<UUID>emptySet())
                .labelVariables()
                .build();
        Assert.assertEquals(rp.getVMAction(vm1).getClass(), StayRunningVMModel.class);
        StayRunningVMModel m1 = (StayRunningVMModel) rp.getVMAction(vm1);
        Assert.assertNotNull(m1.getCSlice());
        Assert.assertNotNull(m1.getDSlice());
        Assert.assertTrue(m1.getCSlice().getHoster().isInstantiatedTo(rp.getNode(n1)));
        Assert.assertTrue(m1.getDSlice().getHoster().isInstantiatedTo(rp.getNode(n1)));
        Assert.assertTrue(m1.getDuration().isInstantiatedTo(0));
        Assert.assertTrue(m1.getStart().isInstantiatedTo(0));
        Assert.assertTrue(m1.getEnd().isInstantiatedTo(0));

        ReconfigurationPlan p = rp.solve(0, false);
        Assert.assertEquals(p.getSize(), 0);
    }
}
