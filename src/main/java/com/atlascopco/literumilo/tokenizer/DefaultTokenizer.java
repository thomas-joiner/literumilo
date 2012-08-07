/*******************************************************************************
 * Copyright 2012 Atlas Copco Drilling Solutions
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.atlascopco.literumilo.tokenizer;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * This is a default implementation of the tokenizer that I have found to work relatively well
 * for US English text.
 *
 * @author Thomas Joiner
 */
public class DefaultTokenizer extends AbstractTokenizer {

	private static final Set<Character> WORD_BREAK_CHARACTERS;

	static {
		Set<Character> wordBreakCharacters = new HashSet<Character>();

		wordBreakCharacters.add('\r');
		wordBreakCharacters.add('\n');
		wordBreakCharacters.add('"');
		wordBreakCharacters.add('(');
		wordBreakCharacters.add(')');
		wordBreakCharacters.add('[');
		wordBreakCharacters.add(']');
		wordBreakCharacters.add(',');
		wordBreakCharacters.add('.');
		wordBreakCharacters.add('?');
		wordBreakCharacters.add('&');
		wordBreakCharacters.add(';');
		wordBreakCharacters.add(':');
		wordBreakCharacters.add('!');
		wordBreakCharacters.add('\u00B7'); // middle dot
		wordBreakCharacters.add('\u0387'); // greek ano teleia
		wordBreakCharacters.add('\u05F4'); // hebrew punctuation gershayim
		wordBreakCharacters.add('\u066c'); // arabic thousands separator
		wordBreakCharacters.add('\u2018'); // left single quotation mark
		wordBreakCharacters.add('\u2019'); // right single quotation mark
		wordBreakCharacters.add('\u201C'); // left double quotation mark
		wordBreakCharacters.add('\u201D'); // right double quotation mark
		wordBreakCharacters.add('\u2024'); // one dot leader
		wordBreakCharacters.add('\u2027'); // hyphenation point
		wordBreakCharacters.add('\uFE13'); // presentation form for vertical colon
		wordBreakCharacters.add('\uFE50'); // small comma
		wordBreakCharacters.add('\uFE52'); // small full stop
		wordBreakCharacters.add('\uFE54'); // small semicolon
		wordBreakCharacters.add('\uFE55'); // small colon
		wordBreakCharacters.add('\uFF07'); // fullwidth apostrophe
		wordBreakCharacters.add('\uFF0C'); // fullwidth comma
		wordBreakCharacters.add('\uFF0E'); // fullwidth full stop
		wordBreakCharacters.add('\uFF1A'); // fullwidth colon

		WORD_BREAK_CHARACTERS = Collections.unmodifiableSet(wordBreakCharacters);
	}

	/**
	 * Tests the character to see if it should be used as a word-break character.
	 *
	 * List taken from {@link http://www.unicode.org/reports/tr29/}
	 *
	 * @param character the character to test
	 * @return true if it is a word-break character
	 */
	@Override
	public boolean isWordBreakCharacter(char character) {
		return Character.isWhitespace(character) || WORD_BREAK_CHARACTERS.contains(character);
	}

}
