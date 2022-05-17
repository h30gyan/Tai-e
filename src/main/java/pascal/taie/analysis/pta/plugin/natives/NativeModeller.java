/*
 * Tai-e: A Static Analysis Framework for Java
 *
 * Copyright (C) 2022 Tian Tan <tiantan@nju.edu.cn>
 * Copyright (C) 2022 Yue Li <yueli@nju.edu.cn>
 *
 * This file is part of Tai-e.
 *
 * Tai-e is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * Tai-e is distributed in the hope that it will be useful,but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Tai-e. If not, see <https://www.gnu.org/licenses/>.
 */

package pascal.taie.analysis.pta.plugin.natives;

import pascal.taie.analysis.pta.core.cs.element.CSVar;
import pascal.taie.analysis.pta.core.solver.Solver;
import pascal.taie.analysis.pta.plugin.Plugin;
import pascal.taie.analysis.pta.pts.PointsToSet;
import pascal.taie.language.classes.JMethod;

/**
 * This class models some native calls by "inlining" their side effects
 * at the call sites to provide better precision for pointer analysis.
 */
public class NativeModeller implements Plugin {

    private Solver solver;

    private NativeModel model;

    @Override
    public void setSolver(Solver solver) {
        this.solver = solver;
        model = new NativeModel(solver);
    }

    @Override
    public void onStart() {
        model.getNativeMethods().forEach(solver::addIgnoredMethod);
    }

    @Override
    public void onNewMethod(JMethod method) {
        method.getIR().invokes(false).forEach(model::handleNewInvoke);
    }

    @Override
    public void onNewPointsToSet(CSVar csVar, PointsToSet pts) {
        if (model.isRelevantVar(csVar.getVar())) {
            model.handleNewPointsToSet(csVar, pts);
        }
    }
}
