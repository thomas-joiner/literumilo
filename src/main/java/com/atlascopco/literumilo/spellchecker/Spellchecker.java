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
package com.atlascopco.literumilo.spellchecker;

import java.util.List;
import java.util.Locale;

/**
 * This interface defines the methods that all spell-checking
 * libraries must implement in order to be used by the system.
 *
 * @author Thomas Joiner
 */
public interface Spellchecker {
	/**
	 * This method will check the given word to see if it is misspelled.
	 * @param word the word to check
	 * @return true if it is misspelled
	 */
	public boolean misspelled(String word);
	/**
	 * This method will return a list of suggestions of words an incorrectly
	 * spelled word could be.
	 * @param word the word to get suggestions for
	 * @return a {@link List} of correctly spelled words that the user may have intended
	 */
	public List<String> suggest(String word);
	/**
	 * This method will set the locale of the dictionary that should be used.
	 * @param locale the locale of the dictionary to use to check words
	 * @throws IllegalArgumentException thrown when an unavailable locale is specified
	 */
	public void setDictionary(Locale locale) throws IllegalArgumentException;
	/**
	 * This method will return a list of all of the available dictionaries.
	 * @return all the available dictionaries
	 */
	public List<Locale> getAvailableDictionaries();
	/**
	 * This method will return the {@link Locale} of the dictionary that is
	 * currently selected.
	 * @return the currently selected dictionary
	 */
	public Locale getCurrentDictionary();
	public void addDictionaryChangeListener(DictionaryChangeListener listener);
	public void removeDictionaryChangeListener(DictionaryChangeListener listener);

	public static interface DictionaryChangeListener {
		public void dictionaryChanged();
	}
}
