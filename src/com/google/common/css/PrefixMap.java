/*
 * Copyright 2019 Art Deco
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

package com.google.common.css;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The map that contains prefixes.
 * {
 *  display: {
 *    flex: [flex, -ms-flex],
 *    inline-flex: [inline-flex, -ms-inline-flexbox]
 *  },
 *  hyphens: {
 *    auto: [auto],
 *  },
 *  -ms-hyphens: {
 *    auto: [auto],
 *   }
 * }
 *
 */
public final class PrefixMap {
  private HashMap<String, String> handledMatchingValueFunctions;
  private HashMap<String, String> handledMatchingValueFunctionsProps;

  private HashMap<String, HashMap<String, ArrayList<String>>> map;
  // e.g., expanding display: flex, but display: -ms-flex is already there
  private HashMap<String, ArrayList<String>> alternativeProps;
  private HashMap<String, ArrayList<String>> globalPrefixesWithProps; // e.g., display:flex
  private HashMap<String, Boolean> globalPrefixes; // e.g., hyphens

  public PrefixMap() {
    this.map = new HashMap<>();
    this.alternativeProps = new HashMap<>();
    this.handledMatchingValueFunctions = new HashMap<>();
    this.handledMatchingValueFunctionsProps = new HashMap<>();
    this.globalPrefixes = new HashMap<>();
    this.globalPrefixesWithProps = new HashMap<>();
  }
  public void addGlobalProp(String propName) { // e.g., hyphens
    this.globalPrefixes.put(propName, true);
  }
  public void addGlobalPropValue(String propName, String propValue) { // e.g., display: flex
    // this.globalPrefixes.put(propName, true);
    ArrayList<String> current = this.globalPrefixesWithProps.get(propName);
    if (current == null) {
      current = Lists.newArrayList();
      this.globalPrefixesWithProps.put(propName, current);
    }
    if (!current.contains(propValue)) current.add(propValue);
  }
  public void addAlternativePropertyName(String name, String alt) {
    if (name.equals(alt)) return;
    ArrayList<String> current = this.alternativeProps.get(name);
    if (current == null) {
      current = Lists.newArrayList();
      this.alternativeProps.put(name, current);
    }
    if (!current.contains(alt)) current.add(alt);
  }
  /**
   * @param matchingValueFunction The function being expanded, e.g., `cacl`.
   * @param propName The property where the match appeared.
   * @param propValue The actual value of the node.
   */
  public void addValueFunction(String matchingValueFunction, String propName, String propValue) {
    String current = this.handledMatchingValueFunctions.get(matchingValueFunction);
    if (current == null) {
      this.handledMatchingValueFunctions.put(matchingValueFunction, propValue);
      this.handledMatchingValueFunctionsProps.put(matchingValueFunction, propName);
      current = propValue;
    }
    if (current.length() > propValue.length()) {
      this.handledMatchingValueFunctions.put(matchingValueFunction, propValue);
      this.handledMatchingValueFunctionsProps.put(matchingValueFunction, propName);
    }
  }
  public void addProperty(String name, String value, String genericValueName) {
    genericValueName = genericValueName == null ? "/" : genericValueName;
    HashMap<String, ArrayList<String>> current = map.get(name);
    if (current == null) {
      current = new HashMap<>();
      map.put(name, current);
    }
    ArrayList<String> list = current.get(genericValueName);
    if (list == null) {
      list = Lists.newArrayList();
      current.put(genericValueName, list);
    }
    if (!list.contains(value)) {
      list.add(value);
    }
  }
  public HashMap<String, ArrayList<String>> getHashMap() {
    HashMap<String, ArrayList<String>> hm = new HashMap<>();
    for (String propName : map.keySet()) {
      if (globalPrefixes.containsKey(propName)) continue;
      ArrayList<String> alt = alternativeProps.get(propName);
      String k = alt == null ? propName : (propName + "|" + String.join("|", alt));

      ArrayList<String> globalGenericValues = globalPrefixesWithProps.get(propName);

      HashMap<String, ArrayList<String>> vals = map.get(propName);
      ArrayList<String> joinedValues = Lists.newArrayList();
      for (String genericValueName : vals.keySet()) {
        if (globalGenericValues != null && globalGenericValues.contains(genericValueName)) {
          continue;
        }
        ArrayList<String> values = vals.get(genericValueName);
        if (genericValueName.equals("/")) {
          joinedValues.add(values.get(0));
        } else {
          ArrayList<String> j = new ArrayList<>();
          j.add(genericValueName);
          for (String v : values) {
            if (v.equals(genericValueName)) continue;
            j.add(v);
          }
          joinedValues.add(String.join("|", j));
        }
      }
      if (joinedValues.size() > 0) {
        hm.put(k, joinedValues);
      }
    }
    for (String key : this.handledMatchingValueFunctions.keySet()) {
      String name = this.handledMatchingValueFunctionsProps.get(key);
      String value = this.handledMatchingValueFunctions.get(key);
      hm.put(name, Lists.newArrayList(value));
    }
    return hm;
  }
}