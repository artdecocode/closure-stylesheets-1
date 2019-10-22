/*
 * Copyright 2019 Art Deco.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.common.css.compiler.passes;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.css.compiler.ast.CssClassSelectorNode;
import com.google.common.css.compiler.ast.CssCombinatorNode;
import com.google.common.css.compiler.ast.CssCombinatorNode.Combinator;
import com.google.common.css.compiler.ast.CssCompilerPass;
import com.google.common.css.compiler.ast.CssIdSelectorNode;
import com.google.common.css.compiler.ast.CssRefinerNode;
import com.google.common.css.compiler.ast.CssRulesetNode;
import com.google.common.css.compiler.ast.CssSelectorListNode;
import com.google.common.css.compiler.ast.CssSelectorNode;
import com.google.common.css.compiler.ast.DefaultTreeVisitor;
import com.google.common.css.compiler.ast.MutatingVisitController;

/**
 * Compiler pass that adds a prefix to all rule sets.
 *
 * @author anton@adc.sh
 */
public class CssPrefixing extends DefaultTreeVisitor
    implements CssCompilerPass {

  private final String rootSelector;
  private final MutatingVisitController visitController;

  public CssPrefixing(MutatingVisitController visitController,
      String rootSelector) {
    this.visitController = visitController;
    this.rootSelector = rootSelector;
  }

  @Override
  public boolean enterRuleset(CssRulesetNode ruleset) {
    return true;
  }

  @Override
  public boolean enterSelectorBlock(CssSelectorListNode selectors) {
    CssSelectorListNode newList = new CssSelectorListNode();

    for (CssSelectorNode node : selectors.getChildren()) {
      if (RefinersContainPrefix(node, rootSelector)) {
        return true;
      }
      CssSelectorNode rootSel = createRootPrefixNode(rootSelector);
      CssCombinatorNode combinator = new CssCombinatorNode(node,
        Combinator.DESCENDANT, null);

      rootSel.setCombinator(combinator);
      newList.addChildToBack(rootSel);
    }
    visitController.replaceCurrentBlockChildWith(
        ImmutableList.of(newList), false /* visitTheReplacementNodes */);
    return true;
  }

  @Override
  public void runPass() {
    visitController.startVisit(this);
  }

  private static CssSelectorNode createRootPrefixNode(String rootSelector) {
    CssSelectorNode rootSel = new CssSelectorNode("");
    if (rootSelector.startsWith(".")) {
      CssClassSelectorNode sel = new CssClassSelectorNode(
        rootSelector.substring(1), null);
      rootSel.getRefiners().addChildToBack(sel);
    } else if (rootSelector.startsWith("#")) {
      CssIdSelectorNode sel = new CssIdSelectorNode(
        rootSelector.substring(1), null);
      rootSel.getRefiners().addChildToBack(sel);
    }
    return rootSel;
  }

  private static boolean RefinersContainPrefix(CssSelectorNode node,
    String prefix) {
    List<CssRefinerNode> refiners = node.getRefiners().getChildren();

    for (CssRefinerNode refiner : refiners) {
      String s = refiner.toString();
      if (prefix.equals(s)) return true;
    }
    return false;
  }
}
