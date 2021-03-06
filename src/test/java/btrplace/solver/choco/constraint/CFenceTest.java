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

import btrplace.model.DefaultModel;
import btrplace.model.Mapping;
import btrplace.model.Model;
import btrplace.model.SatConstraint;
import btrplace.model.constraint.Fence;
import btrplace.model.constraint.Online;
import btrplace.model.constraint.Ready;
import btrplace.plan.ReconfigurationPlan;
import btrplace.plan.event.MigrateVM;
import btrplace.solver.SolverException;
import btrplace.solver.choco.ChocoReconfigurationAlgorithm;
import btrplace.solver.choco.DefaultChocoReconfigurationAlgorithm;
import btrplace.solver.choco.MappingBuilder;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

/**
 * Unit tests for {@link btrplace.model.constraint.Fence}.
 *
 * @author Fabien Hermenier
 */
public class CFenceTest extends ConstraintTestMaterial {

    /**
     * Test getMisPlaced() in various situations.
     */
    @Test
    public void testGetMisPlaced() {
        Mapping m = new MappingBuilder().on(n1, n2, n3, n4)
                .off(n5)
                .run(n1, vm1, vm2)
                .run(n2, vm3)
                .run(n3, vm4)
                .sleep(n4, vm5).build();

        Set<UUID> vms = new HashSet<UUID>(Arrays.asList(vm1, vm2));
        Set<UUID> ns = new HashSet<UUID>(Arrays.asList(n1, n2));
        CFence c = new CFence(new Fence(vms, ns));
        Model mo = new DefaultModel(m);
        Assert.assertTrue(c.getMisPlacedVMs(mo).isEmpty());
        ns.add(vm5);
        Assert.assertTrue(c.getMisPlacedVMs(mo).isEmpty());
        vms.add(vm3);
        Assert.assertTrue(c.getMisPlacedVMs(mo).isEmpty());
        vms.add(vm4);
        Set<UUID> bad = c.getMisPlacedVMs(mo);
        Assert.assertEquals(1, bad.size());
        Assert.assertTrue(bad.contains(vm4));
    }

    @Test
    public void testBasic() throws SolverException {

        Mapping map = new MappingBuilder().on(n1, n2, n3)
                .run(n1, vm1, vm4)
                .run(n2, vm2)
                .run(n3, vm3).build();

        Set<UUID> on = new HashSet<UUID>(Arrays.asList(n1, n3));
        Fence f = new Fence(map.getAllVMs(), on);
        ChocoReconfigurationAlgorithm cra = new DefaultChocoReconfigurationAlgorithm();
        List<SatConstraint> cstrs = new ArrayList<SatConstraint>();
        cstrs.add(f);
        cstrs.add(new Online(map.getAllNodes()));
        Model mo = new DefaultModel(map);
        ReconfigurationPlan p = cra.solve(mo, cstrs);
        Assert.assertNotNull(p);
        Assert.assertTrue(p.getSize() > 0);
        Assert.assertTrue(p.iterator().next() instanceof MigrateVM);
        //Assert.assertEquals(SatConstraint.Sat.SATISFIED, f.isSatisfied(p.getResult()));
        cstrs.add(new Ready(Collections.singleton(vm2)));

        p = cra.solve(mo, cstrs);
        Assert.assertNotNull(p);
        Assert.assertEquals(1, p.getSize()); //Just the suspend of vm2
        //Assert.assertEquals(SatConstraint.Sat.SATISFIED, f.isSatisfied(p.getResult()));


    }
}
