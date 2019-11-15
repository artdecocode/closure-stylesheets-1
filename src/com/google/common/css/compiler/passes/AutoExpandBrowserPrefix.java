/*
 * Copyright 2015 Google Inc.
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

import com.google.common.collect.ImmutableList;
import com.google.common.css.PrefixMap;
import com.google.common.css.compiler.ast.CssCommentNode;
import com.google.common.css.compiler.ast.CssCompilerPass;
import com.google.common.css.compiler.ast.CssDeclarationBlockNode;
import com.google.common.css.compiler.ast.CssDeclarationNode;
import com.google.common.css.compiler.ast.CssFunctionNode;
import com.google.common.css.compiler.ast.CssMixinDefinitionNode;
import com.google.common.css.compiler.ast.CssNode;
import com.google.common.css.compiler.ast.CssPriorityNode;
import com.google.common.css.compiler.ast.CssPropertyNode;
import com.google.common.css.compiler.ast.CssPropertyValueNode;
import com.google.common.css.compiler.ast.CssRootNode;
import com.google.common.css.compiler.ast.CssValueNode;
import com.google.common.css.compiler.ast.DefaultTreeVisitor;
import com.google.common.css.compiler.ast.MutatingVisitController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A compiler pass that automatically detects certain properties that need additional
 * browser specific property declarations, and adds them.
 * The properties to be matched for expansion are provided by the {@link BrowserPrefixGenerator}.
 *
 * <p>This mechanism is an alternative to using conventional mixins.
 * Problems with conventional mixins:
 * - developers have to always remember to use the mixin consistently
 * - they have to go find the appropriate mixins
 * - the framework has to to verify the code and ensure mixins were used correctly
 * Automation addresses all of the above issues.
 *
 * <p>Currently three most common cases are handled:
 * #1 Matching and replacing only the property name. Eg. flex-grow: VALUE;
 * #2 Matching property name and value, replacing the value. Eg. display: flex;
 * #3 Matching property name and value where value is a function, replacing the function name.
 *    Eg. background-image: linear-gradient(ARGS);
 *
 */
public class AutoExpandBrowserPrefix extends DefaultTreeVisitor implements CssCompilerPass {

  private final MutatingVisitController visitController;
  private final ImmutableList<BrowserPrefixRule> expansionRules;
  private boolean inDefMixinBlock;
  private HashMap<String, String> noValues;
  PrefixMap prefixMap;
  private HashMap<String, ArrayList<String>> prefixes;

  public AutoExpandBrowserPrefix(MutatingVisitController visitController, HashMap<String, ArrayList<String>> prefixes) {
    this.visitController = visitController;
    this.expansionRules = BrowserPrefixGenerator.getExpansionRules();
    this.prefixMap = new PrefixMap();
    this.noValues = new HashMap<>();
    this.prefixes = prefixes;
  }

  @Override
  public boolean enterMixinDefinition(CssMixinDefinitionNode node) {
    inDefMixinBlock = true;
    return true;
  }

  @Override
  public void leaveMixinDefinition(CssMixinDefinitionNode node) {
    inDefMixinBlock = false;
  }

  @Override
  public void leaveTree(CssRootNode root) {
    super.leaveTree(root);
    for (String name : this.noValues.keySet()) {
      String value = this.noValues.get(name);
      this.prefixMap.addProperty(name, value, null);
    }
    // System.out.print("debugger");
  }

