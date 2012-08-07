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
package com.atlascopco.literumilo.spellchecker.hunspell;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import com.atlascopco.literumilo.spellchecker.Spellchecker;

import dk.dren.hunspell.Hunspell;
import dk.dren.hunspell.Hunspell.Dictionary;

/**
 * This is an implementation of the {@link Spellchecker} interface that uses HunspellJNA
 * in order to perform the spell-checking.
 *
 * @author Thomas Joiner
 */
public class HunspellSpellchecker implements Spellchecker {

	private final Map<Locale, Dictionary> dictionaries;
	private Dictionary currentDictionary;
	/**
	 * We hold only weak references to the listeners since the lifetime of the {@link Spellchecker}
	 * will ostensibly last the whole program, however more than likely, the components it spell-checks
	 * will be created and destroyed.
	 */
	private final List<WeakReference<DictionaryChangeListener>> listeners;

	public HunspellSpellchecker() {
		this.dictionaries = new HashMap<Locale, Hunspell.Dictionary>();
		this.listeners = new ArrayList<WeakReference<DictionaryChangeListener>>();
	}

	public void addDictionary(Locale locale, Dictionary dictionary) {
		if ( this.currentDictionary == null ) {
			this.currentDictionary = dictionary;
		}

		this.dictionaries.put(locale, dictionary);
	}

	@Override
	public boolean misspelled(String word) {
		return this.currentDictionary.misspelled(word);
	}

	@Override
	public List<String> suggest(String word) {
		return this.currentDictionary.suggest(word);
	}

	@Override
	public void setDictionary(Locale locale) throws IllegalArgumentException {
		if ( !this.dictionaries.containsKey(locale) ) {
			throw new IllegalArgumentException("No dictionary registered for locale: "+locale);
		}

		this.currentDictionary = this.dictionaries.get(locale);

		this.fireDictionaryChange();
	}

	@Override
	public List<Locale> getAvailableDictionaries() {
		return new ArrayList<Locale>(this.dictionaries.keySet());
	}

	@Override
	public Locale getCurrentDictionary() {
		for (Entry<Locale, Dictionary> entry : this.dictionaries.entrySet()) {
			if ( entry.getValue() == this.currentDictionary ) {
				return entry.getKey();
			}
		}
		throw new IllegalStateException("Currently selected dictionary no longer exists.");
	}

	@Override
	public void addDictionaryChangeListener(DictionaryChangeListener listener) {
		this.listeners.add(new WeakReference<DictionaryChangeListener>(listener));
	}

	@Override
	public void removeDictionaryChangeListener(DictionaryChangeListener listener) {
		for (Iterator<WeakReference<DictionaryChangeListener>> iterator = this.listeners.iterator(); iterator.hasNext();) {
			WeakReference<DictionaryChangeListener> weakReference = iterator.next();

			if ( weakReference.get() == null || weakReference.get().equals(listener) ) {
				iterator.remove();
			}
		}
	}

	private void fireDictionaryChange() {
		for (Iterator<WeakReference<DictionaryChangeListener>> iterator = this.listeners.iterator(); iterator.hasNext();) {
			WeakReference<DictionaryChangeListener> weakReference = iterator.next();

			if ( weakReference.get() == null ) {
				iterator.remove();
			} else {
				weakReference.get().dictionaryChanged();
			}
		}
	}

}
