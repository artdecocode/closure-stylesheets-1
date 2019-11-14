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

/**
 * A utility for the AutoExpandBrowserPrefix pass, which provides a list of rules
 * that govern automatic addition of browser specific property declarations.
 *
 * <p>Currently three most common cases are handled:
 * #1 Matching and replacing only the property name. Eg. flex-grow: VALUE;
 * #2 Matching property name and value, replacing the value. Eg. display: flex;
 * #3 Matching property name and value where value is a function, replacing the function name.
 *    Eg. background-image: linear-gradient(ARGS);
 *
 */
public final class BrowserPrefixGenerator {

  private static final ImmutableList<BrowserPrefixRule> EXPANSION_RULES = buildExpansionRules();

  /** Returns the rules for automatic expansion of mixins. */
  public static ImmutableList<BrowserPrefixRule> getExpansionRules() {
    return EXPANSION_RULES;
  }

  private static ImmutableList<BrowserPrefixRule> buildExpansionRules() {
    ImmutableList.Builder<BrowserPrefixRule> builder = ImmutableList.builder();
    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("display")
        .matchPropertyValue("flex")
        .addExpandPropertyValue("-webkit-box")
        .addExpandPropertyValue("-moz-box")
        .addExpandPropertyValue("-webkit-flex")
        .addExpandPropertyValue("-ms-flexbox")
        .build());

    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("display")
        .matchPropertyValue("inline-flex")
        .addExpandPropertyValue("-webkit-inline-box")
        .addExpandPropertyValue("-webkit-inline-flex")
        .addExpandPropertyValue("-ms-inline-flexbox")
        .build());

     builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("flex-flow")
        .addExpandPropertyName("-ms-flex-flow")
        .addExpandPropertyName("-webkit-flex-flow")
        .build());

     builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("flex-direction")
        .addExpandPropertyName("-ms-flex-direction")
        .addExpandPropertyName("-webkit-flex-direction")
        .build());

     builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("flex-wrap")
        .addExpandPropertyName("-moz-flex-wrap")
        .addExpandPropertyName("-ms-flex-wrap")
        .addExpandPropertyName("-webkit-flex-wrap")
        .build());

     builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("flex")
        .addExpandPropertyName("-webkit-box-flex")
        .addExpandPropertyName("-moz-box-flex")
        .addExpandPropertyName("-ms-flex")
        .addExpandPropertyName("-webkit-flex")
        .build());

    // Needed for Chrome 21, Safari 7, IE 10 or less
    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("order")
        .addExpandPropertyName("-webkit-box-ordinal-group")
        .addExpandPropertyName("-moz-box-ordinal-group")
        .addExpandPropertyName("-ms-flex-order")
        .addExpandPropertyName("-webkit-order")
        .build());

    // Needed for Safari 7, Chrome 21 or less
    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("flex-basis")
        .addExpandPropertyName("-webkit-flex-basis")
        .addExpandPropertyName("-ms-flex-preferred-size")
        .build());

    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("flex-grow")
        .addExpandPropertyName("-webkit-box-flex")
        .addExpandPropertyName("box-flex")
        .addExpandPropertyName("-ms-flex-positive")
        .addExpandPropertyName("-webkit-flex-grow")
        .build());

    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("flex-shrink")
        .addExpandPropertyName("-ms-flex-negative")
        .addExpandPropertyName("-webkit-flex-shrink")
        .build());

    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("align-content")
        .addExpandPropertyName("-webkit-align-content")
        .build());

    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("align-items")
        .addExpandPropertyName("-webkit-align-items")
        .build());

    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("align-self")
        .addExpandPropertyName("-webkit-align-self")
        .addExpandPropertyName("-ms-grid-row-align")
        .build());

    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("justify-content")
        .addExpandPropertyName("-webkit-justify-content")
        .build());

    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("text-size-adjust")
        .addExpandPropertyName("-webkit-text-size-adjust")
        .addExpandPropertyName("-moz-text-size-adjust")
        .addExpandPropertyName("-ms-text-size-adjust")
        .build());

    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("animation")
        .addExpandPropertyName("-webkit-animation")
        .addExpandPropertyName("-o-animation")
        .build());

    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("animation-delay")
        .addExpandPropertyName("-webkit-animation-delay")
        .addExpandPropertyName("-o-animation-delay")
        .build());

    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("animation-direction")
        .addExpandPropertyName("-webkit-animation-direction")
        .addExpandPropertyName("-o-animation-direction")
        .build());

    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("animation-duration")
        .addExpandPropertyName("-webkit-animation-duration")
        .addExpandPropertyName("-o-animation-duration")
        .build());

    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("animation-fill-mode")
        .addExpandPropertyName("-webkit-animation-fill-mode")
        .build());

    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("animation-iteration-count")
        .addExpandPropertyName("-webkit-animation-iteration-count")
        .addExpandPropertyName("-o-animation-iteration-count")
        .build());

    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("animation-name")
        .addExpandPropertyName("-webkit-animation-name")
        .addExpandPropertyName("-o-animation-name")
        .build());

    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("animation-timing-function")
        .addExpandPropertyName("-webkit-animation-timing-function")
        .addExpandPropertyName("-o-animation-timing-function")
        .build());

    // Useful for high resolution (retina) displays.
    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("background-size")
        .addExpandPropertyName("-webkit-background-size")
        .addExpandPropertyName("-o-background-size")
        .build());

    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("backface-visibility")
        .addExpandPropertyName("-webkit-backface-visibility")
        .addExpandPropertyName("-o-backface-visibility")
        .build());

    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("border-radius")
        .addExpandPropertyName("-webkit-border-radius")
        .addExpandPropertyName("-moz-border-radius")
        .build());

    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("box-shadow")
        .addExpandPropertyName("-webkit-box-shadow")
        .addExpandPropertyName("-moz-box-shadow")
        .build());

    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("box-sizing")
        .addExpandPropertyName("-webkit-box-sizing")
        .build());

    builder.add(new BrowserPrefixRule.Builder()
        // .matchPropertyName("background-image")
        .matchPropertyValue("linear-gradient")
        .isFunction(true)
        .addExpandPropertyValue("-webkit-linear-gradient")
        .addExpandPropertyValue("-moz-linear-gradient")
        .addExpandPropertyValue("-ms-linear-gradient")
        .addExpandPropertyValue("-o-linear-gradient")
        .build());

    builder.add(new BrowserPrefixRule.Builder()
        // .matchPropertyName("background-image")
        .matchPropertyValue("repeating-linear-gradient")
        .isFunction(true)
        .addExpandPropertyValue("-webkit-repeating-linear-gradient")
        .build());

    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("cursor")
        .matchPropertyValue("grab")
        .addExpandPropertyValue("-moz-grab")
        .addExpandPropertyValue("-webkit-grab")
        .build());

    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("cursor")
        .matchPropertyValue("grabbing")
        .addExpandPropertyValue("-moz-grabbing")
        .addExpandPropertyValue("-webkit-grabbing")
        .build());

    // Needed for Firefox 15, Chrome 25, Safari 6, iOS Safari 6.1 or less
    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyValue("calc")
        .isFunction(true)
        .addExpandPropertyValue("-webkit-calc")
        .addExpandPropertyValue("-moz-calc")
        .build());

    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("column-count")
        .addExpandPropertyName("-webkit-column-count")
        .addExpandPropertyName("-moz-column-count")
        .build());

    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("column-gap")
        .addExpandPropertyName("-webkit-column-gap")
        .addExpandPropertyName("-moz-column-gap")
        .build());

    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("perspective")
        .addExpandPropertyName("-webkit-perspective")
        .build());

    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("hyphens")
        .addExpandPropertyName("-webkit-hyphens")
        .addExpandPropertyName("-moz-hyphens")
        .addExpandPropertyName("-ms-hyphens")
        .build());

    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("min-width")
        .matchPropertyValue("min-content")
        .addExpandPropertyValue("-webkit-min-content")
        .addExpandPropertyValue("-moz-min-content")
        .build());

    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("perspective-origin")
        .addExpandPropertyName("-webkit-perspective-origin")
        .build());

    builder.add(new BrowserPrefixRule.Builder()
        // .matchPropertyName("background-image")
        .matchPropertyValue("radial-gradient")
        .isFunction(true)
        .addExpandPropertyValue("-webkit-radial-gradient")
        .addExpandPropertyValue("-moz-radial-gradient")
        .addExpandPropertyValue("-o-radial-gradient")
        .build());

    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("position")
        .matchPropertyValue("sticky")
        .addExpandPropertyValue("-webkit-sticky")
        .build());

    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("transform")
        .addExpandPropertyName("-webkit-transform")
        .addExpandPropertyName("-ms-transform")
        .addExpandPropertyName("-o-transform")
        .build());

    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("transform-origin")
        .addExpandPropertyName("-webkit-transform-origin")
        .addExpandPropertyName("-ms-transform-origin")
        .addExpandPropertyName("-o-transform-origin")
        .build());

    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("transform-style")
        .addExpandPropertyName("-webkit-transform-style")
        .build());

    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("transition")
        .addExpandPropertyName("-webkit-transition")
        .addExpandPropertyName("-o-transition")
        .build());

    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("transition-delay")
        .addExpandPropertyName("-webkit-transition-delay")
        .addExpandPropertyName("-o-transition-delay")
        .build());

    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("transition-duration")
        .addExpandPropertyName("-webkit-transition-duration")
        .addExpandPropertyName("-o-transition-duration")
        .build());

    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("transition-property")
        .addExpandPropertyName("-webkit-transition-property")
        .addExpandPropertyName("-o-transition-property")
        .build());

    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("transition-timing-function")
        .addExpandPropertyName("-webkit-transition-timing-function")
        .addExpandPropertyName("-o-transition-timing-function")
        .build());

        builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("user-select")
        .addExpandPropertyName("-webkit-user-select")
        .addExpandPropertyName("-moz-user-select")
        .addExpandPropertyName("-ms-user-select")
        .build());

    // Grid support for IE:
    // https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_Grid_Layout/CSS_Grid_and_Progressive_Enhancement
    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("display")
        .matchPropertyValue("grid")
        .addExpandPropertyValue("-ms-grid")
        .build());

    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("grid-template-columns")
        .addExpandPropertyName("-ms-grid-columns")
        .build());
    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("grid-template-rows")
        .addExpandPropertyName("-ms-grid-rows")
        .build());

    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("grid-row-start")
        .addExpandPropertyName("-ms-grid-row")
        .build());
    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("grid-column-start")
        .addExpandPropertyName("-ms-grid-column")
        .build());

    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("justify-self")
        .addExpandPropertyName("-grid-column-align")
        .build());

    builder.add(new BrowserPrefixRule.Builder()
        .matchPropertyName("clip-path")
        .addExpandPropertyName("-webkit-clip-path")
        .build());

    return builder.build();
  }
}
