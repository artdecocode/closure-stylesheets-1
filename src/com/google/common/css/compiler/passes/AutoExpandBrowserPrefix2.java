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

import com.google.common.css.PrefixMap;
import com.google.common.css.compiler.ast.CssCommentNode;
import com.google.common.css.compiler.ast.CssCompilerPass;
import com.google.common.css.compiler.ast.CssDeclarationNode;
import com.google.common.css.compiler.ast.DefaultTreeVisitor;
import com.google.common.css.compiler.ast.MutatingVisitController;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This is the second pass to AutoExpandBrowserPrefix that either removes all non-autoexpanded
 * nodes for the CSS output that contains only prefixed rules, or removes all autoexpanded rules
 * to keep in the source file. This is enabled by `--output-browser-prefix` flag and will
 * produce a JSON map with properties and their values for runtime tests in the browser.
 */
public class AutoExpandBrowserPrefix2 extends DefaultTreeVisitor implements CssCompilerPass {

  private final MutatingVisitController visitController;
  private boolean removingAutoExpanded;
  private boolean removingOriginal;
  HashMap<String, ArrayList<String>> prefixes;
  private PrefixMap prefixMap;

  public AutoExpandBrowserPrefix2(MutatingVisitController visitController, Boolean remove,
    HashMap<String, ArrayList<String>> prefixes) {
    this.visitController = visitController;
    this.removingAutoExpanded = remove;
    this.removingOriginal = !remove;
    this.prefixes = prefixes;
  }

  public AutoExpandBrowserPrefix2(MutatingVisitController mutatingVisitController, boolean b,
		HashMap<String, ArrayList<String>> prefixes2, PrefixMap prefixMap) {
      this(mutatingVisitController, b, prefixes2);
    this.prefixMap = prefixMap;
}

/**
   * Returns false if the property didn't match by value;
   */
  private boolean valueInPrefixes(String propName, String value) {
    ArrayList<String> values = this.prefixes.get(propName);
    if (values == null) return false;
    if (values.size() == 0) return true;
    if (values.contains(value)) return true;
    return false;
  }

  private void addToPrefixMap(CssDeclarationNode declaration, String propName, String propValue) {
    if (prefixMap != null) {
      // e.g., hyphens
      if (declaration.autoExpandedFromValue == null) {
        prefixMap.addAlternativePropertyName(declaration.autoExpandedFromProp, propName);
      } else { // e.g., display: flex
        prefixMap.addProperty(propName, propValue, declaration.autoExpandedFromValue);
      }
    }
  }

  private boolean shouldPreserve(CssDeclarationNode declaration) {
    String propName = declaration.getPropertyName().getValue();
    String propValue = PassUtil.printPropertyValue(declaration.getPropertyValue());

    String fromProp = declaration.autoExpandedFromProp;
    String fromValue = declaration.autoExpandedFromValue;
    boolean a = valueInPrefixes(fromProp, fromValue);
    if (a) {
      if (prefixMap != null) {
        if (fromProp != null && fromValue == null) {
          prefixMap.addGlobalProp(fromProp);
        } else if (fromProp != null) {
          prefixMap.addGlobalPropValue(fromProp, fromValue);
        } else {
          System.out.print("s");
        }
      }
      // addToPrefixMap(declaration, propName, propValue);
      return true;
    }
    // 2. check the expanded match itself
    boolean b = valueInPrefixes(propName, propValue);
    if (b) {
      addToPrefixMap(declaration, propName, propValue);
      return true;
    }
    return false;
  }

  @Override
  public boolean enterDeclaration(CssDeclarationNode declaration) {
    if (this.removingAutoExpanded && declaration.autoExpanded) { // removing expanded nodes from the original tree
      if (declaration.autoExpandedFromProp != null) {
        boolean preserve = shouldPreserve(declaration);
        if (!preserve) visitController.removeCurrentNode();
      } else if (declaration.autoExpandedFromValue != null) {
        // functions
        boolean has = this.prefixes.containsKey(declaration.autoExpandedFromValue);
        if (!has) {
          // check for expanded function ??
          // boolean hasExpanded = this.prefixes.containsKey(declaration.)
        } else {
          return true;
        }
        visitController.removeCurrentNode();
      }
    } else if (this.removingOriginal && !declaration.autoExpanded) {
      // removing original nodes from the expanded tree
      visitController.removeCurrentNode();
    } else if (this.removingOriginal) {
      // remove /* @alternate */ comment
      declaration.setComments(new ArrayList<CssCommentNode>());
      // possibly remove the auto expanded one if it's found in prefixes as we've kept them
      if (declaration.autoExpandedFromProp != null) {
        boolean preserve = shouldPreserve(declaration);
        if (preserve) visitController.removeCurrentNode();
      } else if (declaration.autoExpandedFromValue != null) {
        boolean has = this.prefixes.containsKey(declaration.autoExpandedFromValue);
        if (has) visitController.removeCurrentNode();
        else {
          // add to the map
        }
      }
    }
    return true;
  }

  @Override
  public void runPass() {
    visitController.startVisit(this);
  }
}
