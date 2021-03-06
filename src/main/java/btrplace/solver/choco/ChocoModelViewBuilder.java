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

import btrplace.model.ModelView;
import btrplace.solver.SolverException;

/**
 * The builder that is used by {@link ChocoModelViewBuilder} to create
 * the solver-side views.
 *
 * @author Fabien Hermenier
 */
public interface ChocoModelViewBuilder {

    /**
     * Get the class of the {@link ModelView} that is handled by this builder.
     *
     * @return a Class derived from {@link btrplace.model.ModelView}
     */
    Class<? extends ModelView> getKey();

    /**
     * Build the {@link ChocoModelView} associated to the {@link ModelView}
     * identified as key.
     *
     * @param rp the problem to add
     * @param v  the model constraint
     * @throws SolverException if an error occurred while building the view
     */
    ChocoModelView build(ReconfigurationProblem rp, ModelView v) throws SolverException;

}