  @Override
  public boolean enterDeclaration(CssDeclarationNode declaration) {
    // Do not auto expand properties inside @defmixin blocks.
    // To enable compatibility with existing mixin expansion, don't apply the rules to the
    // mixin definitions. This leaves the mixin expansion unaffected.
    if (inDefMixinBlock) {
      return true;
    }
    CssDeclarationBlockNode parent = (CssDeclarationBlockNode) declaration.getParent();

    ImmutableList.Builder<CssDeclarationNode> expansionNodes = ImmutableList.builder();
    ImmutableList<CssDeclarationNode> replacements = null;
    expansionNodes.add(declaration);
    boolean noValue = false;
    String matchingValueFunction = null;

    for (BrowserPrefixRule rule : expansionRules) {
      // If the name is present in the rule then it must match the declaration.
      if (rule.getMatchPropertyName() != null
          && !rule.getMatchPropertyName().equals(declaration.getPropertyName().getPropertyName())) {
        continue;
      }
      // Handle case #1 when no property value is available.
      if (rule.getMatchPropertyValue() == null) {
        for (CssDeclarationNode ruleExpansionNode : rule.getExpansionNodes()) {
          // todo handle important here
          CssPropertyNode propName = ruleExpansionNode.getPropertyName();
          if (BlockContainsPropName(parent, propName, declaration)) {
            this.prefixMap.addAlternativePropertyName(rule.getMatchPropertyName(),
              propName.getPropertyName());
            continue;
          }
          CssDeclarationNode expansionNode = ruleExpansionNode.deepCopy();
          expansionNode.setPropertyValue(declaration.getPropertyValue().deepCopy());
          expansionNode.setSourceCodeLocation(declaration.getSourceCodeLocation());
          expansionNode.setComments(declaration.getComments());
          expansionNode.appendComment(new CssCommentNode("/* @alternate */", null));
          expansionNode.getPropertyName().setSourceCodeLocation(declaration.getSourceCodeLocation());
          expansionNodes.add(expansionNode);
          noValue = true;
        }
      } else if (!rule.isFunction()) {
        // Handle case #2 where the property value is not a function.
        expansionNodes.addAll(getNonFunctionValueMatches(rule, declaration));
      } else if (hasMatchingValueOnlyFunction(declaration, rule)) {
        // todo check for existing duplicates here
        // todo handle important here
        matchingValueFunction = rule.getMatchPropertyValue();
        // Handle case #3 where the property value is a function. Eg. ~linear-gradient~calc().
        // The rule is value-only and one of the declaration values matches.
        expansionNodes.addAll(expandMatchingValueOnlyFunctions(declaration, rule));
      } else { // this were functions like linear-gradient to
        // specific property (background-image), but are disabled now to become case #3.
        // todo check for existing duplicates here
        // todo handle important here

        // The rule is not value-only or did not match, check other rules.
        expansionNodes.addAll(getOtherMatches(declaration, rule));
      }

      replacements = expansionNodes.build();

      if (replacements.size() > 1) {
        for (CssDeclarationNode node : replacements) {
          if (node.equals(declaration)) continue;
          BrowserPrefixRule.setAutoExpanded(node, rule.getMatchPropertyName(), rule.getMatchPropertyValue());
        }
        visitController.replaceCurrentBlockChildWith(
            replacements, false /* visitTheReplacementNodes */);
        break; // found a match, don't need to look for more
      }
    }
    if (replacements != null && replacements.size() > 1) {
      String value = PassUtil.printPropertyValue(declaration.getPropertyValue()).trim();
      String name = declaration.getPropertyName().getValue();

      if (matchingValueFunction != null) {
        // && this.prefixes.get(matchingValueFunction) == null
        if (this.prefixes.get(matchingValueFunction) == null) {
          this.prefixMap.addValueFunction(matchingValueFunction, name, value);
        }
      } else if (noValue) {
        String current = this.noValues.get(name);
        if (current == null) {
          this.noValues.put(name, value);
          current = value;
        }
        if (current.length() > value.length()) {
          this.noValues.put(name, value);
        }
      } else {
        this.prefixMap.addProperty(name, value, value);
      }
    }
    return true;
  }

