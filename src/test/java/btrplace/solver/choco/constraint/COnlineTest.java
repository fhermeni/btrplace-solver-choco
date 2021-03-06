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
import btrplace.model.constraint.Online;
import btrplace.plan.ReconfigurationPlan;
import btrplace.solver.SolverException;
import btrplace.solver.choco.ChocoReconfigurationAlgorithm;
import btrplace.solver.choco.DefaultChocoReconfigurationAlgorithm;
import btrplace.solver.choco.MappingBuilder;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

/**
 * Unit tests for {@link COnline}.
 *
 * @author Fabien Hermenier
 */
public class COnlineTest extends ConstraintTestMaterial {

    @Test
    public void testInstantiation() {
        Set<UUID> s = new HashSet<UUID>(Arrays.asList(n1, n2));
        Online on = new Online(s);
        COnline con = new COnline(on);
        Assert.assertEquals(con.toString(), on.toString());
    }

    @Test
    public void testSolvableProblem() throws SolverException {
        Mapping map = new MappingBuilder().off(n1).build();
        Model mo = new DefaultModel(map);
        ChocoReconfigurationAlgorithm cra = new DefaultChocoReconfigurationAlgorithm();
        ReconfigurationPlan plan = cra.solve(mo, Collections.<SatConstraint>singleton(new Online(Collections.singleton(n1))));
        Assert.assertNotNull(plan);
        Model res = plan.getResult();
        Assert.assertTrue(res.getMapping().getOnlineNodes().contains(n1));
    }
}
