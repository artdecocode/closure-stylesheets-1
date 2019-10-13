/*
 * Copyright 2011 The Closure Compiler Authors.
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

package com.google.debugging.sourcemap;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.nullToEmpty;

import com.google.common.base.Preconditions;
import com.google.debugging.sourcemap.SourceMapGeneratorV3;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import javax.annotation.Nullable;

/**
 * Collects information mapping the generated (compiled) source back to
 * its original source for debugging purposes.
 *
 * Source Map Revision 3 Proposal:
 * https://docs.google.com/document/d/1U1RGAehQwRypUTovF1KRlpiOFze0b-_2gc6fAH0KY0k/edit?usp=sharing
 *
 * @author johnlenz@google.com (John Lenz)
 */
public final class SourceMapGeneratorV3Merge extends SourceMapGeneratorV3 {
  /**
   * Merges current mapping with {@code mapSectionContents} considering the
   * offset {@code (line, column)}. Any extension in the map section will be
   * ignored.
   *
   * @param line The line offset
   * @param column The column offset
   * @param mapSectionContents The map section to be appended
   * @throws SourceMapParseException
   */
  public void mergeMapSection(int line, int column, String mapSectionContents)
      throws SourceMapParseException {
    setStartingPosition(line, column);
    SourceMapConsumerV3 section = new SourceMapConsumerV3();
    section.parse(mapSectionContents);
    section.visitMappings(new ConsumerEntryVisitor());
  }

  /**
   * Works like {@link #mergeMapSection(int, int, String)}, except that
   * extensions from the @{code mapSectionContents} are merged to the top level
   * source map. For conflicts a {@code mergeAction} is performed.
   *
   * @param line The line offset
   * @param column The column offset
   * @param mapSectionContents The map section to be appended
   * @param mergeAction The merge action for conflicting extensions
   * @throws SourceMapParseException
   */
  public void mergeMapSection(int line, int column, String mapSectionContents,
      ExtensionMergeAction mergeAction)
      throws SourceMapParseException {
    setStartingPosition(line, column);
    SourceMapConsumerV3 section = new SourceMapConsumerV3();
    section.parse(mapSectionContents);
    section.visitMappings(new ConsumerEntryVisitor());
    for (Entry<String, Object> entry : section.getExtensions().entrySet()) {
       String extensionKey = entry.getKey();
       if (extensions.containsKey(extensionKey)) {
         extensions.put(extensionKey,
             mergeAction.merge(extensionKey,
                               extensions.get(extensionKey),
                               entry.getValue()));
       } else {
         extensions.put(extensionKey, entry.getValue());
       }
     }
  }
}