  protected ImmutableList<CssDeclarationNode> getNonFunctionValueMatches(
      BrowserPrefixRule rule, CssDeclarationNode declaration) {
    // Ensure that the property value matches exactly. fix important
    List<CssValueNode> children = declaration.getPropertyValue().getChildren();
    CssPriorityNode priority = null;
    if (children.size() == 2 && children.get(1) instanceof CssPriorityNode) {
      priority = (CssPriorityNode) children.get(1);
    }
    if (!((children.size() == 1 || priority != null)
        && rule.getMatchPropertyValue()
            .equals(declaration.getPropertyValue().getChildAt(0).getValue()))) {
      return ImmutableList.of();
    }
    ImmutableList.Builder<CssDeclarationNode> replacements = ImmutableList.builder();
    // TODO(user): Maybe support multiple values for non-function value-only expansions.
    // this is not applicable since there are no non-function value-only expansions.
    for (CssPropertyValueNode ruleValueNode : rule.getValueOnlyExpansionNodes()) {
      // For valueOnlyExpansionNodes the property name comes from the declaration.
      CssDeclarationNode expansionNode =
          new CssDeclarationNode(
              declaration.getPropertyName(),
              ruleValueNode.deepCopy(),
              declaration.getSourceCodeLocation());
      expansionNode.appendComment(new CssCommentNode("/* @alternate */", null));
      // BrowserPrefixRule.setAutoExpanded(expansionNode, null, rule.getMatchPropertyValue());
      replacements.add(expansionNode);
    }

    // check if the block has a node with the same name, e.g., display: -ms-flex; display: flex
    Boolean includesName = BlockContainsProp((CssDeclarationBlockNode) declaration.getParent(),
        rule.getMatchPropertyName(),
      declaration);

    for (CssDeclarationNode ruleExpansionNode : rule.getExpansionNodes()) {
      if (includesName) {
        CssPropertyNode propName = ruleExpansionNode.getPropertyName();
        String propValue = BlockContainsPropWithValue(
          (CssDeclarationBlockNode) declaration.getParent(),
          propName, ruleExpansionNode.getPropertyValue(), declaration);
        if (propValue != null) {
          this.prefixMap.addProperty(propName.getPropertyName(), propValue,
          rule.getMatchPropertyValue());
          continue;
        }
      }
      CssDeclarationNode expansionNode = ruleExpansionNode.deepCopy();
      expansionNode.setSourceCodeLocation(declaration.getSourceCodeLocation());
      if (priority != null) {
        expansionNode.getPropertyValue().addChildToBack(new CssPriorityNode(priority));
      }
      // BrowserPrefixRule.setAutoExpanded(expansionNode, rule.getMatchPropertyName(), rule.getMatchPropertyValue());
      replacements.add(expansionNode);
    }
    return replacements.build();
  }

  private ImmutableList<CssDeclarationNode> getOtherMatches(
      CssDeclarationNode declaration, BrowserPrefixRule rule) {
    CssValueNode matchValueNode = declaration.getPropertyValue().getChildAt(0);
    if (!(matchValueNode instanceof CssFunctionNode)) {
      return ImmutableList.of();
    }
    CssFunctionNode matchFunctionNode = (CssFunctionNode) matchValueNode;
    if (!matchFunctionNode.getFunctionName().equals(rule.getMatchPropertyValue())) {
      return ImmutableList.of();
    }
    ImmutableList.Builder<CssDeclarationNode> replacements = ImmutableList.builder();
    for (CssDeclarationNode ruleExpansionNode : rule.getExpansionNodes()) {
      CssDeclarationNode expansionNode = ruleExpansionNode.deepCopy();
      CssValueNode expandValueNode = expansionNode.getPropertyValue().getChildAt(0);
      CssFunctionNode expandFunctionNode = (CssFunctionNode) expandValueNode;
      expandFunctionNode.setArguments(matchFunctionNode.getArguments().deepCopy());
      expansionNode.setSourceCodeLocation(declaration.getSourceCodeLocation());
      replacements.add(expansionNode);
    }
    return replacements.build();
  }

  /**
   * Returns true if the value node is a function and matches the rule.
   */
  private static boolean matchesValueOnlyFunction(CssValueNode declarationValueNode,
      BrowserPrefixRule rule) {
    return (declarationValueNode instanceof CssFunctionNode)
        && ((CssFunctionNode) declarationValueNode).getFunctionName()
            .equals(rule.getMatchPropertyValue());
  }

