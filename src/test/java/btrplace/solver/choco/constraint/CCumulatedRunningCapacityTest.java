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

import btrplace.model.*;
import btrplace.model.constraint.CumulatedRunningCapacity;
import btrplace.model.constraint.Fence;
import btrplace.plan.ReconfigurationPlan;
import btrplace.plan.event.ShutdownVM;
import btrplace.solver.SolverException;
import btrplace.solver.choco.ChocoReconfigurationAlgorithm;
import btrplace.solver.choco.DefaultChocoReconfigurationAlgorithm;
import btrplace.solver.choco.durationEvaluator.ConstantDuration;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

/**
 * Unit tests for {@link btrplace.solver.choco.constraint.CCumulatedRunningCapacity}.
 *
 * @author Fabien Hermenier
 */
public class CCumulatedRunningCapacityTest {

    UUID vm1 = UUID.randomUUID();
    UUID vm2 = UUID.randomUUID();
    UUID vm3 = UUID.randomUUID();
    UUID vm4 = UUID.randomUUID();
    UUID vm5 = UUID.randomUUID();
    UUID n1 = UUID.randomUUID();
    UUID n2 = UUID.randomUUID();
    UUID n3 = UUID.randomUUID();

    @Test
    public void testWithSatisfiedConstraint() throws SolverException {
        Mapping map = new DefaultMapping();
        map.addOnlineNode(n1);
        map.addOnlineNode(n2);
        map.addOnlineNode(n3);
        map.addRunningVM(vm1, n1);
        map.addRunningVM(vm2, n1);
        map.addRunningVM(vm3, n3);
        map.addRunningVM(vm4, n3);
        map.addSleepingVM(vm5, n2);
        Model mo = new DefaultModel(map);
        List<SatConstraint> l = new ArrayList<SatConstraint>();
        CumulatedRunningCapacity x = new CumulatedRunningCapacity(map.getAllNodes(), 4);
        x.setContinuous(false);
        l.add(x);
        ChocoReconfigurationAlgorithm cra = new DefaultChocoReconfigurationAlgorithm();
        cra.labelVariables(true);
        cra.getDurationEvaluators().register(ShutdownVM.class, new ConstantDuration(10));
        ReconfigurationPlan plan = cra.solve(mo, l);
        Assert.assertEquals(plan.getSize(), 0);
    }

    @Test
    public void testDiscreteSatisfaction() throws SolverException {
        Mapping map = new DefaultMapping();
        map.addOnlineNode(n1);
        map.addOnlineNode(n2);
        map.addOnlineNode(n3);
        map.addRunningVM(vm1, n1);
        map.addRunningVM(vm2, n1);
        map.addRunningVM(vm3, n2);
        map.addRunningVM(vm4, n2);
        map.addRunningVM(vm5, n2);
        Set<UUID> on = new HashSet<UUID>();
        on.add(n1);
        on.add(n2);
        Model mo = new DefaultModel(map);
        List<SatConstraint> l = new ArrayList<SatConstraint>();
        CumulatedRunningCapacity x = new CumulatedRunningCapacity(on, 4);
        x.setContinuous(false);
        l.add(x);
        ChocoReconfigurationAlgorithm cra = new DefaultChocoReconfigurationAlgorithm();
        cra.labelVariables(true);
        cra.getDurationEvaluators().register(ShutdownVM.class, new ConstantDuration(10));
        ReconfigurationPlan plan = cra.solve(mo, l);
        Assert.assertNotNull(plan);
        System.out.println(plan);
        Assert.assertEquals(plan.getSize(), 1);
    }


    @Test
    public void testContinuousResolution() throws SolverException {
        Mapping map = new DefaultMapping();
        map.addOnlineNode(n1);
        map.addOnlineNode(n2);
        map.addOnlineNode(n3);
        map.addRunningVM(vm1, n1);
        map.addRunningVM(vm2, n1);
        map.addRunningVM(vm3, n2);
        map.addRunningVM(vm4, n2);
        map.addRunningVM(vm5, n3);
        Set<UUID> on = new HashSet<UUID>();
        on.add(n1);
        on.add(n2);
        Model mo = new DefaultModel(map);
        List<SatConstraint> l = new ArrayList<SatConstraint>();
        l.add(new Fence(Collections.singleton(vm5), Collections.singleton(n1)));
        CumulatedRunningCapacity x = new CumulatedRunningCapacity(on, 3);
        x.setContinuous(true);
        l.add(x);
        ChocoReconfigurationAlgorithm cra = new DefaultChocoReconfigurationAlgorithm();
        cra.labelVariables(true);
        cra.getDurationEvaluators().register(ShutdownVM.class, new ConstantDuration(10));
        ReconfigurationPlan plan = cra.solve(mo, l);
        Assert.assertNotNull(plan);
        System.out.println(plan);
        Assert.assertEquals(plan.getSize(), 2);
    }

    @Test
    public void testGetMisplaced() {
        Mapping m = new DefaultMapping();
        m.addOnlineNode(n1);
        m.addOnlineNode(n2);
        m.addOnlineNode(n3);
        m.addRunningVM(vm1, n1);
        m.addRunningVM(vm2, n1);
        m.addRunningVM(vm3, n1);
        m.addRunningVM(vm4, n2);
        m.addReadyVM(vm5);

        Model mo = new DefaultModel(m);

        CumulatedRunningCapacity c = new CumulatedRunningCapacity(m.getAllNodes(), 4);
        CCumulatedRunningCapacity cc = new CCumulatedRunningCapacity(c);

        Assert.assertTrue(cc.getMisPlacedVMs(mo).isEmpty());
        m.addRunningVM(vm5, n3);
        Assert.assertEquals(cc.getMisPlacedVMs(mo), m.getAllVMs());
    }
}