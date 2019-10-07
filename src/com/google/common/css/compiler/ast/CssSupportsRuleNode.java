/*
 * Copyright 2008 Google Inc.
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

package com.google.common.css.compiler.ast;

import java.util.List;

/**
 * A node representing an @supports rule.
 *
 * @author oana@google.com (Oana Florescu)
 *
 */
public class CssSupportsRuleNode extends CssAtRuleNode implements ChunkAware {

  /** The chunk this selector belongs to. */
  private Object chunk;

  /**
   * Constructor of a supports rule.
   */
  public CssSupportsRuleNode() {
    super(CssAtRuleNode.Type.SUPPORTS, new CssLiteralNode("supports"));
  }

  /**
   * Constructor of a supports rule.
   */
  public CssSupportsRuleNode(List<CssCommentNode> comments) {
    super(CssAtRuleNode.Type.SUPPORTS, new CssLiteralNode("supports"), comments);
  }

  /**
   * Constructor of a supports rule.
   */
  public CssSupportsRuleNode(List<CssCommentNode> comments, CssBlockNode block) {
    super(CssAtRuleNode.Type.SUPPORTS, new CssLiteralNode("supports"), block,
        comments);
  }

  /**
   * Copy constructor.
   */
  public CssSupportsRuleNode(CssSupportsRuleNode node) {
    super(node);

    this.chunk = node.getChunk();
  }

  @Override
  public CssSupportsRuleNode deepCopy() {
    return new CssSupportsRuleNode(this);
  }

  @Override
  public CssBlockNode getBlock() {
    // This type is ensured by the constructor.
    return (CssBlockNode) super.getBlock();
  }

  @Override
  public void setChunk(Object chunk) {
    this.chunk = chunk;
  }

  @Override
  public Object getChunk() {
    return chunk;
  }
}