  /**
   * Returns true if the rule is value-only and at least one function value in the declaration
   * matches the rule.
   */
  private static boolean hasMatchingValueOnlyFunction(CssDeclarationNode declaration,
      BrowserPrefixRule rule) {
    if (rule.getValueOnlyExpansionNodes().isEmpty()) {
      return false;
    }
    for (CssValueNode declarationValueNode : declaration.getPropertyValue().getChildren()) {
      if (matchesValueOnlyFunction(declarationValueNode, rule)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns the value-only function expansion for this declaration and rule. For each value-only
   * expansion rule we can match 0 or more values.
   * For example: margin: calc(X) calc(Y); -> margin: -webkit-calc(X) -webkit-calc(Y);
   */
  private static ImmutableList<CssDeclarationNode> expandMatchingValueOnlyFunctions(
      CssDeclarationNode declaration, BrowserPrefixRule rule) {
    ImmutableList.Builder<CssDeclarationNode> expansionNodes = ImmutableList.builder();
    for (CssPropertyValueNode ruleValueNode : rule.getValueOnlyExpansionNodes()) {
      List<CssValueNode> expansionNodeValues =
          new ArrayList<>(declaration.getPropertyValue().numChildren());
      for (CssValueNode declarationValueNode : declaration.getPropertyValue().getChildren()) {
        if (matchesValueOnlyFunction(declarationValueNode, rule)) {
          CssFunctionNode declarationFunctionNode = (CssFunctionNode) declarationValueNode;
          CssFunctionNode expansionFunctionNode =
              (CssFunctionNode) ruleValueNode.getChildAt(0).deepCopy();
          expansionFunctionNode.setArguments(declarationFunctionNode.getArguments().deepCopy());
          expansionNodeValues.add(expansionFunctionNode);
        } else {
          expansionNodeValues.add(declarationValueNode.deepCopy());
        }
      }
      // For valueOnlyExpansionNodes the property name comes from the declaration.
      CssPropertyValueNode expansionValues = new CssPropertyValueNode(expansionNodeValues);
      CssDeclarationNode expansionNode =
          new CssDeclarationNode(
              declaration.getPropertyName(), expansionValues, declaration.getComments(),
          declaration.getSourceCodeLocation());
      // BrowserPrefixRule.setAutoExpanded(expansionNode, null, rule.getMatchPropertyValue());

      expansionNode.appendComment(new CssCommentNode("/* @alternate */", null));
      expansionNodes.add(expansionNode);
    }
    return expansionNodes.build();
  }

  @Override
  public void runPass() {
    visitController.startVisit(this);
  }

  private static boolean BlockContainsProp(CssDeclarationBlockNode node, String matchName,
    CssDeclarationNode declaration) {
    for (CssNode decl : node.getChildren()) {
      CssDeclarationNode d = (CssDeclarationNode) decl;
      if (d.equals(declaration)) {
        continue;
      }
      Boolean nameEquals = d.getPropertyName().getValue().equals(matchName);
      if (nameEquals) return true;
    }
    return false;
  }
  // parent:CssDeclarationBlockNode
  private static String BlockContainsPropWithValue(CssDeclarationBlockNode node, CssPropertyNode propName,
  CssPropertyValueNode propertyValue, CssDeclarationNode declaration) {
    for (CssNode decl : node.getChildren()) {
      CssDeclarationNode d = (CssDeclarationNode) decl;
      if (d.equals(declaration)) {
        continue;
      }
      Boolean nameEquals = d.getPropertyName().getValue().equals(propName.getValue());
      if (!nameEquals) continue;
      String propValue = PassUtil.printPropertyValue(propertyValue);
      Boolean valueEquals = PassUtil.printPropertyValue(d.getPropertyValue())
        .equals(propValue);
      if (valueEquals) return propValue;
    }
    return null;
  }

  private static boolean BlockContainsPropName(CssDeclarationBlockNode node, CssPropertyNode propName,
      CssDeclarationNode declaration) {
    for (CssNode decl : node.getChildren()) {
      CssDeclarationNode d = (CssDeclarationNode) decl;
      if (d.equals(declaration)) continue;
      if (d.getPropertyName().getValue().equals(propName.getValue())) {
        return true;
      }
    }
    return false;
  }
}
