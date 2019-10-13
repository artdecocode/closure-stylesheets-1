/*
 * Copyright 2016 Google Inc.
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

package com.google.common.css.compiler.ast.testing;

import static com.google.common.base.Strings.lenientFormat;
import static com.google.common.truth.Truth.assertAbout;
import static com.google.common.truth.Fact.simpleFact;
import static com.google.common.truth.Truth.assertWithMessage;

import com.google.common.css.SourceCodeLocation;
import com.google.common.truth.FailureMetadata;
import com.google.common.truth.Subject;
import javax.annotation.CheckReturnValue;

/**
 * Truth subject for {@link SourceCodeLocation}.
 */
public class SourceCodeLocationSubject extends Subject {

  private final SourceCodeLocation actual;

  static final Subject.Factory<SourceCodeLocationSubject, SourceCodeLocation> LOCATION =
      new Subject.Factory<SourceCodeLocationSubject, SourceCodeLocation>() {
        @Override
        public SourceCodeLocationSubject createSubject(
            FailureMetadata fm, SourceCodeLocation that) {
          return new SourceCodeLocationSubject(fm, that);
        }
      };

  @CheckReturnValue
  public static SourceCodeLocationSubject assertThat(SourceCodeLocation target) {
    return assertAbout(LOCATION).that(target);
  }

  public SourceCodeLocationSubject(FailureMetadata failureMetadata, SourceCodeLocation subject) {
    super(failureMetadata, subject);
    this.actual = subject;
  }

  public void hasSpan(int beginLine, int beginIndex, int endLine, int endIndex) {
    check("not null").that(this.actual).isNotNull();
    if (!(beginLine == this.actual.getBeginLineNumber()
        && beginIndex == this.actual.getBeginIndexInLine()
        && endLine == this.actual.getEndLineNumber()
        && endIndex == this.actual.getEndIndexInLine())) {
      failWithoutActual(simpleFact(lenientFormat(
        "Location did not match <%s,%s -> %s,%s>, was <%s,%s -> %s,%s>",
        String.valueOf(beginLine),
        String.valueOf(beginIndex),
        String.valueOf(endLine),
        String.valueOf(endIndex),
        String.valueOf(this.actual.getBeginLineNumber()),
        String.valueOf(this.actual.getBeginIndexInLine()),
        String.valueOf(this.actual.getEndLineNumber()),
        String.valueOf(this.actual.getEndIndexInLine()))
      ));
    }
  }

  public void matches(String text) {
    check("not null").that(this.actual).isNotNull();
    String source =
        this.actual
            .getSourceCode()
            .getFileContents()
            .substring(
                this.actual.getBeginCharacterIndex(), this.actual.getEndCharacterIndex());
    check("isEqualTo()").that(source).isEqualTo(text);
  }

  // this is not even used anywhere
  public void isUnknown() {
    check("isNotNull()").that(this.actual).isNotNull();
    check("isUnknown()").that(this.actual.isUnknown());
  }
}
