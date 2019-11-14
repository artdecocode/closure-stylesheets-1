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

import com.google.common.css.compiler.ast.CssCommentNode;
import com.google.common.css.compiler.ast.CssCompilerPass;
import com.google.common.css.compiler.ast.CssDeclarationNode;
import com.google.common.css.compiler.ast.DefaultTreeVisitor;
import com.google.common.css.compiler.ast.MutatingVisitController;
import java.util.ArrayList;

/**
 * This is the second pass to AutoExpandBrowserPrefix that either removes all non-autoexpanded
 * nodes for the CSS output that contains only prefixed rules, or removes all autoexpanded rules
 * to keep in the source file. This is enabled by `--output-browser-prefix` flag and will
 * produce a JSON map with properties and their values for runtime tests in the browser.
 */
public class AutoExpandBrowserPrefix2 extends DefaultTreeVisitor implements CssCompilerPass {

  private final MutatingVisitController visitController;
  private boolean remove;

  public AutoExpandBrowserPrefix2(MutatingVisitController visitController, Boolean remove) {
    this.visitController = visitController;
    this.remove = remove;
  }

  @Override
  public boolean enterDeclaration(CssDeclarationNode declaration) {
    if (this.remove) {
      if (declaration.autoExpanded) {
        visitController.removeCurrentNode();
      }
    } else {
      if (!declaration.autoExpanded) {
        visitController.removeCurrentNode();
      } else {
        declaration.setComments(new ArrayList<CssCommentNode>());
      }
    }
    return true;
  }

  @Override
  public void runPass() {
    visitController.startVisit(this);
  }
}
